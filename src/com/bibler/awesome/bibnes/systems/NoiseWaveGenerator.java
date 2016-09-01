package com.bibler.awesome.bibnes.systems;

public class NoiseWaveGenerator extends WaveGenerator {
	
	private int envelope;
	private int linearFeedback = 1;
	private boolean envelopeLoopFlag;
	private boolean constantVolume;
	private boolean loopNoise;
	private int currentTimer;
	private int decayLevelCounter;
	
	private boolean modeFlag;
	
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
	
	private int[] periodLookup = new int[] {
			4, 8, 16, 32, 64, 96, 128, 160, 202, 254, 380, 508, 762, 1016, 2034, 4068
	};
	private boolean lengthCounterEnabled;
	private boolean lengthCounterHaltFlag;
	private boolean envelopeStartFlag;
	private int envelopeDividerPeriod;
	
	@Override
	public void reset() {
		envelope = 0;
		linearFeedback = 1;
		envelopeLoopFlag = false;
		lengthCounterHaltFlag = false;
		constantVolume = false;
		loopNoise = false;
		currentTimer = 0;
		decayLevelCounter = 0;
		timer = 0;
		modeFlag = false;
		lengthCounterEnabled = false;
		lengthCounter = 0;
		envelopeDividerPeriod = 0;
		envelopeStartFlag = false;
	}
	
	@Override
	public int clock() {
		if(!lengthCounterEnabled) {
			lengthCounter = 0;
		}
		if(currentTimer == 0) {
			currentTimer = timer;
			final int feedback = (linearFeedback & 1) ^ ((modeFlag ? (linearFeedback >> 6 & 1) : (linearFeedback >> 1 & 1)));
			linearFeedback >>= 1;
			linearFeedback |= feedback << 14;
		} else {
			currentTimer--;
		}
		return 0;
	}
	
	@Override
	public void write(int register, int data) {
		switch(register) {
		case 0x0C:
			envelopeLoopFlag = (data >> 5 & 1) == 1;
			lengthCounterHaltFlag = (data >> 5 & 1) == 1;
			constantVolume = (data >> 4 & 1) == 1;
			envelope = data & 0b1111;
			break;
		case 0x0E:
			modeFlag = (data >> 7 & 1) == 1;
			timer = periodLookup[data & 0b1111];
			break;
		case 0x0F:
			lengthCounter = lengthCounterLookup[data >> 4 & 0xF][data >> 3 & 1];
			
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
		if(lengthCounter > 0 && !lengthCounterHaltFlag) {
			lengthCounter--;
		}		
	}
	
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
			if(envelopeLoopFlag) {
				decayLevelCounter = 15;
			}
		} else {
			decayLevelCounter--;;
		}
	}
	
	@Override
	public int getSample() {
		if(lengthCounter == 0) {
			return 0;
		}
		if((linearFeedback & 1) == 1) {
			return 0;
		}
		if(constantVolume) {
			return envelope;
		}
		return decayLevelCounter;
	}

}
