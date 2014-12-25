cvs2unicode, an 8-bit to Unicode converter with automatic codepage detection for CVS
====================================================================================
[![Build Status](https://api.travis-ci.org/unix-junkie/cvs2unicode.png?branch=master)](https://travis-ci.org/unix-junkie/cvs2unicode)

Please follow [this link](http://unix-junkie.github.io/cvs2unicode/) for project documentation.

# Features

 * Support for `:local:` CVS protocol
 * Automatic detection of text/binary contents based on CVS [substitution modes](http://web.mit.edu/gnu/doc/html/cvs_17.html#SEC77). Only text fiels are converted.
 * Support for all standard Cyrillic charsets (`KOI8-R`, `windows-1251`, `ISO-8859-5`, `IBM866`). Files are read line-by-line, so a single versioned file can have differently encoded lines (which is usually the case if the file was re-encoded between revisions).
 * Support for double-encoded files (e.g: KOI-windows-KOI (KWK), Unicode-KOI-Unicode (UKU)). The idea is borrowed from [Andrzej Novosiolov](mailto:andrzej@ukrnet.net), who originally added this feature to the [FAR Manager](https://farmanager.googlecode.com/svn/tags/before_3.0_split/addons/Tables/Cyrillic/E-Mail%20Double%20Conversion/readme.txt). 
 * Automatic codepage detection if external dictionary (`aspell`/`ispell`/`myspell`/`hunspell`) is available.
 * Integration with [Hunspell](http://hunspell.sourceforge.net/) via [HunspellBridJ](https://github.com/thomas-joiner/HunspellBridJ) to spell different forms of the same word. Automatic codepage detection works best with *Hunspell* ([Russian](http://code.google.com/p/hunspell-ru/) and/or [Ukrainian](http://sourceforge.net/projects/ispell-uk/) dictionaries should be installed so that *Hunspell* can find them).
 * Support for interactive charset selection where existing dictionary is not enough (e.g. for misspelled words). New words are automatically added to the user dictionary.
 * [Vim](http://www.vim.org/) integration. If you're unsure which charset to pick, you can jump to exactly the same line with *Vim*, and examine the context.

# Running

```bash
mvn exec:java
```

# Screenshots

![Main window](http://unix-junkie.github.io/cvs2unicode/images/cvs2unicode-linux.png "Main window")
![Interactive disambiguation](http://unix-junkie.github.io/cvs2unicode/images/cvs2unicode.png "Interactive disambiguation")
