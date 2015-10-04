/*-
 * $Id$
 */
package com.github.unix_junkie.cvs2unicode;

import static java.lang.System.getenv;
import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.Files.walkFileTree;
import static java.util.Arrays.asList;
import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static javax.swing.SwingUtilities.invokeLater;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.CharacterCodingException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.LongAdder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.table.DefaultTableModel;

import com.github.unix_junkie.cvs2unicode.cs.DosAsUnicode;
import com.github.unix_junkie.cvs2unicode.cs.IBM866;
import com.github.unix_junkie.cvs2unicode.cs.ISO_8859_1;
import com.github.unix_junkie.cvs2unicode.cs.ISO_8859_5;
import com.github.unix_junkie.cvs2unicode.cs.KOI8_R;
import com.github.unix_junkie.cvs2unicode.cs.KWK;
import com.github.unix_junkie.cvs2unicode.cs.MS1251;
import com.github.unix_junkie.cvs2unicode.cs.UTF_8;
import com.github.unix_junkie.cvs2unicode.ui.InteractiveDisambiguator;
import com.github.unix_junkie.cvs2unicode.ui.MainFrameFactory;

/**
 * @author Andrew ``Bass'' Shcheglov &lt;mailto:andrewbass@gmail.com&gt;
 */
public abstract class Main {
	@Nonnull
	private static final CharsetDecoder DECODERS[] = {
		new ISO_8859_1(),
		new ISO_8859_5(),
		new KOI8_R(),
		new MS1251(),
		new KWK(),
		new IBM866(),
		new DosAsUnicode(),
		new UTF_8(),
	};

	private Main() {
		assert false;
	}

	/**
	 * <p>Reads the next header entry from the RCS-versioned file.</p>
	 *
	 * @param in
	 * @return either a single- or multi-line RCS header entry terminated
	 *         with a semicolon ({@code "access;"} or {@code "symbols\n TAG0:1.1;"}),
	 *         or an empty line if the end of the RCS header is met,
	 *         or {@code null} if the input is truncated (malformed RCS header).
	 * @throws IOException
	 */
	@Nullable
	static String readHeaderLine(final BufferedReader in) throws IOException {
		final StringBuilder headerLine = new StringBuilder();
		String line;
		while ((line = in.readLine()) != null) {
			headerLine.append(line);
			/*
			 * Break on an empty string read ("expand" is missing)
			 * or once ';' is reached.
			 */
			if (line.length() == 0) {
				return line;
			}
			if (line.charAt(line.length() - 1) == ';') {
				break;
			}
			headerLine.append('\n');
		}

		/*
		 * If #append() was never called within the loop, return null
		 * to signal EOF.
		 */
		return headerLine.length() == 0 ? null : headerLine.toString();
	}

