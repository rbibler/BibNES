package com.bibler.awesome.bibnes.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;
import javax.swing.text.JTextComponent;
import javax.swing.text.Utilities;

import com.bibler.awesome.bibnes.assembler.BreakpointManager;

public class EditorLeftPanel extends JPanel implements MouseListener {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3621044496583181750L;
	
	private BreakpointPanel breakpointPanel;
	private TextLine textLinePanel;

	public EditorLeftPanel(JTextComponent component, int height, BreakpointManager bpManager) {
		initialize(component, height, bpManager);
	}
	
	private void initialize(JTextComponent component, int height, BreakpointManager bpManager) {
		breakpointPanel = new BreakpointPanel(component);
		breakpointPanel.setBreakpointManager(bpManager);
		breakpointPanel.setMaximumSize(new Dimension(20, 8000));
		breakpointPanel.setPreferredSize(new Dimension(20, height - 5));
		breakpointPanel.setMinimumSize(new Dimension(20, 20));
		breakpointPanel.setBackground(Color.GRAY);
		setLayout(new BorderLayout());
		add(breakpointPanel, BorderLayout.WEST);
		
		textLinePanel = new TextLine(component);
		add(textLinePanel);
		addMouseListener(this);
	}


	@Override
	public void mouseClicked(MouseEvent arg0) {
		breakpointPanel.updateBreakpoints(arg0.getY());
		
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
