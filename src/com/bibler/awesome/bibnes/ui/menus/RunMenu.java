package com.bibler.awesome.bibnes.ui.menus;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import com.bibler.awesome.bibnes.communications.MessageHandler;
import com.bibler.awesome.bibnes.communications.Notifiable;
import com.bibler.awesome.bibnes.communications.Notifier;
import com.bibler.awesome.bibnes.ui.MainFrame;

public class RunMenu extends JMenu implements Notifier {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6995648999980272320L;
	
	private JMenuItem runItem;
	private RunMenuActionListener actionListener = new RunMenuActionListener();
	private ArrayList<Notifiable> objectsToNotify = new ArrayList<Notifiable>();

	public RunMenu(String menuName) {
		super(menuName);
		initialize();
	}
	
	private void initialize() {
		runItem = new JMenuItem("Run");
		add(runItem);
		runItem.setActionCommand("RUN");
		runItem.addActionListener(actionListener);
	}
	
	public void registerObjectToNotify(MessageHandler handler) {
		if(!objectsToNotify.contains(handler)) {
			objectsToNotify.add(handler);
		}
	}
	
	private MainFrame getParentFrame() {
		Container parent = getParent();
		while(!(parent instanceof MainFrame) && parent != null) {
			parent = parent.getParent();
		}
		return (MainFrame) parent;
	}
	
	private class RunMenuActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			String command = arg0.getActionCommand();
			switch(command) {
			case "RUN":
				MainFrame mainFrame = getParentFrame();
				if(mainFrame != null) {
					mainFrame.runAssembler();
				}
			}
			
		}
		
	}

	@Override
	public void notify(String messageToSend) {
		for(Notifiable notifiable : objectsToNotify) {
			notifiable.takeNotice(messageToSend, this);
		}
		
	}
}
