package com.bibler.awesome.bibnes.tests;

import com.bibler.awesome.bibnes.systems.CPU;
import com.bibler.awesome.bibnes.systems.Memory;

import junit.framework.TestCase;

public class InstructionTest extends TestCase {
	
	private CPU cpu;
	private Memory rom;
	
	final int CARRY_FLAG = 0;
	final int ZERO_FLAG = 1;
	final int INTERRUPT_FLAG = 2;
	final int DECIMAL_FLAG = 3;
	final int BREAK_FLAG = 4;
	final int OVERFLOW_FLAG = 6;
	final int SIGN_FLAG = 7;
	
	protected void initializeCPU() {
		initializeRom();
		cpu = new CPU(rom);
	}
	
	private void initializeRom() {
		if(rom != null) {
			return;
		}
		rom = new Memory(0x8000);
	}
	
	protected void resetCPU() {
		if(rom == null) {
			initializeRom();
		}
		rom.write(0xFFFC, 0x40);
		rom.write(0xFFFD, 0);
		cpu.powerOn();
	}
	
	protected void runNCycles(int cyclesToRun) {
		for(int i = 0; i < cyclesToRun; i++) {
			cpu.cycle();
		}
	}
	
	protected void writeRom(int address, int data) {
		rom.write(address, data);
	}
	
	protected int readRom(int address) {
		return rom.read(address);
	}
	
	protected void setXIndex(int index) {
		cpu.setIndexX(index);
	}
	
	protected void setYIndex(int index) {
		cpu.setIndexY(index);
	}
	
	protected void setAccumulator(int accumulator) {
		cpu.setAccumulator(accumulator);
	}
	
	protected int getAccumulator() {
		return cpu.getAccumulator();
	}
	
	protected int getStatusRegister() {
		return cpu.getStatusRegister();
	}
	
	protected void clearStatusRegister() {
		cpu.clearStatusRegister();
	}
	
	protected void fillStatusRegister() {
		cpu.fillStatusRegister();
	}

}
