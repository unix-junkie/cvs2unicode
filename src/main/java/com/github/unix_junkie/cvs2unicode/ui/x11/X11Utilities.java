/*-
 * $Id$
 */
package com.github.unix_junkie.cvs2unicode.ui.x11;

import java.awt.Dimension;
import java.util.Optional;

/**
 * @author Andrey ``Bass'' Shcheglov &lt;mailto:andrewbass@gmail.com&gt;
 */
public final class X11Utilities {
	private X11Utilities() {
		assert false;
	}

	@SuppressWarnings("null")
	public static Optional<Dimension> getMinimumIconSize() {
		return WmIconSize.INSTANCE.isPresent()
				? Optional.of(WmIconSize.INSTANCE.get().getMinimumIconSize())
				: Optional.empty();
	}

	@SuppressWarnings("null")
	public static Optional<Dimension> getMaximumIconSize() {
		return WmIconSize.INSTANCE.isPresent()
				? Optional.of(WmIconSize.INSTANCE.get().getMaximumIconSize())
				: Optional.empty();
	}
}
