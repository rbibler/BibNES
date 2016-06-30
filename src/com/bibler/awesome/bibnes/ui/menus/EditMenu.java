package com.bibler.awesome.bibnes.ui.menus;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import com.bibler.awesome.bibnes.ui.MainFrame;

public class EditMenu extends JMenu {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -139523814046330841L;
	
	JMenuItem takeScreenShot;
	MainFrame mainFrame;

	public EditMenu(String menuName) {
		super(menuName);
		initialize();
	}
	
	public void initialize() {
		takeScreenShot = new JMenuItem("Screenshot");
		takeScreenShot.addActionListener(new FileMenuActionListener());
		takeScreenShot.setActionCommand("SHOT");
		takeScreenShot.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PRINTSCREEN, 0));
		add(takeScreenShot);
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
			case "SHOT":
				mainFrame.screenshot();
				break;
			}
		}
		
	}

}
