package com.bibler.awesome.bibnes.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import com.bibler.awesome.bibnes.mappers.Mapper;
import com.bibler.awesome.bibnes.utils.NESPalette;

public class PaletteTable extends JPanel implements Runnable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7204456981765739651L;

	private int[] ppuMem;
	private PalettePanel[] paletteSquares = new PalettePanel[32];
	private int squareSize;
	private PatternTableScreen patternTableScreen;
	
	public PaletteTable(int width, int height) {
		setPreferredSize(new Dimension(width, height));
		squareSize = width / 16;
		initializeSquares();
		
		invalidate();
	}
	
	private void initializeSquares() {
		setLayout(new GridLayout(2, 16));
		PalettePanel panel;
		for(int i = 0; i < paletteSquares.length; i++) {
			panel = new PalettePanel(squareSize, 0x22, i / 4);
			paletteSquares[i] = panel;
			add(panel);
		}
	}
	
	public void setPPUMem(int[] ppuMem) {
		this.ppuMem = ppuMem;
		Thread t = new Thread(this);
		t.start();
	}
	
	public void setPatternTableScreen(PatternTableScreen patternTableScreen) {
		this.patternTableScreen = patternTableScreen;
	}
	
	private void update() {
		PalettePanel panel;
		for(int i = 0; i < paletteSquares.length; i++) {
			panel = paletteSquares[i];
			panel.updatePaletteColor(ppuMem[0x3F00 + i]);
		}
	}
	
	@Override
	public void paintComponent(Graphics g) {
		if(ppuMem == null) {
			return;
		}
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
	
	private class PalettePanel extends JPanel {
		
		private JPanel paletteSquare;
		private int colorIndex;
		private int paletteIndex;
		private JLabel colorLabel;
		
		
		public PalettePanel(int size, int colorIndex, int paletteIndex) {
			this.paletteIndex = paletteIndex;
			paletteSquare = new JPanel();
			colorLabel = new JLabel();
			colorLabel.setPreferredSize(new Dimension(32, 20));
			updatePaletteSquareSize(size);
			updatePaletteColor(colorIndex);
			setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
			add(paletteSquare);
			add(new JSeparator(JSeparator.HORIZONTAL));
			add(colorLabel);
			setBorder(BorderFactory.createLineBorder(Color.BLACK));
			addMouseListener(new PaletteUpdateListener());
		}
		
		public void updatePaletteSquareSize(int size) {
			paletteSquare.setPreferredSize(new Dimension(size, size));
			invalidate();
		}
		
		public void updatePaletteColor(int colorIndex) {
			if(this.colorIndex == colorIndex) {
				return;
			}
			this.colorIndex = colorIndex;
			colorLabel.setText(Integer.toHexString(colorIndex).toUpperCase());
			paletteSquare.setBackground(NESPalette.getPixelColor(colorIndex));
		}
		
		public void handleMouseClick(MouseEvent arg0) {
			if(arg0.getClickCount() < 2) {
				patternTableScreen.updateCurrentPaletteIndex(paletteIndex);
			}
		}
	}
	
	private class PaletteUpdateListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent arg0) {
			if(arg0.getSource() instanceof PalettePanel) {
				((PalettePanel) arg0.getSource()).handleMouseClick(arg0);
			}
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {}

		@Override
		public void mouseExited(MouseEvent arg0) {}

		@Override
		public void mousePressed(MouseEvent arg0) {}

		@Override
		public void mouseReleased(MouseEvent arg0) {}
		
	}

}
