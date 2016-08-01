package com.bibler.awesome.bibnes.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import com.bibler.awesome.bibnes.assembler.BreakpointManager;

public class AssemblerInputPanel extends PopoutPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9084230274311465883L;
	
	private JTextPane inputArea;
	private JScrollPane scrollPane;
	private EditorLeftPanel editorLeftPanel;
	private JPanel mainPanel;
	private LookAndFeel currentLookAndFeel;
	
	public AssemblerInputPanel(String title, int tabIndex, int width, int height, BreakpointManager bpManager) {
		super(title, tabIndex, width, height);
		initialize(width, height, bpManager);
	}
	
	public void applyLookAndFeel(LookAndFeel lookAndFeel) {
		this.currentLookAndFeel = lookAndFeel;
		inputArea.setBackground(currentLookAndFeel.getBackgroundColor());
		inputArea.setFont(currentLookAndFeel.getCurrentFont());
	}
	
	private void initialize(int width, int height, BreakpointManager bpManager) {
		setLayout(new BorderLayout());
		inputArea = new JTextPane();
	    editorLeftPanel = new EditorLeftPanel(inputArea, height, bpManager);
		
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
