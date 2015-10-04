/*-
 * $Id$
 */
package com.github.unix_junkie.cvs2unicode;

import static com.github.unix_junkie.cvs2unicode.Utilities.isAscii;
import static java.nio.charset.StandardCharsets.US_ASCII;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetEncoder;
import java.util.List;

import javax.annotation.Nonnull;

/**
 * @author Andrew ``Bass'' Shcheglov &lt;mailto:andrewbass@gmail.com&gt;
 */
public final class Decoder {
	private final CharsetDecoder decoders[];

	private final Dictionary dictionary;

	private final Disambiguator disambiguator;

	/**
	 * @param decoders
	 * @param dictionary
	 * @param disambiguator
	 */
	public Decoder(final CharsetDecoder decoders[],
			final Dictionary dictionary,
			final Disambiguator disambiguator) {
		this.decoders = decoders.clone();
		this.dictionary = dictionary;
		this.disambiguator = disambiguator;
	}

	/**
	 * @param data the line of text within the {@code file}, to be decoded
	 * @param file the file being changed (passed to {@link Dictionary} and
	 *        {@link Disambiguator}).
	 * @param lineNumber the current line number within the {@code file}
	 *        (passed to {@link Dictionary} and {@link Disambiguator}).
	 * @throws CharacterCodingException if thrown by
	 *         {@link CharsetEncoder#encode(CharBuffer)} or
	 *         {@link java.nio.charset.CharsetDecoder#decode(ByteBuffer)}.
	 */
	public String decode(final byte data[], final File file, final int lineNumber)
	throws CharacterCodingException {
		if (isAscii(data)) {
			/*
			 * If the line of text is pure ASCII,
			 * the dictionary is not updated.
			 */
			return new String(data, US_ASCII);
		}

		float maximumHitRating = -1.f;
		@Nonnull
		@SuppressWarnings("null")
		CharsetDecoder detectedDecoder = this.decoders[0];
		for (final CharsetDecoder probableDecoder : this.decoders) {
			final String decodedData = probableDecoder.decode(data);
			final float hitRating = this.dictionary.hitRating(decodedData);
			if (hitRating > maximumHitRating) {
				maximumHitRating = hitRating;
				detectedDecoder = probableDecoder;
			}
		}

		final boolean disambiguationRequired = maximumHitRating == .0f;
		final DecodedToken decodedToken = disambiguationRequired
				? this.disambiguator.decode(data, file, lineNumber)
				: new DecodedToken(data, detectedDecoder);

		final List<DecodedToken> words = decodedToken.splitIntoWords();
		for (final DecodedToken word : words) {
			/*
			 * Don't update the dictionary with the words that
			 * fit into the ASCII table.
			 */
			if (word.isAscii()) {
				continue;
			}
			this.dictionary.add(word, file, lineNumber);
		}

		return decodedToken.getDecodedData();
	}
}
