/*-
 * $Id$
 */
package com.github.unix_junkie.cvs2unicode;

import java.io.File;
import java.nio.charset.CharacterCodingException;

/**
 * @author Andrew ``Bass'' Shcheglov &lt;mailto:andrewbass@gmail.com&gt;
 */
public final class DefaultDisambiguator implements Disambiguator {
	private final CharsetDecoder decoders[];

	private final Dictionary dictionary;

	/**
	 * @param decoders
	 * @param dictionary
	 */
	public DefaultDisambiguator(final CharsetDecoder decoders[],
			final Dictionary dictionary) {
		this.decoders = decoders.clone();
		this.dictionary = dictionary;
	}

	/**
	 * @see Disambiguator#decode(byte[], File, int)
	 */
	@Override
	public DecodedToken decode(final byte data[], final File file, final int lineNumber) {
		try {
			for (final CharsetDecoder decoder : this.decoders) {
				final String decodedData = decoder.decode(data);
				System.out.println("As " + decoder + " (hit rating " + this.dictionary.hitRating(decodedData) + "):\t" + decodedData);
			}
			return null;
		} catch (final CharacterCodingException cce) {
			/*
			 * Never.
			 */
			cce.printStackTrace();
			return null;
		}
	}
}
