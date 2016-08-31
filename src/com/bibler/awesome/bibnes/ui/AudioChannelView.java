package com.bibler.awesome.bibnes.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import com.bibler.awesome.bibnes.systems.APU;



public class AudioChannelView extends JPanel implements Runnable {
	
	private int[] pulseOneSamples;
	private int[] pulseTwoSamples;
	private int[] triSamples;
	private int[] noiseSamples;
	private int[] dmcSamples;
	private boolean paintOnNext;
	private int xSkip;
	private int samplesPerFrame;
	
	private JPanel mainPanel;
	private JPanel bufferUsePanel;
	private JProgressBar bufferUsageBar;
	private APU apu;
	
	public AudioChannelView() {
		super();
		mainPanel = new JPanel() {
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				paintGraph(g);
			}
		};
		bufferUsePanel = new JPanel();
		bufferUsageBar = new JProgressBar();
		bufferUsageBar.setStringPainted(true);
		bufferUsageBar.setPreferredSize(new Dimension(512, 40));
		bufferUsePanel.add(bufferUsageBar);
		mainPanel.setPreferredSize(new Dimension(512, 250));
		mainPanel.setBorder(BorderFactory.createLineBorder(Color.RED));
		mainPanel.setBackground(Color.BLACK);
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		add(mainPanel);
		add(bufferUsePanel);
		Thread t = new Thread(this);
		t.start();
	}
	
	public void setFrame(APU apu) {
		int[][] sampleBuffers = apu.getSampleBuffers();
		pulseOneSamples = sampleBuffers[0];
		pulseTwoSamples = sampleBuffers[1];
		triSamples = sampleBuffers[2];
		noiseSamples = sampleBuffers[3];
		dmcSamples = sampleBuffers[4];
		samplesPerFrame = pulseOneSamples.length;
		xSkip = samplesPerFrame / 512;
		this.apu = apu;
	}
	
	public void updateView() {
		paintOnNext = true;
	}
	
	private void updateBufferView() {
		if(apu == null) {
			return;
		}
		float bufferUsage = apu.getBufferUsage();
		bufferUsageBar.setValue((int) (bufferUsage * 100));
	}
	
	private void paintGraph(Graphics g) {
		if(pulseOneSamples == null) {
			return;
		}
		g.setColor(Color.GREEN);
		int y1;
		int y2;
		int x = 0;
		int yStart = (int) (150 * .75f);
		for(int i = 0; i < samplesPerFrame - (xSkip + 1); i += xSkip) {
			g.setColor(Color.GREEN);
			y1 = Math.abs(pulseOneSamples[i]);
			y2 = Math.abs(pulseOneSamples[i + xSkip]);
			g.drawLine(x, (yStart - y1), x + 1, (yStart - y2));
			g.setColor(Color.BLUE);
			y1 = Math.abs(pulseTwoSamples[i]);
			y2 = Math.abs(pulseTwoSamples[i + xSkip]);
			g.drawLine(x, (yStart - y1), x + 1, (yStart - y2));
			g.setColor(Color.YELLOW);
			y1 = Math.abs(triSamples[i]);
			y2 = Math.abs(triSamples[i + xSkip]);
			g.drawLine(x, (yStart - y1), x + 1, (yStart - y2));
			g.setColor(Color.RED);
			y1 = Math.abs(noiseSamples[i]);
			y2 = Math.abs(noiseSamples[i + xSkip]);
			g.drawLine(x, (yStart - y1), x + 1, (yStart - y2));
			g.setColor(Color.MAGENTA);
			y1 = Math.abs(dmcSamples[i]);
			y2 = Math.abs(dmcSamples[i + xSkip]);
			g.drawLine(x, (yStart - y1), x + 1, (yStart - y2));
			
			x++;
		}
	}

	

	@Override
	public void run() {
		while(!Thread.interrupted()) {
			if(paintOnNext) {
				paintOnNext = false;
				repaint();
			}
			updateBufferView();
			try {
				Thread.sleep(10);
			} catch(InterruptedException e) {}
		}
		
	}

}
