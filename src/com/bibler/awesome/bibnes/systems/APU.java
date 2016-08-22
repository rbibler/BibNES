package com.bibler.awesome.bibnes.systems;

public class APU {
	
	private PulseWaveGenerator pulseOne;
	private PulseWaveGenerator pulseTwo;
	private TriangleWaveGenerator triOne;
	private NoiseWaveGenerator noiseOne;
	private DMCWaveGenerator DMCOne;
	
	private Mixer mixer;
	
	private int cpuDivider;
	
	private int frameCounter;
	private int frameCounterMode;
	private boolean disableFrameInterrupt;
	
	public APU() {
		pulseOne = new PulseWaveGenerator();
		pulseTwo = new PulseWaveGenerator();
		triOne = new TriangleWaveGenerator();
		noiseOne = new NoiseWaveGenerator();
		DMCOne = new DMCWaveGenerator();
		mixer = new Mixer();
	}
	
	public void write(int addressToWrite, int data) {
		final int register = addressToWrite - 0x4000;
		switch(register) {
		case 0:
		case 1:
		case 2:
		case 3:
			pulseOne.write(register, data);
			break;
		case 4:
		case 5:
		case 6:
		case 7:
			pulseTwo.write(register - 4, data);
			break;
		case 8:
		case 0xA:
		case 0xB:
			triOne.write(register, data);
			break;
		case 0xC:
		case 0xE:
		case 0xF:
			noiseOne.write(register, data);
			break;
		case 0x10:
		case 0x11:
		case 0x12:
		case 0x13:
			DMCOne.write(register, data);
			break;
		case 0x15:
			DMCOne.setLengthCounterEnabled((data >> 4 & 1) == 1);
			noiseOne.setLengthCounterEnabled((data >> 3 & 1) == 1);
			triOne.setLengthCounterEnabled((data >> 2 & 1) == 1);
			pulseTwo.setLengthCounterEnabled((data >> 1 & 1) == 1);
			pulseOne.setLengthCounterEnabled((data & 1) == 1);
			break;
		case 0x17:
			frameCounterMode = (data >> 7) & 1;
			disableFrameInterrupt = (data >> 6 & 1) == 1;
			break;
		}
	}
	
	public int read(int addressToRead) {
		
		return 0;
	}
	
	public void clock() {
		if(cpuDivider == 1) {
			cpuDivider = 0;
			apuClock();
		} else {
			cpuDivider = 1;
			apuHalfClock();
		}
	}
	
	private void apuClock() {
		frameCounter++;
		if(frameCounter == 14915 && frameCounterMode == 0) {
			frameCounter = 0;
		} else if(frameCounter == 18641 && frameCounterMode == 1) {
			frameCounter = 0;
		}
		mix();
	}
	
	private void apuHalfClock() {
		if(frameCounter == 3728) {
			clockAllEnvelopes();
			triOne.clockLinearCounter();
		} else if(frameCounter == 7456) {
			clockAllEnvelopes();
			triOne.clockLinearCounter();
			clockAllLengthCounters();
			clockSweepUnits();
		} else if(frameCounter == 11185) {
			clockAllEnvelopes();
			triOne.clockLinearCounter();
		} else if(frameCounter == 14914 && frameCounterMode == 0) {
			clockAllEnvelopes();
			triOne.clockLinearCounter();
			clockAllLengthCounters();
			clockSweepUnits();
		} else if(frameCounter == 18640 && frameCounterMode == 1) {
			clockAllEnvelopes();
			triOne.clockLinearCounter();
			clockAllLengthCounters();
			clockSweepUnits();
		}
	}
	
	private void clockAllEnvelopes() {
		pulseOne.clockEnvelope();
		pulseTwo.clockEnvelope();
		noiseOne.clockEnvelope();
	}
	
	private void clockAllLengthCounters() {
		pulseOne.clockLengthCounter();
		pulseTwo.clockLengthCounter();
		triOne.clockLengthCounter();
		noiseOne.clockLengthCounter();
	}
	
	private void clockSweepUnits() {
		pulseOne.clockSweepUnit();
		pulseTwo.clockSweepUnit();
	}
	
	private void mix() {
		double pulseOneValue = pulseOne.clock();
		double pulseTwoValue = pulseTwo.clock();
		double triValue = triOne.clock();
		double noiseValue = noiseOne.clock();
		double DMCValue = DMCOne.clock();
		mixer.output(pulseOneValue, pulseTwoValue, triValue, noiseValue, DMCValue);
	}

}
