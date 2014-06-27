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
public final class MacVimProvider extends VimProvider {
	/**
	 * @see VimProvider#edit(File, int)
	 */
	@Override
	public void edit(final File file, final int line) throws IOException {
		try {
			if (line <= 0) {
				throw new IllegalArgumentException();
			}

			/*
			 * /Applications/MacPorts/MacVim.app/Contents/MacOS/MacVim
			 * doesn't accept all of the standard Vim arguments.
			 * This is the reason Vim is not launched via
			 * "open /Applications/MacPorts/MacVim.app".
			 */
			final String cmdarray[] = new String[2 + OPTIONS_LENGTH];
			cmdarray[0] = "/Applications/MacPorts/MacVim.app/Contents/MacOS/Vim";
			cmdarray[1] = "-g";
			fillOptions(cmdarray, file, line);
			/*
			 * Don't wait for process termination.
			 */
			getRuntime().exec(cmdarray);
		} catch (final IOException ioe) {
			super.edit(file, line);
		}
	}
}
