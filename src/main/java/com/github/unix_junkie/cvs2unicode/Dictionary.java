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
import java.io.UncheckedIOException;
import java.nio.charset.CharacterCodingException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.annotation.Nonnull;

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

	@Nonnull
	@SuppressWarnings("null")
	private static final Optional<DictionaryChangeListener> EMPTY = Optional.empty();


	private final Set<Hunspell> analyzers = new LinkedHashSet<>();

	private final Set<DecodedToken> userDictionary = new LinkedHashSet<>();

	/**
	 * The word cache which may be invalidated in case any item from
	 * {@link #userDictionary} has its encoding changed.
	 */
	private final SortedSet<String> wordCache = new TreeSet<>();

	@Nonnull
	private final Optional<DictionaryChangeListener> changeListener;

	public Dictionary() {
		this(EMPTY);
	}

	/**
	 * @param changeListener
	 */
	public Dictionary(final Optional<DictionaryChangeListener> changeListener) {
		/*
		 * Dictionaries are loaded *before* the listener is initialized.
		 */
		this.loadHunspellDictionary("ru_RU", "uk_UA");
		this.loadCustomDictionary();

		this.changeListener = changeListener;
	}

	/**
	 * @param decodedToken the word or line of text along with its most probable encoding
	 * @param file the file in which the {@code word} was seen first
	 * @param lineNumber the line number within the [@code file} of the word's
	 *                   first occurrence
	 * @throws CharacterCodingException
	 * @see DictionaryChangeListener#wordAdded
	 */
	public void add(final DecodedToken decodedToken, final File file, final int lineNumber)
	throws CharacterCodingException {
		if (this.changeListener.isPresent() && !this.contains(decodedToken)) {
			this.changeListener.get().wordAdded(decodedToken, file, lineNumber);
		}
		this.add(decodedToken);
	}

	/**
	 * @param decodedToken
	 * @throws CharacterCodingException
	 */
	private void add(final DecodedToken decodedToken) throws CharacterCodingException {
		this.userDictionary.add(decodedToken);
		this.wordCache.add(decodedToken.getDecodedData().toLowerCase());
	}

	/**
	 * @param word
	 */
	public boolean contains(final String word) {
		return this.analyzers.stream().anyMatch(hunspell -> hunspell.spell(word))
				|| this.wordCache.contains(word.toLowerCase());
	}

	/**
	 * @param decodedToken
	 * @throws CharacterCodingException
	 */
	private boolean contains(final DecodedToken decodedToken) throws CharacterCodingException {
		return this.contains(decodedToken.getDecodedData());
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
		@Nonnull
		@SuppressWarnings("null")
		final Optional<String> optionalLine = Optional.of(line);
		final List<String> words = splitIntoWords(optionalLine);

		/*
		 * Retain only non-ASCII words.
		 */
		final List<String> nonAsciiWords = new ArrayList<>();
		for (final String word : words) {
			if (word == null || isAscii(word)) {
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
	 * <p>Loads a hunspell, an ispell, or a simple word-per-line dictionary.
	 * The input stream is closed by this method.</p>
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

					/*
					 * Strip leading BOM in case file is a plain-text one.
					 */
					final String wordWithAffix = line.length() != 0 && line.charAt(0) == '\uFEFF' ? line.substring(1) : line;

					/*
					 * Strip hunspell affix information.
					 */
					final int i = wordWithAffix.indexOf('/');
					final String word = i == -1 ? wordWithAffix : wordWithAffix.substring(0, i);
					try {
						@Nonnull
						@SuppressWarnings("null")
						final String wordLowerCase = word.toLowerCase();
						this.add(new DecodedToken(wordLowerCase));
					} catch (final CharacterCodingException cce) {
						/*
						 * Never.
						 */
						cce.printStackTrace();
					}
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

	@SuppressWarnings("resource")
	private void loadCustomDictionary() {
		try {
			this.loadDictionary(new FileInputStream(getProperty("user.home")
					+ separatorChar
					+ ".cvs2unicode"
					+ separatorChar
					+ "custom.dic"));
		} catch (@SuppressWarnings("unused") final FileNotFoundException fnfe) {
			/*
			 * Ignore.
			 */
		}
	}
}
