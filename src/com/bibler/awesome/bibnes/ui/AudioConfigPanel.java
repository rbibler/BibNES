package com.bibler.awesome.bibnes.ui;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.bibler.awesome.bibnes.systems.APU;
import com.bibler.awesome.bibnes.systems.WaveGenerator;

public class AudioConfigPanel extends JPanel {
	
	private AudioChannelControlPanel pulseOneControl;
	private AudioChannelControlPanel pulseTwoControl;
	private AudioChannelControlPanel triControl;
	private AudioChannelControlPanel noiseControl;
	private AudioChannelControlPanel dmcControl;
	private AudioChannelControlPanel master;
	private APU apu;
	JFrame frame;
	
	public AudioConfigPanel() {
		initializeView();
		
	}
	
	public void setAPU(APU apu) {
		this.apu = apu;
	}
	
	public void showPanel() {
		frame = new JFrame();
		frame.add(this);
		frame.pack();
		frame.setVisible(true);
	}
	
	private void initializeView() {
		
		pulseOneControl = new AudioChannelControlPanel("Pulse One", 0);
		pulseTwoControl = new AudioChannelControlPanel("Pulse Two", 1);
		triControl = new AudioChannelControlPanel("Triangle", 2);
		noiseControl = new AudioChannelControlPanel("Noise", 3);
		dmcControl = new AudioChannelControlPanel("DMC", 4);
		master = new AudioChannelControlPanel("Master", 5);
		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		add(pulseOneControl);
		add(pulseTwoControl);
		add(triControl);
		add(noiseControl);
		add(dmcControl);
		add(master);
	}
	
	protected void updateMaster(int value) {
		this.pulseOneControl.setSliderValue(value);
		this.pulseTwoControl.setSliderValue(value);
		this.triControl.setSliderValue(value);
		this.noiseControl.setSliderValue(value);
		this.dmcControl.setSliderValue(value);
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
		private int channelNumber;
		
		protected AudioChannelControlPanel(String name, int channelNumber) {
			super();
			this.channelNumber = channelNumber;
			initializeView(name);
		}
		
		private void initializeView(String name) {
			setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
			channelName = new JLabel(name);
			enableChannel = new JCheckBox();
			channelVolumeSlider = new JSlider(JSlider.VERTICAL);
			channelVolumeSlider.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(ChangeEvent arg0) {
					final int value = channelVolumeSlider.getValue();
					if(apu != null && channelNumber != 5) {
						apu.updateChannelVolume(channelNumber, (float) (value / (float) channelVolumeSlider.getMaximum()));
					} else if(channelNumber == 5) {
						updateMaster(channelVolumeSlider.getValue());
					}
				}
				
			});
			channelEQView = new JProgressBar();
			add(channelName);
			add(enableChannel);
			add(channelVolumeSlider);
		}
		
		protected void setSliderValue(int value) {
			channelVolumeSlider.setValue(value);
		}
	}
}
