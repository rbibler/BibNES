package com.bibler.awesome.bibnes.systems;

public class TriangleWaveGenerator extends WaveGenerator {
	
	private int linearCounter;
	private boolean lengthCounterDisable;
	private int linearCounterReloadValue;
	
	private int currentTimer;
	private int currentStep;
	private int currentVolume;
	private int[] sequence = new int[] {
		15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0,
		0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15
	};
	
	private boolean linearCounterReloadFlag;
	private boolean lengthCounterEnabled;
	
	public void clockLinearCounter() {
		if(linearCounterReloadFlag) {
			linearCounter = linearCounterReloadValue;
		} else {
			linearCounter--;
		}
		if(!lengthCounterDisable) {
			linearCounterReloadFlag = false;
		}
	}
	
	@Override
	public void clockLengthCounter() {
		if(lengthCounterEnabled) {
			if(lengthCounter > 0 && !lengthCounterDisable) {
				lengthCounter--;
			}
		}
	}
	
	@Override
	public int clock() {
		if(currentTimer == 0) {
			currentTimer = timer;
			if(linearCounter > 0 && lengthCounter > 0) {
				currentStep--;
				if(currentStep < 0) {
					currentStep = sequence.length - 1;
				}
			}
		} else {
			currentTimer--;
		}
		return 0;
	}
	
	@Override
	public void setLengthCounterEnabled(boolean enabled) {
		lengthCounterEnabled = enabled;
		if(!enabled) {
			lengthCounter = 0;
		}
	}
	
	@Override
	public void write(int register, int data) {
		switch(register) {
		case 0x08:
			linearCounterReloadValue = data & 0b1111111;
			lengthCounterDisable = (data >> 7 & 1) == 1;
			break;
		case 0x0A:
			timer &= ~(0xFF);
			timer |= data;
			break;
		case 0x0B:
			timer &= ~(0b11100000000);
			timer |= (data & 7) << 8;
			lengthCounter = data >> 3 & 0b11111;
			linearCounterReloadFlag = true;
			break;
		}
	}
	
	@Override
	public double getSample() {
		currentVolume =  sequence[currentStep];
		return currentVolume;
	}
	
}
