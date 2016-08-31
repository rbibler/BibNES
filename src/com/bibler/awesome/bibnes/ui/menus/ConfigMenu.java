package com.bibler.awesome.bibnes.ui.menus;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;

import com.bibler.awesome.bibnes.communications.MessageHandler;
import com.bibler.awesome.bibnes.communications.Notifiable;
import com.bibler.awesome.bibnes.communications.Notifier;
import com.bibler.awesome.bibnes.ui.MainFrame;

public class ConfigMenu extends JMenu implements Notifier {

	private JMenu audioMenu;
	private JMenu videoMenu;
	private JCheckBoxMenuItem silenceAudioItem;
	private JCheckBoxMenuItem pulseOneEnableItem;
	private JCheckBoxMenuItem pulseTwoEnableItem;
	private JCheckBoxMenuItem triEnableItem;
	private JCheckBoxMenuItem noiseEnableItem;
	private JCheckBoxMenuItem dmcEnableItem;
	private JRadioButtonMenuItem eightBitAudio;
	private JRadioButtonMenuItem sixteenBitAudio;
	private JCheckBoxMenuItem showBGItem;
	private JCheckBoxMenuItem showObjectsItem;
	private AudioEnableActionListener actionListener = new AudioEnableActionListener();
	private ArrayList<Notifiable> objectsToNotify = new ArrayList<Notifiable>();
	private MainFrame mainFrame;

	public ConfigMenu(String menuName) {
		super(menuName);
		initialize();
	}
	
	private void initialize() {
		audioMenu = new JMenu("Audio Options");
		
		silenceAudioItem = new JCheckBoxMenuItem("Silence All Channels");
		audioMenu.add(silenceAudioItem);
		silenceAudioItem.setActionCommand("SILENCE");
		silenceAudioItem.addActionListener(actionListener);
		silenceAudioItem.setSelected(false);
		silenceAudioItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0));
		
		
		pulseOneEnableItem = new JCheckBoxMenuItem("Pulse One");
		audioMenu.add(pulseOneEnableItem);
		pulseOneEnableItem.setActionCommand("P1");
		pulseOneEnableItem.addActionListener(actionListener);
		pulseOneEnableItem.setSelected(true);
		pulseOneEnableItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
		
		
		pulseTwoEnableItem = new JCheckBoxMenuItem("Pulse Two");
		audioMenu.add(pulseTwoEnableItem);
		pulseTwoEnableItem.setActionCommand("P2");
		pulseTwoEnableItem.addActionListener(actionListener);
		pulseTwoEnableItem.setSelected(true);
		pulseTwoEnableItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));
		
		triEnableItem = new JCheckBoxMenuItem("Triangle");
		audioMenu.add(triEnableItem);
		triEnableItem.setActionCommand("TRI");
		triEnableItem.addActionListener(actionListener);
		triEnableItem.setSelected(true);
		triEnableItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0));
		
		noiseEnableItem = new JCheckBoxMenuItem("Noise");
		audioMenu.add(noiseEnableItem);
		noiseEnableItem.setActionCommand("NOISE");
		noiseEnableItem.addActionListener(actionListener);
		noiseEnableItem.setSelected(true);
		noiseEnableItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0));
		
		dmcEnableItem = new JCheckBoxMenuItem("DMC");
		audioMenu.add(dmcEnableItem);
		dmcEnableItem.setActionCommand("DMC");
		dmcEnableItem.addActionListener(actionListener);
		dmcEnableItem.setSelected(true);
		dmcEnableItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
		
		ButtonGroup audioButtonGroup = new ButtonGroup();
		eightBitAudio = new JRadioButtonMenuItem("8 Bit Audio");
		audioMenu.add(eightBitAudio);
		eightBitAudio.setActionCommand("8");
		eightBitAudio.addActionListener(actionListener);
		eightBitAudio.setSelected(true);;
		
		sixteenBitAudio = new JRadioButtonMenuItem("16 Bit Audio");
		audioMenu.add(sixteenBitAudio);
		sixteenBitAudio.addActionListener(actionListener);
		sixteenBitAudio.setActionCommand("16");
		sixteenBitAudio.setSelected(false);
		audioButtonGroup.add(eightBitAudio);
		audioButtonGroup.add(sixteenBitAudio);
		
		videoMenu = new JMenu("Video Options");
		
		VideoMenuActionListener videoListener = new VideoMenuActionListener();
		showBGItem = new JCheckBoxMenuItem("Render Playfield");
		showBGItem.addActionListener(videoListener);
		showBGItem.setActionCommand("BG");
		showBGItem.setSelected(true);
		videoMenu.add(showBGItem);
		
		showObjectsItem = new JCheckBoxMenuItem("Render Objects");
		showObjectsItem.addActionListener(videoListener);
		showObjectsItem.setActionCommand("OBJECTS");
		showObjectsItem.setSelected(true);
		videoMenu.add(showObjectsItem);
		
		add(videoMenu);
		
		add(audioMenu);
	}
	
	public void resetAllAudioChannels() {
		pulseOneEnableItem.setSelected(true);
		pulseTwoEnableItem.setSelected(true);
		triEnableItem.setSelected(true);
		noiseEnableItem.setSelected(true);
		dmcEnableItem.setSelected(true);
	}
	
	public boolean[] getAudioChannelStates() {
		return new boolean[] {
			pulseOneEnableItem.isSelected(),
			pulseTwoEnableItem.isSelected(),
			triEnableItem.isSelected(),
			noiseEnableItem.isSelected(),
			dmcEnableItem.isSelected()
		};
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
	
	private void enableAllAudioItems(boolean enable) {
		mainFrame.enableAudio(enable);
		pulseOneEnableItem.setEnabled(enable);
		pulseTwoEnableItem.setEnabled(enable);
		triEnableItem.setEnabled(enable);
		noiseEnableItem.setEnabled(enable);
		dmcEnableItem.setEnabled(enable);
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
			case "SILENCE":
				enableAllAudioItems(!silenceAudioItem.isSelected());
				break;
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
				
			case "8":
				
				mainFrame.updateAudioParams(8);
				
				break;
			case "16":
				
				mainFrame.updateAudioParams(16);
				
			}
			if(arg0.getSource() instanceof JCheckBoxMenuItem) {
				mainFrame.setAudioChannelEnable(audioChannel, ((JCheckBoxMenuItem) arg0.getSource()).isSelected());
			}
		}
		
	}
	
	private class VideoMenuActionListener implements ActionListener {
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			if(mainFrame == null) {
				mainFrame = getParentFrame();
			}
			String command = arg0.getActionCommand();
			switch(command) {
			case "BG":
				mainFrame.togglePPUDisplay(0, showBGItem.isSelected());
				break;
			case "OBJECTS":
				mainFrame.togglePPUDisplay(1, showObjectsItem.isSelected());
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
