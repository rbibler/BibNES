package com.bibler.awesome.bibnes.ui;

import java.awt.BorderLayout;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.bibler.awesome.bibnes.assembler.Assembler;
import com.bibler.awesome.bibnes.assembler.BreakpointManager;
import com.bibler.awesome.bibnes.communications.MessageHandler;
import com.bibler.awesome.bibnes.io.FileUtils;
import com.bibler.awesome.bibnes.systems.CPU;
import com.bibler.awesome.bibnes.systems.Memory;
import com.bibler.awesome.bibnes.systems.MosBoard;
import com.bibler.awesome.bibnes.systems.Motherboard;
import com.bibler.awesome.bibnes.ui.menus.MainFrameMenu;
import com.bibler.awesome.bibnes.utils.NESProducer;

public class MainFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8675262530989043410L;
	
	private AssemblerMainPanel mainPanel;
	private MessageHandler messageHandler = new MessageHandler();
	private Motherboard board;
	private BreakpointManager bpManager;
	
	public MainFrame() {
		super();
		initialize();
		
		
	}

	private void initialize() {
		try {
        UIManager.setLookAndFeel(
            UIManager.getSystemLookAndFeelClassName());
		} 
		catch (UnsupportedLookAndFeelException | ClassNotFoundException  | InstantiationException | IllegalAccessException e) {}
		bpManager = new BreakpointManager();
		mainPanel = new AssemblerMainPanel(messageHandler, bpManager);
		setLayout(new BorderLayout());
		add(mainPanel, BorderLayout.CENTER);
		setJMenuBar(new MainFrameMenu(messageHandler));
		pack();
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public void runAssembler() {
		Memory machineCode = assemble();
		CPU cpu = new CPU();
		mainPanel.getEmulatorPanel().getHexPane().fillInValues(machineCode);
		cpu.registerObjectToNotify(messageHandler);
		cpu.powerOn();
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
	
	public void debug() {
		Memory machineCode = assemble();
		board = new MosBoard();
		board.setROM(machineCode);
		board.power();
		board.registerObjectToNotify(messageHandler);
		mainPanel.getEmulatorPanel().getHexPane().fillInValues(machineCode);
	}
	
	public void loadNesFileAndRun(boolean debug) {
		File f = FileUtils.loadFile(this);
		NESProducer producer = new NESProducer();
		board = producer.produceNES(f, messageHandler);
		board.power();
		if(!debug) {
			board.runSystem();
		}
	}
	
	public void emulateNES(boolean debug) {
		NESProducer producer = new NESProducer();
		board = producer.produceNES(mainPanel.getInputLines(), messageHandler);
		board.power();
		if(!debug) {
			board.runSystem();
		}
	}
	
	public void screenshot() {
		mainPanel.screenshot();
	}
	
	public void step() {
		board.step();
	}
	
	
	
	

}
