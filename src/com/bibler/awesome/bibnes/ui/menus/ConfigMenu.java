package com.bibler.awesome.bibnes.ui.menus;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import com.bibler.awesome.bibnes.communications.MessageHandler;
import com.bibler.awesome.bibnes.communications.Notifiable;
import com.bibler.awesome.bibnes.communications.Notifier;
import com.bibler.awesome.bibnes.ui.MainFrame;

public class ConfigMenu extends JMenu implements Notifier {

	private JMenu audioMenu;
	private JCheckBoxMenuItem pulseOneEnableItem;
	private JCheckBoxMenuItem pulseTwoEnableItem;
	private JCheckBoxMenuItem triEnableItem;
	private JCheckBoxMenuItem noiseEnableItem;
	private JCheckBoxMenuItem dmcEnableItem;
	private AudioEnableActionListener actionListener = new AudioEnableActionListener();
	private ArrayList<Notifiable> objectsToNotify = new ArrayList<Notifiable>();
	private MainFrame mainFrame;

	public ConfigMenu(String menuName) {
		super(menuName);
		initialize();
	}
	
	private void initialize() {
		audioMenu = new JMenu("Audio Options");
		pulseOneEnableItem = new JCheckBoxMenuItem("Pulse One");
		audioMenu.add(pulseOneEnableItem);
		pulseOneEnableItem.setActionCommand("P1");
		pulseOneEnableItem.addActionListener(actionListener);
		pulseOneEnableItem.setSelected(true);
		
		pulseTwoEnableItem = new JCheckBoxMenuItem("Pulse Two");
		audioMenu.add(pulseTwoEnableItem);
		pulseTwoEnableItem.setActionCommand("P2");
		pulseTwoEnableItem.addActionListener(actionListener);
		pulseTwoEnableItem.setSelected(true);
		
		triEnableItem = new JCheckBoxMenuItem("Triangle");
		audioMenu.add(triEnableItem);
		triEnableItem.setActionCommand("TRI");
		triEnableItem.addActionListener(actionListener);
		triEnableItem.setSelected(true);
		
		noiseEnableItem = new JCheckBoxMenuItem("Noise");
		audioMenu.add(noiseEnableItem);
		noiseEnableItem.setActionCommand("NOISE");
		noiseEnableItem.addActionListener(actionListener);
		noiseEnableItem.setSelected(true);
		
		dmcEnableItem = new JCheckBoxMenuItem("DMC");
		audioMenu.add(dmcEnableItem);
		dmcEnableItem.setActionCommand("DMC");
		dmcEnableItem.addActionListener(actionListener);
		dmcEnableItem.setSelected(true);
		add(audioMenu);
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
	
	private class AudioEnableActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if(mainFrame == null) {
				mainFrame = getParentFrame();
			}
			String command = arg0.getActionCommand();
			int audioChannel = -1;
			switch(command) {
			case "P1":
				audioChannel = 0;
				break;
				
			case "P2":
				audioChannel = 1;
				break;
			case "TRI":
				audioChannel = 2;
				break;
			case "NOISE":
				audioChannel = 3;
				break;
			case "DMC":
				audioChannel = 4;
				break;
			}
			mainFrame.setAudioChannelEnable(audioChannel, ((JCheckBoxMenuItem) arg0.getSource()).isSelected());
		}
		
	}

	@Override
	public void notify(String messageToSend) {
		for(Notifiable notifiable : objectsToNotify) {
			notifiable.takeNotice(messageToSend, this);
		}
		
	}

}
