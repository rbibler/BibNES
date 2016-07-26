package com.bibler.awesome.bibnes.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;

import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.bibler.awesome.bibnes.assembler.Assembler;
import com.bibler.awesome.bibnes.assembler.BreakpointManager;
import com.bibler.awesome.bibnes.assembler.ErrorHandler;
import com.bibler.awesome.bibnes.communications.MessageHandler;
import com.bibler.awesome.bibnes.communications.Notifiable;
import com.bibler.awesome.bibnes.systems.CPU;
import com.bibler.awesome.bibnes.systems.Controller;
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
	private NESScreen nametable;
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
		nametable = new NESScreen(256, 240);
		middlePane = new JTabbedPane();
		middlePane.add("Source", inputPanel);
		middlePane.add("Emulator", nesScreen);
		middlePane.add("Debug", nametable);
		middlePane.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				Object source = arg0.getSource();
				if(source instanceof JTabbedPane) {
					if(middlePane.getSelectedIndex() == 1) {
						nesScreen.requestFocus();
					}
				}
			}
			
		});
		middlePane.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("LEFT"), "none");
		middlePane.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("RIGHT"), "none");
		middlePane.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("UP"), "none");
		middlePane.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("DOWN"), "none");
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
	
	public void setController(Controller controller) {
		nesScreen.addKeyListener(controller);
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
			//emulatorPanel.sendMessageToEmulator(message, notifier);	
		} else if(notifier instanceof NESProducer) {
			final NESProducer prod = (NESProducer) notifier;
			if(message.startsWith("LISTING")) {
				outputPanel.displayListing(message.substring("LISTING".length()));
			} else if(message.startsWith("DONE")){
				emulatorPanel.fillCPUMem(prod.getCPUMem());
				emulatorPanel.fillPPUMem(prod.getPPUMem());
			}
		} else if(notifier instanceof PPU) {
			if(message.equalsIgnoreCase("Frame")) {
				nesScreen.updateFrame(((PPU) notifier).getFrameForPainting());
			} else if(message.equalsIgnoreCase("NT")) {
				//nametable.updateFrame(((PPU) notifier).getCurrentNameTable());
			}
		}
	}

}
