package com.bibler.awesome.bibnes.systems;

public class APU {
	
	private PulseWaveGenerator pulseOne;
	private PulseWaveGenerator pulseTwo;
	private TriangleWaveGenerator triOne;
	private NoiseWaveGenerator noiseOne;
	private DMCWaveGenerator DMCOne;
	
	private boolean DMCEnable;
	private boolean noiseEnable;
	private boolean triEnable;
	private boolean pulse2Enable;
	private boolean pulse1Enable;
	private int frameCounter;
	private boolean disableFrameInterrupt;
	
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
			DMCEnable = (data >> 4 & 1) == 1;
			noiseEnable = (data >> 3 & 1) == 1;
			triEnable = (data >> 2 & 1) == 1;
			pulse2Enable = (data >> 1 & 1) == 1;
			pulse1Enable = (data & 1) == 1;
			break;
		case 0x17:
			frameCounter = (data >> 7) & 1;
			disableFrameInterrupt = (data >> 6 & 1) == 1;
			break;
		}
	}
	
	public int read(int addressToRead) {
		
		return 0;
	}

}
