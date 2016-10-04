package com.bibler.awesome.bibnes.ui;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.BorderFactory;
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
		initializeThread();
	}
	
	public void setAPU(APU apu) {
		this.apu = apu;
		pulseOneControl.setChannel(apu.getChannel(0));
		pulseTwoControl.setChannel(apu.getChannel(1));
		triControl.setChannel(apu.getChannel(2));
		noiseControl.setChannel(apu.getChannel(3));
		dmcControl.setChannel(apu.getChannel(4));
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
	
	private void initializeThread() {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				while(!Thread.interrupted()) {
					pulseOneControl.updateEQ();
					pulseTwoControl.updateEQ();
					triControl.updateEQ();
					noiseControl.updateEQ();
					dmcControl.updateEQ();
					try {
						Thread.sleep(30);
					} catch(InterruptedException e) {}
				}
			}
		});
		t.start();
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
		
		/**
		 * 
		 */
		private static final long serialVersionUID = -1810956641147127719L;
		JLabel channelName;
		JCheckBox enableChannel;
		JSlider channelVolumeSlider;
		JPanel sliderPanel;
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
			sliderPanel = new JPanel() {
				/**
				 * 
				 */
				private static final long serialVersionUID = -2884877153076768492L;

				@Override
				public void paintComponent(Graphics g) {
					super.paintComponent(g);
					if(channelGenerator != null) {
						paintSlider(g);
					}
					
				}
			};
			sliderPanel.add(channelVolumeSlider);
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
			add(sliderPanel);
		}
		
		protected void setSliderValue(int value) {
			channelVolumeSlider.setValue(value);
		}
		
		protected void setChannel(WaveGenerator channelGenerator) {
			this.channelGenerator = channelGenerator;
		} 
		
		private void paintSlider(Graphics g) {
			g.setColor(Color.BLUE);
			final int vol = channelGenerator.getLastSample();
			final int value = (int) ((vol / (float) 16) * channelVolumeSlider.getHeight());
			g.fillRect(0, 0, channelVolumeSlider.getWidth(), value);
		}
		
		protected void updateEQ() {
			sliderPanel.repaint();
		}
	}
}
