/*-
 * $Id$
 */
package com.github.unix_junkie.cvs2unicode;

import java.io.UnsupportedEncodingException;

/**
 * @author Andrew ``Bass'' Shcheglov &lt;mailto:andrewbass@gmail.com&gt;
 */
public interface CharsetDecoder {
	/**
	 * @param in
	 * @throws UnsupportedEncodingException
	 * @see java.nio.charset.CharsetDecoder#decode(java.nio.ByteBuffer)
	 */
	String decode(final byte in[]) throws UnsupportedEncodingException;

	/**
	 * @see java.nio.charset.CharsetDecoder#charset()
	 */
	String charset();
}
