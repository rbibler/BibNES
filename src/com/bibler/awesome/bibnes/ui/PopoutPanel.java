package com.bibler.awesome.bibnes.ui;

import java.awt.Dimension;

import javax.swing.JPanel;

public class PopoutPanel extends JPanel {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6644003528726667940L;
	private int tabIndex;
	private String title;
	private Dimension preferredSize;
	private PopoutPaneHolder parent;
	private boolean poppedOut;
	
	public PopoutPanel(String title, int tabIndex, int width, int height) {
		this.title = title;
		this.tabIndex = tabIndex;
		preferredSize = new Dimension(width, height);
	}
	
	public void setPoppedStatus(boolean status) {
		poppedOut = status;
	}
	
	public void setParent(PopoutPaneHolder parent) {
		this.parent = parent;
	}
	
	public void setWidthHeight(int w, int h) {
		preferredSize = new Dimension(w, h);
	}
	
	public void setTabIndex(int tabIndex) {
		this.tabIndex = tabIndex;
	}
	
	public void popOut() {
		setPreferredSize(preferredSize);
		poppedOut = true;
	}
	
	public int getTabIndex() {
		return tabIndex;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void popBack() {
		poppedOut = false;
		parent.popChildBackIn(this);
	}
	
	public boolean getPoppedStatus() {
		return poppedOut;
	}
}
