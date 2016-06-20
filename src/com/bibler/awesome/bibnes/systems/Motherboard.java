package com.bibler.awesome.bibnes.systems;

import com.bibler.awesome.bibnes.communications.Notifiable;

public class Motherboard {
	
	CPU cpu;
	
	public void write(int addressToWrite, int data) {
		
	}
	
	public void registerObjectToNotify(Notifiable notifiable) {}
	
	public int read(int addressToRead) {
		return 0;
	}
	
	public void step() {}
	
	public void cycle() {}
	
	public void reset() {
		cpu.resetCPU();
	}
	
	public void power() {
		cpu.powerOn();
	}
	
	

}
