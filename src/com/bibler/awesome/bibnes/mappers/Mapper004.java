package com.bibler.awesome.bibnes.mappers;

import com.bibler.awesome.bibnes.systems.NES;

public class Mapper004 extends Mapper {
	
	private int bankSelectRegister;
	private int prgBankMode;
	private int chrBankMode;
	private int chrBank0;
	private int chrBank1;
	private int chrBank2;
	private int chrBank3;
	private int chrBank4;
	private int chrBank5;
	private int prgBank0;
	private int prgBank1;
	private int irqReload;
	private int irqCounter;
	
	private boolean prgRamProtect;
	private boolean prgRamEnable;
	private boolean irqReloadNext;
	private boolean irqDisable;
	private boolean irqEnable;
	private boolean irqPending;
	
	
	@Override
	public void setPrgMemSize(int memSize) {
		super.setPrgMemSize(memSize);
		prgBankSize = 0x2000;
		numPrgBanks = memSize / prgBankSize;
		prgBanks = new int[4];
		setupPrgBanks();
	}
	
	@Override
	public void setChrMemSize(int memSize) {
		super.setChrMemSize(memSize);
		chrBankSize = 0x400;
		numChrBanks = memSize / chrBankSize;
		chrBanks = new int[8];
		setupChrBanks();
	}


	@Override
	public void writePrg(int address, int data) {
		if(address >= 0x6000 && address <= 0x7FFF && prgRamEnable && !prgRamProtect) {
			super.writePrg(address, data);
		} else if(address >= 0x8000 && address <= 0x9FFF) {
			if((address & 1) == 0) {
				bankSelectRegister = data & 7;
				prgBankMode = data >> 6 & 1;
				chrBankMode = data >> 7 & 1;
			} else {
				switch(bankSelectRegister) {
				case 0:
					chrBank0 = data;
					break;
				case 1:
					chrBank1 = data;
					break;
				case 2:
					chrBank2 = data;
					break;
				case 3:
					chrBank3 = data;
					break;
				case 4:
					chrBank4 = data;
					break;
				case 5:
					chrBank5 = data;
					break;
				case 6:
					prgBank0 = data;
					break;
				case 7:
					prgBank1 = data;
					break;
				}
				setupChrBanks();
				setupPrgBanks();
			}
		} else if(address >= 0xA000 && address <= 0xBFFF) {
			if((address & 1) == 0) {
				setMirroring((data & 1) == 0 ? NES.VERT : NES.HORIZ);
			} else {
				prgRamProtect = (data >> 6 & 1) == 1;
				prgRamEnable = (data >> 7 & 1) == 1;
			}
		} else if(address >= 0xC000 && address <= 0xDFFF) {
			if((address & 1) == 0) {
				irqReload = data;
			} else {
				irqReloadNext = true;
			}
		} else if(address >= 0xE000 && address <= 0xFFFF) {
			if((address & 1) == 0) {
				if(irqPending) {
					pullCPUIRQHigh();
				}
				irqPending = false;
				irqDisable = true;
			} else {
				irqEnable = true;
				//System.out.println("Enabled from Mapper");
			}
		}
	} 
	
	private void setupPrgBanks() {
		//System.out.println("Prg Mode: " + prgBankMode + " prgBank 0: " + prgBank0 + " prgBank1: " + prgBank1);
		switch(prgBankMode) {
		case 0:
			prgBanks[0] = prgBank0 * prgBankSize;
			prgBanks[1] = prgBank1 * prgBankSize;
			prgBanks[2] = (numPrgBanks - 2) * prgBankSize;
			prgBanks[3] = (numPrgBanks - 1) * prgBankSize;
			break;
		case 1:
			prgBanks[0] = (numPrgBanks - 2) * prgBankSize;
			prgBanks[1] = prgBank1 * prgBankSize;
			prgBanks[2] = prgBank0 * prgBankSize;
			prgBanks[3] = (numPrgBanks - 1) * prgBankSize;
			break;
		}
	}
	
	private void setupChrBanks() {
		switch(chrBankMode) {
		case 0:
			chrBanks[0] = (chrBank0 & 0xFE) * chrBankSize;
			chrBanks[1] = (chrBank0 | 1) * chrBankSize;
			chrBanks[2] = (chrBank1 & 0xFE) * chrBankSize;
			chrBanks[3] = (chrBank1 | 1) * chrBankSize;
			chrBanks[4] = chrBank2 * chrBankSize;
			chrBanks[5] = chrBank3 * chrBankSize;
			chrBanks[6] = chrBank4 * chrBankSize;
			chrBanks[7] = chrBank5 * chrBankSize;
			break;
		case 1:
			chrBanks[0] = chrBank2 * chrBankSize;
			chrBanks[1] = chrBank3 * chrBankSize;
			chrBanks[2] = chrBank4 * chrBankSize;
			chrBanks[3] = chrBank5 * chrBankSize;
			chrBanks[4] = (chrBank0 & 0xFE) * chrBankSize;
			chrBanks[5] = (chrBank0 | 1) * chrBankSize;
			chrBanks[6] = (chrBank1 & 0xFE) * chrBankSize;
			chrBanks[7] = (chrBank1 | 1) *  chrBankSize;
			break;
		}
	}
	
	@Override
	public void writeChr(int address, int data) {
		checkForRisingEdge(address);
		super.writeChr(address, data);
	}
	
	@Override
	public int readChr(int address) {
		checkForRisingEdge(address);
		return super.readChr(address);
	}
	
	
	private int a12Counter = 0;
	private boolean lastA12;
	
    
    public void checkForRisingEdge(int address) {
        //run on every PPU cycle (wasteful...)
        //clocks scanline counter every time A12 line goes from low to high
        //on PPU address bus, _except_ when it has been less than 8 PPU cycles 
        //since the line last went low.
        boolean a12 = ((address >> 12 & 1) != 0);
        if (a12 && (!lastA12)) {
            //rising edge
            if ((a12Counter <= 0)) {
                clockScanCounter();
            }
        } else if (!a12 && lastA12) {
            //falling edge
            a12Counter = 8;
        }

        --a12Counter;
        lastA12 = a12;
    }

    private void clockScanCounter() {
        if (irqReloadNext || (irqCounter == 0)) {
            irqCounter = irqReload;
            irqReloadNext = false;
        } else {
            --irqCounter;
        }
        if ((irqCounter == 0)) {
        	if(irqEnable & !irqPending) {
        		pullCPUIRQLow();
        		irqPending = true;
        	}
        }
    }
}
