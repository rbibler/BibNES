package com.bibler.awesome.bibnes.systems;

public class DMCWaveGenerator extends WaveGenerator {
	
	private boolean IRQEnable;
	private boolean loopSample;
	private boolean silence;
	private boolean bufferEmpty = true;
	private int directLoad;
	private int sampleAddress;
	private int sampleLength;
	private int bytesRemaining;
	private int currentAddress;
	private int sampleBuffer;
	private int outputShift;
	private int outputCounter;
	private int timer;
	private int currentTimer;
	
	private NES nes;
	
	public DMCWaveGenerator(NES nes) {
		this.nes = nes;
	}
	
	@Override
	public void reset() {
		bufferEmpty = true;
		silence = true;
		outputCounter = 0;
		timer = 0;
		currentTimer = 0;
		directLoad = 0;
		sampleAddress = 0;
		sampleLength = 0;
		bytesRemaining = 0;
		currentAddress = 0;
		sampleBuffer = 0;
		outputShift = 0;
		loopSample = false;
		IRQEnable = false;
	}
	
	private int[] rateTable = new int[] {
		0x1AC, 0x17C, 0x154, 0x140, 0x11E, 0x0FE, 0x0E2, 0x0D6, 0x0BE, 0x0A0, 0x08E, 0x080, 0x06A, 0x054, 0x048, 0x036
	};
	
	public void setLengthCounterEnabled(boolean enabled) {
		if (enabled) {
            if (bytesRemaining == 0) {
                restartSample();
            }
        } else {
            bytesRemaining = 0;
            silence = true;
        }
        //if (statusdmcint) {
          //  --cpu.interrupt;
           // statusdmcint = false;
        //}
	}
	
	@Override
	public void write(int register, int data) {
		switch(register) {
		case 0x10:
			IRQEnable = (data >> 7 & 1) == 1;
			loopSample = (data >> 6 & 1) == 1;
			timer = rateTable[data & 0xF];
			currentTimer = timer + 1;
			break;
		case 0x11:
			directLoad = data & 0x7F;
			break;
		case 0x12:
			sampleAddress = (data << 6) + 0xc000;
			currentAddress = sampleAddress;
			break;
		case 0x13:
			sampleLength = (data << 4) + 1;
			bytesRemaining = sampleLength;
			break;
		}
	}
	
	@Override
	public int getSample() {
		return directLoad;
	}
	
	@Override
	public int clock() {
		if(bufferEmpty && bytesRemaining > 0) {
			fillBuffer();
		}
		if(currentTimer == 0) {
			currentTimer = timer;
			clockOutput();
		} else {
			currentTimer--;
		}
		return 0;
	}
	
	private void clockOutput() {
		if(!silence) {
			
			if((outputShift & 1) == 0) {
				if(directLoad > 1) {
					directLoad -= 2;
				}
			} else if(directLoad < 126) {
				directLoad += 2;
			}
		}
		outputShift >>= 1;
		outputCounter--;
		if(outputCounter <= 0) {
			outputCounter = 8;
			if(!bufferEmpty) {
				silence = false;
				outputShift = sampleBuffer;
				bufferEmpty = true;
			} else {
				silence = true;
			}
		}
	}
	
	private void fillBuffer() {
		sampleBuffer = nes.readDMCByte(currentAddress++);
		outputShift = sampleBuffer;
		bufferEmpty = false;
		if(currentAddress > 0xFFFF) {
			currentAddress = 0x8000;
		}
		bytesRemaining--;
		if(bytesRemaining == 0) {
			if(loopSample) {
				restartSample();
			} else if(IRQEnable) {
				nes.interrupt();
			}
		}
	}
	
	private void restartSample() {
		currentAddress = sampleAddress;
		bytesRemaining = sampleLength;
	}

}
