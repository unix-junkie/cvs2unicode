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
	 * @param word the lower-case word prepresentation
	 * @param file the file in which the {@code word} was seen first
	 * @param lineNumber the line number within the [@code file} of the word's
	 *                   first occurrence
	 * @param decoder the most probable encoding of the current line within
	 *        the {@code file} (specified by {@code lineNumber}).
	 * @see Dictionary#add(String, File, int, CharsetDecoder)
	 */
	void wordAdded(final String word, final File file, final int lineNumber,
			final CharsetDecoder decoder);
}
