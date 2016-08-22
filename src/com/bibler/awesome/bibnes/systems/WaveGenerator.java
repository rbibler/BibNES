package com.bibler.awesome.bibnes.systems;

public class WaveGenerator {

	protected int timer;
	protected int lengthCounter;
	
	
	public void write(int register, int data) {};
	
	public int clock() { return 0; };
	
	public void setLengthCounterEnabled(boolean enabled) {};
	
	public void clockLengthCounter() {};
	
	public int getLengthCounter() {
		return lengthCounter;
	}
}
