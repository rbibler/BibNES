package com.bibler.awesome.bibnes.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class EmulatorStatusPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1345986640738164373L;

	private JLabel accumulator;
	private JLabel accumulatorValue;
	private JLabel xIndex;
	private JLabel xIndexValue;
	private JLabel yIndex;
	private JLabel yIndexValue;
	private JLabel programCounter;
	private JLabel programCounterValue;
	private JLabel stackPointer;
	private JLabel stackPointerValue;
	private JLabel statusHeading;
	private JLabel statusValues;
	
	private JPanel accumulatorPanel;
	private JPanel xIndexPanel;
	private JPanel yIndexPanel;
	private JPanel programCounterPanel;
	private JPanel stackPointerPanel;
	private JPanel statusPanel;
	
	public EmulatorStatusPanel() {
		super();
		initialize();
	}
	
	private void initialize() {
		setupLabels();
		setupGrids();
		setMinimumSize(new Dimension(200, 200));
	}
	
	private void setupLabels() {
		accumulator = new JLabel("A");
		accumulatorValue = new JLabel("00");
		accumulatorPanel = new JPanel();
		accumulatorPanel.add(accumulator);
		accumulatorPanel.add(accumulatorValue);
		accumulatorPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		
		xIndex = new JLabel("X");
		xIndexValue = new JLabel("00");
		xIndexPanel = new JPanel();
		xIndexPanel.add(xIndex);
		xIndexPanel.add(xIndexValue);
		
		yIndex = new JLabel("Y");
		yIndexValue = new JLabel("00");
		yIndexPanel = new JPanel();
		yIndexPanel.add(yIndex);
		yIndexPanel.add(yIndexValue);
		
		programCounter = new JLabel("PC");
		programCounterValue = new JLabel("00");
		programCounterPanel = new JPanel();
		programCounterPanel.add(programCounter);
		programCounterPanel.add(programCounterValue);
		
		stackPointer = new JLabel("SP");
		stackPointerValue = new JLabel("00");
		stackPointerPanel = new JPanel();
		stackPointerPanel.add(stackPointer);
		stackPointerPanel.add(stackPointerValue);
		
		statusHeading = new JLabel("NV-BDIZC");
		statusValues = new JLabel("00000000");
		statusPanel = new JPanel();
		statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
		statusPanel.add(statusHeading);
		statusPanel.add(statusValues);
		
	}
	
	private void setupGrids() {
		GridBagConstraints c = new GridBagConstraints();
		setLayout(new GridBagLayout());
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.BOTH;
		add(accumulatorPanel, c);
		c.gridy = 1;
		add(xIndexPanel, c);
		c.gridy = 2;
		add(yIndexPanel, c);
		c.gridy = 3;
		c.gridwidth = 2;
		add(statusPanel, c);
		c.gridwidth = 1;
		c.gridx = 1;
		c.gridy = 0;
		c.anchor = GridBagConstraints.EAST;
		add(programCounterPanel, c);
		c.gridy = 1;
		add(stackPointerPanel, c);
		
	}
}
