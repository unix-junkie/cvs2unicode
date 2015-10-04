/*-
 * $Id$
 */
package com.github.unix_junkie.cvs2unicode.ui;

import static com.github.unix_junkie.cvs2unicode.Main.toFile;
import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.NORTHWEST;
import static java.awt.GridBagConstraints.RELATIVE;
import static java.awt.GridBagConstraints.REMAINDER;
import static java.awt.GridBagConstraints.WEST;
import static java.lang.System.getProperty;
import static java.lang.System.getenv;
import static javax.swing.BorderFactory.createBevelBorder;
import static javax.swing.border.BevelBorder.LOWERED;

import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.AbstractListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListModel;

import com.github.unix_junkie.cvs2unicode.editor.EditorProvider;
import com.github.unix_junkie.cvs2unicode.editor.VimFactory;

/**
 * A "message" component inserted into JOptionPane.
 *
 * @author Andrew ``Bass'' Shcheglov &lt;mailto:andrewbass@gmail.com&gt;
 */
final class Message extends JPanel {
	private static final long serialVersionUID = -8713182259370167107L;

	private static final String KEY_NONE = "none";

	private static final String KEY_ENCODINGS = "encodings";

	static final EditorProvider EDITOR = new VimFactory().newEditor();

	private static final String LOCAL_CVSROOT_PATH = getLocalCvsrootPath();


	/**
	 * Current file.
	 */
	File file;

	/**
	 * Current line.
	 */
	int line;

	private final JTextField fileTextField;

	private final JTextField lineTextField;

	private final JList<String> encodingsList = new JList<>();

	private final JPanel encodingsPanel = new JPanel();

	private final CardLayout encodingsPanelLayout = new CardLayout();

	Message() {
		/*
		 * Layout:
		 *
		 * +---------------+
		 * | 1 |     3     |
		 * |---------------|
		 * | 1 |   2   | 1 |
		 * |---------------|
		 * |   2   |   2   |
		 * +---------------+
		 */
		this.setLayout(new GridBagLayout());

		final JLabel fileLabel = new JLabel("File:");
		fileLabel.setDisplayedMnemonic('F');
		final GridBagConstraints fileLabelConstraints = new GridBagConstraints();
		// gridwidth can't be RELATIVE
		fileLabelConstraints.anchor = WEST;
		fileLabelConstraints.insets = new Insets(5, 5, 5, 5);
		this.add(fileLabel, fileLabelConstraints);

		this.fileTextField = new JTextField();
		fileLabel.setLabelFor(this.fileTextField);
		this.fileTextField.setEditable(false);
		final GridBagConstraints fileTextFieldConstraints = new GridBagConstraints();
		fileTextFieldConstraints.gridwidth = REMAINDER;
		fileTextFieldConstraints.weightx = 1.0;
		fileTextFieldConstraints.fill = HORIZONTAL;
		fileTextFieldConstraints.insets = new Insets(5, 0, 5, 5);
		this.add(this.fileTextField, fileTextFieldConstraints);

		final JLabel lineLabel = new JLabel("Line:");
		lineLabel.setDisplayedMnemonic('L');
		final GridBagConstraints lineLabelConstraints = new GridBagConstraints();
		lineLabelConstraints.gridheight = RELATIVE;
		lineLabelConstraints.anchor = WEST;
		lineLabelConstraints.insets = new Insets(0, 5, 10, 5);
		this.add(lineLabel, lineLabelConstraints);

		this.lineTextField = new JTextField();
		this.lineTextField.setEditable(false);
		lineLabel.setLabelFor(this.lineTextField);
		final GridBagConstraints lineTextFieldConstraints = new GridBagConstraints();
		lineTextFieldConstraints.gridwidth = 2; // Can't be RELATIVE
		lineTextFieldConstraints.gridheight = RELATIVE;
		lineTextFieldConstraints.weightx = 1.0;
		lineTextFieldConstraints.fill = HORIZONTAL;
		lineTextFieldConstraints.insets = new Insets(0, 0, 10, 5);
		this.add(this.lineTextField, lineTextFieldConstraints);

		final JButton lineButton = new JButton("...");
		lineButton.addActionListener(e -> {
			final File fileSnapshot = Message.this.file;
			if (fileSnapshot == null) {
				return;
			}

			try {
				EDITOR.edit(fileSnapshot, Message.this.line);
			} catch (final IOException ioe) {
				lineButton.setEnabled(false);
				lineButton.setToolTipText(ioe.getMessage());
			}
		});
		lineButton.setDefaultCapable(false);
		lineButton.setToolTipText("Go to line");
		final GridBagConstraints lineButtonConstraints = new GridBagConstraints();
		lineButtonConstraints.gridwidth = REMAINDER;
		lineButtonConstraints.gridheight = RELATIVE;
		lineButtonConstraints.insets = new Insets(0, 0, 10, 5);
		this.add(lineButton, lineButtonConstraints);

		final String encodingsToolTip = "Encoding tables (if any) previously used for this file";

		final JLabel encodingsLabel = new JLabel("Encoding(s) previously used:");
		encodingsLabel.setDisplayedMnemonic('E');
		encodingsLabel.setToolTipText(encodingsToolTip);
		encodingsLabel.setLabelFor(this.encodingsList);
		final GridBagConstraints encodingsLabelConstraints = new GridBagConstraints();
		encodingsLabelConstraints.gridwidth = 2; // Can't be RELATIVE
		encodingsLabelConstraints.gridheight = REMAINDER;
		encodingsLabelConstraints.anchor = NORTHWEST;
		encodingsLabelConstraints.insets = new Insets(0, 5, 15, 5);
		this.add(encodingsLabel, encodingsLabelConstraints);

		this.encodingsPanel.setLayout(this.encodingsPanelLayout);
		final GridBagConstraints encodingPanelConstraints = new GridBagConstraints();
		encodingPanelConstraints.gridwidth = REMAINDER;
		encodingPanelConstraints.gridheight = REMAINDER;
		encodingPanelConstraints.insets = new Insets(0, 0, 15, 5);
		encodingPanelConstraints.fill = BOTH;
		this.add(this.encodingsPanel, encodingPanelConstraints);

		final JLabel noneLabel = new JLabel("none");
		noneLabel.setToolTipText(encodingsToolTip);
		this.encodingsPanel.add(noneLabel, KEY_NONE);

		this.encodingsList.setBorder(createBevelBorder(LOWERED));
		this.encodingsList.setToolTipText(encodingsToolTip);
		this.encodingsPanel.add(this.encodingsList, KEY_ENCODINGS);
	}

