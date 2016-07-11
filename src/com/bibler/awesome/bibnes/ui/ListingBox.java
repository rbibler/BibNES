package com.bibler.awesome.bibnes.ui;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.Utilities;

import com.bibler.awesome.bibnes.assembler.BreakpointManager;

public class ListingBox extends MessageBox {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6705905323773937260L;
	private DefaultHighlighter.DefaultHighlightPainter highlightPainter = 
		        new DefaultHighlighter.DefaultHighlightPainter(Color.CYAN);
	
	private ArrayList<Integer> breakpoints = new ArrayList<Integer>();
	private ArrayList<Object> highlights = new ArrayList<Object>();
	private BreakpointManager manager;
	
	public ListingBox() {
		super();
		messageArea.addMouseListener(new ListingClickListener());
	}
	
	public void setBreakpointManager(BreakpointManager manager) {
		this.manager = manager;
	}
	
	private void highlightLine() {
		int start = -1;
		int end = -1;
		try {
			start = Utilities.getRowStart(messageArea, messageArea.getCaretPosition());	
			end = Utilities.getRowEnd(messageArea, start);
		} catch (BadLocationException e) {}
		if(!breakpoints.contains(start)) {
			try {
				highlights.add(messageArea.getHighlighter().addHighlight(start, end, highlightPainter));
				breakpoints.add(start);
				final int breakpoint = Integer.parseInt(messageArea.getText(start, 4), 16);
				manager.addBreakPoint(breakpoint);
			} catch(BadLocationException e) {}
		} else {
			int index = breakpoints.indexOf(start);
			breakpoints.remove(index);
			messageArea.getHighlighter().removeHighlight(highlights.get(index));
			highlights.remove(index);
		}
		
	}
	
	private class ListingClickListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent arg0) {
			if(arg0.getClickCount() == 2) {
				highlightLine();
			}
			
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {}

		@Override
		public void mouseExited(MouseEvent arg0) {}

		@Override
		public void mousePressed(MouseEvent arg0) {}

		@Override
		public void mouseReleased(MouseEvent arg0) {}
		
	}

}
