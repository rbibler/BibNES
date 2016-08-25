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
	private boolean envelopeStartFlag;
	private int currentTimer;
	private int currentStep;
	private int currentVolume;
	private int decayLevelCounter;
	private int envelopeDividerPeriod;
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
	
	public void clockEnvelope() {
		if(envelopeStartFlag) {
			envelopeStartFlag = false;
			decayLevelCounter = 15;
			envelopeDividerPeriod = envelope;
		} else {
			clockEnvelopeDivider();
		}
	}
	
	private void clockEnvelopeDivider() {
		if(envelopeDividerPeriod == 0) {
			envelopeDividerPeriod = envelope;
			clockEnvelopeDecayCounter();
		} else {
			envelopeDividerPeriod--;
		}
	}
	
	private void clockEnvelopeDecayCounter() {
		if(decayLevelCounter == 0) {
			if(lengthCounterHalt) {
				decayLevelCounter = 15;
			}
		} else {
			decayLevelCounter--;;
		}
	}
	
	public void clockSweepUnit() {
		
	}
	
	@Override
	public void write(int register, int data) {
		switch(register) {
		case 0:
			switch(data >> 6 & 3) {
			case 0:
				duty = 0b1000000;
				break;
			case 1:
				duty = 0b1100000;
				break;
			case 2:
				duty = 0b1111000;
				break;
			case 3:
				duty = 0b10011111;
				break;
			}
			lengthCounterHalt = (data >> 5 & 1) == 1;
			constantVolume = (data >> 4 & 1) == 1;
			envelope = data & 0b1111;
			if(constantVolume) {
				currentVolume = envelope;
			}
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
			lengthCounter = lengthCounterLookup[data >> 4 & 0xF][data >> 3 & 1];
			currentStep = 7;
			currentTimer = timer + 1;
			envelopeStartFlag = true;
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
			currentTimer = timer + 1;
			currentStep--;
			if(currentStep < 0) {
				currentStep = 7;
			}
		} else {
			currentTimer--;
		}
		return 0;
	}
	
	@Override
	public double getSample() {
		currentVolume = constantVolume ? envelope : decayLevelCounter;
		final int dutyLevel = (duty >> currentStep & 1);
		final double sample = (lengthCounter > 0 && timer >= 8) ? (currentVolume * dutyLevel) : 0;
		return sample;
	}

}
