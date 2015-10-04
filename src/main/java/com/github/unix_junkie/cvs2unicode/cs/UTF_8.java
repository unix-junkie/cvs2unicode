/*-
 * $Id$
 */
package com.github.unix_junkie.cvs2unicode.cs;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.nio.charset.Charset;

import javax.annotation.Nonnull;

import com.github.unix_junkie.cvs2unicode.AbstractCharsetDecoder;

/**
 * @author Andrew ``Bass'' Shcheglov &lt;mailto:andrewbass@gmail.com&gt;
 */
public final class UTF_8 extends AbstractCharsetDecoder {
	@Nonnull
	@SuppressWarnings("null")
	private static final Charset CHARSET = UTF_8;

	public UTF_8() {
		super(CHARSET);
	}
}
