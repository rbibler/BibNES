package com.bibler.awesome.bibnes.ui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import com.bibler.awesome.bibnes.assembler.Assembler;
import com.bibler.awesome.bibnes.communications.MessageHandler;
import com.bibler.awesome.bibnes.communications.Notifiable;

public class AssemblerMainPanel extends JSplitPane implements Notifiable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7673270466202998997L;
	private AssemblerInputPanel inputPanel;
	private AssemblerOutputPanel outputPanel;
	
	public AssemblerMainPanel(MessageHandler handler) {
		super(JSplitPane.VERTICAL_SPLIT);
		handler.registerObjectToNotify(this);
		initialize();
	}
	
	private void initialize() {
		inputPanel = new AssemblerInputPanel(600,600);
		outputPanel = new AssemblerOutputPanel(600,200);
		setPreferredSize(new Dimension(600, 800));
		add(inputPanel);
		add(outputPanel);
		setDividerLocation(600);
	}
	
	public String[] getInputLines() {
		return inputPanel.getInputLines();
	}

	@Override
	public void takeNotice(String message, Object notifier) {
		if(notifier instanceof Assembler) {
			Assembler assembler = (Assembler) notifier;
			if(message.startsWith("ERROR")) {
				outputPanel.registerError(message);
			} else if(message.startsWith("DONE")) {
				outputPanel.displayListing(assembler.generateListing());
				outputPanel.displayMachineCode(assembler.getMachineCode());
			}
		}
		
		
		
	}

}
