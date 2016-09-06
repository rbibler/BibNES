package com.bibler.awesome.bibnes.mappers;

public class Mapper009 extends Mapper {
	
	private int latch0;
	private int latch1;
	private int bankSelect;
	private int numBanks;
	private int firstFixedBank;
	private int secondFixedBank;
	private int thirdFixedBank;
	private int chrBank0;
	private int chrBank1;
	
	@Override
	public void setPrgMemSize(int size) {
		super.setPrgMemSize(size);
		numBanks = size / 0x2000;
		thirdFixedBank = numBanks - 1;
		secondFixedBank = thirdFixedBank - 1;
		firstFixedBank = secondFixedBank - 1;
	}
	
	@Override
	public void writePrg(int address, int data) {
		if(address >= 0xA000 && address <= 0xAFFF) {
			bankSelect = data & 0b1111;
		} else if(address >= 0xB000 && address <= 0xBFFF) {
			if(latch0 == 0xFD) {
				chrBank0 = data & 0b11111;
			}
		} else if(address >= 0xC000 && address <= 0xCFFF) {
			if(latch0 == 0xFE) {
				chrBank0 = data & 0b11111;
			}
		} else if(address >= 0xD000 && address <= 0xDFFF) {
			if(latch1 == 0xFD) {
				chrBank1 = data & 0b11111;
			}
		} else if(address >= 0xE000 && address <= 0xEFFF) {
			if(latch1 == 0xFE) {
				chrBank1 = data & 0b11111;
			}
		}
	}
	
	@Override
	public int readPrg(int address) {
		if(address >= 0xA000) {
			if(address < 0xC000) {
				return prgMem[firstFixedBank * 0x2000 + (address - 0xA000)];
			} else if(address < 0xE000) {
				return prgMem[secondFixedBank * 0x2000 + (address - 0xC000)];
			} else if(address <= 0xFFFF) {
				return prgMem[thirdFixedBank * 0x2000 + (address - 0xE000)];
			}
		} else if(address >= 0x8000) {
			return prgMem[bankSelect * 0x2000 + (address - 0x8000)];
		} 
		return address >> 8;
	}
	
	@Override
	public int readChr(int address) {
		if(address == 0xFD8) {
			latch0 = 0xFD;
		} else if(address == 0xFE8) {
			latch0 = 0xFE;
		} else if(address >= 0x1FD8 && address <= 0x1FDF) {
			latch1 = 0xFD;
		} else if(address >= 0x1FE8 && address <= 0x1FEF) {
			latch1 = 0xFE;
		}
		if(address < 0x1000) {
			return chrMem[chrBank0 * 0x2000 + address];
		} else if(address < 0x2000) {
			return chrMem[chrBank1 * 0x2000 + (address - 0x1000)];
		}
		return address >> 8;
	}

}
