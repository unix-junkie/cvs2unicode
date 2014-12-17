/*-
 * $Id$
 */
package com.github.unix_junkie.cvs2unicode;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.MalformedInputException;
import java.nio.charset.UnmappableCharacterException;

/**
 * @author Andrew ``Bass'' Shcheglov &lt;mailto:andrewbass@gmail.com&gt;
 */
public abstract class AbstractCharsetDecoder implements CharsetDecoder {
	private final Charset charset;

	/**
	 * @param charset
	 */
	protected AbstractCharsetDecoder(final Charset charset) {
		this.charset = charset;
	}

	/**
	 * @see CharsetDecoder#decode(byte[])
	 */
	@Override
	public final String decode(final byte in[]) throws CharacterCodingException {
		try {
			return this.charset.newDecoder().decode(ByteBuffer.wrap(in)).toString();
		} catch (final MalformedInputException | UnmappableCharacterException e) {
			return new String(in, this.charset);
		}
	}

	/**
	 * @see CharsetDecoder#encode(String)
	 */
	@Override
	public final ByteBuffer encode(final String in) throws CharacterCodingException {
		/*
		 * We're relying on buffers' underlying arrays of being of the
		 * same size to avoid extra copying later.
		 *
		 * For multi-byte charsets like UTF-8, the buffer returned wraps
		 * an array significantly longer than buffer's limit.
		 */
		final ByteBuffer buf;
		try {
			buf = this.charset.newEncoder().encode(CharBuffer.wrap(in));
		} catch (final UnmappableCharacterException uce) {
			return ByteBuffer.wrap(in.getBytes(this.charset));
		}
		buf.position(0);

		final int limit = buf.limit();
		if (limit == buf.capacity()) {
			/*
			 * Single-byte encodings.
			 */
			return buf;
		}

		/*
		 * UTF-8 etc.
		 */
		final byte data[] = new byte[limit];
		buf.get(data);
		return ByteBuffer.wrap(data);
	}

	/**
	 * @see CharsetDecoder#charset()
	 */
	@Override
	public final String charset() {
		return this.charset.name();
	}

	/**
	 * @see Object#toString()
	 */
	@Override
	public final String toString() {
		return this.charset();
	}
}
