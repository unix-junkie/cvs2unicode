/*-
 * $Id$
 */
package com.github.unix_junkie.cvs2unicode.editor;

import java.io.File;
import java.io.IOException;

/**
 * @author Andrew ``Bass'' Shcheglov &lt;mailto:andrewbass@gmail.com&gt;
 */
public interface EditorProvider {
	/**
	 * @param file
	 * @param line
	 * @throws IOException
	 */
	void edit(final File file, final int line) throws IOException;
}
