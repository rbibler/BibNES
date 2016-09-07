package com.bibler.awesome.bibnes.mappers;

import com.bibler.awesome.bibnes.systems.NES;

public class Mapper {
	
	protected int[] prgMem;
	protected int[] chrMem;
	protected int[] prgRam;
	protected int[] chrRam;
	
	protected int[] prgBanks;
	protected int[] chrBanks;
	
	protected int prgMemSize;
	protected int chrMemSize;
	
	protected NES nes;
	
	
	public Mapper() {
		
	}
	
	public void setNES(NES nes) {
		this.nes = nes;
	}
	
	public static Mapper getMapper(int mapperNum) {
		switch(mapperNum) {
			case 0:
				return new Mapper();
			case 1:
				return new MMC1();
			case 2:
				return new Mapper002();
			case 9:
				return new Mapper009();
			default:
				return new Mapper();
		}
	}
	
	public void setPrgMem(int[] prgMem) {
		this.prgMem = prgMem;
	}
	
	public void setChrMem(int[] chrMem) {
		if(chrMem == null) {
			chrRam = new int[0x2000];
		}
		this.chrMem = chrMem;
		
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
		if(address >= 0x8000) {
			prgMem[(address - 0x8000) % prgMemSize] = data;
		}
	}
	
	public int readPrg(int address) {
		int ret = 0;
		if(address >= 0x8000) {
			ret = prgMem[(address - 0x8000) % prgMemSize];
		}
		return ret;
	}
	
	public void writeChr(int address, int data) {
		if(address < 0x2000 && chrRam != null) {
			chrRam[address % chrRam.length] = data;
		}
	}
	
	public int readChr(int address) {
		int ret = 0;
		if(address < 0x2000) {
			if(chrMem != null) {
				ret = chrMem[address % chrMemSize];
			} else if(chrRam != null) {
				ret = chrRam[address % chrRam.length];
			}
		}
		return ret;
	}
	
	protected void setMirroring(int mirrorType) {
		nes.setMirror(mirrorType);
	}

}
