package com.bibler.awesome.bibnes.mappers;

import com.bibler.awesome.bibnes.systems.NES;

public class Mapper001 extends Mapper {
	
	private int shiftRegister;
	private int shiftCounter;
	private int controlRegister;
	private int chrReg1;
	private int chrReg2;
	private int prgReg;
	private int prgMode = 3;
	private int chrMode;
	
	public Mapper001() {
		
	}

	
	@Override
	public void setPrgMemSize(int memSize) {
		super.setPrgMemSize(memSize);
		numPrgBanks = memSize / 0x4000;
		prgBanks = new int[2];
		updatePrgBanks();
	}
	
	@Override
	public void setChrMemSize(int memSize) {
		super.setChrMemSize(memSize);
		numChrBanks = memSize / 0x1000;
		chrBanks = new int[2];
		updateChrBanks();
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
							setMirroring(NES.HORIZ);
						} else if(mirrorType == 2) {
							setMirroring(NES.VERT);
						} else {
							setMirroring(NES.SINGLE_SCREEN);
						}
						
					} else if(address < 0xC000) {
						chrReg1 = shiftRegister;
						updateChrBanks();
					} else if(address < 0xE000) {
						chrReg2 = shiftRegister;
						updateChrBanks();
					} else {
						prgReg = shiftRegister;
						updatePrgBanks();
					}
					clearShift();
				}
			}	
		}
	}
	
	private void updatePrgBanks() {
		switch(prgMode) {
		case 0:
		case 1:
			prgBankSize = 0x8000;
			prgBanks[0] = (prgReg & 0b1110) * 0x4000;
			break;
		case 2:
			prgBankSize = 0x4000;
			prgBanks[0] = 0x8000;
			prgBanks[1] = (prgReg & 0xF) * prgBankSize;
			break;
		case 3:
			prgBankSize = 0x4000;
			prgBanks[0] = (prgReg & 0xF) * prgBankSize;
			prgBanks[1] = ((numPrgBanks - 1) * prgBankSize);
			break;
		}
	}
	
	private void updateChrBanks() {
		switch(chrMode) {
		case 0:
			chrBankSize = 0x2000;
			chrBanks[0] = (chrReg1 & 0b11110) * 0x1000;
			break;
		case 1:
			chrBankSize = 0x1000;
			chrBanks[0] = (chrReg1 * 0x1000);
			chrBanks[1] = (chrReg2 * 0x1000);
			break;
		}
	}
	
	/*@Override
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
	}*/
	
	/*@Override
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
	}*/
	
	/*@Override
	public void writeChr(int address, int data) {
		if(address > 0x2000) {
			return;
		}
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
			chrMem[newAddress % chrMemSize] = data;
		} else if(chrRam != null) {
			chrRam[newAddress % chrRam.length] = data;
		}
	}*/
	
	private void clearShift() {
		shiftRegister = 0x10;
		shiftCounter = 0;
	}

}
