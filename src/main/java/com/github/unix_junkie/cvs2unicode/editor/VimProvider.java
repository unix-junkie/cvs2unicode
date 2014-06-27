/*-
 * $Id$
 */
package com.github.unix_junkie.cvs2unicode.editor;

import static java.lang.Runtime.getRuntime;

import java.io.File;
import java.io.IOException;

/**
 * @author Andrew ``Bass'' Shcheglov &lt;mailto:andrewbass@gmail.com&gt;
 */
public class VimProvider implements EditorProvider {
	protected static final int OPTIONS_LENGTH = 8;

	/**
	 * @see EditorProvider#edit(File, int)
	 */
	@Override
	public void edit(final File file, final int line) throws IOException {
		if (line <= 0) {
			throw new IllegalArgumentException();
		}

		final String cmdarray[] = new String[1 + OPTIONS_LENGTH];
		cmdarray[0] = "gvim";
		fillOptions(cmdarray, file, line);
		/*
		 * Don't wait for process termination.
		 */
		getRuntime().exec(cmdarray);
	}

	/**
	 * @param cmdarray
	 * @param file
	 * @param line
	 */
	protected static final void fillOptions(final String cmdarray[], final File file, final int line)  {
		int i = cmdarray.length - OPTIONS_LENGTH;
		cmdarray[i++] = "+" + line;
		cmdarray[i++] = "-c";
		cmdarray[i++] = "normal! V";
		cmdarray[i++] = "-c";
		cmdarray[i++] = "set cursorline";
		cmdarray[i++] = "-c";
		cmdarray[i++] = "set number";
		cmdarray[i++] = file.getPath();
	}
}
