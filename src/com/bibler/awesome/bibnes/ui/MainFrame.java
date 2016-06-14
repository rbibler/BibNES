package com.bibler.awesome.bibnes.ui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.bibler.awesome.bibnes.assembler.Assembler;
import com.bibler.awesome.bibnes.communications.MessageHandler;
import com.bibler.awesome.bibnes.ui.menus.MainFrameMenu;

public class MainFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8675262530989043410L;
	
	private AssemblerMainPanel mainPanel;
	private MessageHandler messageHandler = new MessageHandler();
	
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
		
		mainPanel = new AssemblerMainPanel(messageHandler);
		setLayout(new BorderLayout());
		add(mainPanel, BorderLayout.CENTER);
		setJMenuBar(new MainFrameMenu(messageHandler));
		pack();
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public void runAssembler() {
		Assembler assembler = new Assembler();
		messageHandler.registerObjectToNotify(assembler);
		assembler.registerObjectToNotify(messageHandler);
		assembler.passOne(mainPanel.getInputLines());
	}
	
	

}
