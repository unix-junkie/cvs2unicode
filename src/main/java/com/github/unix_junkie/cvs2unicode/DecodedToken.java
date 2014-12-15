/*-
 * $Id$
 */
package com.github.unix_junkie.cvs2unicode;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import javax.annotation.Nonnull;

/**
 * @author Andrew ``Bass'' Shcheglov &lt;mailto:andrewbass@gmail.com&gt;
 */
public class DecodedToken {
	@Nonnull
	private CharsetDecoder decoder;

	@Nonnull
	private final byte data[];

	public DecodedToken(@Nonnull final CharsetDecoder decoder,
			@Nonnull final byte data[]) {
		this.decoder = decoder;
		this.data = data.clone();
	}

	public final ByteBuffer getData() {
		return ByteBuffer.wrap(this.data).asReadOnlyBuffer();
	}

	public final String getDecodedData() throws UnsupportedEncodingException {
		return this.decoder.decode(this.data);
	}

	@Nonnull
	public final CharsetDecoder getDecoder() {
		return this.decoder;
	}

	public final void setDecoder(@Nonnull final CharsetDecoder decoder) {
		this.decoder = decoder;
	}
}
