package com.bibler.awesome.bibnes.systems;

import com.bibler.awesome.bibnes.assembler.BreakpointManager;
import com.bibler.awesome.bibnes.communications.Notifiable;

public class Motherboard {
	
	CPU cpu;
	BreakpointManager breakpoints;
	
	public void write(int addressToWrite, int data) {
		
	}
	
	public void registerObjectToNotify(Notifiable notifiable) {}
	
	public int read(int addressToRead) {
		return 0;
	}
	
	public void step() {}
	
	public void stepNext() {}
	
	public void cycle() {}
	
	public void reset() {
		cpu.resetCPU();
	}
	
	public void power() {
		
	}
	
	public void runSystem() {}
	
	public void pause() {}
	
	public void resume() {}
	
	public void setROM(Memory rom) {}
	
	public void setBreakpointManager(BreakpointManager breakpoints) {
		this.breakpoints = breakpoints;
	}
	
	

}