	/**
	 * @param file
	 * @return the CVS keyword expansion mode for this particular file,
	 *         one of "b", "k", "kv", "kvl", "o", "v".
	 * @throws IOException
	 */
	private static String getCvsKeywords(final File file) throws IOException {
		/*
		 * The encoding used when reading a file's header
		 * can be arbitrary (even US-ASCII), since CVS
		 * tag and branch names can only contain ASCII.
		 */
		try (final BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), "ISO-8859-1"))) {
			/*-
			 * Sample header of an RCS/CVS file
			 * ("branch" is optional, "symbols" may be multiline):
			 *
			 * head    1.1;
			 * branch	1.1.1;
			 * access;
			 * symbols	XYZ;
			 * locks; strict;
			 * comment @# @;
			 * expand  @k@;
			 */
			readHeaderLine(in); // skip "head"
			final String branchOrAccess = readHeaderLine(in); // skip "branch" or "access"
			if (branchOrAccess == null) {
				throw new IllegalArgumentException(file.getName() + ": EOF reached.");
			}
			int i = branchOrAccess.startsWith("branch") ? 0 : 1;
			while (i++ < 4) {
				/*
				 * Skip 3 to 4 lines more.
				 */
				readHeaderLine(in);
			}
			final String expand = readHeaderLine(in);
			if (expand == null) {
				throw new IllegalArgumentException(file.getName() + ": EOF reached while searching for keyword expansion.");
			}

			if (expand.length() == 0) {
				/*
				 * If the keyword expansion is not specified,
				 * then the value is "kv" (the default).
				 */
				return "kv";
			}

			if (!expand.matches("^expand\\s+\\@[bklov]{1,3}\\@\\;$")) {
				throw new IllegalArgumentException(file.getName() + ": Incorrect keyword expansion format: " + expand);
			}

			@Nonnull
			@SuppressWarnings("null")
			final String cvsKeywords = expand.substring("expand\t@".length(), expand.length() - 2);
			return cvsKeywords;
		}
	}

	/**
	 * @param file
	 * @return {@code true} if a file is marked a binary (-kb) in CVS.
	 * @throws IOException
	 */
	private static boolean isBinary(final File file) throws IOException {
		return getCvsKeywords(file).equals("b");
	}

	/**
	 * @param file
	 * @return {@code true} if a file is a versioned (i.e. managed by CVS) plain-text one.
	 * @throws IOException
	 */
	static boolean isVersionedTextFile(final File file) throws IOException {
		return file.getName().endsWith(",v") && !isBinary(file);
	}

	public static long countTextFiles(final File directory) throws IOException {
		final LongAdder textFileCount = new LongAdder();

		/*
		 * We're not following any links here.
		 * Interested in regular files only.
		 */
		walkFileTree(directory.toPath(), new SimpleFileVisitor<Path>() {
			/**
			 * @see SimpleFileVisitor#visitFile(Object, BasicFileAttributes)
			 */
			@Nullable
			@Override
			public FileVisitResult visitFile(@Nullable final Path path,
					@Nullable final BasicFileAttributes attrs)
			throws IOException {
				if (path == null || attrs == null) {
					return CONTINUE;
				}

				@Nonnull
				@SuppressWarnings("null")
				final File file = path.toFile();
				if (attrs.isRegularFile() && isVersionedTextFile(file)) {
					textFileCount.increment();
				}
				return super.visitFile(path, attrs);
			}
		});

		return textFileCount.longValue();
	}

	/**
	 * @param cvsroot
	 * @throws IOException
	 */
	public static File toFile(@Nullable final String cvsroot) throws IOException {
		if (cvsroot == null) {
			throw new IOException("CVSROOT is undefined; exiting...");
		}

		if (!cvsroot.matches("^\\:local\\:.*$")) {
			throw new IOException("Only :local: scheme is supported; exiting...");
		}

		final File localCvsRoot = new File(cvsroot.substring(":local:".length()));
		if (!localCvsRoot.exists()) {
			throw new IOException("No such file or directory: " + localCvsRoot);
		}
		if (!localCvsRoot.isDirectory()) {
			throw new IOException("Not a directory: " + localCvsRoot);
		}

		return localCvsRoot;
	}

	/**
	 * @param args
	 */
	public static void main(final String args[]) {
		final String cvsroot = getenv("CVSROOT");

		@Nonnull
		@SuppressWarnings("null")
		final ExecutorService backgroundWorker = newSingleThreadExecutor();

		try {
			JFrame.setDefaultLookAndFeelDecorated(true);
			JDialog.setDefaultLookAndFeelDecorated(true);
			MetalLookAndFeel.setCurrentTheme(new DefaultMetalTheme());
			UIManager.setLookAndFeel(new MetalLookAndFeel());
		} catch (final UnsupportedLookAndFeelException ulafe) {
			ulafe.printStackTrace();
		}

		final DefaultTableModel tableModel = new DefaultTableModel(
				new Vector<>(),
				new Vector<>(asList("Word", "File", "Line", "Encoding"))) {
			private static final long serialVersionUID = 4182081799950235108L;

			/**
			 * @see DefaultTableModel#isCellEditable(int, int)
			 */
			@Override
			public boolean isCellEditable(final int row, final int column) {
				return false;
			}
		};

		final Dictionary dictionary = new Dictionary((word, file, lineNumber) -> invokeLater(() -> {
			try {
				tableModel.addRow(new String[] {word.getDecodedData().toLowerCase(), file.getName(), String.valueOf(lineNumber), word.getDecoder().charset()});
			} catch (final CharacterCodingException cce) {
				/*
				 * Never.
				 */
				cce.printStackTrace();
			}
		}));
		final InteractiveDisambiguator disambiguator = new InteractiveDisambiguator(DECODERS);
		final Decoder decoder = new Decoder(DECODERS, dictionary, disambiguator);

		final JFrame frame = MainFrameFactory.newInstance(cvsroot,
				tableModel,
				backgroundWorker,
				new FileProcessor(decoder, cvsroot));
		disambiguator.setParent(frame);
		frame.pack();
		frame.setVisible(true);
	}
}
