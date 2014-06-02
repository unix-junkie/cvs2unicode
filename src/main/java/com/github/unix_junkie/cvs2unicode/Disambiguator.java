/*-
 * $Id$
 */
package com.github.unix_junkie.cvs2unicode;

import java.io.File;

/**
 * @author Andrew ``Bass'' Shcheglov &lt;mailto:andrewbass@gmail.com&gt;
 */
public interface Disambiguator {
	/**
	 * Transcodes the data which can't be transcoded automatically,
	 * possibly with the user's intervention.
	 *
	 * @param data the 8-bit data to be transcoded.
	 * @param file the file being changed
	 * @return the transcoded Unicode string.
	 */
	String decode(final byte data[], File file);
}
