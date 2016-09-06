package com.bibler.awesome.bibnes.mappers;

public class Mapper002 extends Mapper {
	
	private int bankSelect;
	private int numBanks;
	private int lastBankStart;
	
	@Override
	public void setPrgMemSize(int size) {
		super.setPrgMemSize(size);
		numBanks = size / 0x4000;
		lastBankStart = size - 0x4000;
	}
	
	@Override
	public void writePrg(int address, int data) {
		if(address >= 0x8000 && address <= 0xFFFF) {
			bankSelect = data;
		}
	}
	
	@Override
	public int readPrg(int address) {
		if(address >= 0xC000 && address <= 0xFFFF) {
			return prgMem[lastBankStart + (address - 0xC000)];
		} else {
			final int offset = address - 0x8000;
			return prgMem[(bankSelect * 0x4000) + offset];
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
