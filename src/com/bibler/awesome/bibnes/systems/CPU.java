package com.bibler.awesome.bibnes.systems;

import com.bibler.awesome.bibnes.utils.StringUtils;

public class CPU {
	
	final int CARRY_FLAG = 0;
	final int ZERO_FLAG = 1;
	final int INTERRUPT_FLAG = 2;
	final int DECIMAL_FLAG = 3;
	final int BREAK_FLAG = 4;
	final int OVERFLOW_FLAG = 6;
	final int SIGN_FLAG = 7;
	
	
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
	private int cyclesRemaining;
	private int instructionCycles;
	private boolean pageBoundaryFlag;
	
	//Debug
	private int totalCycles;
	
	public CPU(Memory memorySpace) {
		this.memorySpace = memorySpace;
	}
	
	public void cycle() {
		if(cyclesRemaining == 0) {
			fetch();
		} else {
			execute();
		}
		cyclesRemaining--;
		totalCycles++;
		System.out.println("Cycles: " + cyclesRemaining + " Accumulator: " + StringUtils.intToHexString(accumulator) + " Total Cycles: " + totalCycles);
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
	
	public void clearStatusRegister() {
		statusRegister = 0;
	}
	
	public void fillStatusRegister() {
		statusRegister = 0xFF;
	}
	
	private void execute() {
		switch(instruction) {
			case 0x01:
				indexedIndirect();
				ORA();
				break;
			case 0x05:
				zeroPage();
				ORA();
				break;
			case 0x06:
				zeroPage();
				ASL();
				break;
			case 0x09:
				immediate();
				ORA();
				break;
			case 0x0D:
				absolute();
				ORA();
				break;
			case 0x0E:
				absolute();
				ASL();
				break;
			case 0x11:
				indirectIndexed();
				ORA();
				break;
			case 0x15:
				zeroPageIndexed();
				ORA();
				break;
			case 0x16:
				zeroPageIndexed();
				ASL();
				break;
			case 0x18:
				CLC();
				break;
			case 0x19:
				absoluteIndexedY();
				ORA();
				break;
			case 0x1D:
				absoluteIndexedX();
				ORA();
				break;
			case 0x1E:
				absoluteIndexedX();
				ASL();
				break;
			case 0x21:									// AND Indexed Indirect
				indexedIndirect();
				AND();
				break;
			case 0x24:									// BIT Zero Page
				zeroPage();
				BIT();
				break;
			case 0x25:									
				zeroPage();
				AND();
				break;
			case 0x26:
				zeroPage();
				ROL();
				break;
			case 0x29:
				immediate();
				AND();
				break;
			case 0x2A:
				accumulator();
				ROL();
				break;
			case 0x2C:
				absolute();
				BIT();
				break;
			case 0x2D:
				absolute();
				AND();
				break;
			case 0x2E:
				absolute();
				ROL();
				break;
			case 0x31:
				indirectIndexed();
				AND();
				break;
			case 0x35:
				zeroPageIndexed();
				AND();
				break;
			case 0x36:
				zeroPageIndexed();
				ROL();
				break;
			case 0x38:
				SEC();
				break;
			case 0x39:
				absoluteIndexedY();
				AND();
				break;
			case 0x3D:
				absoluteIndexedX();
				AND();
				break;
			case 0x3E:
				absoluteIndexedX();
				ROL();
				break;
			case 0x58:
				CLI();
				break;
			case 0x66:
				zeroPage();
				ROR();
				break;
			case 0x69:									// ADC Immediate
				immediate();
				ADC();
				break;
			case 0x6A:
				accumulator();
				ROR();
				break;
			case 0x6E:
				absolute();
				ROR();
				break;
			case 0x76:
				zeroPageIndexed();
				ROR();
				break;
			case 0x78:
				SEI();
				break;
			case 0x7E:
				absoluteIndexedX();
				ROR();
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
			case 0xB8:
				CLV();
				break;
			case 0xB9:									// LDA Absolute Y
				absoluteIndexedY();
				LDA();
				break;
			case 0xBD:									// LDA Absolute X
				absoluteIndexedX();
				LDA();
				break;
			case 0xD8:
				CLD();
				break;
			case 0xF8:
				SED();
				break;
		}	
		
	}
	
	
	private void fetch() {
		instruction = memorySpace.read(programCounter++);
		instructionCycles = instructionTimes[instruction];
		cyclesRemaining += instructionCycles;
	}
	
	//Instructions
	
	private void ADC() {
		if(cyclesRemaining == 1) {
			final int addResult = (accumulator + dataRegister) + (statusRegister & 1);
			statusRegister ^= (-((addResult >> 8) & 1) ^ statusRegister) & 1;					// set carry flag
			statusRegister ^= (-((addResult >> 7) & 1) ^ statusRegister) & (1 << SIGN_FLAG);			// set sign flag
			statusRegister ^= (-(addResult == 0 ? 1 : 0) ^ statusRegister) & (1 << ZERO_FLAG);		// set zero flag
			final int overflow = ((accumulator ^ addResult) & (dataRegister ^ addResult) & 0x80) == 0 ? 0 : 1;
			statusRegister ^= (-overflow ^ statusRegister) & (1 << OVERFLOW_FLAG);
			accumulator = (addResult & 0xFF);
		}
	}
	
	private void AND() {
		if(cyclesRemaining == 1) {
			accumulator &= dataRegister;
			statusRegister ^= (-((accumulator >> 7) & 1) ^ statusRegister) & (1 << SIGN_FLAG);			// set sign flag
			statusRegister ^= (-(accumulator == 0 ? 1 : 0) ^ statusRegister) & (1 << ZERO_FLAG);		// set zero flag
		}
	}
	
	private void ASL() {
		if(cyclesRemaining == 2) {
			dataRegister = dataRegister << 1;
			statusRegister ^= (-((dataRegister >> 7) & 1) ^ statusRegister) & (1 << SIGN_FLAG);			// set sign flag
			statusRegister ^= (-(dataRegister == 0 ? 1 : 0) ^ statusRegister) & (1 << ZERO_FLAG);		// set zero flag
			statusRegister ^= (-((dataRegister >> 8) & 1) ^ statusRegister) & 1;					// set carry flag
			
		} else if(cyclesRemaining == 1) {
			memorySpace.write(dataCounter, dataRegister);
		}
	}
	
	private void BIT() {
		if(cyclesRemaining == 1) {
			int result = dataRegister & accumulator;
			statusRegister ^= (-(result == 0 ? 1 : 0) ^ statusRegister) & (1 << ZERO_FLAG);		// set zero flag
			statusRegister ^= (-(dataRegister >> 7 & 1) ^ statusRegister) & (1 << SIGN_FLAG);
			statusRegister ^= (-(dataRegister >> 6 & 1) ^ statusRegister) & (1 << OVERFLOW_FLAG);
			
		}
	}
	
	private void CLC() {
		if(cyclesRemaining == 1) {
			statusRegister &= ~1;
		}
	}
	
	private void SEC() {
		if(cyclesRemaining == 1) {
			statusRegister |= 1;
		}
	}
	
	private void CLI() {
		if(cyclesRemaining == 1) {
			statusRegister &= ~(1 << INTERRUPT_FLAG);
		}
	}
	
	private void SEI() {
		if(cyclesRemaining == 1) {
			statusRegister |= (1 << INTERRUPT_FLAG);
		}
		
	}
	
	private void CLV() {
		if(cyclesRemaining == 1) {
			statusRegister &= ~(1 << OVERFLOW_FLAG);
		}
	}
	
	private void CLD() {
		if(cyclesRemaining == 1) {
			statusRegister &= ~(1 << DECIMAL_FLAG);
		}
	}
	
	private void SED() {
		if(cyclesRemaining == 1) {
			statusRegister |= (1 << DECIMAL_FLAG);
		}
	}
	
	private void LDA() {
		if(cyclesRemaining == 1) {
			accumulator = dataRegister;
		}
	}
	
	private void ORA() {
		if(cyclesRemaining == 1) {
			accumulator |= dataRegister;
			statusRegister ^= (-((accumulator >> 7) & 1) ^ statusRegister) & (1 << SIGN_FLAG);			// set sign flag
			statusRegister ^= (-(accumulator == 0 ? 1 : 0) ^ statusRegister) & (1 << ZERO_FLAG);		// set zero flag
		}
	}
	
	private void ROL() {
		if(cyclesRemaining == 1) {
			dataRegister = dataRegister << 1;
			int carryFlag = statusRegister & 1;
			dataRegister ^= (-carryFlag ^ dataRegister) & 1;
			statusRegister ^= (-((dataRegister >> 8) & 1) ^ statusRegister) & 1;			// set carry flag
			dataRegister &= 0xFF;
			if(instruction == 0x2A) {
				accumulator = dataRegister;
			} else {
				memorySpace.write(dataCounter, dataRegister);
			}
		}
	}
	
	private void ROR() {
		if(cyclesRemaining == 1) {
			int carryFlag = statusRegister & 1;
			statusRegister ^= (-((dataRegister) & 1) ^ statusRegister) & 1;			// set carry flag
			dataRegister = dataRegister >> 1;
			dataRegister ^= (-carryFlag ^ dataRegister) & (1 << 7);
			dataRegister &= 0xFF;
			if(instruction == 0x6A) {
				accumulator = dataRegister;
			} else {
				memorySpace.write(dataCounter, dataRegister);
			}
		}
	}
	
	//Address modes
	private void accumulator() {
		if(cyclesRemaining == instructionCycles - 1) {
			dataRegister = accumulator;
		}
	}
	
	private void immediate() {
		if(cyclesRemaining == instructionCycles - 1) {
			dataCounter = programCounter++;
			dataRegister = memorySpace.read(dataCounter);
		}
	}

	
	private void relative() {
		
	}
	
	private void absolute() {
		if(cyclesRemaining == instructionCycles - 1) {
			dataCounter = memorySpace.read(programCounter++);
		} else if(cyclesRemaining == instructionCycles - 2) {
			dataCounter |= memorySpace.read(programCounter++) << 8;
		} else if(cyclesRemaining == instructionCycles - 3) {
			dataRegister = memorySpace.read(dataCounter);
		}
		
	}
	
	private void zeroPage() {
		if(cyclesRemaining == instructionCycles - 1) {
			dataCounter = memorySpace.read(programCounter++);
		} else if(cyclesRemaining == instructionCycles - 2) {
			dataRegister = memorySpace.read(dataCounter);
		}
	}
	
	private void indirect() {
		
	}
	
	private void absoluteIndexedX() {
		if(cyclesRemaining == instructionCycles - 1) {
			dataCounter = memorySpace.read(programCounter++);
		} else if(cyclesRemaining == instructionCycles - 2) {
			dataCounter |= memorySpace.read(programCounter++) << 8;
		} else if(cyclesRemaining == instructionCycles - 3) {
			if((dataCounter + indexX & 0xFF00) != (dataCounter & 0xFF00) && !pageBoundaryFlag && instruction != 0x1E) {
				cyclesRemaining++;
				pageBoundaryFlag = true;
			} else {
				pageBoundaryFlag = false;
				dataCounter = dataCounter + indexX;
				dataRegister = memorySpace.read(dataCounter);
			}
		}
		
	}
	
	private void absoluteIndexedY() {
		if(cyclesRemaining == instructionCycles - 1) {
			dataCounter = memorySpace.read(programCounter++);
		} else if(cyclesRemaining == instructionCycles - 2) {
			dataCounter |= memorySpace.read(programCounter++) << 8;
		} else if(cyclesRemaining == instructionCycles - 3) {
			if((dataCounter + indexY & 0xFF00) != (dataCounter & 0xFF00) && !pageBoundaryFlag) {
				cyclesRemaining++;
				pageBoundaryFlag = true;
			} else {
				pageBoundaryFlag = false;
				dataCounter = dataCounter + indexY;
				dataRegister = memorySpace.read(dataCounter);
			}
		}
	}
	
	private void zeroPageIndexed() {
		if(cyclesRemaining == instructionCycles - 1) {
			dataCounter = memorySpace.read(programCounter++);
		} else if(cyclesRemaining == instructionCycles - 2) {
			dataCounter = (dataCounter + indexX) & 0xFF;
		} else if(cyclesRemaining == instructionCycles - 3) {
			dataRegister = memorySpace.read(dataCounter);
		}
	}
	
	private void indexedIndirect() {
		if(cyclesRemaining == instructionCycles - 1) {
			dataCounter = memorySpace.read(programCounter++);
		} else if(cyclesRemaining == instructionCycles - 2) {
			dataRegister = (dataCounter + indexX) & 0xFF;
		} else if(cyclesRemaining == instructionCycles - 3) {
			dataCounter = memorySpace.read(dataRegister);
		} else if(cyclesRemaining == instructionCycles - 4) {
			dataCounter |= memorySpace.read(dataRegister + 1) << 8;
		} else if(cyclesRemaining == instructionCycles - 5) {
			dataRegister = memorySpace.read(dataCounter);
		}
	}
	
	private void indirectIndexed() {
		if(cyclesRemaining == instructionCycles - 1) {
			dataRegister = memorySpace.read(programCounter++);
		} else if(cyclesRemaining == instructionCycles - 2) {
			dataCounter = memorySpace.read(dataRegister);
		} else if(cyclesRemaining == instructionCycles - 3) {
			dataCounter |= memorySpace.read(dataRegister + 1) << 8;
		} else if(cyclesRemaining == instructionCycles - 4) {
			if((dataCounter + indexY & 0xFF00) != (dataCounter & 0xFF00) && !pageBoundaryFlag) {
				cyclesRemaining++;
				pageBoundaryFlag = true;
			} else {
				pageBoundaryFlag = false;
				dataCounter = dataCounter + indexY;
				dataRegister = memorySpace.read(dataCounter);
			}
		}
	}
	
	private int[] instructionTimes = new int[] {
		//  0 1 2 3 4 5 6 7 8 9 A B C D E F	
			0,6,0,0,0,3,5,0,0,2,0,0,0,4,6,0,	// 0
			0,5,0,0,0,4,6,0,2,4,0,0,0,4,7,0,	// 1
			0,6,0,0,3,3,5,0,0,2,2,0,4,4,6,0,	// 2
			0,5,0,0,0,4,6,0,2,4,0,0,0,4,7,0,	// 3
			0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,	// 4
			0,0,0,0,0,0,0,0,2,0,0,0,0,0,0,0,	// 5
			0,0,0,0,0,0,5,0,0,2,2,0,0,0,6,0,	// 6
			0,0,0,0,0,0,6,0,2,0,0,0,0,0,7,0,	// 7
			0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,	// 8
			0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,	// 9
			0,6,0,0,0,3,0,0,0,2,0,0,0,4,0,0,	// A
			0,5,0,0,0,4,0,0,2,4,0,0,0,4,0,0,	// B
			0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,	// C
			0,0,0,0,0,0,0,0,2,0,0,0,0,0,0,0,	// D
			0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,	// E
			0,0,0,0,0,0,0,0,2,0,0,0,0,0,0,0		// F
			
	};

}
