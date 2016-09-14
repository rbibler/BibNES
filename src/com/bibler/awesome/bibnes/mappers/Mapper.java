package com.bibler.awesome.bibnes.mappers;

import com.bibler.awesome.bibnes.systems.NES;

public class Mapper {
	
	protected int[] prgMem;
	protected int[] chrMem;
	protected int[] prgRam;
	
	protected int[] prgBanks;
	protected int[] chrBanks;
	
	protected int prgMemSize;
	protected int chrMemSize;
	protected int prgBankSize;
	protected int chrBankSize;
	protected int numPrgBanks;
	protected int numChrBanks;
	
	protected boolean hasPrgRam;
	
	protected NES nes;
	
	
	public Mapper() {
		
	}
	
	public void setNES(NES nes) {
		this.nes = nes;
	}
	
	public static Mapper getMapper(int mapperNum) {
		switch(mapperNum) {
			case 0:
				return new Mapper000();
			case 1:
				return new Mapper001();
			case 2:
				return new Mapper002();
			case 4:
				return new Mapper004();
			case 9:
				return new Mapper009();
			default:
				return new Mapper000();
		}
	}
	
	public void setPrgMem(int[] prgMem) {
		this.prgMem = prgMem;
	}
	
	public void setPrgRamSize(int ramSize) {
		this.prgRam = new int[ramSize];
		hasPrgRam = true;
	}
	
	public void setChrMem(int[] chrMem) {
		if(chrMem == null) {
			chrMem= new int[0x2000];
			chrBanks = new int[2];
			chrBanks[1] = 0x1000;
		} else {
			this.chrMem = chrMem;
		}
		
	}
	
	public void setPrgMemSize(int prgMemSize) {
		this.prgMemSize = prgMemSize;
		prgMem = new int[prgMemSize];
	}
	
	public void setChrMemSize(int chrMemSize) {
		this.chrMemSize = chrMemSize;
		chrMem = new int[chrMemSize];
	}
	
	public void writePrg(int address, int data) {
		//if(address >= 0x8000) {
		//	prgMem[(address - 0x8000) % prgMemSize] = data;
		//} else 
		if(address >= 0x6000 && hasPrgRam && address <= 0x8000) {
			prgRam[address - 0x6000] = data;
		}
	}
	
	public int readPrg(int address) {
		/*int ret = 0;
		if(address >= 0x8000) {
			ret = prgMem[(address - 0x8000) % prgMemSize];
		}*/
		if(address >= 0x6000 && address < 0x8000 && hasPrgRam) {
			return prgRam[address - 0x6000];
		} else if(address >= 0x8000){
			final int offset = address - 0x8000;
			final int bankNum = offset / prgBankSize;
			return prgMem[prgBanks[bankNum] + (offset - (bankNum * prgBankSize))];
		} else {
			return address >> 8;
		}
	}
	
	public void writeChr(int address, int data) {
		if(address < 0x2000 && chrMem != null) {
			final int offset = address % chrBankSize;
			final int bankNum = address / chrBankSize;
			chrMem[(chrBanks[bankNum] + offset) % chrMem.length] = data;
		}
	}
	
	public int readChr(int address) {
		int ret = 0;
		final int offset = address % chrBankSize;
		final int bankNum = address / chrBankSize;
		if(address < 0x2000) {
			if(chrMem != null) {
				ret = chrMem[(chrBanks[bankNum] + (offset)) % chrMem.length];
			} 
		}
		return ret;
	}
	
	protected void setMirroring(int mirrorType) {
		nes.setMirror(mirrorType);
	}
	
	protected void pullCPUIRQLow() {
		nes.pullCPUIRQLow();
	}
	
	protected void pullCPUIRQHigh() {
		nes.pullCPUIRQHigh();
	}

}
