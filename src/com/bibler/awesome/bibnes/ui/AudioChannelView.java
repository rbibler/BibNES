package com.bibler.awesome.bibnes.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.JPanel;



public class AudioChannelView extends JPanel {
	
	private byte[] frame;
	
	
	public AudioChannelView() {
		super();
		setPreferredSize(new Dimension(512, 250));
		setBorder(BorderFactory.createLineBorder(Color.RED));
		setBackground(Color.BLACK);
	}
	
	public void setFrame(byte[] frame) {
		this.frame = frame;
	}
	
	public void updateView() {
		revalidate();
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
		
		for(int i = 0; i < frame.length - (xSkip + 1); i += xSkip) {
			y1 = frame[i];
			y2 = frame[i + xSkip];
			g.drawLine(x, y1, x + 1, y2);
			x++;
			System.out.println("y1: " + y1 + " y2: " + y2);
		}
	}

}
