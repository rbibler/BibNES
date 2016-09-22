package com.bibler.awesome.bibnes.ui;

import java.awt.BorderLayout;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.bibler.awesome.bibnes.assembler.Assembler;
import com.bibler.awesome.bibnes.assembler.BreakpointManager;
import com.bibler.awesome.bibnes.communications.MessageHandler;
import com.bibler.awesome.bibnes.controllers.JInputControllerManager;
import com.bibler.awesome.bibnes.controllers.KeyboardController;
import com.bibler.awesome.bibnes.controllers.USBGamepad;
import com.bibler.awesome.bibnes.io.FileUtils;
import com.bibler.awesome.bibnes.systems.CPU;
import com.bibler.awesome.bibnes.systems.Memory;
import com.bibler.awesome.bibnes.systems.NES;
import com.bibler.awesome.bibnes.ui.menus.MainFrameMenu;
import com.bibler.awesome.bibnes.utils.NESProducer;

public class MainFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8675262530989043410L;
	
	private AssemblerMainPanel mainPanel;
	private MessageHandler messageHandler = new MessageHandler();
	private NES board;
	private BreakpointManager bpManager;
	private MainFrameMenu mainFrameMenu;
	private Preferences prefs;
	
	public MainFrame() {
		super();
		initialize();
		KeyboardFocusManager focusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();	
		
	}
	

	private void initialize() {
		try {
        UIManager.setLookAndFeel(
            UIManager.getSystemLookAndFeelClassName());
		} 
		catch (UnsupportedLookAndFeelException | ClassNotFoundException  | InstantiationException | IllegalAccessException e) {}
		prefs = Preferences.userNodeForPackage(MainFrame.class);
		String defaultRomRootPath = prefs.get("ROM_ROOT_PATH", "C:/users/ryan/desktop/nes/roms");
		bpManager = new BreakpointManager();
		mainPanel = new AssemblerMainPanel(messageHandler, bpManager, this, defaultRomRootPath);
		setLayout(new BorderLayout());
		add(mainPanel, BorderLayout.CENTER);
		mainFrameMenu = new MainFrameMenu(messageHandler);
		setJMenuBar(mainFrameMenu);
		pack();
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public void runAssembler() {
		Memory machineCode = assemble();
		CPU cpu = new CPU();
		cpu.registerObjectToNotify(messageHandler);
		cpu.powerOn(null);
		cpu.resetCPU();
		cpu.run();
	}

	private Memory assemble() {
		Assembler assembler = new Assembler();
		messageHandler.registerObjectToNotify(assembler);
		assembler.registerObjectToNotify(messageHandler);
		Memory machineCode = assembler.passOne(mainPanel.getInputLines());
		return machineCode;
	}
	
	public void loadNesFileAndRun(boolean debug) {
		File f = FileUtils.loadFile(this, false);
		runNESFile(f);
	}
	
	public void runNESFile(File f) {
		if(f == null) {
			return;
		}
		if(board != null) {
			board.kill();
		}
		KeyboardController gamepad = new KeyboardController();
		KeyEventDispatcher keyEventDispatcher = new KeyEventDispatcher() {
			  @Override
			  public boolean dispatchKeyEvent(final KeyEvent e) {
				  return gamepad.processKey(e);
			  }
			};
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(keyEventDispatcher);
		mainPanel.setController(gamepad);
		bpManager.clearAllBreakpoints();
		NESProducer producer = new NESProducer();
		if(board != null) {
			board.pause();
			board.unregisterAll();
			
		}
		board = (NES) producer.produceNES(f, messageHandler);
		setupAudioChannelStates();
		mainPanel.setBoard(board);
		board.setPeripheral(gamepad);
		board.setBreakpointManager(bpManager);
		board.power();
		board.runSystem();
	}
	
	private void setupAudioChannelStates() {
		boolean[] states = mainFrameMenu.getConfigMenu().getAudioChannelStates();
		for(int i = 0; i < states.length; i++) {
			board.setAudioChannelEnable(i, states[i]);
		}
	}
	
	public void emulateNES(boolean debug) {
		board.runSystem();
	}
	
	public void runEmulator() {
		board.runEmulator();
	}
	
	public void screenshot() {
		mainPanel.screenshot();
	}
	
	public void step() {
		//board.stepNext();
	}
	
	public void frame() {
		board.nextFrame();
	}
	
	public void pause() {
		board.pause();
	}
	
	public void reset() {
		board.reset();
	}
	
	public void setAudioChannelEnable(int audioChannel, boolean enable) {
		board.setAudioChannelEnable(audioChannel, enable);
	}
	
	public void enableAudio(boolean enable) {
		board.enableAudio(enable);
	}
	
	public void updateAudioParams(int paramNum) {
		board.updateAudioParams(paramNum);
	}
	
	public void togglePPUDisplay(int bgOrObjects, boolean display) {
		board.getPPU().toggleDisplay(bgOrObjects, display);
	}
	
	public void setRomRootPath() {
		File f = FileUtils.loadFile(this, true);
		if(f != null && f.isDirectory()) {
			prefs.put("ROM_ROOT_PATH", f.getAbsolutePath());
			System.out.println("Set rom path to: " + prefs.get("ROM_ROOT_PATH", "NOT FOUND!"));
			try {
				prefs.flush();
			} catch (BackingStoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			
		}
	}

}
