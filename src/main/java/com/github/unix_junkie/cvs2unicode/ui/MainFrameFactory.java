/*-
 * $Id$
 */
package com.github.unix_junkie.cvs2unicode.ui;

import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.EAST;
import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.RELATIVE;
import static java.awt.GridBagConstraints.REMAINDER;
import static javax.swing.Box.createHorizontalGlue;
import static javax.swing.JFrame.EXIT_ON_CLOSE;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.table.TableModel;

/**
 * @author Andrew ``Bass'' Shcheglov &lt;mailto:andrewbass@gmail.com&gt;
 */
public final class MainFrameFactory {
	private MainFrameFactory() {
		assert false;
	}

	/**
	 * @param listModel
	 * @param tableModel
	 * @wbp.parser.entryPoint
	 */
	public static JFrame newInstance(final ListModel<String> listModel, final TableModel tableModel) {
		final JMenuBar menubar = new JMenuBar();
		menubar.add(new JMenu("File"));
		menubar.add(new JMenu("Edit"));
		menubar.add(createHorizontalGlue());
		menubar.add(new JMenu("Help"));


		final JPanel contentPane = new JPanel(new GridBagLayout());


		final String cvsRootToolTipText = "The CVSROOT environment variable";

		final JTextField cvsRootTextField = new JTextField();

		final JLabel cvsRootLabel = new JLabel();
		cvsRootLabel.setText("CVSROOT:");
		cvsRootLabel.setToolTipText(cvsRootToolTipText);
		cvsRootLabel.setLabelFor(cvsRootTextField);
		final GridBagConstraints cvsRootLabelConstraints = new GridBagConstraints();
		cvsRootLabelConstraints.anchor = EAST;
		cvsRootLabelConstraints.gridwidth = RELATIVE;
		cvsRootLabelConstraints.insets = new Insets(10, 10, 5, 5);
		contentPane.add(cvsRootLabel, cvsRootLabelConstraints);

		cvsRootTextField.setEditable(false);
		cvsRootTextField.setToolTipText(cvsRootToolTipText);
		final GridBagConstraints cvsRootTextFieldConstraints = new GridBagConstraints();
		cvsRootTextFieldConstraints.gridwidth = REMAINDER;
		cvsRootTextFieldConstraints.weightx = 1.0;
		cvsRootTextFieldConstraints.fill = HORIZONTAL;
		cvsRootTextFieldConstraints.insets = new Insets(10, 0, 5, 10);
		contentPane.add(cvsRootTextField, cvsRootTextFieldConstraints);


		final JProgressBar progressBar = new JProgressBar();

		final JButton startButton = new JButton();
		startButton.setText("Convert CVS Repository");
		startButton.addActionListener(e -> {
			startButton.setEnabled(false);
			progressBar.setIndeterminate(true);
		});
		final GridBagConstraints startButtonConstraints = new GridBagConstraints();
		startButtonConstraints.gridwidth = RELATIVE;
		startButtonConstraints.insets = new Insets(0, 10, 5, 5);
		contentPane.add(startButton, startButtonConstraints);

		final GridBagConstraints progressBarConstraints = new GridBagConstraints();
		progressBarConstraints.gridwidth = REMAINDER;
		progressBarConstraints.weightx = 1.0;
		progressBarConstraints.fill = HORIZONTAL;
		progressBarConstraints.insets = new Insets(0, 0, 5, 10);
		contentPane.add(progressBar, progressBarConstraints);


		final JList<String> list = new JList<>();
		list.setModel(listModel);

		final JScrollPane listScrollPane = new JScrollPane();
		listScrollPane.setViewportView(list);
		final GridBagConstraints listConstraints = new GridBagConstraints();
		listConstraints.gridwidth = REMAINDER;
		listConstraints.gridheight = RELATIVE;
		listConstraints.weightx = 1.0;
		listConstraints.weighty = 1.0;
		listConstraints.fill = BOTH;
		listConstraints.insets = new Insets(0, 10, 5, 10);
		contentPane.add(listScrollPane, listConstraints);


		final JTable table = new JTable();
		table.setModel(tableModel);

		final JScrollPane tableScrollPane = new JScrollPane();
		tableScrollPane.setViewportView(table);
		final GridBagConstraints tableConstraints = new GridBagConstraints();
		tableConstraints.gridwidth = REMAINDER;
		tableConstraints.gridheight = REMAINDER;
		tableConstraints.weightx = 1.0;
		tableConstraints.weighty = 1.0;
		tableConstraints.fill = BOTH;
		tableConstraints.insets = new Insets(0, 10, 10, 10);
		contentPane.add(tableScrollPane, tableConstraints);

		final JFrame mainFrame = new JFrame();
		mainFrame.setTitle("cvs2unicode");
		mainFrame.getContentPane().setLayout(new BorderLayout());
		mainFrame.setPreferredSize(new Dimension(640, 900));
		mainFrame.setResizable(true);
		mainFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		mainFrame.setJMenuBar(menubar);
		mainFrame.getContentPane().add(contentPane);

		return mainFrame;
	}
}
