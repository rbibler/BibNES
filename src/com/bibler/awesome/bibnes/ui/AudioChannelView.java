package com.bibler.awesome.bibnes.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.JPanel;



public class AudioChannelView extends JPanel implements Runnable {
	
	private byte[] frame;
	private boolean paintOnNext;
	
	
	public AudioChannelView() {
		super();
		setPreferredSize(new Dimension(512, 250));
		setBorder(BorderFactory.createLineBorder(Color.RED));
		setBackground(Color.BLACK);
		Thread t = new Thread(this);
		t.start();
	}
	
	public void setFrame(byte[] frame) {
		this.frame = frame;
	}
	
	public void updateView() {
		paintOnNext = true;
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if(frame == null) {
			return;
		}
		g.setColor(Color.GREEN);
		int y1;
		int y2;
		int x = 0;
		int xSkip = frame.length / 512;
		int yStart = (int) (150 * .75f);
		for(int i = 0; i < frame.length - (xSkip + 1); i += xSkip) {
			y1 = Math.abs(frame[i]);
			y2 = Math.abs(frame[i + xSkip]);
			g.drawLine(x, (yStart - y1), x + 1, (yStart - y2));
			x++;
		}
	}

	@Override
	public void run() {
		while(!Thread.interrupted()) {
			if(paintOnNext) {
				repaint();
				paintOnNext = false;
			}
			try {
				Thread.sleep(10);
			} catch(InterruptedException e) {}
		}
		
	}

}
