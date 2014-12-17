/*-
 * $Id$
 */
package com.github.unix_junkie.cvs2unicode.cs;

import java.nio.charset.Charset;

import com.github.unix_junkie.cvs2unicode.AbstractCharsetDecoder;

/**
 * @author Andrew ``Bass'' Shcheglov &lt;mailto:andrewbass@gmail.com&gt;
 */
public class KOI8_R extends AbstractCharsetDecoder {
	public KOI8_R() {
		super(Charset.forName("KOI8-R"));
	}
}
