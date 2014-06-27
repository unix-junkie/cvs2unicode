/*-
 * $Id$
 */
package com.github.unix_junkie.cvs2unicode;

import static com.github.unix_junkie.cvs2unicode.Utilities.isAscii;
import static com.github.unix_junkie.cvs2unicode.Utilities.splitIntoWords;
import static java.io.File.separatorChar;
import static java.lang.System.exit;
import static java.lang.System.getProperty;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toCollection;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.atlascopco.hunspell.Hunspell;

/**
 * @author Andrew ``Bass'' Shcheglov &lt;mailto:andrewbass@gmail.com&gt;
 */
public final class Dictionary {
	private static final String POSIX_SEARCH_PATHS[] = {
		"/usr/share/hunspell",
		"/usr/local/share/hunspell",
		getProperty("user.dir"),
	};

	private static final String DARWIN_SEARCH_PATHS[] = {
		"/System/Library/Spelling",
		"/Library/Spelling",
		getProperty("user.home") + File.separatorChar + "Spelling",
		"/opt/local/share/hunspell",
		"/sw/share/hunspell",
	};

	private static final String DICTIONARY_SUFFIX = ".dic";

	private static final String AFFIX_SUFFIX = ".aff";


	private final Set<Hunspell> analyzers = new LinkedHashSet<>();

	private final SortedSet<String> userDictionary = new TreeSet<>();

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
		this.loadHunspellDictionary("ru_RU", "uk_UA");
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
		this.userDictionary.add(word.toLowerCase());
	}

	/**
	 * @param word
	 */
	public boolean contains(final String word) {
		return this.analyzers.stream().anyMatch(hunspell -> hunspell.spell(word))
				|| this.userDictionary.contains(word.toLowerCase());
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
		for (final String word : this.userDictionary) {
			out.println(word);
		}
	}

	/**
	 * @param locales the list of locales in "xx_XX" format (i. e. "ru_RU", "uk_UK" etc.).
	 */
	private void loadHunspellDictionary(final String ... locales) {
		final Set<String> basenames = new LinkedHashSet<>(asList(locales));

		final Set<String> searchPaths = new LinkedHashSet<>();
		stream(POSIX_SEARCH_PATHS).map(File::new).filter(File::exists).map(file -> {
			try {
				return file.getCanonicalPath();
			} catch (final IOException ioe) {
				throw new UncheckedIOException(ioe);
			}
		}).collect(toCollection(() -> searchPaths));

		if (getProperty("os.name").equals("Mac OS X")) {
			stream(DARWIN_SEARCH_PATHS).map(File::new).filter(File::exists).map(file -> {
				try {
					return file.getCanonicalPath();
				} catch (final IOException ioe) {
					throw new UncheckedIOException(ioe);
				}
			}).collect(toCollection(() -> searchPaths));
		}


		final Set<String> dictionaries = new LinkedHashSet<>();

		basenames.forEach(basename -> {
			searchPaths.stream().map(searchPath -> new File(searchPath, basename + DICTIONARY_SUFFIX)).forEach(dictionary -> {
				try {
					final File affix = new File(dictionary.getCanonicalFile().getParent(), basename + AFFIX_SUFFIX);
					if (dictionary.exists() && affix.exists()) {
						final String dictionaryPath = dictionary.getCanonicalPath();
						dictionaries.add(dictionaryPath.substring(0, dictionaryPath.length() - 4));
					}
				} catch (final IOException ioe) {
					throw new UncheckedIOException(ioe);
				}
			});
		});

		dictionaries.stream().map(dictionary -> new Hunspell(dictionary + DICTIONARY_SUFFIX, dictionary + AFFIX_SUFFIX)).collect(toCollection(() -> this.analyzers));
	}

	/**
	 * Loads a hunspell, an ispell, or a simple word-per-line dictionary.
	 *
	 * @param in the input stream used to load the dictionary from.
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
