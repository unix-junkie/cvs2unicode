/*-
 * $Id$
 */
package com.github.unix_junkie.cvs2unicode.editor;

import static java.lang.System.getProperty;

/**
 * @author Andrew ``Bass'' Shcheglov &lt;mailto:andrewbass@gmail.com&gt;
 */
public final class VimFactory implements EditorFactory {
	/**
	 * @see EditorFactory#newEditor()
	 */
	@Override
	public VimProvider newEditor() {
		return getProperty("os.name").equals("Mac OS X") ? new MacVimProvider() : new VimProvider();
	}
}
