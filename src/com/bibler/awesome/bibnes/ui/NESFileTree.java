package com.bibler.awesome.bibnes.ui;

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileFilter;

import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

public class NESFileTree extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1343860277540846570L;
	private JTree fileTree;
	private DefaultTreeModel treeModel;
	private DefaultMutableTreeNode rootNode;
	private MainFrame mainFrame;
	
	public NESFileTree(File root, int width, int height) {
		super();
		setPreferredSize(new Dimension(width, height));
		rootNode = new DefaultMutableTreeNode(new NESFileTreeNode(root));
		treeModel = new DefaultTreeModel(rootNode);
		fileTree = new JTree(treeModel);
		fileTree.setShowsRootHandles(true);
		setupChildren(root, rootNode);
		fileTree.expandPath(new TreePath(rootNode.getPath()));
		fileTree.addMouseListener(new NESTreeMouseListener());
		add(fileTree);
		
	}
	
	public void setMainFrame(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
	}
	
	
	private boolean isNESFile(File f) {
		final String name = f.getName().toLowerCase();
		final int length = name.length();
		if(length > 4) {
			return name.substring(length - 4, length).contains("nes");
		} else {
			return false;
		}
	}
	
	private void setupChildren(File rootFile, DefaultMutableTreeNode rootNode) {
		File[] files = rootFile.listFiles(new FileFilter() {

			@Override
			public boolean accept(File arg0) {
				return arg0.isDirectory() || isNESFile(arg0);
			}
			
		});
		if(files == null) {
			return;
		}
		for(File file : files) {
			DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(new NESFileTreeNode(file));
			rootNode.add(childNode);
			if(file.isDirectory()) {
				setupChildren(file, childNode);
			}
		}
	}
	
	private void openFileOnDoubleClick() {
		DefaultMutableTreeNode selection = (DefaultMutableTreeNode) fileTree.getLastSelectedPathComponent();
		NESFileTreeNode node = (NESFileTreeNode) selection.getUserObject();
		mainFrame.runNESFile(node.open());
	}
	
	private class NESTreeMouseListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent arg0) {
			if(arg0.getClickCount() == 2) {
				openFileOnDoubleClick();
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
