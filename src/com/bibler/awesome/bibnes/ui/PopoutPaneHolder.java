package com.bibler.awesome.bibnes.ui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JTabbedPane;

public class PopoutPaneHolder extends JTabbedPane {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3967169832107352165L;
	private PopoutPanel[] panels;
	
	public PopoutPaneHolder(int size) {
		super();
		panels = new PopoutPanel[size];
		addMouseListener(new PopoutPanelClickListener());
	}
	
	public void addPopoutPanel(PopoutPanel panel) {
		panels[panel.getTabIndex()] = panel;
		if(panel.getTabIndex() < getTabCount()) {
			insertTab(panel.getTitle(), null, panel,null, panel.getTabIndex());
		} else {
			add(panel.getTitle(), panel);
		}
	}
	
	public void orderPanels() {
		
	}
	
	private void popOutPanel() {
		final PopoutPanel p = (PopoutPanel) this.getComponentAt(getSelectedIndex());
		final PopoutFrame frame = new PopoutFrame(p);
		frame.setVisible(true);
	}
	
	private void clearAll() {
		while(getTabCount() > 0) {
			remove(0);
		}
	}
	
	public void popChildBackIn(PopoutPanel p) {
		clearAll();
		int selectedIndex = 0;
		for(PopoutPanel panel : panels) {
			if(panel == null || panel.getPoppedStatus()) {
				continue;
			}
			add(panel.getTitle(), panel);
			if(panel == p) {
				selectedIndex = getTabCount() - 1;
			}
		}
		setSelectedIndex(selectedIndex);
	}
	
	private class PopoutPanelClickListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent arg0) {
			if(arg0.getClickCount() == 2) {
				popOutPanel();
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
