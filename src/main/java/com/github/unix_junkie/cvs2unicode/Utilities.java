/*-
 * $Id$
 */
package com.github.unix_junkie.cvs2unicode;

import static java.util.Collections.emptyList;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Andrew ``Bass'' Shcheglov &lt;mailto:andrewbass@gmail.com&gt;
 */
public abstract class Utilities {
	private static final String WORD_DELIMITERS = "\t \\|/`':;,.?!@#$%^&*()[]{}<>_+=\"\u00AB\u00B7\u00BB\uFEFF";

	private static final Pattern WORD_DELIMITERS_PATTERN = Pattern.compile(regex());

	private Utilities() {
		assert false;
	}

	private static String regex() {
		final StringBuilder regex = new StringBuilder();
		regex.append('[');
		WORD_DELIMITERS.chars().forEach(c -> regex.append('\\').append((char) c));
		regex.append("]+");
		@Nonnull
		@SuppressWarnings("null")
		final String regexString = regex.toString();
		return regexString;
	}

	/**
	 * Splits the line of text into words.
	 *
	 * @param line
	 */
	public static List<String> splitIntoWords(@Nullable final String line) {
		if (line == null || line.length() == 0) {
			@Nonnull
			@SuppressWarnings("null")
			final List<String> emptyList = emptyList();
			return emptyList;
		}

		final List<String> words = new ArrayList<>();
		for (final String probablyWord : WORD_DELIMITERS_PATTERN.split(line)) {
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
