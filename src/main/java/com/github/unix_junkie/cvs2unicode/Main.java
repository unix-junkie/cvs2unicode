/*-
 * $Id$
 */
package com.github.unix_junkie.cvs2unicode;

import static java.lang.System.currentTimeMillis;
import static java.lang.System.getenv;
import static java.nio.file.Files.walkFileTree;
import static java.util.Arrays.asList;
import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static javax.swing.SwingUtilities.invokeLater;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.LongAdder;

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
	 * @param in
	 * @throws IOException
	 */
	private static String readHeaderLine(final BufferedReader in) throws IOException {
		final StringBuilder headerLine = new StringBuilder();
		String line;
		while ((line = in.readLine()) != null) {
			headerLine.append(line);
			/*
			 * Break on an empty string read ("expand" is missing)
			 * or once ';' is reached.
			 */
			if (line.length() == 0 || line.charAt(line.length() - 1) == ';') {
				break;
			}
			headerLine.append('\n');
		}
		return headerLine.toString();
	}

	/**
	 * @param file
	 * @return the CVS keyword expansion mode for this particular file,
	 *         one of "b", "k", "kv", "kvl", "o", "v".
	 */
	private static String getCvsKeywords(final File file) {
		try {
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
				int i = readHeaderLine(in).startsWith("branch") ? 0 : 1; // skip "branch" or "access"
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

				return expand.substring("expand\t@".length(), expand.length() - 2);
			}
		} catch (final IOException ioe) {
			/*
			 * Fatal.
			 */
			ioe.printStackTrace();
			System.exit(0);
			return null;
		}
	}

	/**
	 * @param file
	 * @return {@code true} if a file is marked a binary (-kb) in CVS.
	 */
	private static boolean isBinary(final File file) {
		return getCvsKeywords(file).equals("b");
	}

	/**
	 * @param file
	 * @return {@code true} if a file is a versioned (i.e. managed by CVS) plain-text one.
	 */
	static boolean isVersionedTextFile(final File file) {
		return file.getName().endsWith(",v") && !isBinary(file);
	}

	/**
	 * @param decoder
	 * @param file
	 */
	static void processFile(final Decoder decoder, final File file) {
		if (!isVersionedTextFile(file)) {
			/*
			 * Skip binary files.
			 *
			 * We don't know whether files not managed by CVS
			 * are text or binary, so not doing any conversion.
			 */
			return;
		}

		try {
			/*
			 * Encoding used when reading files.
			 * Can be any 8-bit encoding.
			 */
			final String encoding = "ISO-8859-1";
			try (final BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding))) {
				String line;
				int lineNumber = 0;
				while ((line = in.readLine()) != null) {
					decoder.decode(line.getBytes(encoding), file, ++lineNumber);
				}
			}
		} catch (final IOException ioe) {
			/*
			 * Fatal.
			 */
			ioe.printStackTrace();
			System.exit(0);
		}
	}

	static long countTextFiles(final File directory) throws IOException {
		final LongAdder textFileCount = new LongAdder();

		/*
		 * We're not following any links here.
		 * Interested in regular files only.
		 */
		walkFileTree(directory.toPath(), new SimpleFileVisitor<Path>() {
			/**
			 * @see SimpleFileVisitor#visitFile(Object, BasicFileAttributes)
			 */
			@Override
			public FileVisitResult visitFile(final Path file,
					final BasicFileAttributes attrs)
			throws IOException {
				if (attrs.isRegularFile() && isVersionedTextFile(file.toFile())) {
					textFileCount.increment();
				}
				return super.visitFile(file, attrs);
			}
		});

		return textFileCount.longValue();
	}

	/**
	 * @param decoder
	 * @param directory
	 * @throws IOException
	 */
	static void processDirectory(final Decoder decoder, final File directory) throws IOException {
		walkFileTree(directory.toPath(), new SimpleFileVisitor<Path>() {
			/**
			 * @see SimpleFileVisitor#visitFile(Object, BasicFileAttributes)
			 */
			@Override
			public FileVisitResult visitFile(final Path file,
					final BasicFileAttributes attrs)
			throws IOException {
				if (attrs.isRegularFile()) {
					processFile(decoder, file.toFile());
				}
				return super.visitFile(file, attrs);
			}
		});
	}

	/**
	 * @param args
	 */
	public static void main(final String args[]) {
		final String cvsroot = getenv("CVSROOT");
		if (cvsroot == null) {
			System.out.println("CVSROOT is undefined; exiting...");
			return;
		}

		if (!cvsroot.matches("^\\:local\\:.*$")) {
			System.out.println("Only :local: scheme is supported; exiting...");
			return;
		}

		final File localCvsRoot = new File(cvsroot.substring(":local:".length()));
		if (!localCvsRoot.exists()) {
			System.out.println("No such file or directory: " + localCvsRoot);
			return;
		}
		if (!localCvsRoot.isDirectory()) {
			System.out.println("Not a directory: " + localCvsRoot);
			return;
		}

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

		final Dictionary dictionary = new Dictionary((word, file, lineNumber, decoder) -> invokeLater(() -> {
			tableModel.addRow(new String[] {word, file.getName(), String.valueOf(lineNumber), decoder.charset()});
			System.out.println(word);
		}));
		final InteractiveDisambiguator disambiguator = new InteractiveDisambiguator(DECODERS, localCvsRoot);
		final Decoder decoder = new Decoder(DECODERS, dictionary, disambiguator);

		final JFrame frame = MainFrameFactory.newInstance(cvsroot, tableModel, backgroundWorker, () -> {
			final long t0 = currentTimeMillis();
			long textFileCount = -1;
			try {
				textFileCount = countTextFiles(localCvsRoot);
			} finally {
				final long t1 = currentTimeMillis();
				System.out.println("Found " + textFileCount + " versioned text file(s) in " + (t1 - t0) + " millisecond(s).");
			}
			processDirectory(decoder, localCvsRoot);
			return null;
		});
		disambiguator.setParent(frame);
		frame.pack();
		frame.setVisible(true);
	}
}
