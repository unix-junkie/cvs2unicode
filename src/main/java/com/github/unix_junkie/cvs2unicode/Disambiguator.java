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
	 * @param lineNumber the number of line being changed within the file
	 * @return the transcoded Unicode string along with the {@linkplain
	 *         CharsetDecoder decoder} used.
	 */
	DecodedToken decode(final byte data[], final File file, final int lineNumber);
}
