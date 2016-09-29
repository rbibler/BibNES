package com.bibler.awesome.bibnes.ui;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSlider;

import com.bibler.awesome.bibnes.systems.WaveGenerator;

public class AudioConfigPanel extends JPanel {
	
	AudioChannelControlPanel pulseOneControl;
	AudioChannelControlPanel pulseTwoControl;
	AudioChannelControlPanel triControl;
	AudioChannelControlPanel noiseControl;
	AudioChannelControlPanel dmcControl;
	JFrame frame;
	
	public AudioConfigPanel() {
		initializeView();
		
	}
	
	public void showPanel() {
		frame = new JFrame();
		frame.add(this);
		frame.pack();
		frame.setVisible(true);
	}
	
	private void initializeView() {
		pulseOneControl = new AudioChannelControlPanel("Pulse One");
		pulseTwoControl = new AudioChannelControlPanel("Pulse Two");
		triControl = new AudioChannelControlPanel("Triangle");
		noiseControl = new AudioChannelControlPanel("Noise");
		dmcControl = new AudioChannelControlPanel("DMC");
		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		add(pulseOneControl);
		add(pulseTwoControl);
		add(triControl);
		add(noiseControl);
		add(dmcControl);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -8024947255755838546L;

	private class AudioChannelControlPanel extends JPanel {
		
		JLabel channelName;
		JCheckBox enableChannel;
		JSlider channelVolumeSlider;
		JProgressBar channelEQView;
		WaveGenerator channelGenerator;
		
		protected AudioChannelControlPanel(String name) {
			super();
			initializeView(name);
		}
		
		private void initializeView(String name) {
			setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
			channelName = new JLabel(name);
			enableChannel = new JCheckBox();
			channelVolumeSlider = new JSlider(JSlider.VERTICAL);
			channelEQView = new JProgressBar();
			add(channelName);
			add(enableChannel);
			add(channelVolumeSlider);
		}
	}
}
