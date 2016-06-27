package com.bibler.awesome.bibnes.systems;

public class Cartridge {
	
	private Memory PRGRom;
	private Memory CHRRom;
	
	public Cartridge(Memory PRGRom, Memory CHRRom) {
		this.PRGRom = PRGRom;
		this.CHRRom = CHRRom;
	}
	
	public static Cartridge createCartridge(int mapper, Memory PRGRom, Memory CHRRom) {
		switch(mapper) {
		case 0:
			return new Cartridge(PRGRom, CHRRom);
		}
		return null;
	}
	
	public static Cartridge createCartridge(int mapper, int prgSize, int chrSize, Memory rawMem) {
		switch(mapper) {
			case 0:
				Memory prg = new Memory(prgSize);
				Memory chr = new Memory(chrSize);
				for(int i = 0; i < prgSize; i++) {
					prg.write(i, rawMem.read(i));
				}
				for(int i = 0; i < chrSize; i++) {
					chr.write(i, rawMem.read(i + prgSize));
				}
				return new Cartridge(prg, chr);
		}
		return null;
	}
	
	public Memory getPrgMem() {
		return PRGRom;
	}
	
	public Memory getChrMem() {
		return CHRRom;
	}
	
	public Memory getCombinedRoms() {
		Memory mem = new Memory(PRGRom.size() + CHRRom.size());
		for(int i = 0; i < PRGRom.size(); i++) {
			mem.write(i, PRGRom.read(i));
		}
		int prgSize = PRGRom.size();
		for(int i = 0; i < CHRRom.size(); i++) {
			mem.write(i + prgSize, CHRRom.read(i));
		}
		return mem;
	}
	
	public void writePrg(int addressToWrite, int data) {
		PRGRom.write(addressToWrite, data);
	}
	
	public void writeCHR(int addressToWrite, int data) {
		CHRRom.write(addressToWrite, data);
	}
	
	public int readPrg(int addressToRead) {
		return PRGRom.read(addressToRead);
	}
	
	public int readCHR(int addressToRead) {
		return CHRRom.read(addressToRead);
	}
	
	

}
