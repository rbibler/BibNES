package com.bibler.awesome.bibnes.mappers;

public class Mapper002 extends Mapper {
	
	private int numBanks;
	
	public Mapper002() {
		prgBankSize = 0x4000;
		chrBankSize = 0x1000;
	}
	
	@Override
	public void setPrgMemSize(int size) {
		super.setPrgMemSize(size);
		numBanks = size / prgBankSize;
		prgBanks = new int[2];
		prgBanks[1] = size - prgBankSize;
	}
	
	@Override
	public void writePrg(int address, int data) {
		if(address >= 0x8000 && address <= 0xFFFF) {
			prgBanks[0] = (data % numBanks) * prgBankSize;
		}
	}

}
