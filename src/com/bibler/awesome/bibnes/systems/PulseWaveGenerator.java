package com.bibler.awesome.bibnes.systems;

public class PulseWaveGenerator extends WaveGenerator {
	
	private int envelope;
	private boolean sweepEnabled;
	private int sweepPeriod;
	private int sweepNegate;
	private int sweepShift;
	private int duty;
	private int channelVolume;
	private boolean envelopeLoop;
	private boolean constantVolume;
	
	@Override
	public void write(int register, int data) {
		switch(register) {
		case 0:
			duty = data >> 6 & 3;
			envelopeLoop = (data >> 5 & 1) == 1;
			constantVolume = (data >> 4 & 1) == 1;
			envelope = data & 0b1111;
			break;
		case 1:
			sweepEnabled = (data >> 7 & 1) == 1;
			sweepPeriod = (data >> 4) & 7;
			sweepNegate = data >> 3 & 1;
			sweepShift = data & 7;
			break;
		case 2:
			timer &= ~0xFF;
			timer |= data;
			break;
		case 3:
			timer &= ~(7 << 8);
			timer |= (data & 7) << 8;
			lengthCounter = data >> 3 & 0b11111;
			break;
		}
	}

}