	/**
	 * Should be invoked from AWT event queue.
	 *
	 * @param file
	 */
	void setFile(final File file) {
		this.file = file;

		final String rcsFileRelativePath = getRcsFileRelativePath(file);
		this.fileTextField.setText(rcsFileRelativePath.endsWith(",v") ? rcsFileRelativePath.substring(0, rcsFileRelativePath.length() - 2) : rcsFileRelativePath);
	}

	/**
	 * Should be invoked from AWT event queue.
	 *
	 * @param line
	 */
	void setLine(final int line) {
		this.line = line;
		this.lineTextField.setText(Integer.toString(line));
	}

	/**
	 * @param previouslyUsedEncodings
	 * @todo use a shared model instance
	 */
	void setPreviouslyUsedEncodings(final String ... previouslyUsedEncodings) {
		this.encodingsPanelLayout.show(this.encodingsPanel, previouslyUsedEncodings.length == 0 ? KEY_NONE : KEY_ENCODINGS);

		/*
		 * Despite BasicListUI.Handler#propertyChange(...) does essentially
		 * the same (except for the #updateLayoutStateNeeded property
		 * which is set to 1 on list mode change), merely revalidating
		 * and repainting the list isn't enough. If we want the list to
		 * resize, we need to change the model.
		 */
		this.encodingsList.setModel(new AbstractListModel<String>() {
			private static final long serialVersionUID = 1414472465303671659L;

			/**
			 * @see ListModel#getSize()
			 */
			@Override
			public int getSize() {
				return previouslyUsedEncodings.length;
			}

			/**
			 * @see ListModel#getElementAt(int)
			 */
			@Nullable
			@Override
			public String getElementAt(final int index) {
				return previouslyUsedEncodings[index];
			}
		});
	}

	private static String getLocalCvsrootPath() {
		try {
			@Nonnull
			@SuppressWarnings("null")
			final String localCvsRootPath = toFile(Optional.ofNullable(getenv("CVSROOT"))).getPath();
			return localCvsRootPath;
		} catch (final IOException ignored) {
			/*
			 * Ignore - we're checking the same at application startup.
			 */
			return "";
		}
	}

	/**
	 * @param file
	 */
	private static String getRcsFileRelativePath(final File file) {
		final String path = file.getPath();
		final String rcsFileRelativePath = path.startsWith(LOCAL_CVSROOT_PATH) ? path.substring(LOCAL_CVSROOT_PATH.length()) : path;
		@Nonnull
		@SuppressWarnings("null")
		final String normalizedRcsRelativePath = getProperty("os.name").startsWith("Windows") ? rcsFileRelativePath.replace('\\', '/') : rcsFileRelativePath;
		return normalizedRcsRelativePath;
	}
}
