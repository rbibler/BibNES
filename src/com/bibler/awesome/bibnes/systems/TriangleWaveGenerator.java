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
	
	private int[][] lengthCounterLookup = new int[][] {
		{0x0A, 0xFE},
		{0x14, 0x02},
		{0x28, 0x04},
		{0x50, 0x06},
		{0xA0, 0x08},
		{0x3C, 0x0A},
		{0x0E, 0x0C},
		{0x1A, 0x0E},
		{0x0C, 0x10},
		{0x18, 0x12},
		{0x30, 0x14},
		{0x60, 0x16},
		{0xC0, 0x18},
		{0x48, 0x1A},
		{0x10, 0x1C},
		{0x20, 0x1E},	
	};
	
	private boolean linearCounterReloadFlag;
	private boolean lengthCounterEnabled;
	
	@Override
	public void reset() {
		linearCounterReloadFlag = false;
		lengthCounterEnabled = false;
		linearCounter = 0;
		linearCounterReloadValue = 0;
		lengthCounterDisable = false;
		lengthCounterEnabled = false;
		lengthCounter = 0;
		currentTimer = 0;
		timer = 0;
		currentStep = 0;
		currentVolume = 0;
	}
	
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
			//lengthCounter = data >> 3 & 0b11111;
			lengthCounter = lengthCounterLookup[data >> 4 & 0xF][data >> 3 & 1];
			linearCounterReloadFlag = true;
			break;
		}
	}
	
	@Override
	public int getSample() {
		currentVolume =  sequence[currentStep];
		if(timer < 2) {
			currentVolume = 0;
		}
		return currentVolume;
	}
	
}
