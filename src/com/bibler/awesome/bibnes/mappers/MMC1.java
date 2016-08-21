package com.bibler.awesome.bibnes.mappers;

import com.bibler.awesome.bibnes.systems.NES;

public class MMC1 extends Mapper {
	
	private int shiftRegister;
	private int shiftCounter;
	private int controlRegister;
	private int chrReg1;
	private int chrReg2;
	private int prgReg;
	private int prgMode = 3;
	private int chrMode;
	private int numBanks;

	
	@Override
	public void setPrgMemSize(int memSize) {
		super.setPrgMemSize(memSize);
		numBanks = memSize / 0x4000;
	}
	
	@Override
	public void writePrg(int address, int data) {
		if(address >= 0x8000) {
			if((data >> 7 & 1) == 1) {
				clearShift();
			} else if(shiftCounter < 5) {
				shiftRegister >>= 1;
				shiftRegister |= (data & 1) << 4;
				shiftCounter++;
				if(shiftCounter == 5) {
					if(address < 0xA000) {
						controlRegister = shiftRegister;
						prgMode = controlRegister >> 2 & 3;
						chrMode = controlRegister >> 3 & 1;
						
						int mirrorType = controlRegister & 3;
						if(mirrorType == 3) {
							nes.setMirror(NES.HORIZ);
						} else if(mirrorType == 2) {
							nes.setMirror(NES.VERT);
						} else {
							nes.setMirror(NES.SINGLE_SCREEN);
						}
						
					} else if(address < 0xC000) {
						chrReg1 = shiftRegister;
					} else if(address < 0xE000) {
						chrReg2 = shiftRegister;
					} else {
						prgReg = shiftRegister;
					}
					clearShift();
				}
			}	
		}
	}
	
	@Override
	public int readPrg(int address) {
		int newAddress = address - 0x8000;
		switch(prgMode) {
		case 0:
		case 1:
			newAddress |= (prgReg >> 1) << 15;
			break;
		case 2:
			// Check to see if we're in the fixed range.
			if((address >> 14 & 3) != prgMode) {						
				newAddress |= prgReg << 14;
			}
			break;
		case 3:
			if((address >> 14 & 3) != prgMode) {
				newAddress |= prgReg << 14;
			} else {
				newAddress |= (numBanks - 1) << 14;
			}
			break;
		}
		return prgMem[newAddress % prgMemSize];
	}
	
	@Override
	public int readChr(int address) {
		int newAddress = address;
		switch(chrMode) {
		case 0:
			newAddress |= (chrReg1 >> 1) << 13;
			break;
		case 1:
			if(address < 0x1000) {
				newAddress |= chrReg1 << 12;
			} else {
				newAddress |= chrReg2 << 12;
			}
			break;
		}
		if(chrMem != null) {
			return chrMem[newAddress % chrMemSize];
		} else if(chrRam != null) {
			return chrRam[newAddress % chrRam.length];
		}
		return newAddress >> 8;
	}
	
	@Override
	public void writeChr(int address, int data) {
		if(address > 0x2000) {
			return;
		}
		int newAddress = address;
		/*switch(chrMode) {
		case 0:
			newAddress |= (chrReg1 >> 1) << 13;
			break;
		case 1:
			if(address < 0x1000) {
				newAddress |= chrReg1 << 12;
			} else {
				newAddress |= chrReg2 << 12;
			}
			break;
		}*/
		if(chrMem != null) {
			chrMem[newAddress % chrMemSize] = data;
		} else if(chrRam != null) {
			chrRam[newAddress % chrRam.length] = data;
		}
	}
	
	private void clearShift() {
		shiftRegister = 0x10;
		shiftCounter = 0;
	}

}
