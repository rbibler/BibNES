package com.bibler.awesome.bibnes.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.bibler.awesome.bibnes.assembler.BreakpointManager;
import com.bibler.awesome.bibnes.systems.Memory;

public class AssemblerOutputPanel extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2892883435285304497L;
	private ListingBox listingPanel;
	private HexPanel hexPanel;
	private ConsoleMessageBox consolePanel;
	private JTabbedPane tabs;
	
	public AssemblerOutputPanel(int width, int height, BreakpointManager bpManager) {
		super();
		initialize(width, height, bpManager);
	}
	
	private void initialize(int width, int height, BreakpointManager bpManager) {
		setLayout(new BorderLayout());
		tabs = new JTabbedPane();
		listingPanel = new ListingBox();
		listingPanel.setBreakpointManager(bpManager);
		hexPanel = new HexPanel();
		consolePanel = new ConsoleMessageBox();
		tabs.setPreferredSize(new Dimension(width, height));
		listingPanel.setPreferredSize(new Dimension(width, height));
		hexPanel.setPreferredSize(new Dimension(width, height));
		consolePanel.setPreferredSize(new Dimension(width, height));
		tabs.addTab("Console", consolePanel);
		tabs.addTab("Listing", listingPanel);
		tabs.addTab("Output", hexPanel);
		add(tabs, BorderLayout.CENTER);
		
	}
	
	public void registerError(String errorMessage) {
		consolePanel.displayErrorMessage(errorMessage);
	}
	
	public void updateStep(int pc) {
		listingPanel.updateCurrentLine(pc);
	}
	
	public void displayListing(String listing) {
		listingPanel.deleteAll();
		listingPanel.setTextColor(Color.BLACK);
		listingPanel.displayListing(listing);
	}
	
	public void displayMachineCode(Memory machineCode) {
		hexPanel.displayMachineCode(machineCode);
	}

}
