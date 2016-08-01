package com.bibler.awesome.bibnes.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class PopoutFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6827577900714387530L;
	
	private PopoutPanel panel;
	
	public PopoutFrame(PopoutPanel panel) {
		super();
		this.panel = panel;
		panel.popOut();
		JPanel wrapper = new JPanel();
		wrapper.setLayout(new BorderLayout());
		wrapper.add(panel, BorderLayout.CENTER);
		add(wrapper);
		setTitle(panel.getTitle());
		pack();
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				panel.popBack();
			}
		});
	}
	
	@Override
	public Dimension getMaximumSize() {
		return panel.getMaximumSize();
	}

}
