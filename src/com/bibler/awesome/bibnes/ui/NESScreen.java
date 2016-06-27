package com.bibler.awesome.bibnes.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class NESScreen extends JPanel {
	
	BufferedImage screenImage;
	private int width;
	private int height;
	double scaleX;
	double scaleY;
	
	public NESScreen(int width, int height) {
		super();
		this.width = width;
		this.height = height;
		setPreferredSize(new Dimension(width, height));
		screenImage = new  BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
		scaleX = 256.0 / width;
		scaleY = 240.0 / height;
	}
	
	public void updateFrame(int[] frameArray) {
		for(int i = 0; i < frameArray.length; i++) {
			screenImage.setRGB(i % 256, i / 256, frameArray[i]);
		}
		repaint();
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if(screenImage == null) {
			return;
		}
		int w = this.getWidth() / 256;
		int h = this.getHeight() / 240;
		int ratio = Math.min(w, h);
		g.drawImage(screenImage, 0, 0, ratio * 256, ratio * 240, null);
	}

}
