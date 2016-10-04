package com.bibler.awesome.bibnes.systems;

public class WaveGenerator {

	protected int timer;
	protected int lengthCounter;
	protected int lastSample;
	protected int totalSamples;
	protected int sampleCountSinceLastAverage;
	
	
	public void write(int register, int data) {};
	
	public int clock() { return 0; };
	
	public void setLengthCounterEnabled(boolean enabled) {};
	
	public void clockLengthCounter() {};
	
	public int getLengthCounter() {
		return lengthCounter;
	}
	
	public int getSample() {
		return 0;
	}
	
	public int getLastSample() {
		final int avgSamples = (int) (totalSamples / (float) sampleCountSinceLastAverage);
		sampleCountSinceLastAverage = 0;
		totalSamples = 0;
		return avgSamples;
	}
	
	public int getSamples(byte[] samples) {
		return 0;
	}
	
	public void reset() {};
}
