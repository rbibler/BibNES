package com.bibler.awesome.bibnes.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;

import com.bibler.awesome.bibnes.systems.CPU;
import com.bibler.awesome.bibnes.systems.NES;

import tv.porst.jhexview.JHexView;
import tv.porst.jhexview.SimpleDataProvider;
import tv.porst.jhexview.JHexView.DefinitionStatus;

public class EmulatorPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3221558980728538118L;
	
	private EmulatorStatusPanel statusPanel;
	private JTabbedPane tabPane;
	private JHexView cpuPane;
	private JHexView ppuPane;
	private JHexView oamPane;
	private boolean running;
	
	public EmulatorPanel(int width, int height) {
		super();
		setPreferredSize(new Dimension(width, height));
		tabPane = new JTabbedPane();
		statusPanel = new EmulatorStatusPanel();
		tabPane.add("Status", statusPanel);
		cpuPane = new JHexView();
		tabPane.add("CPU Memory", cpuPane);
		ppuPane = new JHexView();
		tabPane.add("PPU Memory", ppuPane);
		oamPane = new JHexView();
		tabPane.add("OAM Memory", oamPane);
		setLayout(new BorderLayout());
		add(tabPane, BorderLayout.CENTER);
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				while(running) {
					update();
					try {
						Thread.sleep(10);
					} catch(InterruptedException e) {}
				}
				
			}
			
		});
		running = true;
		t.start();
		
	}
	
	private void update() {
		cpuPane.repaint();
		ppuPane.repaint();
		oamPane.repaint();
	}
	
	public void fillCPUMem(int[] cpuMem) {
		cpuPane.setData(new SimpleDataProvider(cpuMem));
		cpuPane.setDefinitionStatus(DefinitionStatus.DEFINED);
		cpuPane.setEnabled(true);
		cpuPane.setBytesPerColumn(1);
		cpuPane.repaint();
	}
	
	public void fillPPUMem(int[] ppuMem) {
		ppuPane.setData(new SimpleDataProvider(ppuMem));
		ppuPane.setDefinitionStatus(DefinitionStatus.DEFINED);
		ppuPane.setEnabled(true);
		ppuPane.setBytesPerColumn(1);
		ppuPane.repaint();
	}
	
	public void fillOAMMem(int[] oamMem) {
		oamPane.setData(new SimpleDataProvider(oamMem));
		oamPane.setDefinitionStatus(DefinitionStatus.DEFINED);
		oamPane.setEnabled(true);
		oamPane.setBytesPerColumn(1);
		oamPane.repaint();
	}

	public void setCPU(CPU cpu) {
		statusPanel.setCPU(cpu);
		
	}
	
	

}
