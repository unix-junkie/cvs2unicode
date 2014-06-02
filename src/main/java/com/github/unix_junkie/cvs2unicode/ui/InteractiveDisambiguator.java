/*-
 * $Id$
 */
package com.github.unix_junkie.cvs2unicode.ui;

import static javax.swing.JOptionPane.QUESTION_MESSAGE;
import static javax.swing.JOptionPane.showInputDialog;

import java.awt.Component;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTextField;

import com.github.unix_junkie.cvs2unicode.CharsetDecoder;
import com.github.unix_junkie.cvs2unicode.Disambiguator;

/**
 * @author Andrew ``Bass'' Shcheglov &lt;mailto:andrewbass@gmail.com&gt;
 */
public final class InteractiveDisambiguator implements Disambiguator {
	private final CharsetDecoder decoders[];

	private final Component parent;

	private final JTextField message = new JTextField();

	{
		this.message.setEditable(false);
	}

	/**
	 * @param decoders
	 * @param parent
	 */
	public InteractiveDisambiguator(final CharsetDecoder decoders[],
			final Component parent) {
		this.decoders = decoders.clone();
		this.parent = parent;
	}

	/**
	 * @see Disambiguator#decode(byte[], File)
	 */
	@Override
	public String decode(final byte data[], final File file) {
		try {
			final List<Option> options = new ArrayList<>();
			for (final CharsetDecoder decoder : this.decoders) {
				final String decodedData = decoder.decode(data);
				options.add(new Option(decoder, decodedData));
			}
			this.message.setText("File: " + file.getPath() + ':');
			String line;
			do {
				final Option option = (Option) showInputDialog(this.parent,
						this.message,
						"Select an Option",
						QUESTION_MESSAGE,
						null,
						options.toArray(),
						null);
				line = option == null ? null : option.getText();
			} while (line == null);
			return line;
		} catch (final UnsupportedEncodingException uee) {
			/*
			 * Never.
			 */
			uee.printStackTrace();
			return null;
		}
	}

	/**
	 * @param text
	 */
	static String escapeHtml(final String text) {
		/*
		 * &apos; is not supported by Swing widgets
		 * (which are HTML 3.2 compliant),
		 * so it doesn't need to be escaped here.
		 */
		return text.replace("&", "&amp;")
				.replace("<", "&lt;")
				.replace(">", "&gt;")
				.replace("\"", "&quot;");
	}

	/**
	 * @author Andrew ``Bass'' Shcheglov &lt;mailto:andrewbass@gmail.com&gt;
	 */
	private static final class Option {
		private final CharsetDecoder decoder;

		private final String text;

		/**
		 * @param decoder
		 * @param text
		 */
		Option(final CharsetDecoder decoder, final String text) {
			this.decoder = decoder;
			this.text = text;
		}

		String getText() {
			return this.text;
		}

		/**
		 * @see Object#toString()
		 */
		@Override
		public String toString() {
			return "<html><font face = \"Courier New\">" +
					"<font color = \"#008000\">" + this.decoder.charset() + "</font>" +
					": " +
					"<font color = \"#000080\">" + escapeHtml(this.getText()) + "</font>" +
					"</font></html>";
		}
	}
}
