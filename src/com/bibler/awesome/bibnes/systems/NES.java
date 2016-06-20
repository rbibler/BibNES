package com.bibler.awesome.bibnes.systems;

import com.bibler.awesome.bibnes.communications.Notifiable;

public class NES extends Motherboard{
	
	private PPU ppu;
	private APU apu;
	private Memory cpuRam;
	private Memory ppuRam;
	private Cartridge cart;
	
	private int cycleCount;
	
	public NES() {
		cpu = new CPU(this);
		ppu = new PPU();
		apu = new APU();
		cpuRam = new Memory(0x800);
	}
	
	@Override
	public void registerObjectToNotify(Notifiable objectToNotify) {
		cpu.registerObjectToNotify(objectToNotify);
		cpuRam.registerObject(objectToNotify);
	}
	
	public void setCart(Cartridge cart) {
		this.cart = cart;
	}
	
	@Override
	public void cycle() {
		//ppu.cycle();
		if(cycleCount % 3 == 0) {
			cpu.cycle();
		}
		cycleCount++;
	}
	
	@Override
	public void step() {
		do {
			cycle();
		} while(cpu.getCyclesRemaining() > 0);
		cycle();
		cycle();
		cycle();
		System.out.println("Stepped");
	}
	
	public void writeToRam(int addressToWrite, int data) {
		cpuRam.write(addressToWrite, data);
	}
	
	public void writeToPPU(int addressToWrite, int data) {
		ppu.write(addressToWrite, data);
	}
	
	public void writeToAPU(int addressToWrite, int data) {
		apu.write(addressToWrite, data);
	}
	
	public void writeToCart(int addressToWrite, int data) {
		cart.writePrg(addressToWrite, data);
	}
	
	public int readFromRam(int addressToRead) {
		return cpuRam.read(addressToRead);
	}
	
	public int readFromPPU(int addressToRead) {
		return ppu.read(addressToRead);
	}
	
	public int readFromAPU(int addressToRead) {
		return apu.read(addressToRead);
	}
	
	public int readFromCart(int addressToRead) {
		return cart.readPrg(addressToRead);
	}
	
	@Override
	public void write(int addressToWrite, int data) {
		if(addressToWrite < 0x1000) {
			writeToRam(addressToWrite, data);
		} else if(addressToWrite >= 0x2000 && addressToWrite < 0x4000) {
			writeToPPU(addressToWrite, data);
		} else if(addressToWrite >= 0x4000 && addressToWrite < 0x4020) {
			writeToAPU(addressToWrite, data);
		} else {
			writeToCart(addressToWrite, data);
		}
	}
	
	@Override
	public int read(int addressToRead) {
		int retVal = 0;
		if(addressToRead < 0x1000) {
			retVal = readFromRam(addressToRead);
		} else if(addressToRead >= 0x2000 && addressToRead < 0x4000) {
			retVal = readFromPPU(addressToRead);
		} else if(addressToRead >= 0x4000 && addressToRead < 0x4020) {
			retVal = readFromAPU(addressToRead);
		} else {
			retVal = readFromCart(addressToRead);
		}
		return retVal;
	}
	
	

}
