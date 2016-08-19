package com.bibler.awesome.bibnes.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import com.bibler.awesome.bibnes.mappers.Mapper;
import com.bibler.awesome.bibnes.utils.NESPalette;

public class PatternTableScreen extends JPanel implements Runnable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3276564516789712403L;
	private Mapper mapper;
	private int[] ppuMem;
	private int squareSize;
	private int currentPaletteIndex;
	
	private BufferedImage left;
	private BufferedImage right;
	
	public PatternTableScreen(int width, int height) {
		setPreferredSize(new Dimension(width, height));
		squareSize = width / 2;
		left = new BufferedImage(128, 128, BufferedImage.TYPE_4BYTE_ABGR);
		right = new BufferedImage(128, 128, BufferedImage.TYPE_4BYTE_ABGR);
	}
	
	public void setMapper(Mapper mapper, int[] ppuMem) {
		this.mapper = mapper;
		this.ppuMem = ppuMem;
		startThread();
	}
	
	private void startThread() {
		Thread t = new Thread(this);
		t.start();
	}
	
	private void update() {
		int lowByteLeft;
		int highByteLeft;
		int lowByteRight;
		int highByteRight;
		int lowAddress;
		int highAddress;
		int pixel;
		int x;
		int y;
		int tileStart;
		for(int i = 0; i < 256; i++) {
			tileStart = i * 16;
			x = (i % 16) * 8;
			for(int k = 0; k < 8; k++) {
				lowAddress = tileStart + k;
				highAddress = lowAddress + 8;
				lowByteLeft = mapper.readChr(lowAddress);
				highByteLeft = mapper.readChr(highAddress);
				
				lowByteRight = mapper.readChr(lowAddress + 0x1000);
				highByteRight = mapper.readChr(highAddress + 0x1000);
				y = ((i / 16) * 8) + k;
				for(int j = 7; j >= 0; j--) {
					pixel = ((lowByteLeft >> j & 1) | ((highByteLeft >> j &  1) << 1)) | (currentPaletteIndex << 2);
					pixel = NESPalette.getPixel(ppuMem[0x3F00 + pixel]);
					left.setRGB(x + (7 - j), y, pixel);
					//Right table
					pixel = ((lowByteRight >> j & 1) | ((highByteRight >> j &  1) << 1)) | (currentPaletteIndex << 2);
					pixel = NESPalette.getPixel(ppuMem[0x3F00 + pixel]);
					right.setRGB(x + (7 - j), y, pixel);
				}
			}
		}
		repaint();
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(left, 0, 0, squareSize, squareSize, null);
		g.drawImage(right, squareSize, 0, squareSize, squareSize, null);
		g.drawRect(0, 0, squareSize, squareSize);
		g.drawRect(squareSize, 0, squareSize, squareSize);
	}

	@Override
	public void run() {
		while(true) {
			update();
			try {
				Thread.sleep(10);
			} catch(InterruptedException e) {}
		}
	}

}
