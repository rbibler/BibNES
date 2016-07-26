package com.bibler.awesome.bibnes.ui.menus;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

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
	private JMenuItem debugItem;
	private JMenuItem stepItem;
	private JMenuItem emulateItem;
	private JMenuItem frameItem;
	private RunMenuActionListener actionListener = new RunMenuActionListener();
	private ArrayList<Notifiable> objectsToNotify = new ArrayList<Notifiable>();
	private MainFrame mainFrame;

	public RunMenu(String menuName) {
		super(menuName);
		initialize();
	}
	
	private void initialize() {
		runItem = new JMenuItem("Run");
		add(runItem);
		runItem.setActionCommand("RUN");
		runItem.addActionListener(actionListener);
		
		debugItem = new JMenuItem("Debug");
		add(debugItem);
		debugItem.setActionCommand("DEBUG");
		debugItem.addActionListener(actionListener);
		
		emulateItem = new JMenuItem("Emulate");
		add(emulateItem);
		emulateItem.setActionCommand("EMULATE");
		emulateItem.addActionListener(actionListener);
		
		stepItem = new JMenuItem("Step");
		add(stepItem);
		stepItem.setActionCommand("STEP");
		stepItem.addActionListener(actionListener);
		stepItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
		
		frameItem = new JMenuItem("Next Frame");
		add(frameItem);
		frameItem.setActionCommand("FRAME");
		frameItem.addActionListener(actionListener);
		frameItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0));
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
			if(mainFrame == null) {
				mainFrame = getParentFrame();
			}
			String command = arg0.getActionCommand();
			switch(command) {
			case "RUN":
				
				if(mainFrame != null) {
					//mainFrame.runAssembler();
					mainFrame.runEmulator();
				}
				break;
			case "STEP":
				mainFrame.step();
				break;
			case "DEBUG":
				mainFrame.debug();
				//mainFrame.emulateNES(true);
				break;
			case "EMULATE":
				mainFrame.emulateNES(true);
				break;
			case "FRAME":
				mainFrame.frame();
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
