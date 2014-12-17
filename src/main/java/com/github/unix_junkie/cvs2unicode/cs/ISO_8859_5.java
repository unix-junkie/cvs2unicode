/*-
 * $Id$
 */
package com.github.unix_junkie.cvs2unicode.cs;

import java.nio.charset.Charset;

import com.github.unix_junkie.cvs2unicode.AbstractCharsetDecoder;

/**
 * @author Andrew ``Bass'' Shcheglov &lt;mailto:andrewbass@gmail.com&gt;
 */
public final class ISO_8859_5 extends AbstractCharsetDecoder {
	public ISO_8859_5() {
		super(Charset.forName("ISO-8859-5"));
	}
}
