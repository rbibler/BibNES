package com.bibler.awesome.bibnes.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import com.bibler.awesome.bibnes.systems.NES;
import com.bibler.awesome.bibnes.systems.PPU;
import com.bibler.awesome.bibnes.utils.NESPalette;

public class NametableScreen extends PopoutPanel implements Runnable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1283110918355588216L;
	private BufferedImage nt1;
	private BufferedImage nt2;
	private BufferedImage nt3;
	private BufferedImage nt4;
	
	private boolean running;
	
	private int[] ppuMem;
	private PPU ppu;
	private NES nes;
	
	public NametableScreen(String title, int tabIndex, int width, int height) {
		super(title, tabIndex, width, height);
		nt1 = new BufferedImage(256, 240, BufferedImage.TYPE_4BYTE_ABGR);
		nt2 = new BufferedImage(256, 240, BufferedImage.TYPE_4BYTE_ABGR);
		nt3 = new BufferedImage(256, 240, BufferedImage.TYPE_4BYTE_ABGR);
		nt4 = new BufferedImage(256, 240, BufferedImage.TYPE_4BYTE_ABGR);
		Thread t = new Thread(this);
		running = true;
		t.start();
	}
	
	public void setNES(NES nes) {
		this.nes =nes;
	}
	
	private void update() {
		if(ppuMem != null) {
			//updateScreen(0x2000, nt1);
			//updateScreen(0x2400, nt2);
			//updateScreen(0x2800, nt3);
			//updateScreen(0x2C00, nt4);
		}
		repaint();
	}
	
	public void setPPUMem(int[] ppuMem, PPU ppu) {
		this.ppuMem = ppuMem;
		this.ppu = ppu;
	}
	
	private void updateScreen(int ntAddress, BufferedImage img) {
		int pixel;
		int row;
		int col;
		int address;
		int lowBg = 0;
		int highBg = 0;
		int ntByte;
		int fineY;
		int x;
		int y;
		int attrX;
		int attrY;
		int curAttr;
		final int length = 256 * 240;
		for(int i = 0; i < length; i++) {
			x = i % 256;
			y = (i / 256);
			row = y / 8;
			col = x / 8;
			ntByte = nes.ppuRead(ntAddress + (row * 32) + col);
			curAttr = nes.ppuRead(ntAddress + 0x3C0 + (((y / 32) * 8) + (x / 32))) & 0xFF;
			row = (ntByte / 16);
			col = ntByte % 16;
			fineY = (y % 8);
			address = (1  << 0xC) | (row << 8) | (col << 4) | fineY & 7; 
			if(address >= 0) {
				lowBg = nes.ppuRead(address);
			}
			address = (1 << 0xC) | (row << 8) | (col << 4) | (1 << 3) | fineY & 7;
			if(address >= 0) {
				highBg = nes.ppuRead(address);
			}
			int attrStart = (((y / 32) * 32) * 256) + (((x / 32) * 32));
			attrX = (x / 32) * 4;
			attrY = (y / 32) * 4;
			int ntX = x / 8;
			int ntY = y / 8;
			attrStart = i - attrStart;
			int attrBitShift = (((ntX - attrX) / 2) * 2) + (((ntY - attrY) / 2) * 4);
			int palVal = ((curAttr >> attrBitShift) & 3) << 2;
			pixel = ((highBg >> (7 - (i % 8)) & 1) << 1 | (lowBg >> (7 -(i % 8)) & 1));
			img.setRGB(x, y, NESPalette.getPixel(nes.ppuRead(0x3F00 + (palVal + pixel))));
		}
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(nt1, 0, 0, null);
		g.drawImage(nt2, 256, 0, null);
		g.drawImage(nt3, 0, 240, null);
		g.drawImage(nt4, 256, 240, null);
		/*if(ppu != null) {
			g.setColor(Color.RED);
			final int x = ppu.currentXScroll;
			g.drawLine(x, 0, x, 480);
		}*/
	}

	@Override
	public void run() {
		while(running) {
			update();
			try {
				Thread.sleep(10);
			} catch(InterruptedException e) {}
		}
		
	}

}
