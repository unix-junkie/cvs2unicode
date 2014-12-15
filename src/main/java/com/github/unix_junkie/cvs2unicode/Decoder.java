/*-
 * $Id$
 */
package com.github.unix_junkie.cvs2unicode;

import static com.github.unix_junkie.cvs2unicode.Utilities.isAscii;
import static com.github.unix_junkie.cvs2unicode.Utilities.splitIntoWords;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.List;

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
	 */
	public Decoder(final CharsetDecoder decoders[],
			final Dictionary dictionary) {
		this(decoders, dictionary, new DefaultDisambiguator(decoders, dictionary));
	}

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
	 * @param data
	 * @param file the file being changed
	 * @param lineNumber
	 */
	public String decode(final byte data[], final File file, final int lineNumber) {
		try {
			if (isAscii(data)) {
				/*
				 * If the line of text is pure ASCII,
				 * the dictionary is not updated.
				 */
				return new String(data, "US-ASCII");
			}

			float maximumHitRating = -1.f;
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
					: new DecodedToken(detectedDecoder, data);

			final String decodedData = decodedToken.getDecodedData();
			final List<String> words = splitIntoWords(decodedData);
			for (final String word : words) {
				/*
				 * Don't update the dictionary with the words that
				 * fit into the ASCII table.
				 */
				if (isAscii(word.getBytes("UTF-8"))) {
					continue;
				}
				this.dictionary.add(word, file, lineNumber, decodedToken.getDecoder());
			}

			return decodedData;
		} catch (final UnsupportedEncodingException uee) {
			/*
			 * Never.
			 */
			uee.printStackTrace();
			return null;
		}
	}
}
