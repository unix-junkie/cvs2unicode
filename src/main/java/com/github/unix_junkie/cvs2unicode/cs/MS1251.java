/*-
 * $Id$
 */
package com.github.unix_junkie.cvs2unicode.cs;

import java.nio.charset.Charset;

import com.github.unix_junkie.cvs2unicode.AbstractCharsetDecoder;

/**
 * @author Andrew ``Bass'' Shcheglov &lt;mailto:andrewbass@gmail.com&gt;
 */
public final class MS1251 extends AbstractCharsetDecoder {
	public MS1251() {
		super(Charset.forName("windows-1251"));
	}
}
