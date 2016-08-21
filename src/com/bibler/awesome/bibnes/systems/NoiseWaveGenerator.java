package com.bibler.awesome.bibnes.systems;

public class NoiseWaveGenerator extends WaveGenerator {
	
	private int envelope;
	private int linearFeedback;
	private boolean loopEnable;
	private boolean constantVolume;
	private boolean loopNoise;
	private int noisePeriod;
	
	@Override
	public void write(int register, int data) {
		switch(register) {
		case 0x0C:
			loopEnable = (data >> 5 & 1) == 1;
			constantVolume = (data >> 4 & 1) == 1;
			envelope = data & 0b1111;
			break;
		case 0x0E:
			loopNoise = (data >> 7 & 1) == 1;
			noisePeriod = data & 0b1111;
			break;
		case 0x0F:
			lengthCounter = (data >> 3) & 0b11111;
			break;
		}
	}

}
