package com.bibler.awesome.bibnes.systems;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;

public class Controller extends Peripheral implements KeyListener {
	
	private int buttonsByte;
	private int latch;
	private int output;
	
	public final static int UP = 0x01;
	public final static int DOWN = 0x02;
	public final static int LEFT = 0x03;
	public final static int RIGHT = 0x04;
	public final static int SELECT = 0x05;
	public final static int START = 0x06;
	public final static int A = 0x07;
	public final static int B = 0x08;
	
	private HashMap<Integer, Integer> keyMap = new HashMap<Integer, Integer>();
	
	public Controller() {
		fillKeyMap();
	}
	
	private void fillKeyMap() {
		keyMap.put(KeyEvent.VK_A, 1);
		keyMap.put(KeyEvent.VK_S, 1 << 1);
		keyMap.put(KeyEvent.VK_SPACE, 1 << 2);
		keyMap.put(KeyEvent.VK_ENTER, 1 << 3);
		keyMap.put(KeyEvent.VK_UP, 1 << 4);
		keyMap.put(KeyEvent.VK_DOWN, 1 << 5);
		keyMap.put(KeyEvent.VK_LEFT, 1 << 6);
		keyMap.put(KeyEvent.VK_RIGHT,  1 << 7);
	}
	
	@Override
	public synchronized int read() {
		strobe();
		return output;
	}
	
	@Override
	public synchronized void write() {
		latch = buttonsByte;
	}
	
	public synchronized void strobe() {
		 output = latch & 1;
	     latch = ((latch >> 1) | 0x100);
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		int keyEvent = arg0.getKeyCode();
		if(!keyMap.containsKey(keyEvent)) {
			return;
		}
		buttonsByte |= keyMap.get(keyEvent);
		System.out.println("buttonsByte: " + buttonsByte);
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		int keyEvent = arg0.getKeyCode();
		if(!keyMap.containsKey(keyEvent)) {
			return;
		}
		buttonsByte &= ~keyMap.get(keyEvent);
	}

	@Override
	public void keyTyped(KeyEvent arg0) {}

}
