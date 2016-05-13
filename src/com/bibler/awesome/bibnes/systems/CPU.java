package com.bibler.awesome.bibnes.systems;

import com.bibler.awesome.bibnes.utils.StringUtils;

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
	
	
	//Memory map; contains ROM and RAM
	private Memory memorySpace;
	
	//Control variables
	private int cycles;
	private boolean pageBoundaryFlag;
	
	//Debug
	private int totalCycles;
	
	public CPU(Memory memorySpace) {
		this.memorySpace = memorySpace;
	}
	
	public void cycle() {
		if(cycles == 0) {
			fetch();
		} else {
			execute();
		}
		cycles--;
		totalCycles++;
		System.out.println("Cycles: " + cycles + " Accumulator: " + StringUtils.intToHexString(accumulator) + " Total Cycles: " + totalCycles);
	}
	
	public void powerOn() {
		programCounter = memorySpace.read(0xFFFC) | memorySpace.read(0xFFFD) << 8;
		statusRegister = 0;
	}
	
	public void setAccumulator(int accumulator) {
		this.accumulator = accumulator;
	}
	
	public void setIndexX(int x) {
		this.indexX = x;
	}
	
	public void setIndexY(int y) {
		this.indexY = y;
	}
	
	public int getAccumulator() {
		return accumulator;
	}
	
	public int getDataCounter() {
		return dataCounter;
	}
	
	public int getDataRegister() {
		return dataRegister;
	}
	
	public int getStatusRegister() {
		return statusRegister;
	}
	
	public int getTotalCycles() {
		return totalCycles;
	}
	
	private void execute() {
		switch(instruction) {
			case 0x69:									// ADC Immediate
				immediate();
				ADC();
				break;
			case 0xA1:
				indexedIndirect();
				LDA();
				break;
			case 0xA5:									// LDA Zero Page
				zeroPage();
				LDA();
				break;
			case 0xA9:									// LDA Immediate
				immediate();
				LDA();
				break;
			case 0xAD:									// LDA Absolute
				absolute();
				LDA();
				break;
			case 0xB1:
				indirectIndexed();
				LDA();
				break;
			case 0xB5:									// LDA Zero Page X
				zeroPageIndexed();
				LDA();
				break;
			case 0xB9:									// LDA Absolute Y
				absoluteIndexedY();
				LDA();
				break;
			case 0xBD:									// LDA Absolute X
				absoluteIndexedX();
				LDA();
				break;
		}	
		
	}
	
	
	private void fetch() {
		instruction = memorySpace.read(programCounter++);
		cycles += instructionCycles[instruction];
	}
	
	//Instructions
	
	private void ADC() {
		if(cycles == 1) {
			final int addResult = (accumulator + dataRegister) + (statusRegister & 1);
			statusRegister ^= (-((addResult >> 8) & 1) ^ statusRegister) & 1;					// set carry flag
			statusRegister ^= (-((addResult >> 7) & 1) ^ statusRegister) & (1 << 6);			// set sign flag
			statusRegister ^= (-(addResult == 0 ? 1 : 0) ^ statusRegister) & (1 << 1);		// set zero flag
			final int overflow = ((accumulator ^ addResult) & (dataRegister ^ addResult) & 0x80) == 0 ? 0 : 1;
			statusRegister ^= (-overflow ^ statusRegister) & (1 << 5);
			accumulator = (addResult & 0xFF);
		}
	}
	
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
			dataCounter = programCounter++;
			dataRegister = memorySpace.read(dataCounter);
		}
	}
	
	private void implied() {
		
	}
	
	private void relative() {
		
	}
	
	private void absolute() {
		if(cycles == 3) {
			dataCounter = memorySpace.read(programCounter++);
		} else if(cycles == 2) {
			dataCounter |= memorySpace.read(programCounter++) << 8;
		} else if(cycles == 1) {
			dataRegister = memorySpace.read(dataCounter);
		}
		
	}
	
	private void zeroPage() {
		if(cycles == 2) {
			dataCounter = memorySpace.read(programCounter++);
		} else if(cycles == 1) {
			dataRegister = memorySpace.read(dataCounter);
		}
	}
	
	private void indirect() {
		
	}
	
	private void absoluteIndexedX() {
		if(cycles == 3) {
			dataCounter = memorySpace.read(programCounter++);
		} else if(cycles == 2) {
			dataCounter |= memorySpace.read(programCounter++) << 8;
		} else if(cycles == 1) {
			if((dataCounter + indexX & 0xFF00) != (dataCounter & 0xFF00) && !pageBoundaryFlag) {
				cycles++;
				pageBoundaryFlag = true;
			} else {
				pageBoundaryFlag = false;
				dataCounter = dataCounter + indexX;
				dataRegister = memorySpace.read(dataCounter);
			}
		}
		
	}
	
	private void absoluteIndexedY() {
		if(cycles == 3) {
			dataCounter = memorySpace.read(programCounter++);
		} else if(cycles == 2) {
			dataCounter |= memorySpace.read(programCounter++) << 8;
		} else if(cycles == 1) {
			if((dataCounter + indexY & 0xFF00) != (dataCounter & 0xFF00) && !pageBoundaryFlag) {
				cycles++;
				pageBoundaryFlag = true;
			} else {
				pageBoundaryFlag = false;
				dataCounter = dataCounter + indexY;
				dataRegister = memorySpace.read(dataCounter);
			}
		}
	}
	
	private void zeroPageIndexed() {
		if(cycles == 3) {
			dataCounter = memorySpace.read(programCounter++);
		} else if(cycles == 2) {
			dataCounter = (dataCounter + indexX) & 0xFF;
		} else if(cycles == 1) {
			dataRegister = memorySpace.read(dataCounter);
		}
	}
	
	private void indexedIndirect() {
		if(cycles == 5) {
			dataCounter = memorySpace.read(programCounter++);
		} else if(cycles == 4) {
			dataRegister = (dataCounter + indexX) & 0xFF;
		} else if(cycles == 3) {
			dataCounter = memorySpace.read(dataRegister);
		} else if(cycles == 2) {
			dataCounter |= memorySpace.read(dataRegister + 1) << 8;
		} else if(cycles == 1) {
			dataRegister = memorySpace.read(dataCounter);
		}
	}
	
	private void indirectIndexed() {
		if(cycles == 4) {
			dataRegister = memorySpace.read(programCounter++);
		} else if(cycles == 3) {
			dataCounter = memorySpace.read(dataRegister);
		} else if(cycles == 2) {
			dataCounter |= memorySpace.read(dataRegister + 1) << 8;
		} else if(cycles == 1) {
			if((dataCounter + indexY & 0xFF00) != (dataCounter & 0xFF00) && !pageBoundaryFlag) {
				cycles++;
				pageBoundaryFlag = true;
			} else {
				pageBoundaryFlag = false;
				dataCounter = dataCounter + indexY;
				dataRegister = memorySpace.read(dataCounter);
			}
		}
	}
	
	private int[] instructionCycles = new int[] {
			0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,											//0-F
			0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,											//10-1F
			0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,											//20-2F
			0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,											//30-3F
			0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,											//40-4F
			0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,											//50-5F
			0,0,0,0,0,0,0,0,0,2,0,0,0,0,0,0,											//60-6F
			0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,											//70-7F
			0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,											//80-8F
			0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,											//90-9F
			0,6,0,0,0,3,0,0,0,2,0,0,0,4,0,0,											//A0-AF
			0,5,0,0,0,4,0,0,0,4,0,0,0,4,0,0,											//B0-BF
			0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,											//C0-CF
			0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,											//D0-DF
			0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,											//E0-EF
			
	};

}
