package com.bibler.awesome.bibnes.systems;

public class PulseWaveGenerator extends WaveGenerator {
	
	private int envelope = 7;
	private boolean sweepEnabled;
	private int sweepPeriod;
	private int sweepNegate;
	private int sweepShift;
	private double duty;
	private int channelVolume;
	private boolean lengthCounterHalt;
	private boolean constantVolume;
	private boolean lengthCounterEnabled;
	private boolean envelopeStartFlag;
	private int currentTimer;
	private int currentStep;
	private double frequency;
	private long periodSamples;
	private long sampleNumber;
	private double cpuClockSpeed = 1789773;
	private double sampleRate = 44100;
	private int currentVolume;
	private int decayLevelCounter;
	private int envelopeDividerPeriod;
	
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
		//if(data > 0) {
			//System.out.println("Wrote to pulse register " + register + " data " + Integer.toBinaryString(data));
		//}
		switch(register) {
		case 0:
			switch(data >> 6 & 3) {
			case 0:
				duty = .125;
				break;
			case 1:
				duty = .25;
				break;
			case 2:
				duty = .50;
				break;
			case 3:
				duty = .75;
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
			lengthCounter = data >> 3 & 0b11111;
			currentStep = 7;
			currentTimer = timer;
			frequency = cpuClockSpeed / (16 * (timer + 1));
			periodSamples = (long) (sampleRate / frequency);
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
			currentTimer = timer;
			currentStep--;
			if(currentStep < 0) {
				currentStep = 7;
			}
		} else {
			currentTimer--;
		}
		return 0;
		//return lengthCounter > 0 ? (7 * ((duty >> currentStep) & 1)) : 0;
	}
	
	@Override
	public double getSample() {
		if(periodSamples == 0) {
			return periodSamples;
		}
		double value = 0;
		//if(lengthCounter > 0) {
			if(sampleNumber < (periodSamples * duty)) {
				value = 1.0;
			} else {
				value = -1.0;
			}
		//}
		sampleNumber = (sampleNumber + 1) % periodSamples;
		return value;
	}
	
	@Override
	public int getSamples(byte[] samples) {
		currentVolume = constantVolume ? envelope : decayLevelCounter;
		double volumeMultiplier = (currentVolume / 15.0);
		for(int i = 0; i < samples.length; i++) {
			
			double ds = getSample() * (Byte.MAX_VALUE * volumeMultiplier);
			byte ss = (byte) Math.round(ds);
			samples[i] = (byte) (ss);
		}
		return samples.length;
	}

}
