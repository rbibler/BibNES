package com.bibler.awesome.bibnes.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import com.bibler.awesome.bibnes.assembler.Assembler;
import com.bibler.awesome.bibnes.assembler.BreakpointManager;
import com.bibler.awesome.bibnes.assembler.ErrorHandler;
import com.bibler.awesome.bibnes.communications.MessageHandler;
import com.bibler.awesome.bibnes.communications.Notifiable;
import com.bibler.awesome.bibnes.systems.CPU;
import com.bibler.awesome.bibnes.systems.Memory;
import com.bibler.awesome.bibnes.systems.NES;

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
	
	public AssemblerMainPanel(MessageHandler handler, BreakpointManager bpManager) {
		super(JSplitPane.HORIZONTAL_SPLIT);
		handler.registerObjectToNotify(this);
		initialize(bpManager);
	}
	
	private void initialize(BreakpointManager bpManager) {
		inputStatusPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		inputOutputPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		LookAndFeel currentLF = setupLookAndFeel();
		inputPanel = new AssemblerInputPanel(900,600, bpManager);
		inputPanel.applyLookAndFeel(currentLF);
		outputPanel = new AssemblerOutputPanel(900,200);
		emulatorPanel = new EmulatorPanel(200, 600);
		projectPanel = new ProjectPanel(200, 600);
		inputStatusPane.add(inputPanel);
		inputStatusPane.add(emulatorPanel);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		inputStatusPane.setDividerLocation((int) (screenSize.getWidth() * .7));
		inputOutputPane.add(inputStatusPane);
		inputOutputPane.add(outputPanel);
		inputOutputPane.setDividerLocation(600);
		setPreferredSize(new Dimension(1000, 800));
		add(projectPanel);
		add(inputOutputPane);
		setDividerLocation(200);
	}
	
	private LookAndFeel setupLookAndFeel() {
		LookAndFeel retLF = new LookAndFeel();
		retLF.setBackgroundColor(Color.WHITE);
		retLF.setCurrentFont(new Font("COURIER", Font.PLAIN, 14));
		retLF.setStandardTextColor(Color.BLACK);
		return retLF;
	}
	
	public String[] getInputLines() {
		return inputPanel.getInputLines();
	}
	
	public EmulatorPanel getEmulatorPanel() {
		return emulatorPanel;
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
		} else if(notifier instanceof CPU || notifier instanceof Memory || notifier instanceof NES) {
			emulatorPanel.sendMessageToEmulator(message, notifier);	
		}
	}

}
