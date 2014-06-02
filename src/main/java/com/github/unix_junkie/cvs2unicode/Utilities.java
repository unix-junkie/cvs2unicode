/*-
 * $Id$
 */
package com.github.unix_junkie.cvs2unicode;

import static java.util.Collections.emptyList;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Andrew ``Bass'' Shcheglov &lt;mailto:andrewbass@gmail.com&gt;
 */
public abstract class Utilities {
	private static final String WORD_DELIMITERS = "t \\|/':;,.?!@#$%^&*()[]{}<>_+=\"";

	private static final String REGEXP = regexp();

	private Utilities() {
		assert false;
	}

	private static String regexp() {
		final StringBuilder regexp = new StringBuilder();
		regexp.append('[');
		for (int i = 0; i < WORD_DELIMITERS.length(); i++) {
			regexp.append('\\').append(WORD_DELIMITERS.charAt(i));
		}
		regexp.append(']');
		return regexp.toString();
	}

	/**
	 * Splits the line of text into words.
	 *
	 * @param line
	 */
	public static List<String> splitIntoWords(final String line) {
		if (line == null || line.length() == 0) {
			return emptyList();
		}

		final List<String> words = new ArrayList<>();
		for (final String probablyWord : line.split(REGEXP)) {
			if (probablyWord.length() == 0) {
				continue;
			}
			words.add(probablyWord);
		}
		return words;
	}

	/**
	 * @param data
	 */
	public static boolean isAscii(final String data) {
		for (int i = 0; i < data.length(); i++) {
			if (data.charAt(i) > 127) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @param data
	 */
	public static boolean isAscii(final byte data[]) {
		for (final byte b : data) {
			if (b < 0) {
				return false;
			}
		}
		return true;
	}
}
