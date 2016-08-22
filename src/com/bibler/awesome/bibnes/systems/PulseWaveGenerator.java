package com.bibler.awesome.bibnes.systems;

public class PulseWaveGenerator extends WaveGenerator {
	
	private int envelope = 7;
	private boolean sweepEnabled;
	private int sweepPeriod;
	private int sweepNegate;
	private int sweepShift;
	private int duty;
	private int channelVolume;
	private boolean lengthCounterHalt;
	private boolean constantVolume;
	private boolean lengthCounterEnabled;
	private int currentTimer;
	private int currentStep;
	
	public void clockEnvelope() {
		
	}
	
	public void clockSweepUnit() {
		
	}
	
	@Override
	public void write(int register, int data) {
		//if(data > 0) {
			//System.out.println("Wrote to pulse register " + register + " data " + Integer.toBinaryString(data));
		//}
		switch(register) {
		case 0:
			switch(data >> 6 & 3) {
			case 0:
				duty = 0b01000000;
				break;
			case 1:
				duty = 0b01100000;
				break;
			case 2:
				duty = 0b01111000;
				break;
			case 3:
				duty = 0b10011111;
				break;
			}
			lengthCounterHalt = (data >> 5 & 1) == 1;
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
			currentStep = 7;
			currentTimer = timer;
			break;
		}
	}
	
	@Override
	public void setLengthCounterEnabled(boolean enabled) {
		lengthCounterEnabled = enabled;
		if(!enabled) {
			lengthCounter = 0;
		}
	}
	
	@Override
	public void clockLengthCounter() {
		if(lengthCounterEnabled) {
			if(lengthCounter > 0 && !lengthCounterHalt) {
				lengthCounter--;
			}
		}
	}
	
	@Override
	public int clock() {
		if(currentTimer == 0) {
			currentTimer = timer;
			currentStep--;
			if(currentStep < 0) {
				currentStep = 7;
			}
		} else {
			currentTimer--;
		}
		return lengthCounter > 0 ? (7 * ((duty >> currentStep) & 1)) : 0;
	}

}
