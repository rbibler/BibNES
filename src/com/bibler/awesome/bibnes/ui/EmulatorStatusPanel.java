package com.bibler.awesome.bibnes.ui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.bibler.awesome.bibnes.systems.CPU;
import com.bibler.awesome.bibnes.utils.DigitUtils;
import com.bibler.awesome.bibnes.utils.StringUtils;

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
	private JLabel currentInst;
	
	private JPanel accumulatorPanel;
	private JPanel xIndexPanel;
	private JPanel yIndexPanel;
	private JPanel programCounterPanel;
	private JPanel stackPointerPanel;
	private JPanel statusPanel;
	
	private CPU cpu;
	private boolean running;
	
	public EmulatorStatusPanel() {
		super();
		initialize();
	}
	
	public void setCPU(CPU cpu) {
		this.cpu = cpu;
		setupThread();
	}
	
	private void setupThread() {
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				while(running) {
					updateValues();
					try {
						Thread.sleep(10);
					} catch(InterruptedException e) {}
				}
			}
		});
		running = true;
		t.start();
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
		accumulatorPanel.setPreferredSize(new Dimension(100, 25));
		
		xIndex = new JLabel("X");
		xIndexValue = new JLabel("00");
		xIndexPanel = new JPanel();
		xIndexPanel.add(xIndex);
		xIndexPanel.add(xIndexValue);
		xIndexPanel.setPreferredSize(new Dimension(100, 25));
		
		yIndex = new JLabel("Y");
		yIndexValue = new JLabel("00");
		yIndexPanel = new JPanel();
		yIndexPanel.add(yIndex);
		yIndexPanel.add(yIndexValue);
		yIndexPanel.setPreferredSize(new Dimension(100, 25));
		
		programCounter = new JLabel("PC");
		programCounterValue = new JLabel("00");
		programCounterPanel = new JPanel();
		programCounterPanel.add(programCounter);
		programCounterPanel.add(programCounterValue);
		programCounterPanel.setPreferredSize(new Dimension(100, 25));
		
		stackPointer = new JLabel("SP");
		stackPointerValue = new JLabel("00");
		stackPointerPanel = new JPanel();
		stackPointerPanel.add(stackPointer);
		stackPointerPanel.add(stackPointerValue);
		stackPointerPanel.setPreferredSize(new Dimension(100, 25));
		
		statusHeading = new JLabel("NV-BDIZC");
		statusValues = new JLabel("00000000");
		
		currentInst = new JLabel("Inst: ");
		statusPanel = new JPanel();
		statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
		statusPanel.add(statusHeading);
		statusPanel.add(statusValues);
		statusPanel.add(currentInst);
		accumulatorPanel.setPreferredSize(new Dimension(200, 25));
		
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
	
	private void updateValues() {
		updateAccumulator(cpu.getAccumulator());
		updateXIndex(cpu.getXIndex());
		updateYIndex(cpu.getYIndex());
		updateProgramCounter(cpu.getProgramCounter());
		updateStackPointer(cpu.getStackPointer());
		updateStatusRegister(cpu.getStatusRegister());
		updateCurrentInst(cpu.getCurrentInstruction());
	}
	
	public void updateAccumulator(int accumulatorValue) {
		this.accumulatorValue.setText(StringUtils.intToHexString(accumulatorValue));
	}
	
	public void updateXIndex(int xIndexValue) {
		this.xIndexValue.setText(StringUtils.intToHexString(xIndexValue));
	}
	
	public void updateYIndex(int yIndexValue) {
		this.yIndexValue.setText(StringUtils.intToHexString(yIndexValue));
	}
	
	public void updateProgramCounter(int programCounterValue) {
		this.programCounterValue.setText("" + programCounterValue);
	}
	
	public void updateStackPointer(int stackPointerValue) {
		this.stackPointerValue.setText(StringUtils.intToHexString(stackPointerValue));
	}
	
	public void updateStatusRegister(int statusValues) {
		this.statusValues.setText(StringUtils.intToPaddedString(statusValues, 8, DigitUtils.BIN));
	}
	
	public void updateCurrentInst(String inst) {
		this.currentInst.setText("Inst: " + inst);
	}
}
