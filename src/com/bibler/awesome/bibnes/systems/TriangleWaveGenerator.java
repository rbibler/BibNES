package com.bibler.awesome.bibnes.systems;

public class TriangleWaveGenerator extends WaveGenerator {
	
	private int linearCounter;
	private boolean lengthCounterDisable;
	private int linearCounterReloadValue;
	
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
			break;
		}
	}

}
