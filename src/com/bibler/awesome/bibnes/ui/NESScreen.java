package com.bibler.awesome.bibnes.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import com.bibler.awesome.bibnes.systems.NES;
import com.bibler.awesome.bibnes.utils.NESPalette;

public class NESScreen extends PopoutPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2850055628329603061L;
	BufferedImage screenImage;
	double scaleX;
	double scaleY;
	int screenshotCount;
	
	private NES nes;
	
	boolean flash; 
	
	public NESScreen(String title, int tabIndex, int width, int height) {
		super(title, tabIndex, width, height);
		screenImage = new  BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
		scaleX = 256.0 / width;
		scaleY = 240.0 / height;
		final Dimension screenDims = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		setMaximumSize(new Dimension( (screenDims.width / 256) * 256, (screenDims.height / 240) * 240));
	}
	
	public void screenshot() {
		DateFormat df = new SimpleDateFormat("MM-dd-yyyy");
		Date today = Calendar.getInstance().getTime();
		String date = df.format(today);
		String fileName = "C:/users/ryan/desktop/test/screens/" +
				date + "_" + screenshotCount++ + ".png";
		File f = new File(fileName);
		if(!f.getParentFile().exists()) {
			f.getParentFile().mkdirs();
		}
		try {
			flash = ImageIO.write(screenImage, "png", f);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(flash) {
			System.out.println("Screenshot took and saved to: " + fileName);
		} else {
			System.out.println("Error! No save");
		}
	}
	
	public void updateFrame(int[] frameArray) {
		for(int i = 0; i < frameArray.length; i++) {
			screenImage.setRGB(i % 256, i / 256, frameArray[i]);
		}
		repaint();
	}
	
	public void setBoard(NES nes) {
		this.nes = nes;
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
		g.setColor(Color.GREEN);
		if(nes != null) {
			g.drawString("FPS: " + nes.averageFrameRate, 200, 15);
		}
	}

}
