package com.bibler.awesome.bibnes.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import javax.swing.JComponent;
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
import com.bibler.awesome.bibnes.controllers.KeyboardController;
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
	private PopoutPaneHolder middlePane;
	private AssemblerInputPanel inputPanel;
	private NESScreen nesScreen;
	private NametableScreen nametable;
	private PaletteTable paletteTable;
	private PopoutPanel debugPanel;
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
		inputPanel = new AssemblerInputPanel("Source", 0, 900,600, bpManager);
		nesScreen = new NESScreen("Emulator", 1, 256, 240);
		debugPanel = new PopoutPanel("Debug", 2, 1024, 960);
		nametable = new NametableScreen();
		debugPanel.add(nametable);
		paletteTable = new PaletteTable(512, 64);
		debugPanel.add(paletteTable);
		nametable = new NametableScreen();
		middlePane = new PopoutPaneHolder(10);
		middlePane.addPopoutPanel(inputPanel);
		inputPanel.setParent(middlePane);
		inputPanel.setPoppedStatus(false);
		middlePane.addPopoutPanel(nesScreen);
		nesScreen.setParent(middlePane);
		nesScreen.setPoppedStatus(false);
		middlePane.addPopoutPanel(debugPanel);
		debugPanel.setParent(middlePane);
		debugPanel.setPoppedStatus(false);
		middlePane.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("LEFT"), "none");
		middlePane.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("RIGHT"), "none");
		middlePane.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("UP"), "none");
		middlePane.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("DOWN"), "none");
		middlePane.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				Object source = arg0.getSource();
				if(source instanceof JTabbedPane) {
					try {
						if(middlePane.getComponentAt(middlePane.getSelectedIndex()) instanceof NESScreen) {
							nesScreen.requestFocus();
						}
					} catch(ArrayIndexOutOfBoundsException e) {}
				}
			}
			
		});
		inputPanel.applyLookAndFeel(currentLF);
		outputPanel = new AssemblerOutputPanel("", 0, 900,200, bpManager);
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
	
	public void setController(KeyboardController controller) {
		nesScreen.addKeyListener(controller);
	}
	
	public void setBoard(NES nes) {
		nesScreen.setBoard(nes);
		paletteTable.setPPUMem(nes.getPPUMem());
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
		} else if(notifier instanceof NESProducer) {
			final NESProducer prod = (NESProducer) notifier;
			if(message.startsWith("LISTING")) {
				outputPanel.displayListing(message.substring("LISTING".length()));
			} else if(message.startsWith("DONE")){
				final PPU ppu = prod.getPPU();
				emulatorPanel.setCPU(prod.getCPU());
				emulatorPanel.fillCPUMem(prod.getCPUMem());
				emulatorPanel.fillPPUMem(prod.getPPUMem());
				emulatorPanel.fillOAMMem(ppu.getOamMem());
				nametable.setPPUMem(prod.getPPUMem(), ppu);
				nametable.setNES(prod.getNES());
			}
		} else if(notifier instanceof PPU) {
			if(message.equalsIgnoreCase("Frame")) {
				nesScreen.updateFrame(((PPU) notifier).getFrameForPainting());
			} 
		}
	}

}
