package com.bibler.awesome.bibnes.mappers;

public class Mapper002 extends Mapper {
	
	private int prgBank1;
	private int prgBank2;
	private int numBanks;
	
	@Override
	public void setPrgMemSize(int size) {
		super.setPrgMemSize(size);
		numBanks = size / 0x4000;
		prgBank2 = numBanks - 1;
	}
	
	@Override
	public void writePrg(int address, int data) {
		if(address >= 0x8000 && address <= 0xFFFF) {
			prgBank1 = data % numBanks;
		}
	}
	
	@Override
	public int readPrg(int address) {
		if(address >= 0xC000 && address <= 0xFFFF) {
			return prgMem[(prgBank2 * 0x4000) + (address - 0xC000)];
		} else {
			final int offset = address - 0x8000;
			return prgMem[(prgBank1 * 0x4000) + offset];
		}
	}
	
	@Override
	public int readChr(int address) {
		if(chrMem != null) {
			return chrMem[address % chrMemSize];
		} else if(chrRam != null) {
			return chrRam[address % chrRam.length];
		}
		return address >> 8;
	}
	
	@Override
	public void writeChr(int address, int data) {
		if(chrMem != null) {
			chrMem[address % chrMemSize] = data;
		} else if(chrRam != null) {
			chrRam[address % chrRam.length] = data;
		}
	}

}
