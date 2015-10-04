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
public class KOI8_R extends AbstractCharsetDecoder {
	@Nonnull
	@SuppressWarnings("null")
	private static final Charset CHARSET = Charset.forName("KOI8-R");

	public KOI8_R() {
		super(CHARSET);
	}
}
