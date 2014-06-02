/*-
 * $Id$
 */
package com.github.unix_junkie.cvs2unicode;

/**
 * @author Andrew ``Bass'' Shcheglov &lt;mailto:andrewbass@gmail.com&gt;
 */
public interface DictionaryChangeListener {
	/**
	 * @param word
	 */
	void wordAdded(final String word);
}
