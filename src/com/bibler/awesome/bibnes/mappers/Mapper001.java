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
		System.out.println("mem size: " + memSize);
		super.setChrMemSize(memSize);
		numChrBanks = memSize / 0x1000;
		chrBanks = new int[2];
		chrBankSize = 0x1000;
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
						chrMode = controlRegister >> 4 & 1;
						int mirrorType = controlRegister & 3;
						if(mirrorType == 3) {
							setMirroring(NES.HORIZ);
						} else if(mirrorType == 2) {
							setMirroring(NES.VERT);
						} else {
							setMirroring(NES.SINGLE_SCREEN);
						}
						
					} else if(address < 0xC000) {
						chrReg1 = shiftRegister % numChrBanks;
						updateChrBanks();
					} else if(address < 0xE000) {
						chrReg2 = shiftRegister % numChrBanks;
						updateChrBanks();
					} else {
						prgReg = shiftRegister;
						updatePrgBanks();
					}
					clearShift();
				}
			}	
		} else {
			super.writePrg(address, data);
		}
	}
	
	private void updatePrgBanks() {
		//System.out.println("Prg Mode: " + prgMode + " prgReg " + prgReg);
		switch(prgMode) {
		case 0:
		case 1:
			prgBankSize = 0x4000;
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
		//System.out.println("Chr Mode: " + chrMode + " chrReg1 " + chrReg1 + " chrReg2 " + chrReg2 );
		switch(chrMode) {
		case 0:
			chrBanks[0] = (chrReg1 & 0b11110) * 0x1000;
			break;
		case 1:
			chrBanks[0] = (chrReg1 * 0x1000);
			chrBanks[1] = (chrReg2 * 0x1000);
			break;
		}
	}
	

	
	private void clearShift() {
		shiftRegister = 0x0;
		shiftCounter = 0;
		controlRegister |= 0xc;
	}

}
