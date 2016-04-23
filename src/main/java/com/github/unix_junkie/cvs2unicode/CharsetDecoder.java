/*-
 * $Id$
 */
package com.github.unix_junkie.cvs2unicode;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetEncoder;

import javax.annotation.Nonnull;

/**
 * @author Andrew ``Bass'' Shcheglov &lt;mailto:andrewbass@gmail.com&gt;
 */
public interface CharsetDecoder {
	/**
	 * @param in
	 * @throws CharacterCodingException
	 * @see #decode(ByteBuffer)
	 * @see java.nio.charset.CharsetDecoder#decode(ByteBuffer)
	 */
	String decode(final byte in[]) throws CharacterCodingException;

	/**
	 * @param in
	 * @throws CharacterCodingException
	 * @see #decode(byte[])
	 * @see java.nio.charset.CharsetDecoder#decode(ByteBuffer)
	 */
	default String decode(final ByteBuffer in) throws CharacterCodingException {
		/*
		 * Assume position == 0, limit == capacity and use an underlying
		 * array. Otherwise, in a multi-threaded environment we would
		 * need to either duplicate the buffer so that the position and
		 * limit are owned by current thread only, or use
		 * synchronization.
		 */
		@Nonnull
		@SuppressWarnings("null")
		final byte data[] = in.array();
		return this.decode(data);
	}

	/**
	 * @param in
	 * @throws CharacterCodingException
	 * @see CharsetEncoder#encode(CharBuffer)
	 */
	ByteBuffer encode(final String in) throws CharacterCodingException;

	/**
	 * @see java.nio.charset.CharsetDecoder#charset()
	 */
	String charset();
}
