package com.bibler.awesome.bibnes.ui;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;
import javax.swing.text.Highlighter;
import javax.swing.text.Utilities;

import com.bibler.awesome.bibnes.assembler.BreakpointManager;

public class ListingBox extends MessageBox {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6705905323773937260L;
	private HashMap<Integer, DefaultHighlightPainter> painters = new HashMap<Integer, DefaultHighlightPainter>();
	
	private ArrayList<Integer> breakpoints = new ArrayList<Integer>();
	private ArrayList<Object> highlights = new ArrayList<Object>();
	private HashMap<Integer, Integer> pcToLines = new HashMap<Integer, Integer>();
	private BreakpointManager manager;
	
	private Object lastHighlight;
	private boolean debug = false;
	
	public ListingBox() {
		super();
		messageArea.addMouseListener(new ListingClickListener());
		messageArea.setEditable(false);
		messageArea.setSelectionColor(new Color(0, 0, 0, 0));
		fillPainterMap();
	}
	
	private void fillPainterMap() {
		Field[] colorFields = Color.class.getDeclaredFields();

		for (Field cf : colorFields) {
            int modifiers = cf.getModifiers();
            if (!Modifier.isPublic(modifiers)) continue;

            Color c = null;
			try {
				c = (Color)cf.get(null);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            if (!painters.containsKey(c))
                painters.put(c.getRGB(), new DefaultHighlightPainter(c));
        }
		
	}
	
	public void displayListing(String listing) {
		String[] lines = listing.split("\n");
		String line;
		int offset = 0;
		for(int i = 0; i < lines.length; i++) {
			line = lines[i];
			pcToLines.put(Integer.parseInt(line.substring(0,4), 16), offset);
			offset += line.length() + 1;
			writeNewLineToBox(line);
		}
	}
	
	public void updateCurrentLine(int pc) {
		if(!debug) {
			return;
		}
		if(pcToLines.containsKey(pc)) {
			int offset = pcToLines.get(pc);
			messageArea.setCaretPosition(offset);
			highlightLine(Color.RED);
		}
	}
	
	
	public void setBreakpointManager(BreakpointManager manager) {
		this.manager = manager;
	}
	
	private void highlightLine(Color color) {
		if(lastHighlight != null) {
			messageArea.getHighlighter().removeHighlight(lastHighlight);
		}
		int start = -1;
		int end = -1;
		try {
			start = Utilities.getRowStart(messageArea, messageArea.getCaretPosition());	
			end = Utilities.getRowEnd(messageArea, start);
			lastHighlight = messageArea.getHighlighter().addHighlight(start, end, painters.get(color.getRGB()));
		} catch (BadLocationException e) {}
		
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
				highlights.add(messageArea.getHighlighter().addHighlight(start, end, painters.get(Color.BLUE.getRGB())));
				breakpoints.add(start);
				final int breakpoint = Integer.parseInt(messageArea.getText(start, 4), 16);
				manager.addBreakPoint(breakpoint);
			} catch(BadLocationException e) {}
		} else {
			int index = breakpoints.indexOf(start);
			breakpoints.remove(index);
			messageArea.getHighlighter().removeHighlight(highlights.get(index));
			highlights.remove(index);
			int breakpoint = 0;
			try {
				breakpoint = Integer.parseInt(messageArea.getText(start, 4), 16);
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (BadLocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			manager.removeBreakpoint(breakpoint);
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
