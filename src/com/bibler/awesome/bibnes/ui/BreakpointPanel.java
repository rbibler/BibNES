package com.bibler.awesome.bibnes.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;

import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.Utilities;

import com.bibler.awesome.bibnes.assembler.BreakpointManager;

public class BreakpointPanel extends EditorLineWatcher {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1924689578563818796L;
	private ArrayList<Integer> breakpointLines = new ArrayList<Integer>();
	private BreakpointManager bpManager;

	public BreakpointPanel(JTextComponent component) {
		super(component);
		// TODO Auto-generated constructor stub
	}
	
	public void setBreakpointManager(BreakpointManager bpManager) {
		this.bpManager = bpManager;
	}
	
	public void updateBreakpoints(int yOffset) {
		int lineNum = 0;
		String line = "";
		int rowStart = 0;
		int rowEnd = 0;
		try {
			rowStart = Utilities.getRowStart(component, component.viewToModel(new Point(0, yOffset)));
			rowEnd = Utilities.getRowEnd(component, component.viewToModel(new Point(0, yOffset)));
			line = component.getText(rowStart, rowEnd - rowStart);
			lineNum = getLineNumber(rowStart);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		if(!breakpointLines.contains(lineNum)) {
			if(bpManager.verifyLineAndAdd(line, lineNum)) {
				breakpointLines.add(lineNum);
			}
		} else {
			breakpointLines.remove(breakpointLines.indexOf(lineNum));
			bpManager.removeBreakpoint(lineNum);
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
