package com.bibler.awesome.bibnes.mappers;

import com.bibler.awesome.bibnes.systems.NES;

public class Mapper009 extends Mapper {
	
	private int latchLeft = 0xFE;
	private int latchRight = 0xFE;
	private int bankSelect;
	private int leftBank0;
	private int leftBank1;
	private int rightBank0;
	private int rightBank1;
	private int numChrBanks;
	
	public Mapper009() {
		prgBankSize = 0x2000;
		chrBankSize = 0x1000;
	}
	
	@Override
	public void setPrgMemSize(int size) {
		super.setPrgMemSize(size);
		numPrgBanks = size / prgBankSize;
		final int nativeBanks = size / 0x8000;
		prgBanks = new int[nativeBanks];
		prgBanks[nativeBanks - 1] = size - (prgBankSize * 1);
		prgBanks[nativeBanks - 2] = size - (prgBankSize * 2);
		prgBanks[nativeBanks - 3] = size - (prgBankSize * 3);
	}
	
	@Override
	public void setChrMemSize(int size) {
		super.setChrMemSize(size);
		numChrBanks = size / chrBankSize;
		chrBanks = new int[numChrBanks];
	}
	
	@Override
	public void writePrg(int address, int data) {
		if(address >= 0xA000 && address <= 0xAFFF) {
			bankSelect = data & 0b1111;
			prgBanks[0] = prgBankSize * bankSelect;
		} else if(address >= 0xB000 && address <= 0xBFFF) {
			leftBank0 = data & 0x1F;
			updatePPUBanks();
		} else if(address >= 0xC000 && address <= 0xCFFF) {
			leftBank1 = data & 0x1F;
			updatePPUBanks();
		} else if(address >= 0xD000 && address <= 0xDFFF) {
			rightBank0 = data & 0x1F;
			updatePPUBanks();
		} else if(address >= 0xE000 && address <= 0xEFFF) {
			rightBank1 = data & 0x1F;
			updatePPUBanks();
		} else if(address >= 0xF000 && address <= 0xFFFF) {
			if((data & 1) == 1) {
				setMirroring(NES.HORIZ);
			} else if((data & 1) == 0) {
				setMirroring(NES.VERT);
			}
		}
	}
	
	private void updatePPUBanks() {
		if(latchLeft == 0xFD) {
			chrBanks[0] = leftBank0 * chrBankSize;
		} else {
			chrBanks[0] = leftBank1 * chrBankSize;
		}
		if(latchRight == 0xFD) {
			chrBanks[1] = rightBank0 * chrBankSize;
		} else {
			chrBanks[1] = rightBank1 * chrBankSize;
		}
	}
	
	
	@Override
	public int readChr(int address) {
		int retval = super.readChr(address);
		if(address == 0xFD8) {
			latchLeft = 0xFD;
			updatePPUBanks();
		} else if(address == 0xFE8) {
			latchLeft = 0xFE;
			updatePPUBanks();
		} else if(address >= 0x1FD8 && address <= 0x1FDF) {
			latchRight = 0xFD;
			updatePPUBanks();
		} else if(address >= 0x1FE8 && address <= 0x1FEF) {
			latchRight = 0xFE;
			updatePPUBanks();
		}
		return retval;
	}
}
