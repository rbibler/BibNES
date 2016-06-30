package com.bibler.awesome.bibnes.systems;

import com.bibler.awesome.bibnes.communications.Notifiable;

public class MosBoard extends Motherboard {
	
	Memory CPURam = new Memory(0x800);
	Memory CPURom;
	
	public MosBoard() {
		super();
		cpu = new CPU(this);
	}
	
	@Override
	public void power() {
		//cpu.powerOn();
		cpu.setProgramCounter(0x800);
		cpu.resetCPU();
	}
	
	@Override
	public void registerObjectToNotify(Notifiable notifiable) {
		cpu.registerObjectToNotify(notifiable);
	}
	
	@Override
	public void setROM(Memory CPURom) {
		this.CPURom = CPURom;
	}
	
	@Override
	public int read(int addressToRead) {
		if(addressToRead < 0x800) {
			return CPURam.read(addressToRead);
		} else {
			return CPURom.read(addressToRead);
		}
	}
	
	@Override
	public void write(int addressToWrite, int data) {
		if(addressToWrite < 0x800) {
			CPURam.write(addressToWrite, data);
		} else {
			CPURom.write(addressToWrite, data);
		}
	}
	
	@Override
	public void step() {
		do {
			cpu.cycle();
		} while(cpu.getCyclesRemaining() > 0);
	}

}
