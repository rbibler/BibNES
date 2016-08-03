package com.bibler.awesome.bibnes.mappers;

public class MMC1 extends Mapper {
	
	private int shiftRegister;
	private int shiftCounter;
	private int controlRegister;
	private int chrReg1;
	private int chrReg2;
	private int prgReg;
	private int prgMode;
	private int chrMode;
	
	@Override
	public void writePrg(int address, int data) {
		if(address >= 0x8000) {
			if((data >> 7 & 1) == 1) {
				clearShift();
			} else if(shiftCounter < 5) {
				shiftRegister >>= 1;
				shiftRegister |= (data & 1) << 4;
				shiftCounter++;
			} else if(shiftCounter == 5) {
				if(address < 0xA000) {
					controlRegister = shiftRegister;
					prgMode = controlRegister >> 2 & 3;
					chrMode = controlRegister >> 3 & 1;
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
	
	@Override
	public int readPrg(int address) {
		int newAddress = address;
		switch(prgMode) {
		case 0:
		case 1:
			newAddress = address | (prgReg >> 1) << 14;
			break;
		case 2:
			// Check to see if we're in the fixed range.
			if((address >> 14 & 3) == prgMode) {						
				newAddress = address % 0x8000;
			} else {
				newAddress = address | prgReg << 14;
			}
		}
		return prgMem[newAddress % prgMemSize];
	}
	
	@Override
	public int readChr(int address) {
		return 0;
	}
	
	private void clearShift() {
		shiftRegister = 0x10;
		shiftCounter = 0;
	}

}
