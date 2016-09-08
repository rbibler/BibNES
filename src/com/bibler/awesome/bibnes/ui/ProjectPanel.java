package com.bibler.awesome.bibnes.ui;

import java.awt.Dimension;
import java.io.File;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class ProjectPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6573189874395574557L;
	
	private JScrollPane scrollPane;
	private NESFileTree fileTree;
	
	public ProjectPanel(int width, int height) {
		super();
		setPreferredSize(new Dimension(width, height));
		fileTree = new NESFileTree(new File("C:/users/rbibl/documents/repos/bibnes/nesfiles"), width, height);
		scrollPane = new JScrollPane(fileTree);
		scrollPane.setPreferredSize(new Dimension(width, height));
		add(scrollPane);
		
	}
	
	public void setMainFrame(MainFrame mainFrame) {
		fileTree.setMainFrame(mainFrame);
	}

}
