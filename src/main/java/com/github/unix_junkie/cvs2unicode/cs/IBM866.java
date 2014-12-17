/*-
 * $Id$
 */
package com.github.unix_junkie.cvs2unicode.cs;

import java.nio.charset.Charset;

import com.github.unix_junkie.cvs2unicode.AbstractCharsetDecoder;

/**
 * @author Andrew ``Bass'' Shcheglov &lt;mailto:andrewbass@gmail.com&gt;
 */
public final class IBM866 extends AbstractCharsetDecoder {
	public IBM866() {
		super(Charset.forName("IBM866"));
	}
}
