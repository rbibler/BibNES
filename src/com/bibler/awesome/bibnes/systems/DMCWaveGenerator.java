package com.bibler.awesome.bibnes.systems;

public class DMCWaveGenerator extends WaveGenerator {
	
	private boolean IRQEnable;
	private boolean loopSample;
	private int frequencyIndex;
	private int directLoad;
	private int sampleAddress;
	private int sampleLength;
	
	@Override
	public void write(int register, int data) {
		switch(register) {
		case 0x10:
			IRQEnable = (data >> 7 & 1) == 1;
			loopSample = (data >> 6 & 1) == 1;
			frequencyIndex = data & 0xF;
			break;
		case 0x11:
			directLoad = data & 0x7F;
			break;
		case 0x12:
			sampleAddress = 0b11 | (data << 6);
			break;
		case 0x13:
			sampleLength = 1 | (data << 4);
			break;
		}
	}

}
