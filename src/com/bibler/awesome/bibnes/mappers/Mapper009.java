package com.bibler.awesome.bibnes.mappers;

import com.bibler.awesome.bibnes.systems.NES;

public class Mapper009 extends Mapper {
	
	private int latch0 = 0xFE;
	private int latch1 = 0xFE;
	private int bankSelect;
	private int numBanks;
	private int firstFixedBank;
	private int secondFixedBank;
	private int thirdFixedBank;
	private int chrBank0;
	private int chrBank1;
	private int chrBank2;
	private int chrBank3;
	private int numChrBanks;
	private int oldLatch1;
	
	private final int PRG_BANK_SIZE = 0x2000;
	private final int CHR_BANK_SIZE = 0x1000;
	
	@Override
	public void setPrgMemSize(int size) {
		super.setPrgMemSize(size);
		numBanks = size / PRG_BANK_SIZE;
		final int nativeBanks = size / 0x8000;
		prgBanks = new int[nativeBanks];
		prgBanks[nativeBanks - 1] = size - (PRG_BANK_SIZE * 1);
		prgBanks[nativeBanks - 2] = size - (PRG_BANK_SIZE * 2);
		prgBanks[nativeBanks - 3] = size - (PRG_BANK_SIZE * 3);
	}
	
	@Override
	public void setChrMemSize(int size) {
		super.setChrMemSize(size);
		numChrBanks = size / CHR_BANK_SIZE;
		int nativeBanks = size / 0x2000;
		chrBanks = new int[nativeBanks];
	}
	
	@Override
	public void writePrg(int address, int data) {
		if(address >= 0xA000 && address <= 0xAFFF) {
			bankSelect = data & 0b1111;
			prgBanks[0] = PRG_BANK_SIZE * bankSelect;
		} else if(address >= 0xB000 && address <= 0xCFFF) {
			chrBank0 = data & 0b11111 % numChrBanks;
		} else if(address >= 0xD000 && address <= 0xDFFF) {
			chrBank2 = data & 0b11111 % numChrBanks;
		} else if(address >= 0xE000 && address <= 0xEFFF) {
			chrBank3 = data & 0b11111 % numChrBanks;
		} else if(address >= 0xF000 && address <= 0xFFFF) {
			if((data & 1) == 1) {
				setMirroring(NES.HORIZ);
			} else if((data & 1) == 0) {
				setMirroring(NES.VERT);
			}
		}
	}
	
	@Override
	public int readPrg(int address) {
		/*if(address >= 0xA000) {
			if(address < 0xC000) {
				return prgMem[firstFixedBank * 0x2000 + (address - 0xA000)];
			} else if(address < 0xE000) {
				return prgMem[secondFixedBank * 0x2000 + (address - 0xC000)];
			} else if(address <= 0xFFFF) {
				return prgMem[thirdFixedBank * 0x2000 + (address - 0xE000)];
			}
		} else if(address >= 0x8000) {
			return prgMem[bankSelect * 0x2000 + (address - 0x8000)];
		} */
		final int offset = address - 0x8000;
		final int bankNum = offset / PRG_BANK_SIZE;
		return prgMem[prgBanks[bankNum] + (offset - (bankNum * PRG_BANK_SIZE))];
		//return address >> 8;
	}
	
	
	@Override
	public int readChr(int address) {
		int retvalue = address >> 8;
		if(address < 0x1000) {
			retvalue = chrMem[(chrBank0 * 0x1000) + address];
		} else if(address < 0x2000) {
			retvalue = chrMem[(latch1 == 0xFD ? (chrBank2 * 0x1000) : (chrBank3 * 0x1000)) + (address - 0x1000)];
		}
			if((address >= 0xFD0 && address <= 0xFDF)
					|| (address >= 0x1FD0 && address <= 0x1FDF)) {
				latch1 = 0xFD;
			} else if((address >= 0xFE0 && address <= 0x0FEF) 
					|| (address >= 0x1FE0 && address <= 0x1FEF)) {
				latch1 = 0xFE;
			} 
		
		
		return retvalue;
	}
}
