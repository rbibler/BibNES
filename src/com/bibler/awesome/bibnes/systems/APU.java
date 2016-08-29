package com.bibler.awesome.bibnes.systems;

import com.bibler.awesome.bibnes.ui.AudioChannelView;

public class APU {
	
	private PulseWaveGenerator pulseOne;
	private PulseWaveGenerator pulseTwo;
	private TriangleWaveGenerator triOne;
	private NoiseWaveGenerator noiseOne;
	private DMCWaveGenerator DMCOne;
	
	private boolean pulseOneEnabled = true;
	private boolean pulseTwoEnabled = true;
	private boolean triEnabled = true;
	private boolean noiseEnabled = true;
	private boolean dmcEnabled = true;
	private boolean filteringOn = false;
	
	private int cycles = 0;
	private int outputSamples;
	private double smoothedValue;
	private double newValue;
	private double smoothing = .5;
	private int totalAPUCycles;
	
	private Mixer mixer;
	
	private int cpuDivider;
	
	private int frameCounter;
	private int frameCounterMode;
	private int frameStep;
	private boolean disableFrameInterrupt;
	byte[] pulseOneSamples = new byte[512];
	byte[] pulseTwoSamples = new byte[512];
	byte[] triSamples = new byte[512];
	
	private int remainder;
	private double accumulator;
	private int apuCycle;
	
	private int sampleRate = 1786860 / 44100;
	private int volumeMultiplier;
	private int bitRate;
	
	private final int FRAME_DIVIDER_PERIOD = 7456;
	
	private AudioChannelView audioChannelView;
	
	public APU(NES nes) {
		pulseOne = new PulseWaveGenerator();
		pulseTwo = new PulseWaveGenerator();
		triOne = new TriangleWaveGenerator();
		noiseOne = new NoiseWaveGenerator();
		DMCOne = new DMCWaveGenerator(nes);
		mixer = new Mixer();
		frameCounter = FRAME_DIVIDER_PERIOD;
		frameStep = 1;
		updateAudioParams(16);
	}
	
	public void updateAudioParams(int bitRate) {
		this.bitRate = bitRate;
		if(bitRate == 8) {
			volumeMultiplier = 0xFF;
		} else if(bitRate == 16) {
			volumeMultiplier = 32768;
		}
		mixer.updateParameters(bitRate);
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
			frameCounter = 0;
			frameCounterMode = (data >> 7) & 1;
			disableFrameInterrupt = (data >> 6 & 1) == 1;
			break;
		}
	}
	
	public int read(int addressToRead) {
		if(addressToRead == 0x4015) {
			final int ret = readStatus();
			return ret;
		} else {
			return addressToRead >> 8;
		}
	}
	
	
	public void clock() {
		triOne.clock();
		DMCOne.clock();
		if((cycles & 1) == 0) {
			apuClock();
			if(cycles % sampleRate == 0) {
				newValue = getSamples();
				if(bitRate == 16 && filteringOn) {
					newValue = lowpass_filter(highpass_filter((int) newValue));
				}
				mixer.outputSample((int) newValue);
				outputSamples++;
			}
		}
		cycles++;
	}
	
	public void finishFrame() {
		mixer.flushSamples();
		cycles = 0;
		outputSamples = 0;
		//if(audioChannelView != null) {
			//audioChannelView.updateView();
		//}
	}
	
	public void reset() {
		frameCounter = FRAME_DIVIDER_PERIOD;
		pulseOne.reset();
		pulseTwo.reset();
		triOne.reset();
		noiseOne.reset();
		DMCOne.reset();
		frameStep = 1;
	}
	
	public void setChannelEnabled(int channel, boolean enabled) {
		switch(channel) {
		case 0:
			pulseOneEnabled = enabled;
			break;
		case 1:
			pulseTwoEnabled = enabled;
			break;
		case 2:
			triEnabled = enabled;
			break;
		case 3:
			noiseEnabled = enabled;
			break;
		case 4:
			dmcEnabled = enabled;
			break;
		}
	}
	
	private int dckiller;
	private int lpaccum;
	
	 private int highpass_filter(int sample) {
	        //for killing the dc in the signal
	        sample += dckiller;
	        dckiller -= sample >> 8;//the actual high pass part
	        dckiller += (sample > 0 ? -1 : 1);//guarantees the signal decays to exactly zero
	        return sample;
	    }

	    private int lowpass_filter(int sample) {
	        sample += lpaccum;
	        lpaccum -= sample * 0.9;
	        return lpaccum;
	    }
	
	
	private int getSamples() {
		double pulseOneByte = pulseOneEnabled ? pulseOne.getSample() : 0;
		double pulseTwoByte = pulseTwoEnabled ? pulseTwo.getSample() : 0;
		double tri = triEnabled ? triOne.getSample() : 0;
		double noise = noiseEnabled ? noiseOne.getSample() : 0;
		double dmc = dmcEnabled ? DMCOne.getSample() : 0;
		double total = (.00752 * (pulseOneByte + pulseTwoByte)) + ((0.00851 * tri) + (noise * .00494) + (dmc * .0033f));
		total *= volumeMultiplier;
		return (int) total; 
	}
	
	private int readStatus() {
		final int noiseLength = (noiseOne.getLengthCounter() > 0 ? 1 : 0);
		final int triLength = (triOne.getLengthCounter() > 0 ? 1 : 0);
		final int pulseOneLength = (pulseOne.getLengthCounter() > 0 ? 1 : 0);
		final int pulseTwoLength = (pulseTwo.getLengthCounter() > 0 ? 1 : 0);
		return pulseOneLength | (pulseTwoLength << 1) | (triLength << 2) | (noiseLength << 3);
	}
	
	private void apuClock() {
		pulseOne.clock();
		pulseTwo.clock();
		noiseOne.clock();
		totalAPUCycles++;
	}
	
	public void stepFrame() {
		
		switch(frameStep) {
		case 1:
			clockAllEnvelopes();
			triOne.clockLinearCounter();
			break;
		case 2:
			clockAllEnvelopes();
			triOne.clockLinearCounter();
			clockAllLengthCounters();
			clockSweepUnits();
			break;
		case 3:
			clockAllEnvelopes();
			triOne.clockLinearCounter();
			break;
		case 4:
			if(frameCounterMode == 0) {
				clockAllEnvelopes();
				triOne.clockLinearCounter();
				clockAllLengthCounters();
				clockSweepUnits();
				frameStep = 0;
				totalAPUCycles = 0;
			}
			break;
		case 5:
			if(frameCounterMode == 1) {
				clockAllEnvelopes();
				triOne.clockLinearCounter();
				clockAllLengthCounters();
				clockSweepUnits();
				frameStep = 0;
				totalAPUCycles = 0;
			}
			break;
		}
		frameStep++;
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
	
	public void setAudioChannelView(AudioChannelView audioChannelView) {
		this.audioChannelView = audioChannelView;
	}
	
	public byte[] getFrame() {
		return mixer.getFrame();
	}
	
	public boolean bufferHasLessThan(int samples) {
		return mixer.bufferHasLessThan(samples);
	}

}
