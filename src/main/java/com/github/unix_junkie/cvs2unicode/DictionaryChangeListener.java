/*-
 * $Id$
 */
package com.github.unix_junkie.cvs2unicode;

import java.io.File;

/**
 * @author Andrew ``Bass'' Shcheglov &lt;mailto:andrewbass@gmail.com&gt;
 */
public interface DictionaryChangeListener {
	/**
	 * @param decodedToken the word or line of text along with its most probable encoding
	 * @param file the file in which the {@code word} was seen first
	 * @param lineNumber the line number within the [@code file} of the word's
	 *                   first occurrence
	 * @see Dictionary#add(DecodedToken, File, int)
	 */
	void wordAdded(final DecodedToken decodedToken, final File file, final int lineNumber);
}
