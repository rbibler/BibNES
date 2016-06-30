package com.bibler.awesome.bibnes.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

public class MessageBox extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2694446203282724161L;
	
	private JScrollPane scrollPane;
	private JTextPane messageArea;
	private StyleContext styleContext;
	private AttributeSet attributeSet;
	private StyledDocument doc;
	
	public MessageBox() {
		super();
		initialize();
	}
	
	private void initialize() {
		styleContext = StyleContext.getDefaultStyleContext();
		setLayout(new BorderLayout());
		messageArea = new JTextPane();
		doc = messageArea.getStyledDocument();
		scrollPane = new JScrollPane(messageArea);
		add(scrollPane, BorderLayout.CENTER);
		Font f = new Font("Courier", Font.PLAIN, 13);
		messageArea.setFont(f);
		setTextColor(Color.BLACK);
	}
	
	public void deleteAll() {
		messageArea.setText("");
	}
	
	public void setTextColor(Color textColor) {
		attributeSet = styleContext.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, textColor);
		messageArea.setCharacterAttributes(attributeSet, false);
	}
	
	public void writeNewLineToBox(String lineToWrite) {
		try {
			doc.insertString(doc.getLength(), lineToWrite + "\n", attributeSet);
		} catch (BadLocationException e) {}
	}
	
	public void writeNewStringToBox(String stringToWrite, int offset, boolean replace) {
		int stringLength = stringToWrite.length();
		if(stringLength == 0) {
			stringLength = 1;
		}
		int docLength = doc.getLength();
		if(replace && offset < docLength) {
			try {
				doc.remove(offset, stringLength);
			} catch (BadLocationException e) { e.printStackTrace(); }
		} 
		try {
			doc.insertString(offset, stringToWrite, attributeSet);
		} catch (BadLocationException e) {}
	}
	
	public int getCaretPos() {
		return messageArea.getCaretPosition();
	}
	
	public int getLength() {
		return doc.getLength();
	}

}
