/*-
 * $Id$
 */
package com.github.unix_junkie.cvs2unicode.cs;

import java.io.UnsupportedEncodingException;

import com.github.unix_junkie.cvs2unicode.CharsetDecoder;

/**
 * Attempts to partially recover the text in <em>IBM866</em> which has been
 * treated as a <em>UTF-8</em> text.
 *
 * @author Andrew ``Bass'' Shcheglov &lt;mailto:andrewbass@gmail.com&gt;
 */
public final class DosAsUnicode implements CharsetDecoder {
	/**
	 * @see CharsetDecoder#decode(byte[])
	 */
	@Override
	public String decode(final byte in[]) throws UnsupportedEncodingException {
		final String s = new String(in, "UTF-8");
		final StringBuilder accumulator = new StringBuilder();
		final StringBuilder result = new StringBuilder();
		for (int i = 0, n = s.length(); i < n; i++) {
			final char c = s.charAt(i);
			if (c == '\ufffd') {
				/*
				 * Unicode replacement character.
				 */
				final String accumulatorContents = new String(accumulator.toString().getBytes("UTF-8"), "IBM866");
				result.append(accumulatorContents);
				result.append(c);
				accumulator.setLength(0);
			} else {
				accumulator.append(c);
			}
		}

		final String accumulatorContents = new String(accumulator.toString().getBytes("UTF-8"), "IBM866");
		result.append(accumulatorContents);

		return result.toString();
	}

	/**
	 * @see CharsetDecoder#charset()
	 */
	@Override
	public String charset() {
		return "IBM866 as UTF-8";
	}
}
