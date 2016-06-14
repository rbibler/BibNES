package com.bibler.awesome.bibnes.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class AssemblerInputPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9084230274311465883L;
	
	private JTextArea inputArea;
	private JScrollPane scrollPane;
	
	public AssemblerInputPanel(int width, int height) {
		super();
		setPreferredSize(new Dimension(width, height));
		initialize(width, height);
	}
	
	private void initialize(int width, int height) {
		setLayout(new BorderLayout());
		inputArea = new JTextArea();
		scrollPane = new JScrollPane(inputArea);
		scrollPane.setPreferredSize(new Dimension(width, height));
		add(scrollPane, BorderLayout.CENTER);
	}
	
	public String[] getInputLines() {
		String s = inputArea.getText();
		return s.split("\n");
	}

}
