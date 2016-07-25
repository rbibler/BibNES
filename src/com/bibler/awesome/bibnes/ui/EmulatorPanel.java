package com.bibler.awesome.bibnes.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;

import com.bibler.awesome.bibnes.systems.NES;

public class EmulatorPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3221558980728538118L;
	
	private EmulatorStatusPanel statusPanel;
	private JTabbedPane tabPane;
	private HexPane cpuPane;
	private HexPane ppuPane;
	
	public EmulatorPanel(int width, int height) {
		super();
		setPreferredSize(new Dimension(width, height));
		tabPane = new JTabbedPane();
		statusPanel = new EmulatorStatusPanel();
		tabPane.add("Status", statusPanel);
		cpuPane = new HexPane();
		tabPane.add("CPU Memory", cpuPane);
		ppuPane = new HexPane();
		tabPane.add("PPU Memory", ppuPane);
		setLayout(new BorderLayout());
		add(tabPane, BorderLayout.CENTER);
	}
	
	public HexPane getHexPane() {
		return cpuPane;
	}
	
	
	
	public void fillCPUMem(int[] cpuMem) {
		cpuPane.fillInValues(cpuMem);
	}
	
	public void fillPPUMem(int[] ppuMem) {
		ppuPane.fillInValues(ppuMem);
	}
	
	public void sendMessageToEmulator(String message, Object notifier) {
		if(message.startsWith("STEP")) {
			statusPanel.handleMessage(message, notifier);
		} else if(message.startsWith("CPUMEM")) {
			cpuPane.parseMemUpdate(message.substring(6));
		} else if(message.startsWith("PPUMEM")) {
			ppuPane.parseMemUpdate(message.substring(6));
		} 
	}

}
