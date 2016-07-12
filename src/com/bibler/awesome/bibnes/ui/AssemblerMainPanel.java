package com.bibler.awesome.bibnes.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;

import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

import com.bibler.awesome.bibnes.assembler.Assembler;
import com.bibler.awesome.bibnes.assembler.BreakpointManager;
import com.bibler.awesome.bibnes.assembler.ErrorHandler;
import com.bibler.awesome.bibnes.communications.MessageHandler;
import com.bibler.awesome.bibnes.communications.Notifiable;
import com.bibler.awesome.bibnes.systems.CPU;
import com.bibler.awesome.bibnes.systems.Memory;
import com.bibler.awesome.bibnes.systems.NES;
import com.bibler.awesome.bibnes.systems.PPU;
import com.bibler.awesome.bibnes.utils.NESProducer;

public class AssemblerMainPanel extends JSplitPane implements Notifiable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7673270466202998997L;
	private JTabbedPane middlePane;
	private AssemblerInputPanel inputPanel;
	private NESScreen nesScreen;
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
		nesScreen = new NESScreen(256, 240);
		middlePane = new JTabbedPane();
		middlePane.add("Source", inputPanel);
		middlePane.add("Emulator", nesScreen);
		inputPanel.applyLookAndFeel(currentLF);
		outputPanel = new AssemblerOutputPanel(900,200, bpManager);
		emulatorPanel = new EmulatorPanel(200, 600);
		projectPanel = new ProjectPanel(200, 600);
		inputStatusPane.add(middlePane);
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
	
	public void screenshot() {
		nesScreen.screenshot();
	}

	@Override
	public void takeNotice(String message, Object notifier) {
		if(notifier instanceof Assembler) {
			Assembler assembler = (Assembler) notifier;
			if(message.startsWith("DONE")) {
				outputPanel.displayListing(assembler.generateListing());
			}
		} else if(notifier instanceof ErrorHandler) {
			outputPanel.registerError(message);
		} else if(notifier instanceof CPU || notifier instanceof Memory || notifier instanceof NES) {
			if(message.startsWith("STEP") && notifier instanceof NES) {
				outputPanel.updateStep(Integer.parseInt(message.substring(4)));
			}
			emulatorPanel.sendMessageToEmulator(message, notifier);	
		} else if(notifier instanceof NESProducer) {
			if(message.startsWith("LISTING")) {
				outputPanel.displayListing(message.substring("LISTING".length()));
			} else {
				outputPanel.displayMachineCode(((NESProducer) notifier).getMachineCode());
			}
		} else if(notifier instanceof PPU) {
			if(message.equalsIgnoreCase("Frame")) {
				nesScreen.updateFrame(((PPU) notifier).getFrameForPainting());
			}
		}
	}

}
