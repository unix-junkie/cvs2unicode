/*-
 * $Id$
 */
package com.github.unix_junkie.cvs2unicode.ui;

import static javax.swing.JFrame.EXIT_ON_CLOSE;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListModel;

/**
 * @author Andrew ``Bass'' Shcheglov &lt;mailto:andrewbass@gmail.com&gt;
 */
public final class MainFrameFactory {
	private MainFrameFactory() {
		assert false;
	}

	/**
	 * @param listModel
	 * @wbp.parser.entryPoint
	 */
	public static JFrame newInstance(final ListModel<String> listModel) {
		final JList<String> list = new JList<>();
		list.setModel(listModel);

		final JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(list);

		final JFrame frmCvsunicode = new JFrame();
		frmCvsunicode.setTitle("cvs2unicode");
		frmCvsunicode.getContentPane().setLayout(new BorderLayout());
		frmCvsunicode.setPreferredSize(new Dimension(640, 900));
		frmCvsunicode.setResizable(true);
		frmCvsunicode.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frmCvsunicode.getContentPane().add(scrollPane);

		return frmCvsunicode;
	}
}
