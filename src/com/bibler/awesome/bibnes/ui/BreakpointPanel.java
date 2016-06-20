package com.bibler.awesome.bibnes.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;

import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.Utilities;

public class BreakpointPanel extends EditorLineWatcher {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1924689578563818796L;
	private ArrayList<Integer> breakpointLines = new ArrayList<Integer>();

	public BreakpointPanel(JTextComponent component) {
		super(component);
		// TODO Auto-generated constructor stub
	}
	
	public void updateBreakpoints(int yOffset) {
		int line = 0;
		try {
			line = getLineNumber(Utilities.getRowStart(component, component.viewToModel(new Point(0, yOffset))));
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(!breakpointLines.contains(line)) {
			breakpointLines.add(line);
		} else {
			breakpointLines.remove(breakpointLines.indexOf(line));
		}
		repaint();
	}
	
	public Integer[] getBreakPoints() {
		Integer[] returnArray = new Integer[breakpointLines.size()];
		return breakpointLines.toArray(returnArray);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.BLUE);
		int currentLine;
		int y;
		for(int i = 0; i < breakpointLines.size(); i++) {
			currentLine = breakpointLines.get(i);
			y = getLineYOffset(currentLine, component.getFontMetrics(component.getFont()));
			g.fillOval(5, y - 1, 8, 8);
		}
	}

}
