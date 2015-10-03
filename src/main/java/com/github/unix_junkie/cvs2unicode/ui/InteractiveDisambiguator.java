/*-
 * $Id$
 */
package com.github.unix_junkie.cvs2unicode.ui;

import static javax.swing.JOptionPane.QUESTION_MESSAGE;
import static javax.swing.JOptionPane.showInputDialog;
import static javax.swing.SwingUtilities.invokeAndWait;
import static javax.swing.SwingUtilities.invokeLater;

import java.awt.Component;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.CharacterCodingException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.github.unix_junkie.cvs2unicode.CharsetDecoder;
import com.github.unix_junkie.cvs2unicode.DecodedToken;
import com.github.unix_junkie.cvs2unicode.Disambiguator;

/**
 * @author Andrew ``Bass'' Shcheglov &lt;mailto:andrewbass@gmail.com&gt;
 */
public final class InteractiveDisambiguator implements Disambiguator {
	private final CharsetDecoder decoders[];

	@Nullable
	private volatile Component parent;

	private final Message message = new Message();

	/**
	 * @param decoders
	 */
	public InteractiveDisambiguator(final CharsetDecoder decoders[]) {
		this.decoders = decoders.clone();
	}

	boolean flag = true;

	/**
	 * @see Disambiguator#decode(byte[], File, int)
	 */
	@Nonnull
	@Override
	public DecodedToken decode(final byte data[], final File file, final int lineNumber) {
		try {
			final List<Option> options = new ArrayList<>();
			for (final CharsetDecoder decoder : this.decoders) {
				options.add(new Option(data, decoder));
			}
			invokeLater(() -> {
				this.message.setFile(file);
				this.message.setLine(lineNumber);
				this.message.setPreviouslyUsedEncodings(this.flag ? new String[]{} : new String[]{"KOI8-R", "IBM866", "CP1251"}); // TBD
				this.flag = !this.flag;
			});
			DecodedToken decodedToken;
			do {
				final Option option[] = new Option[1];
				invokeAndWait(() -> {
					option[0] = (Option) showInputDialog(this.parent,
							this.message,
							"Select an Option",
							QUESTION_MESSAGE,
							null,
							options.toArray(),
							null);
				});
				decodedToken = option[0];
			} while (decodedToken == null);
			return decodedToken;
		} catch (final InterruptedException ie) {
			/*
			 * Never.
			 */
			Thread.currentThread().interrupt();
			throw new RuntimeException(ie);
		} catch (final InvocationTargetException ite) {
			/*
			 * Never.
			 */
			final Throwable cause = ite.getCause();
			if (cause instanceof RuntimeException) {
				throw (RuntimeException) cause;
			} else if (cause instanceof Error) {
				throw (Error) cause;
			}
			throw new RuntimeException(cause);
		}
	}

	/**
	 * @param parent
	 */
	public void setParent(@Nullable final Component parent) {
		this.parent = parent;
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
	private static final class Option extends DecodedToken {
		/**
		 * @param data
		 * @param decoder
		 */
		Option(final byte data[], final CharsetDecoder decoder) {
			super(data, decoder);
		}

		private String toString(final String decodedData) {
			return "<html><font face = \"Courier New\">" +
					"<font color = \"#008000\">" + this.getDecoder().charset() + "</font>" +
					": " +
					"<font color = \"#000080\">" + escapeHtml(decodedData) + "</font>" +
					"</font></html>";
		}

		/**
		 * @see Object#toString()
		 */
		@Override
		public String toString() {
			try {
				return this.toString(this.getDecodedData());
			} catch (final CharacterCodingException cce) {
				/*
				 * Never.
				 */
				cce.printStackTrace();
				return this.toString(cce.getMessage());
			}
		}
	}
}
