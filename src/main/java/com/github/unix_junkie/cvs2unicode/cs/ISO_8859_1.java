/*-
 * $Id$
 */
package com.github.unix_junkie.cvs2unicode.cs;

import static java.nio.charset.StandardCharsets.ISO_8859_1;

import java.nio.charset.Charset;

import javax.annotation.Nonnull;

import com.github.unix_junkie.cvs2unicode.AbstractCharsetDecoder;

/**
 * @author Andrew ``Bass'' Shcheglov &lt;mailto:andrewbass@gmail.com&gt;
 */
public final class ISO_8859_1 extends AbstractCharsetDecoder {
	@Nonnull
	@SuppressWarnings("null")
	private static final Charset CHARSET = ISO_8859_1;

	public ISO_8859_1() {
		super(CHARSET);
	}
}
