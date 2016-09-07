package com.bibler.awesome.bibnes.mappers;

public class Mapper000 extends Mapper {
	
	
	
	@Override
	public void setPrgMemSize(int size) {
		super.setPrgMemSize(size);
		numPrgBanks = 1;
		prgBankSize = 0x7fff;
		prgBanks = new int[1];
	}
	
	@Override
	public void setChrMemSize(int size) {
		super.setChrMemSize(size);
		numChrBanks = 2;
		chrBankSize = 0x1000;
		chrBanks = new int[numChrBanks];
		chrBanks[1] = 0x1000;
	}

}
