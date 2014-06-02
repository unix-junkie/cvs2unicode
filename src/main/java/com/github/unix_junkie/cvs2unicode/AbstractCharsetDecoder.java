/*-
 * $Id$
 */
package com.github.unix_junkie.cvs2unicode;

import java.io.UnsupportedEncodingException;

/**
 * @author Andrew ``Bass'' Shcheglov &lt;mailto:andrewbass@gmail.com&gt;
 */
public abstract class AbstractCharsetDecoder implements CharsetDecoder {
	private final String charset;

	/**
	 * @param charset
	 */
	protected AbstractCharsetDecoder(final String charset) {
		this.charset = charset;
	}

	/**
	 * @see CharsetDecoder#decode(byte[])
	 */
	@Override
	public final String decode(final byte in[]) throws UnsupportedEncodingException {
		return new String(in, this.charset);
	}

	/**
	 * @see CharsetDecoder#charset()
	 */
	@Override
	public final String charset() {
		return this.charset;
	}

	/**
	 * @see Object#toString()
	 */
	@Override
	public final String toString() {
		return this.charset();
	}
}
