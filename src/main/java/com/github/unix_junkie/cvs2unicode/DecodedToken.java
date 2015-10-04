/*-
 * $Id$
 */
package com.github.unix_junkie.cvs2unicode;

import static java.util.Collections.unmodifiableList;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

	public DecodedToken(final String word) throws CharacterCodingException {
		this.decoder = new UTF_8();
		this.data = this.decoder.encode(word);
	}

	/**
	 * @param word
	 * @param decoder the most probable encoding of the word/line specified by {@code data}.
	 * @throws CharacterCodingException
	 */
	public DecodedToken(final String word,
			final CharsetDecoder decoder) throws CharacterCodingException {
		this.data = decoder.encode(word);
		this.decoder = decoder;
	}

	/**
	 * @param data
	 * @param decoder the most probable encoding of the word/line specified by {@code data}.
	 */
	public DecodedToken(final byte data[],
			final CharsetDecoder decoder) {
		/*
		 * Avoid cloning a byte array -- it's not getting changed anyway.
		 */
		@Nonnull
		@SuppressWarnings("null")
		final ByteBuffer wrappedData = ByteBuffer.wrap(data);
		this.data = wrappedData;
		this.decoder = decoder;
	}

	public final String getDecodedData() throws CharacterCodingException {
		return this.decoder.decode(this.data);
	}

	public final CharsetDecoder getDecoder() {
		return this.decoder;
	}

	public final void setDecoder(final CharsetDecoder decoder) {
		this.decoder = decoder;
	}

	public List<DecodedToken> splitIntoWords() throws CharacterCodingException {
		final List<DecodedToken> decodedTokens = new ArrayList<>();
		@Nonnull
		@SuppressWarnings("null")
		final Optional<String> decodedData = Optional.of(this.getDecodedData());
		for (final String word : Utilities.splitIntoWords(decodedData)) {
			@Nonnull
			@SuppressWarnings("null")
			final String nonNullWord = word;
			decodedTokens.add(new DecodedToken(nonNullWord, this.decoder));
		}
		@Nonnull
		@SuppressWarnings("null")
		final List<DecodedToken> unmodifiableDecodedTokens = unmodifiableList(decodedTokens);
		return unmodifiableDecodedTokens;
	}

	public boolean isAscii() {
		/*
		 * Use an underlying array. Otherwise, in a multi-threaded
		 * environment we would need to eitehr duplicate the buffer so
		 * that the position and limit are owned by current thread only,
		 * or use synchronization.
		 */
		@Nonnull
		@SuppressWarnings("null")
		final byte dataBytes[] = this.data.array();
		return Utilities.isAscii(dataBytes);
	}
}
