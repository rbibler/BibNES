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
			doc.insertString(doc.getLength(), lineToWrite + "\n", null);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}