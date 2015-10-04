/*-
 * $Id$
 */
package com.github.unix_junkie.cvs2unicode;

import static com.github.unix_junkie.cvs2unicode.Main.readHeaderLine;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * @author Andrey ``Bass'' Shcheglov &lt;mailto:andrewbass@gmail.com&gt;
 */
@RunWith(JUnit4.class)
public final class MainTest {
	@Test
	@SuppressWarnings("static-method")
	public void testReadHeaderLine() throws IOException {
		final String rcsFile = "head    1.1;\n"
				+ "access;\n"
				+ "symbols;\n"
				+ "locks; strict;\n"
				+ "comment @# @;\n"
				+ "\n"
				+ "\n"
				+ "1.1\n"
				+ "date    2004.05.27.16.26.26;    author cvsadmin;        state Exp;\n"
				+ "branches;\n"
				+ "next    ;\n"
				+ "\n"
				+ "\n"
				+ "desc\n"
				+ "@@\n"
				+ "\n"
				+ "\n"
				+ "1.1\n"
				+ "log\n"
				+ "@conn_jdbc -> connjdbc\n"
				+ "@\n"
				+ "text\n"
				+ "@*.swp\n"
				+ "*.tar\n"
				+ "*.gz\n"
				+ "@\n";
		try (final BufferedReader rcsReader = new BufferedReader(new StringReader(rcsFile))) {
			assertEquals("head    1.1;", readHeaderLine(rcsReader).get());
			assertEquals("access;", readHeaderLine(rcsReader).get());
			assertEquals("symbols;", readHeaderLine(rcsReader).get());
			assertEquals("locks; strict;", readHeaderLine(rcsReader).get());
			assertEquals("comment @# @;", readHeaderLine(rcsReader).get());
			assertEquals("", readHeaderLine(rcsReader).get());
		}
	}

	@Test
	@SuppressWarnings("static-method")
	public void testReadHeaderLineExpand() throws IOException {
		final String rcsFile = "head\t1.1;\n"
				+ "access;\n"
				+ "symbols\n"
				+ "\tTAG2:1.1.0.2\n"
				+ "\tTAG1:1.1\n"
				+ "\tTAG0:1.1;\n"
				+ "locks; strict;\n"
				+ "comment	@# @;\n"
				+ "expand	@b@;\n"
				+ "\n"
				+ "\n"
				+ "1.1\n"
				+ "date	2004.04.28.08.02.37;	author cvsadmin;	state Exp;\n"
				+ "branches;\n"
				+ "next	;\n"
				+ "\n"
				+ "\n"
				+ "desc\n"
				+ "@@\n"
				+ "\n"
				+ "\n"
				+ "1.1\n"
				+ "log\n"
				+ "@start\n"
				+ "@\n"
				+ "text\n"
				+ "@@\n";
		try (final BufferedReader rcsReader = new BufferedReader(new StringReader(rcsFile))) {
			assertEquals("head\t1.1;", readHeaderLine(rcsReader).get());
			assertEquals("access;", readHeaderLine(rcsReader).get());
			assertEquals("symbols\n"
					+ "\tTAG2:1.1.0.2\n"
					+ "\tTAG1:1.1\n"
					+ "\tTAG0:1.1;", readHeaderLine(rcsReader).get());
			assertEquals("locks; strict;", readHeaderLine(rcsReader).get());
			assertEquals("comment	@# @;", readHeaderLine(rcsReader).get());
			assertEquals("expand	@b@;", readHeaderLine(rcsReader).get());
			assertEquals("", readHeaderLine(rcsReader).get());
		}
	}

	@Test
	@SuppressWarnings("static-method")
	public void testReadHeaderLineTruncatedInput() throws IOException {
		final String rcsFile = "head    1.1;\n"
				+ "access;\n"
				+ "symbols;\n"
				+ "locks; strict;\n"
				+ "comment @# @;\n";
		try (final BufferedReader rcsReader = new BufferedReader(new StringReader(rcsFile))) {
			assertEquals("head    1.1;", readHeaderLine(rcsReader).get());
			assertEquals("access;", readHeaderLine(rcsReader).get());
			assertEquals("symbols;", readHeaderLine(rcsReader).get());
			assertEquals("locks; strict;", readHeaderLine(rcsReader).get());
			assertEquals("comment @# @;", readHeaderLine(rcsReader).get());
			assertFalse(readHeaderLine(rcsReader).isPresent());
		}
	}
}
