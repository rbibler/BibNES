package com.bibler.awesome.bibnes.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet.CharacterAttribute;
import javax.swing.text.BadLocationException;
import javax.swing.text.Utilities;

public class AssemblerInputPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9084230274311465883L;
	
	private JTextPane inputArea;
	private JScrollPane scrollPane;
	private JPanel breakPointPanel;
	private JTextPane lineNumberPane;
	private JPanel mainPanel;
	private TextLine textLinePanel;
	
	private int lastRowStart;
	private int currentLineNumber;
	
	public AssemblerInputPanel(int width, int height) {
		super();
		setPreferredSize(new Dimension(width, height));
		initialize(width, height);
	}
	
	private void initialize(int width, int height) {
		setLayout(new BorderLayout());
		inputArea = new JTextPane();
		breakPointPanel = new JPanel();
		breakPointPanel.setMaximumSize(new Dimension(20, 8000));
		breakPointPanel.setPreferredSize(new Dimension(20, height-5));
		breakPointPanel.setMinimumSize(new Dimension(20, 20));
		breakPointPanel.setBackground(Color.GRAY);
	
		textLinePanel = new TextLine(inputArea);
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BorderLayout());
		leftPanel.add(breakPointPanel, BorderLayout.WEST);
		leftPanel.add(textLinePanel);
		
		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		//mainPanel.add(breakPointPanel, BorderLayout.WEST);
		//mainPanel.add(textLinePanel);
		mainPanel.add(leftPanel, BorderLayout.WEST);
		mainPanel.add(inputArea);
		scrollPane = new JScrollPane(mainPanel);
		scrollPane.setPreferredSize(new Dimension(width, height));
		scrollPane.setRowHeaderView(leftPanel);
		add(scrollPane, BorderLayout.CENTER);
	}
	
	public String[] getInputLines() {
		String s = inputArea.getText();
		return s.split("\n");
	}
	
	public void addLineNumber() {
		try {
			Font f = inputArea.getFont();
			lineNumberPane.setFont(f);
			lineNumberPane.getDocument().insertString(lineNumberPane.getDocument().getLength(), "" + ++currentLineNumber + "\n", null);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void removeLineNumber() {
		try {
			int length = lineNumberPane.getDocument().getLength();
			int start = Utilities.getRowStart(lineNumberPane, length  -1);
			lineNumberPane.getDocument().remove(start, length - start);
			currentLineNumber--;
		} catch(BadLocationException e) {}
	}
	
	private void updateLineCount() {
		int newLineCount = getLineCount();
		System.out.println("new line count: " + newLineCount);
		System.out.println("Caret Pos: " + inputArea.getCaretPosition() + " length: " + inputArea.getDocument().getLength());
		int diff = newLineCount - currentLineNumber;
		for(int i = Math.abs(diff); i > 0; i--) {
			if(diff > 0) {
				addLineNumber();
			} else {
				removeLineNumber();
			}
		}
		
	}
	
	private int getLineCount() {
		int line = getInputLines().length; 
		return line;
	}
	
	private void checkCaretPos() {
		try {
			int rowStart = Utilities.getRowStart(inputArea, inputArea.getCaretPosition());
			if(rowStart != lastRowStart) {
				
			}
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private class InputAreaListener implements DocumentListener {

		@Override
		public void changedUpdate(DocumentEvent arg0) {}

		@Override
		public void insertUpdate(DocumentEvent arg0) {
			updateLineCount();
			
		}

		@Override
		public void removeUpdate(DocumentEvent arg0) {
			updateLineCount();
			
		}
		
	}
}
