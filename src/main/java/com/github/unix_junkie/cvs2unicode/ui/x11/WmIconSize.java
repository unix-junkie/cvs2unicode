/*-
 * $Id$
 */
package com.github.unix_junkie.cvs2unicode.ui.x11;

import static java.lang.Runtime.getRuntime;
import static java.lang.String.format;
import static java.lang.Thread.currentThread;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.util.logging.Level.SEVERE;

import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

/**
 * <p>Runs {@code xprop -root WM_ICON_SIZE}, parses its output and reports the
 * icon sizes supported by X11 window manager.</p>
 *
 * @author Andrey ``Bass'' Shcheglov &lt;mailto:andrewbass@gmail.com&gt;
 */
final class WmIconSize {
	private static final Logger LOGGER = Logger.getLogger(WmIconSize.class.getName());

	private static final String ATOM = "WM_ICON_SIZE";

	/**
	 * Sample output: {@code "WM_ICON_SIZE(WM_ICON_SIZE) = 8, 8, 60, 60, 1, 1"}.
	 */
	private static final Pattern PATTERN = Pattern.compile(format("\\s*%s\\s*\\(%s\\)\\s*=\\s*(\\d+),\\s*(\\d+),\\s*(\\d+),\\s*(\\d+),\\s*(\\d+),\\s*(\\d+)\\s*", ATOM, ATOM));

	private static final String XPROP_PATHS[] = {
		"xprop",
		"/usr/X11R6/bin/xprop",
		"/usr/openwin/bin/xprop",
	};

	static final Optional<WmIconSize> INSTANCE = newInstance();

	private final Dimension minimumIconSize;

	private final Dimension maximumIconSize;

	private final int incrementalSizeChangeX;

	private final int incrementalSizeChangeY;

	private WmIconSize(final Dimension minimumIconSize,
			final Dimension maximumIconSize,
			final int incrementalSizeChangeX,
			final int incrementalSizeChangeY) {
		this.minimumIconSize = minimumIconSize;
		this.maximumIconSize = maximumIconSize;
		this.incrementalSizeChangeX = incrementalSizeChangeX;
		this.incrementalSizeChangeY = incrementalSizeChangeY;
	}

	@SuppressWarnings("null")
	Dimension getMinimumIconSize() {
		return (Dimension) this.minimumIconSize.clone();
	}

	@SuppressWarnings("null")
	Dimension getMaximumIconSize() {
		return (Dimension) this.maximumIconSize.clone();
	}

	int getIncrementalSizeChangeX() {
		return this.incrementalSizeChangeX;
	}

	int getIncrementalSizeChangeY() {
		return this.incrementalSizeChangeY;
	}

	private static Optional<WmIconSize> newInstance() {
		for (final String xpropPath : XPROP_PATHS) {
			final Process xprop;
			try {
				xprop = getRuntime().exec(new String[] {
						xpropPath,
						/*
						 * Query the root window.
						 */
						"-root",
						/*
						 * Data format for WM_ICON_SIZE atom: 32-bit integer.
						 * 32c (cardinal) or 32x (hex) is also suitable.
						 */
						"-f", ATOM, "32i",
						/*
						 * The atom to query.
						 */
						ATOM});
			} catch (final IOException ioe) {
				/*
				 * We don't need any stack trace here.
				 */
				LOGGER.finest(ioe.getMessage());
				continue;
			}

			final int xpropCode;
			try {
				xpropCode = xprop.waitFor();
			} catch (@SuppressWarnings("unused") final InterruptedException ie) {
				currentThread().interrupt();
				continue;
			}

			if (xpropCode != 0) {
				LOGGER.finest(format("xprop exited with code %d", xpropCode));
				continue;
			}

			final String xpropOutput;
			try (final BufferedReader in = new BufferedReader(new InputStreamReader(xprop.getInputStream(), US_ASCII))) {
				xpropOutput = in.readLine();
			} catch (final IOException ioe) {
				LOGGER.log(SEVERE, "", ioe);
				continue;
			}

			if (xpropOutput == null) {
				continue;
			}

			final Matcher matcher = PATTERN.matcher(xpropOutput);
			if (!matcher.matches()) {
				/*
				 * Some X11 window managers do not set any WM_ICON_SIZE atom
				 * (KDE, Xfce, AfterStep, FVWM, LessTif MWM, TWM).
				 */
				LOGGER.finest(xpropOutput);
				continue;
			}

			@Nonnull
			@SuppressWarnings("null")
			final Optional<WmIconSize> instance = Optional.of(new WmIconSize(new Dimension(Integer.parseInt(matcher.group(1)),
							Integer.parseInt(matcher.group(2))),
					new Dimension(Integer.parseInt(matcher.group(3)),
							Integer.parseInt(matcher.group(4))),
					Integer.parseInt(matcher.group(5)),
					Integer.parseInt(matcher.group(6))));
			return instance;
		}

		@Nonnull
		@SuppressWarnings("null")
		final Optional<WmIconSize> empty = Optional.empty();
		return empty;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see Object#toString()
	 */
	@Override
	@SuppressWarnings("null")
	public String toString() {
		return String.format("%s(%s):\n"
				+ "                minimum icon size: %d by %d\n"
				+ "                maximum icon size: %d by %d\n"
				+ "                incremental size change: %d by %d",
				ATOM, ATOM,
				this.minimumIconSize.width, this.minimumIconSize.height,
				this.maximumIconSize.width, this.maximumIconSize.height,
				this.incrementalSizeChangeX, this.incrementalSizeChangeY);
	}
}
