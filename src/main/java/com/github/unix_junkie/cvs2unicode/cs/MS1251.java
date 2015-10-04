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
public final class MS1251 extends AbstractCharsetDecoder {
	@Nonnull
	@SuppressWarnings("null")
	private static final Charset CHARSET = Charset.forName("windows-1251");

	public MS1251() {
		super(CHARSET);
	}
}
