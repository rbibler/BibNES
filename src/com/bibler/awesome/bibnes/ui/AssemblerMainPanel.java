package com.bibler.awesome.bibnes.ui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import com.bibler.awesome.bibnes.assembler.Assembler;
import com.bibler.awesome.bibnes.assembler.ErrorHandler;
import com.bibler.awesome.bibnes.communications.MessageHandler;
import com.bibler.awesome.bibnes.communications.Notifiable;
import com.bibler.awesome.bibnes.systems.CPU;

public class AssemblerMainPanel extends JSplitPane implements Notifiable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7673270466202998997L;
	private AssemblerInputPanel inputPanel;
	private AssemblerOutputPanel outputPanel;
	private ProjectPanel projectPanel;
	private EmulatorPanel emulatorPanel;
	private JSplitPane inputStatusPane;
	private JSplitPane inputOutputPane;
	
	public AssemblerMainPanel(MessageHandler handler) {
		super(JSplitPane.HORIZONTAL_SPLIT);
		handler.registerObjectToNotify(this);
		initialize();
	}
	
	private void initialize() {
		inputStatusPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		inputOutputPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		inputPanel = new AssemblerInputPanel(600,600);
		outputPanel = new AssemblerOutputPanel(600,200);
		emulatorPanel = new EmulatorPanel(200, 600);
		projectPanel = new ProjectPanel(200, 600);
		inputStatusPane.add(inputPanel);
		inputStatusPane.add(emulatorPanel);
		inputStatusPane.setDividerLocation(600);
		inputOutputPane.add(inputStatusPane);
		inputOutputPane.add(outputPanel);
		inputOutputPane.setDividerLocation(600);
		setPreferredSize(new Dimension(1000, 800));
		add(projectPanel);
		add(inputOutputPane);
		setDividerLocation(200);
	}
	
	public String[] getInputLines() {
		return inputPanel.getInputLines();
	}

	@Override
	public void takeNotice(String message, Object notifier) {
		if(notifier instanceof Assembler) {
			Assembler assembler = (Assembler) notifier;
			if(message.startsWith("DONE")) {
				outputPanel.displayListing(assembler.generateListing());
				outputPanel.displayMachineCode(assembler.getMachineCode());
			}
		} else if(notifier instanceof ErrorHandler) {
			outputPanel.registerError(message);
		} else if(notifier instanceof CPU) {
			emulatorPanel.sendMessageToEmulator(message, notifier);
		}
		
		
		
	}

}
