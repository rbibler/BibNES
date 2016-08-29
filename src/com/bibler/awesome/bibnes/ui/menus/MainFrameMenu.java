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
	private ConfigMenu configMenu;
	
	
	public MainFrameMenu(MessageHandler handler) {
		super();
		setupMenu(handler);
	}
	
	private void setupMenu(MessageHandler handler) {
		fileMenu = new FileMenu("File");
		editMenu = new EditMenu("Edit");
		runMenu = new RunMenu("Run");
		configMenu = new ConfigMenu("Options");
		add(fileMenu);
		add(editMenu);
		add(runMenu);
		add(configMenu);
		runMenu.registerObjectToNotify(handler);
		configMenu.registerObjectToNotify(handler);
	
	}
	
	public ConfigMenu getConfigMenu() {
		return configMenu;
	}
}
