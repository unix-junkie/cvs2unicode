/*-
 * $Id$
 */
package com.github.unix_junkie.cvs2unicode;

import static com.github.unix_junkie.cvs2unicode.Utilities.isAscii;
import static com.github.unix_junkie.cvs2unicode.Utilities.splitIntoWords;
import static java.io.File.separatorChar;
import static java.lang.System.exit;
import static java.lang.System.getProperty;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author Andrew ``Bass'' Shcheglov &lt;mailto:andrewbass@gmail.com&gt;
 */
public final class Dictionary {
	private final SortedSet<String> dictionary = new TreeSet<>();

	private final DictionaryChangeListener changeListener;

	public Dictionary() {
		this(null);
	}

	/**
	 * @param changeListener
	 */
	public Dictionary(final DictionaryChangeListener changeListener) {
		/*
		 * Dictionaries are loaded *before* the listener is initialized.
		 */
		this.loadBundledDictionary("/ru_RU-ispell.dic");
		this.loadBundledDictionary("/ru_RU-hunspell.dic");
		this.loadBundledDictionary("/ru_RU-hunspell-debian.dic");
		this.loadCustomDictionary();

		this.changeListener = changeListener;
	}

	/**
	 * @param word
	 */
	public void add(final String word) {
		if (this.changeListener != null && !this.contains(word)) {
			this.changeListener.wordAdded(word.toLowerCase());
		}
		this.dictionary.add(word.toLowerCase());
	}

	/**
	 * @param word
	 */
	public boolean contains(final String word) {
		return this.dictionary.contains(word.toLowerCase());
	}

	/**
	 * Produces the <em>hit rating</em> for a given line of text: the
	 * weighted ratio of words found in the dictionary to the total number of
	 * non-ASCII words in this phrase. The longer is the word, the
	 * more weight it contributes. If the dictionary doesn't contain any
	 * words from the phrase, then {@code 0.0f} is returned.
	 *
	 * @param line the line of text.
	 * @return the the <em>hit rating</em> for a given line of text.
	 */
	public float hitRating(final String line) {
		final List<String> words = splitIntoWords(line);

		/*
		 * Retain only non-ASCII words.
		 */
		final List<String> nonAsciiWords = new ArrayList<>();
		for (final String word : words) {
			if (isAscii(word)) {
				continue;
			}

			nonAsciiWords.add(word);
		}

		/*
		 * This is just a safety net: pure ASCII lines are not decoded.
		 */
		if (nonAsciiWords.isEmpty()) {
			return .0f;
		}
		int occurrences = 0;
		int length = 0;
		for (final String word : nonAsciiWords) {
			final int weight = word.length();
			if (this.contains(word)) {
				occurrences += weight;
			}
			length += weight;
		}

		/*
		 * Don't allow non-zero metric based on a single character match.
		 */
		if (occurrences == 1 && length > 1) {
			return .0f;
		}

		return (float) occurrences / length;
	}

	/**
	 * @param out
	 */
	public void print(final PrintStream out) {
		for (final String word : this.dictionary) {
			out.println(word);
		}
	}

	/**
	 * @param in
	 */
	private void loadDictionary(final InputStream in) {
		try {
			try (final BufferedReader in2 = new BufferedReader(new InputStreamReader(in, "UTF-8"))) {
				String line;
				while ((line = in2.readLine()) != null) {
					/*
					 * Hunspell dictionaries have a header line
					 * consisting of a zero-width NBSP (0xFEFF in UTF-16BE)
					 * followed by the number of words in the dictionary.
					 */
					if (line.matches("\uFEFF?[0-9]+")) {
						continue;
					}

					final int i = line.indexOf('/');
					final String word = i == -1 ? line : line.substring(0, i);
					this.add(word);
				}
			}
		} catch (final IOException ioe) {
			/*
			 * Fatal.
			 */
			ioe.printStackTrace();
			exit(0);
		}
	}

	/**
	 * Loads a hunspell or ispell dictionary.
	 *
	 * @param name
	 */
	private void loadBundledDictionary(final String name) {
		this.loadDictionary(Dictionary.class.getResourceAsStream(name));
	}

	private void loadCustomDictionary() {
		try {
			this.loadDictionary(new FileInputStream(getProperty("user.home")
					+ separatorChar
					+ ".cvs2unicode"
					+ separatorChar
					+ "custom.dic"));
		} catch (final FileNotFoundException fnfe) {
			/*
			 * Ignore.
			 */
		}
	}
}
