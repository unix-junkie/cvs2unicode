/*-
 * $Id$
 */
package com.github.unix_junkie.cvs2unicode.cs;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;

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
	private static final CharsetDecoder UNICODE = new UTF_8();

	private static final CharsetDecoder KOI = new KOI8_R();

	/**
	 * @see CharsetDecoder#decode(byte[])
	 */
	@Override
	public String decode(final byte in[]) throws CharacterCodingException {
		return UNICODE.decode(KOI.encode(UNICODE.decode(in)));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see CharsetDecoder#encode(String)
	 */
	@Override
	public ByteBuffer encode(final String in) throws CharacterCodingException {
		return UNICODE.encode(KOI.decode(UNICODE.encode(in)));
	}

	/**
	 * @see CharsetDecoder#charset()
	 */
	@Override
	public String charset() {
		return "UTF-8 \u2192 KOI8-R \u2192 UTF-8";
	}
}
