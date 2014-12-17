/*-
 * $Id$
 */
package com.github.unix_junkie.cvs2unicode;

import static java.util.Collections.unmodifiableList;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.github.unix_junkie.cvs2unicode.cs.UTF_8;

/**
 * @author Andrew ``Bass'' Shcheglov &lt;mailto:andrewbass@gmail.com&gt;
 */
public class DecodedToken {
	@Nonnull
	private final ByteBuffer data;

	@Nonnull
	private CharsetDecoder decoder;

	public DecodedToken(@Nonnull final String word) throws CharacterCodingException {
		this.decoder = new UTF_8();
		this.data = this.decoder.encode(word);
	}

	/**
	 * @param word
	 * @param decoder the most probable encoding of the word/line specified by {@code data}.
	 * @throws CharacterCodingException
	 */
	public DecodedToken(@Nonnull final String word,
			@Nonnull final CharsetDecoder decoder) throws CharacterCodingException {
		this.data = decoder.encode(word);
		this.decoder = decoder;
	}

	/**
	 * @param data
	 * @param decoder the most probable encoding of the word/line specified by {@code data}.
	 */
	public DecodedToken(@Nonnull final byte data[],
			@Nonnull final CharsetDecoder decoder) {
		/*
		 * Avoid cloning a byte array -- it's not getting changed anyway.
		 */
		this.data = ByteBuffer.wrap(data);
		this.decoder = decoder;
	}

	public final String getDecodedData() throws CharacterCodingException {
		return this.decoder.decode(this.data);
	}

	@Nonnull
	public final CharsetDecoder getDecoder() {
		return this.decoder;
	}

	public final void setDecoder(@Nonnull final CharsetDecoder decoder) {
		this.decoder = decoder;
	}

	public List<DecodedToken> splitIntoWords() throws CharacterCodingException {
		final List<DecodedToken> decodedTokens = new ArrayList<>();
		for (final String word : Utilities.splitIntoWords(this.getDecodedData())) {
			decodedTokens.add(new DecodedToken(word, this.decoder));
		}
		return unmodifiableList(decodedTokens);
	}

	public boolean isAscii() {
		/*
		 * Use an underlying array. Otherwise, in a multi-threaded
		 * environment we would need to eitehr duplicate the buffer so
		 * that the position and limit are owned by current thread only,
		 * or use synchronization.
		 */
		return Utilities.isAscii(this.data.array());
	}
}
