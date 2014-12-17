/*-
 * $Id$
 */
package com.github.unix_junkie.cvs2unicode.cs;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;

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
	private static final CharsetDecoder KOI = new KOI8_R();

	private static final CharsetDecoder WINDOWS = new MS1251();

	/**
	 * @see CharsetDecoder#decode(byte[])
	 */
	@Override
	public String decode(final byte in[]) throws CharacterCodingException {
		return KOI.decode(WINDOWS.encode(KOI.decode(in)));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see CharsetDecoder#encode(String)
	 */
	@Override
	public ByteBuffer encode(final String in) throws CharacterCodingException {
		return KOI.encode(WINDOWS.decode(KOI.encode(in)));
	}

	/**
	 * @see CharsetDecoder#charset()
	 */
	@Override
	public String charset() {
		return "KOI8-R \u2192 windows-1251, KOI8-R \u2192 UTF-8";
	}
}
