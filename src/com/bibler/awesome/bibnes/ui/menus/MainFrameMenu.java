package com.bibler.awesome.bibnes.ui.menus;

import javax.swing.JMenu;
import javax.swing.JMenuBar;

import com.bibler.awesome.bibnes.communications.MessageHandler;

public class MainFrameMenu extends JMenuBar {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8353668161880122983L;
	private JMenu fileMenu;
	private JMenu editMenu;
	private RunMenu runMenu;
	
	
	public MainFrameMenu(MessageHandler handler) {
		super();
		setupMenu(handler);
	}
	
	private void setupMenu(MessageHandler handler) {
		fileMenu = new FileMenu("File");
		editMenu = new EditMenu("Edit");
		runMenu = new RunMenu("Run");
		add(fileMenu);
		add(editMenu);
		add(runMenu);
		runMenu.registerObjectToNotify(handler);
	
	}
}
