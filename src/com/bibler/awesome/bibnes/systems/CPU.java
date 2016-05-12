package com.bibler.awesome.bibnes.systems;

public class CPU {
	
	
	//Registers
	private int accumulator;
	private int dataRegister;
	private int dataCounter;
	private int indexX;
	private int indexY;
	private int instruction;
	private int programCounter;
	private int stackPointer;
	private int statusRegister;
	
	
	//External Logic
	private Memory ROM;
	
	//Internal Logic
	private Memory RAM;
	
	//Control variables
	private int cycles;
	
	//Debug
	private int totalCycles;
	
	public CPU(Memory ROM, int ramSize) {
		this.ROM = ROM;
		RAM = new Memory(ramSize);
	}
	
	
	public void cycle() {
		if(cycles == 0) {
			fetch();
		} else {
			execute();
		}
		cycles--;
		totalCycles++;
		System.out.println("Cycles: " + cycles + " Accumulator: " + accumulator + " Total Cycles: " + totalCycles);
	}
	
	private void execute() {
		switch(instruction) {
		case 0x44:									// LDA Immediate
			immediate();
			LDA();
		break;
		}
	}
	
	
	private void fetch() {
		instruction = ROM.read(programCounter++);
		cycles += instructionCycles[instruction];
	}
	
	//Instructions
	
	private void LDA() {
		if(cycles == 1) {
			accumulator = dataRegister;
		}
	}
	
	//Address modes
	
	
	private void accumulator() {
		
	}
	
	private void immediate() {
		if(cycles == 1) {
			dataRegister = ROM.read(programCounter++);
		}
	}
	
	private void implied() {
		
	}
	
	private void relative() {
		
	}
	
	private void absolute() {
		
	}
	
	private void zeroPage() {
		
	}
	
	private void indirect() {
		
	}
	
	private void abosluteIndexed() {
		
	}
	
	private void zeroPageIndexed() {
		
	}
	
	private void indexedIndirect() {
		
	}
	
	private void indirectIndexed() {
		
	}
	
	private int[] instructionCycles = new int[] {
			0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
			0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
			0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
			0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
			0,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,
			0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
			0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
			0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
			0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
	};

}
