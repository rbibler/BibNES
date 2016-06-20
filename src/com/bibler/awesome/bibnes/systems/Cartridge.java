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
