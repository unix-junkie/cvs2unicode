cvs2unicode, an 8-bit to Unicode converter with automatic codepage detection for CVS
====================================================================================
[![Build Status](https://api.travis-ci.org/unix-junkie/cvs2unicode.png?branch=master)](https://travis-ci.org/unix-junkie/cvs2unicode)

Please follow [this link](http://unix-junkie.github.io/cvs2unicode/) for project documentation.

This project includes the [Russian ispell dictionary](ftp://scon155.phys.msu.su/pub/russian/ispell/rus-ispell.tar.gz) 
by [Alexander Lebedev](http://scon155.phys.msu.su/eng/lebedev.html).
The dictionary has been converted to UTF-8.

This project also contains the [Russian hunspell dictionary](http://code.google.com/p/hunspell-ru/)
available under LGPL 3.0. The original 20131101 version as well as the modified
20120501 [version from the Debian project](http://packages.debian.org/source/stable/hunspell-ru)
are included.

TODO:
* Rather than load hunspell dictionaries directly, integrate any of the Java APIs available:
    * [Java API for Hunspell, based on JNA](http://dren.dk/hunspell.html)
    * [HunspellBridJ](http://thomas-joiner.github.io/HunspellBridJ/1.0.0-SNAPSHOT/) which uses [BridJ](http://nativelibs4java.sourceforge.net/bridj/api/stable/)
    * [LanguageTool](https://languagetool.org/)
