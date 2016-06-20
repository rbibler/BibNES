package com.bibler.awesome.bibnes.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.bibler.awesome.bibnes.systems.NES;

public class EmulatorPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3221558980728538118L;
	
	private EmulatorStatusPanel statusPanel;
	private JTabbedPane tabPane;
	private HexPane hexPane;
	
	public EmulatorPanel(int width, int height) {
		super();
		setPreferredSize(new Dimension(width, height));
		tabPane = new JTabbedPane();
		statusPanel = new EmulatorStatusPanel();
		tabPane.add("Status", statusPanel);
		hexPane = new HexPane();
		tabPane.add("Memory", hexPane);
		setLayout(new BorderLayout());
		add(tabPane, BorderLayout.CENTER);
	}
	
	public HexPane getHexPane() {
		return hexPane;
	}
	
	public void sendMessageToEmulator(String message, Object notifier) {
		if(message.startsWith("STEP")) {
			statusPanel.handleMessage(message, notifier);
		} else if(message.startsWith("MEM")) {
			hexPane.parseMemUpdate(message.substring(3));
		} else if(message.equalsIgnoreCase("FILL_CPU_MEM")) {
			hexPane.fillInValues(((NES) notifier).getCPUMem());
		}
	}

}
