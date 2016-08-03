package com.bibler.awesome.bibnes.mappers;

public class MMC1 extends Mapper {
	
	private int shiftRegister;
	private int shiftCounter;
	private int controlRegister;
	private int chrReg1;
	private int chrReg2;
	private int prgReg;
	
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
	
	private void clearShift() {
		shiftRegister = 0x10;
		shiftCounter = 0;
	}

}
