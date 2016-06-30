package com.bibler.awesome.bibnes.ui;

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

public class NESScreen extends JPanel {
	
	BufferedImage screenImage;
	private int width;
	private int height;
	double scaleX;
	double scaleY;
	int screenshotCount;
	
	boolean flash; 
	
	public NESScreen(int width, int height) {
		super();
		this.width = width;
		this.height = height;
		setPreferredSize(new Dimension(width, height));
		screenImage = new  BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
		scaleX = 256.0 / width;
		scaleY = 240.0 / height;
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
