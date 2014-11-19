/*-
 * $Id$
 */
package com.github.unix_junkie.cvs2unicode.ui;

import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.RELATIVE;
import static java.awt.GridBagConstraints.REMAINDER;
import static javax.swing.JFrame.EXIT_ON_CLOSE;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
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
		final JPanel contentPane = new JPanel(new GridBagLayout());


		final JProgressBar progressBar = new JProgressBar();
		final GridBagConstraints progressBarConstraints = new GridBagConstraints();
		progressBarConstraints.gridwidth = REMAINDER;
		progressBarConstraints.weightx = 1.0;
		progressBarConstraints.fill = HORIZONTAL;
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
		contentPane.add(tableScrollPane, tableConstraints);

		final JFrame frmCvsunicode = new JFrame();
		frmCvsunicode.setTitle("cvs2unicode");
		frmCvsunicode.getContentPane().setLayout(new BorderLayout());
		frmCvsunicode.setPreferredSize(new Dimension(640, 900));
		frmCvsunicode.setResizable(true);
		frmCvsunicode.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frmCvsunicode.getContentPane().add(contentPane);

		return frmCvsunicode;
	}
}
