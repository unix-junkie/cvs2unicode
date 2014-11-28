/*-
 * $Id$
 */
package com.github.unix_junkie.cvs2unicode;

/**
 * @author Andrew ``Bass'' Shcheglov &lt;mailto:andrewbass@gmail.com&gt;
 */
@FunctionalInterface
public interface FileProcessedListener {
	void fileProcessed();
}
