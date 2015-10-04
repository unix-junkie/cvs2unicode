/*-
 * $Id$
 */
package com.github.unix_junkie.cvs2unicode.cs;

import java.nio.charset.Charset;

import javax.annotation.Nonnull;

import com.github.unix_junkie.cvs2unicode.AbstractCharsetDecoder;

/**
 * @author Andrew ``Bass'' Shcheglov &lt;mailto:andrewbass@gmail.com&gt;
 */
public final class IBM866 extends AbstractCharsetDecoder {
	@Nonnull
	@SuppressWarnings("null")
	private static final Charset CHARSET = Charset.forName("IBM866");

	public IBM866() {
		super(CHARSET);
	}
}
