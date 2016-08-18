package com.bibler.awesome.bibnes.ui;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

import com.bibler.awesome.bibnes.mappers.Mapper;
import com.bibler.awesome.bibnes.utils.NESPalette;

public class PaletteTable extends JPanel implements Runnable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7204456981765739651L;

	private int[] ppuMem;
	
	private int squareSize;
	
	public PaletteTable(int width, int height) {
		setPreferredSize(new Dimension(width, height));
		squareSize = width / 16;
	}
	
	public void setPPUMem(int[] ppuMem) {
		this.ppuMem = ppuMem;
		Thread t = new Thread(this);
		t.start();
	}
	
	private void update() {
		//repaint();
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		for(int i = 0; i < 16; i++) {
			g.setColor(NESPalette.getPixelColor(ppuMem[0x3F00 + i]));
			g.fillRect(i * squareSize, 0, squareSize, squareSize);
			g.setColor(NESPalette.getPixelColor(ppuMem[0x3F10 + i]));
			g.fillRect(i * squareSize, squareSize, squareSize, squareSize);
		}
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
