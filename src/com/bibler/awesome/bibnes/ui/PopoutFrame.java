package com.bibler.awesome.bibnes.ui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

public class PopoutFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6827577900714387530L;
	
	public PopoutFrame(PopoutPanel panel) {
		super();
		panel.popOut();
		add(panel);
		setTitle(panel.getTitle());
		pack();
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				panel.popBack();
			}
		});
	}

}
