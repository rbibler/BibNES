package com.bibler.awesome.bibnes.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

public class AssemblerInputPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9084230274311465883L;
	
	private JTextPane inputArea;
	private JScrollPane scrollPane;
	private EditorLeftPanel editorLeftPanel;
	private JPanel mainPanel;
	
	public AssemblerInputPanel(int width, int height) {
		super();
		setPreferredSize(new Dimension(width, height));
		initialize(width, height);
	}
	
	private void initialize(int width, int height) {
		setLayout(new BorderLayout());
		inputArea = new JTextPane();
	    editorLeftPanel = new EditorLeftPanel(inputArea, height);
		
		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(editorLeftPanel, BorderLayout.WEST);
		mainPanel.add(inputArea);
		scrollPane = new JScrollPane(mainPanel);
		scrollPane.setPreferredSize(new Dimension(width, height));
		scrollPane.setRowHeaderView(editorLeftPanel);
		add(scrollPane, BorderLayout.CENTER);
	}
	
	public String[] getInputLines() {
		String s = inputArea.getText();
		return s.split("\n");
	}
	
}
