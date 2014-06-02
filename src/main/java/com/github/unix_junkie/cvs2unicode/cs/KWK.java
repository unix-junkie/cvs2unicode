/*-
 * $Id$
 */
package com.github.unix_junkie.cvs2unicode.cs;

import java.io.UnsupportedEncodingException;

import com.github.unix_junkie.cvs2unicode.CharsetDecoder;

/**
 * KOI-windows-KOI (KWK) decoder. Files which have been erroneously double-encoded,
 * can be read using:
 * <pre>
 * $ cat file | iconv -f KOI8-R | iconv -t CP1251 | iconv -f KOI8-R
 * </pre>
 *
 * @author Andrew ``Bass'' Shcheglov &lt;mailto:andrewbass@gmail.com&gt;
 */
public final class KWK implements CharsetDecoder {
	/**
	 * @see CharsetDecoder#decode(byte[])
	 */
	@Override
	public String decode(final byte in[]) throws UnsupportedEncodingException {
		return new String(new String(in, "KOI8-R").getBytes("windows-1251"), "KOI8-R");
	}

	/**
	 * @see CharsetDecoder#charset()
	 */
	@Override
	public String charset() {
		return "KOI8-R \u2192 windows-1251, KOI8-R \u2192 UTF-8";
	}
}
