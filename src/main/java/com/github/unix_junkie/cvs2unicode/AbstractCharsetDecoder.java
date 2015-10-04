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

import javax.annotation.Nonnull;

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
			@Nonnull
			@SuppressWarnings("null")
			final String decodedData = this.charset.newDecoder().decode(ByteBuffer.wrap(in)).toString();
			return decodedData;
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
			/*
			 * Same as above, but doesn't throw
			 * UnmappableCharacterException, silently replacing
			 * unmappable characters with '?'. 
			 */
			@Nonnull
			@SuppressWarnings("null")
			final ByteBuffer wrappedData = ByteBuffer.wrap(in.getBytes(this.charset));
			return wrappedData;
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

		@Nonnull
		@SuppressWarnings("null")
		final ByteBuffer wrappedData = ByteBuffer.wrap(data);
		return wrappedData;
	}

	/**
	 * @see CharsetDecoder#charset()
	 */
	@Override
	public final String charset() {
		@Nonnull
		@SuppressWarnings("null")
		final String charsetName = this.charset.name();
		return charsetName;
	}

	/**
	 * @see Object#toString()
	 */
	@Override
	public final String toString() {
		return this.charset();
	}
}
