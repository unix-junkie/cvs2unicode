/*-
 * $Id$
 */
package com.github.unix_junkie.cvs2unicode.cs;

import java.io.UnsupportedEncodingException;

import com.github.unix_junkie.cvs2unicode.CharsetDecoder;

/**
 * Unicode-KOI-Unicode (UKU) decoder. The result of encoding can be observed via:
 * <pre>
 * <b>$</b> cat file | iconv -t UTF-8 | iconv -f KOI8-R | iconv -t UTF-8
 * </pre>
 *
 * Conversely, files which have been erroneously double-encoded, can be read using:
 * <pre>
 * <b>$</b> cat file | iconv -f UTF-8 | iconv -t KOI8-R | iconv -f UTF-8
 * </pre>
 *
 * @author Andrew ``Bass'' Shcheglov &lt;mailto:andrewbass@gmail.com&gt;
 */
public final class UKU implements CharsetDecoder {
	/**
	 * @see CharsetDecoder#decode(byte[])
	 */
	@Override
	public String decode(final byte in[]) throws UnsupportedEncodingException {
		return new String(new String(in, "UTF-8").getBytes("KOI8-R"), "UTF-8");
	}

	/**
	 * @see CharsetDecoder#charset()
	 */
	@Override
	public String charset() {
		return "UTF-8 \u2192 KOI8-R \u2192 UTF-8";
	}
}
