package com.bibler.awesome.bibnes.ui;

import java.awt.Dimension;

import javax.swing.JPanel;

public class EmulatorPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3221558980728538118L;
	
	private EmulatorStatusPanel statusPanel;
	
	public EmulatorPanel(int width, int height) {
		super();
		setPreferredSize(new Dimension(width, height));
		statusPanel = new EmulatorStatusPanel();
		add(statusPanel);
	}
	
	public void sendMessageToEmulator(String message, Object notifier) {
		statusPanel.handleMessage(message, notifier);
	}

}
