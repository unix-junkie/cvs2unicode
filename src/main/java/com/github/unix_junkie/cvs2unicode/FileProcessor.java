/*-
 * $Id$
 */
package com.github.unix_junkie.cvs2unicode;

import static com.github.unix_junkie.cvs2unicode.Main.isVersionedTextFile;
import static com.github.unix_junkie.cvs2unicode.Main.toFile;
import static java.lang.System.currentTimeMillis;
import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.Files.walkFileTree;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Andrew ``Bass'' Shcheglov &lt;mailto:andrewbass@gmail.com&gt;
 */
public final class FileProcessor {
	@Nonnull
	final Decoder decoder;

	@Nonnull
	private final Optional<String> cvsroot;

	/**
	 * @param decoder
	 * @param cvsroot
	 */
	public FileProcessor(final Decoder decoder, final Optional<String> cvsroot) {
		this.decoder = decoder;
		this.cvsroot = cvsroot;
	}

	/**
	 * @param l
	 * @throws IOException
	 */
	public String processDirectory(final FileProcessedListener l) throws IOException {
		final long t0 = currentTimeMillis();
		walkFileTree(toFile(this.cvsroot).toPath(), new SimpleFileVisitor<Path>() {
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

				if (attrs.isRegularFile()) {
					@Nonnull
					@SuppressWarnings("null")
					final File file = path.toFile();
					processFile(FileProcessor.this.decoder, file, l);
				}
				return super.visitFile(path, attrs);
			}
		});
		final long t1 = currentTimeMillis();
		return "Completed in " + MILLISECONDS.toSeconds(t1 - t0) + " second(s).";
	}

	/**
	 * @param decoder
	 * @param file
	 * @throws IOException
	 */
	static void processFile(final Decoder decoder, final File file, final FileProcessedListener l) throws IOException {
		if (!isVersionedTextFile(file)) {
			/*
			 * Skip binary files.
			 *
			 * We don't know whether files not managed by CVS
			 * are text or binary, so not doing any conversion.
			 */
			return;
		}

		/*
		 * Encoding used when reading files.
		 * Can be any 8-bit encoding.
		 */
		final String encoding = "ISO-8859-1";
		try (final BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding))) {
			String line;
			int lineNumber = 0;
			while ((line = in.readLine()) != null) {
				@Nonnull
				@SuppressWarnings("null")
				final byte lineBytes[] = line.getBytes(encoding);
				/*
				 * Decoded line of text is ignored so far.
				 */
				decoder.decode(lineBytes, file, ++lineNumber);
			}
		}

		l.fileProcessed();
	}
}
