package com.bibler.awesome.bibnes.ui.menus;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import com.bibler.awesome.bibnes.ui.MainFrame;

public class FileMenu extends JMenu {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -901728829221367902L;
	
	JMenuItem loadRom;
	JMenuItem setRomRootPath;
	MainFrame mainFrame;

	public FileMenu(String menuName) {
		super(menuName);
		initialize();
	}
	
	private void initialize() {
		FileMenuActionListener listener = new FileMenuActionListener();
		loadRom = new JMenuItem("Load ROM");
		loadRom.addActionListener(listener);
		loadRom.setActionCommand("LOAD");
		add(loadRom);
		
		setRomRootPath = new JMenuItem("Set Rom Folder");
		setRomRootPath.addActionListener(listener);
		setRomRootPath.setActionCommand("SET_PATH");
		add(setRomRootPath);
	}
	
	private MainFrame getParentFrame() {
		Container parent = getParent();
		while(!(parent instanceof MainFrame) && parent != null) {
			parent = parent.getParent();
		}
		return (MainFrame) parent;
	}
	
	private class FileMenuActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if(mainFrame == null) {
				mainFrame = getParentFrame();
			}
			String command = arg0.getActionCommand();
			switch(command) {
			case "LOAD":
				mainFrame.loadNesFileAndRun(true);
				break;
			case "SET_PATH":
				mainFrame.setRomRootPath();
			}
		}
		
	}

}
