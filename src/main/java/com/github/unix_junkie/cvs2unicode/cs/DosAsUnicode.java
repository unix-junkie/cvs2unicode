/*-
 * $Id$
 */
package com.github.unix_junkie.cvs2unicode.cs;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;

import javax.annotation.Nonnull;

import com.github.unix_junkie.cvs2unicode.CharsetDecoder;

/**
 * Attempts to partially recover the text in <em>IBM866</em> which has been
 * treated as a <em>UTF-8</em> text.
 *
 * @author Andrew ``Bass'' Shcheglov &lt;mailto:andrewbass@gmail.com&gt;
 */
public final class DosAsUnicode implements CharsetDecoder {
	private static final CharsetDecoder UNICODE = new UTF_8();

	private static final CharsetDecoder DOS = new IBM866();

	/**
	 * @see CharsetDecoder#decode(byte[])
	 */
	@Override
	public String decode(final byte in[]) throws CharacterCodingException {
		final String s = UNICODE.decode(in);
		final StringBuilder accumulator = new StringBuilder();
		final StringBuilder result = new StringBuilder();
		for (int i = 0, n = s.length(); i < n; i++) {
			final char c = s.charAt(i);
			if (c == '\ufffd') {
				/*
				 * Unicode replacement character.
				 */
				@Nonnull
				@SuppressWarnings("null")
				final String accumulatorContents = accumulator.toString();
				final String decodedAccumulatorContents = DOS.decode(UNICODE.encode(accumulatorContents));
				result.append(decodedAccumulatorContents);
				result.append(c);
				accumulator.setLength(0);
			} else {
				accumulator.append(c);
			}
		}

		@Nonnull
		@SuppressWarnings("null")
		final String accumulatorContents = accumulator.toString();

		final String decodedAccumulatorContents = DOS.decode(UNICODE.encode(accumulatorContents));
		result.append(decodedAccumulatorContents);

		@Nonnull
		@SuppressWarnings("null")
		final String resultString = result.toString();
		return resultString;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see CharsetDecoder#encode(String)
	 */
	@Override
	public ByteBuffer encode(final String in) throws CharacterCodingException {
		@Nonnull
		@SuppressWarnings("null")
		final byte dosEncoded[] = DOS.encode(in).array();
		return UNICODE.encode(UNICODE.decode(dosEncoded));
	}

	/**
	 * @see CharsetDecoder#charset()
	 */
	@Override
	public String charset() {
		return "IBM866 as UTF-8";
	}
}
