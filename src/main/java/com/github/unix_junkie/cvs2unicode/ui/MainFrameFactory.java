/*-
 * $Id$
 */
package com.github.unix_junkie.cvs2unicode.ui;

import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.EAST;
import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.RELATIVE;
import static java.awt.GridBagConstraints.REMAINDER;
import static java.lang.System.currentTimeMillis;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static javax.swing.BorderFactory.createBevelBorder;
import static javax.swing.Box.createHorizontalGlue;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import static javax.swing.JOptionPane.showMessageDialog;
import static javax.swing.SwingUtilities.invokeLater;
import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;
import static javax.swing.border.BevelBorder.LOWERED;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableModel;

/**
 * @author Andrew ``Bass'' Shcheglov &lt;mailto:andrewbass@gmail.com&gt;
 */
public final class MainFrameFactory {
	private static final WindowListener WINDOW_CLOSING_LISTENER = new WindowAdapter() {
		@Override
		public void windowClosing(final WindowEvent e) {
			final JFrame frame = (JFrame) e.getSource();
			showMessageDialog(frame, "A background operation is currently in progress. Please wait until it completes and try again.");
		}
	};

	private MainFrameFactory() {
		assert false;
	}

	/**
	 * @param cvsRoot
	 * @param tableModel
	 * @param backgroundWorker
	 * @param work
	 * @wbp.parser.entryPoint
	 */
	public static JFrame newInstance(final String cvsRoot,
			final TableModel tableModel,
			final ExecutorService backgroundWorker,
			final Callable<Void> work) {
		final JMenuBar menuBar = new JMenuBar();
		menuBar.add(new JMenu("File"));
		menuBar.add(new JMenu("Edit"));
		menuBar.add(createHorizontalGlue());
		menuBar.add(new JMenu("Help"));


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
		cvsRootTextField.setText(cvsRoot);
		cvsRootTextField.setToolTipText(cvsRootToolTipText);
		final GridBagConstraints cvsRootTextFieldConstraints = new GridBagConstraints();
		cvsRootTextFieldConstraints.gridwidth = REMAINDER;
		cvsRootTextFieldConstraints.weightx = 1.0;
		cvsRootTextFieldConstraints.fill = HORIZONTAL;
		cvsRootTextFieldConstraints.insets = new Insets(10, 0, 5, 10);
		contentPane.add(cvsRootTextField, cvsRootTextFieldConstraints);


		final JProgressBar progressBar = new JProgressBar();

		final JLabel statusBar = new JLabel();

		final JFrame mainFrame = new JFrame();

		final JButton startButton = new JButton();
		startButton.setText("Convert CVS Repository");
		startButton.addActionListener(e -> {
			startButton.setEnabled(false);
			progressBar.setIndeterminate(true);
			setMenuBarEnabled(menuBar, false);
			setExitOptionEnabled(mainFrame, false);
			backgroundWorker.submit(() -> {
				try {
					final long t0 = currentTimeMillis();
					statusBar.setText("Busy...");
					work.call();
					final long t1 = currentTimeMillis();
					statusBar.setText("Completed in " + MILLISECONDS.toSeconds(t1 - t0) + " second(s).");
				} catch (final InterruptedException ie) {
					/*
					 * Re-set the interrupted state
					 */
					Thread.currentThread().interrupt();
					statusBar.setText("Interrupted.");
				} catch (final Exception ex) {
					ex.printStackTrace();
					statusBar.setText(ex.getMessage());
				}
				invokeLater(() -> {
					startButton.setEnabled(true);
					progressBar.setIndeterminate(false);
					setMenuBarEnabled(menuBar, true);
					setExitOptionEnabled(mainFrame, true);
				});
			});
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


		final JTable table = new JTable();
		table.setModel(tableModel);

		final JScrollPane tableScrollPane = new JScrollPane();
		tableScrollPane.setViewportView(table);
		final GridBagConstraints tableConstraints = new GridBagConstraints();
		tableConstraints.gridwidth = REMAINDER;
		tableConstraints.gridheight = RELATIVE;
		tableConstraints.weightx = 1.0;
		tableConstraints.weighty = 1.0;
		tableConstraints.fill = BOTH;
		tableConstraints.insets = new Insets(0, 10, 5, 10);
		contentPane.add(tableScrollPane, tableConstraints);

		statusBar.setText("Ready.");
		statusBar.setBorder(createBevelBorder(LOWERED));
		final GridBagConstraints statusBarConstraints = new GridBagConstraints();
		statusBarConstraints.gridwidth = REMAINDER;
		statusBarConstraints.gridheight = REMAINDER;
		statusBarConstraints.weightx = 1.0;
		statusBarConstraints.fill = HORIZONTAL;
		statusBarConstraints.insets = new Insets(0, 10, 10, 10);
		contentPane.add(statusBar, statusBarConstraints);

		mainFrame.setTitle("cvs2unicode");
		mainFrame.getContentPane().setLayout(new BorderLayout());
		mainFrame.setPreferredSize(new Dimension(640, 900));
		mainFrame.setResizable(true);
		mainFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		mainFrame.setJMenuBar(menuBar);
		mainFrame.getContentPane().add(contentPane);

		return mainFrame;
	}

	static void setExitOptionEnabled(final JFrame frame, final boolean enabled) {
		if (enabled) {
			frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
			frame.removeWindowListener(WINDOW_CLOSING_LISTENER);
		} else {
			frame.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
			frame.addWindowListener(WINDOW_CLOSING_LISTENER);
		}
	}

	static void setMenuBarEnabled(final JMenuBar menuBar, final boolean enabled) {
		for (int i = 0, n = menuBar.getMenuCount(); i < n; i++) {
			final JMenu menu = menuBar.getMenu(i);
			if (menu != null) {
				menu.setEnabled(enabled);
			}
		}
	}
}
