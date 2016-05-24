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
	final int NMI = 0x4E4D49;
	
	
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
	private boolean NMIFlag;
	
	//Debug
	private int totalCycles;
	
	public CPU(Memory memorySpace) {
		this.memorySpace = memorySpace;
	}
	
	public void cycle() {
		if(cyclesRemaining == 0) {
			if(NMIFlag) {
				NMIFlag = false;
				cyclesRemaining = 6;
				instruction = NMI;
			} else {
				fetch();
			}
		} else {
			execute();
		}
		cyclesRemaining--;
		totalCycles++;
		System.out.println("Cycles: " + cyclesRemaining + " Accumulator: " + StringUtils.intToHexString(accumulator) + " Total Cycles: " + totalCycles);
	}
	
	public void powerOn() {
		programCounter = memorySpace.read(0xFFFC) | memorySpace.read(0xFFFD) << 8;
		resetCPU();
	}
	
	public void resetCPU() {
		statusRegister = 0;
		stackPointer = 0xFF;
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
	
	public void setProgramCounter(int programCounter) {
		this.programCounter = programCounter;
	}
	
	public int getProgramCounter() {
		return programCounter;
	}
	
	public int getStackPointer() {
		return stackPointer;
	}
	
	public void setStackPointer(int stackPointer) {
		this.stackPointer = stackPointer;
	}
	
	public int getYIndex() {
		return indexY;
	}
	
	public int getXIndex() {
		return indexX;
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
	
	public void setNMI() {
		NMIFlag = true;
	}
	
	private void execute() {
		switch(instruction) {
			case 0x00:
				BRK();
				break;
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
			case 0x08:
				PHP();
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
			case 0x10:
				relative();
				BPL();
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
			case 0x20:
				absolute();
				JSR();
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
			case 0x28:
				PLP();
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
			case 0x30:
				relative();
				BMI();
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
			case 0x40:
				RTI();
				break;
			case 0x41:
				indexedIndirect();
				EOR();
				break;
			case 0x45:
				zeroPage();
				EOR();
				break;
			case 0x46:
				zeroPage();
				LSR();
				break;
			case 0x48:
				PHA();
				break;
			case 0x49:
				immediate();
				EOR();
				break;
			case 0x4A:
				accumulator();
				LSR();
				break;
			case 0x4C:
				absolute();
				JMP();
				break;
			case 0x4D:
				absolute();
				EOR();
				break;
			case 0x4E:
				absolute();
				LSR();
				break;
			case 0x50:
				relative();
				BVC();
				break;
			case 0x51:
				indirectIndexed();
				EOR();
				break;
			case 0x55:
				zeroPageIndexed();
				EOR();
				break;
			case 0x56:
				zeroPageIndexed();
				LSR();
				break;
			case 0x58:
				CLI();
				break;
			case 0x59:
				absoluteIndexedY();
				EOR();
				break;
			case 0x5D:
				absoluteIndexedX();
				EOR();
				break;
			case 0x5E:
				absoluteIndexedX();
				LSR();
				break;
			case 0x60:
				RTS();
				break;
			case 0x61:
				indexedIndirect();
				ADC();
				break;
			case 0x65:
				zeroPage();
				ADC();
				break;
			case 0x66:
				zeroPage();
				ROR();
				break;
			case 0x68:
				PLA();
				break;
			case 0x69:									// ADC Immediate
				immediate();
				ADC();
				break;
			case 0x6A:
				accumulator();
				ROR();
				break;
			case 0x6C:
				indirect();
				JMP();
				break;
			case 0x6D:
				absolute();
				ADC();
				break;
			case 0x6E:
				absolute();
				ROR();
				break;
			case 0x70:
				relative();
				BVS();
				break;
			case 0x71:
				indirectIndexed();
				ADC();
				break;
			case 0x75:
				zeroPageIndexed();
				ADC();
				break;
			case 0x76:
				zeroPageIndexed();
				ROR();
				break;
			case 0x78:
				SEI();
				break;
			case 0x79:
				absoluteIndexedY();
				ADC();
				break;
			case 0x7D:
				absoluteIndexedX();
				ADC();
				break;
			case 0x7E:
				absoluteIndexedX();
				ROR();
				break;
			case 0x81:
				indexedIndirect();
				STA();
				break;
			case 0x84:
				zeroPage();
				STY();
				break;
			case 0x85:
				zeroPage();
				STA();
				break;
			case 0x86:
				zeroPage();
				STX();
				break;
			case 0x88:
				DEY();
				break;
			case 0x8A:
				TXA();
				break;
			case 0x8C:
				absolute();
				STY();
				break;
			case 0x8D:
				absolute();
				STA();
				break;
			case 0x8E:
				absolute();
				STX();
				break;
			case 0x90:
				relative();
				BCC();
				break;
			case 0x91:
				indirectIndexed();
				STA();
				break;
			case 0x94:
				zeroPageIndexed();
				STY();
				break;
			case 0x95:
				zeroPageIndexed();
				STA();
				break;
			case 0x96:
				zeroPageIndexedY();
				STX();
				break;
			case 0x98:
				TYA();
				break;
			case 0x99:
				absoluteIndexedY();
				STA();
				break;
			case 0x9A:
				TXS();
				break;
			case 0x9D:
				absoluteIndexedX();
				STA();
				break;
			case 0xA0:
				immediate();
				LDY();
				break;
			case 0xA1:
				indexedIndirect();
				LDA();
				break;
			case 0xA2:
				immediate();
				LDX();
				break;
			case 0xA4:
				zeroPage();
				LDY();
				break;
			case 0xA5:									// LDA Zero Page
				zeroPage();
				LDA();
				break;
			case 0xA6:
				zeroPage();
				LDX();
				break;
			case 0xA8:
				TAY();
				break;
			case 0xA9:									// LDA Immediate
				immediate();
				LDA();
				break;
			case 0xAA:
				TAX();
				break;
			case 0xAC:
				absolute();
				LDY();
				break;
			case 0xAD:									// LDA Absolute
				absolute();
				LDA();
				break;
			case 0xAE:
				absolute();
				LDX();
				break;
			case 0xB0:
				relative();
				BCS();
				break;
			case 0xB1:
				indirectIndexed();
				LDA();
				break;
			case 0xB4:
				zeroPageIndexed();
				LDY();
				break;
			case 0xB5:									// LDA Zero Page X
				zeroPageIndexed();
				LDA();
				break;
			case 0xB6:
				zeroPageIndexedY();
				LDX();
				break;
			case 0xB8:
				CLV();
				break;
			case 0xB9:									// LDA Absolute Y
				absoluteIndexedY();
				LDA();
				break;
			case 0xBA:
				TSX();
				break;
			case 0xBC:
				absoluteIndexedX();
				LDY();
				break;
			case 0xBD:									// LDA Absolute X
				absoluteIndexedX();
				LDA();
				break;
			case 0xBE:
				absoluteIndexedY();
				LDX();
				break;
			case 0xC0:
				immediate();
				CPY();
				break;
			case 0xC1:
				indexedIndirect();
				CMP();
				break;
			case 0xC4:
				zeroPage();
				CPY();
				break;
			case 0xC5:
				zeroPage();
				CMP();
				break;
			case 0xC6:
				zeroPage();
				DEC();
				break;
			case 0xC8:
				INY();
				break;
			case 0xC9:
				immediate();
				CMP();
				break;
			case 0xCA:
				DEX();
				break;
			case 0xCC:
				absolute();
				CPY();
				break;
			case 0xCD:
				absolute();
				CMP();
				break;
			case 0xCE:
				absolute();
				DEC();
				break;
			case 0xD0:
				relative();
				BNE();
				break;
			case 0xD1:
				indexedIndirect();
				CMP();
				break;
			case 0xD5:
				zeroPageIndexed();
				CMP();
				break;
			case 0xD6:
				zeroPageIndexed();
				DEC();
				break;
			case 0xD8:
				CLD();
				break;
			case 0xD9:
				absoluteIndexedY();
				CMP();
				break;
			case 0xDD:
				absoluteIndexedX();
				CMP();
				break;
			case 0xDE:
				absoluteIndexedX();
				DEC();
				break;
			case 0xE0:
				immediate();
				CPX();
				break;
			case 0xE1:
				indexedIndirect();
				SBC();
				break;
			case 0xE4:
				zeroPage();
				CPX();
				break;
			case 0xE5:
				zeroPage();
				SBC();
				break;
			case 0xE6:
				zeroPage();
				INC();
				break;
			case 0xE8:
				INX();
				break;
			case 0xE9:
				immediate();
				SBC();
				break;
			case 0xEC:
				absolute();
				CPX();
				break;
			case 0xED:
				absolute();
				SBC();
				break;
			case 0xEE:
				absolute();
				INC();
				break;
			case 0xF0:
				relative();
				BEQ();
				break;
			case 0xF1:
				indirectIndexed();
				SBC();
				break;
			case 0xF5:
				zeroPageIndexed();
				SBC();
				break;
			case 0xF6:
				zeroPageIndexed();
				INC();
				break;
			case 0xF8:
				SED();
				break;
			case 0xF9:
				absoluteIndexedY();
				SBC();
				break;
			case 0xFD:
				absoluteIndexedX();
				SBC();
				break;
			case 0xFE:
				absoluteIndexedX();
				INC();
				break;
			case NMI:
				NMI();
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
	
	private void SBC() {
		if(cyclesRemaining == 1) {
			final int subtractResult = (accumulator - dataRegister - (~statusRegister & 1));
			statusRegister ^= (-((subtractResult >> 7) & 1) ^ statusRegister) & (1 << SIGN_FLAG);			// set sign flag
			statusRegister ^= (-(subtractResult == 0 ? 1 : 0) ^ statusRegister) & (1 << ZERO_FLAG);		// set zero flag
			int carryFlag = 0;
			if(subtractResult >= 0 && subtractResult <= 0xFF) {
				carryFlag = 1;
			}
			statusRegister ^= (-carryFlag ^ statusRegister) & 1;
			final int overflow = ((accumulator ^ subtractResult) & (dataRegister ^ subtractResult) & 0x80) == 0 ? 0 : 1;
			statusRegister ^= (-overflow ^ statusRegister) & (1 << OVERFLOW_FLAG);
			accumulator = (subtractResult & 0xFF);
		}
	}
	
	private void CMP() {
		if(cyclesRemaining == 1) {
			final int result = accumulator - dataRegister - (~statusRegister & 1);
			int carryFlag = 0;
			if(result >= 0 && result <= 0xFF) {
				carryFlag = 1;
			}
			statusRegister ^= (-carryFlag ^ statusRegister) & 1;
			statusRegister ^= (-((result >> 7) & 1) ^ statusRegister) & (1 << SIGN_FLAG);			// set sign flag
			statusRegister ^= (-(result == 0 ? 1 : 0) ^ statusRegister) & (1 << ZERO_FLAG);		// set zero flag
		}
	}
	
	private void CPX() {
		if(cyclesRemaining == 1) {
			final int result = indexX - dataRegister - (~statusRegister & 1);
			int carryFlag = 0;
			if(result >= 0 && result <= 0xFF) {
				carryFlag = 1;
			}
			statusRegister ^= (-carryFlag ^ statusRegister) & 1;
			statusRegister ^= (-((result >> 7) & 1) ^ statusRegister) & (1 << SIGN_FLAG);			// set sign flag
			statusRegister ^= (-(result == 0 ? 1 : 0) ^ statusRegister) & (1 << ZERO_FLAG);		// set zero flag
		}
	}
	
	private void CPY() {
		if(cyclesRemaining == 1) {
			final int result = indexY - dataRegister - (~statusRegister & 1);
			int carryFlag = 0;
			if(result >= 0 && result <= 0xFF) {
				carryFlag = 1;
			}
			statusRegister ^= (-carryFlag ^ statusRegister) & 1;
			statusRegister ^= (-((result >> 7) & 1) ^ statusRegister) & (1 << SIGN_FLAG);			// set sign flag
			statusRegister ^= (-(result == 0 ? 1 : 0) ^ statusRegister) & (1 << ZERO_FLAG);		// set zero flag
		}
	}
	
	private void DEC() {
		if(cyclesRemaining == 2) {
			dataRegister = (dataRegister - 1) & 0xFF;
			statusRegister ^= (-((dataRegister >> 7) & 1) ^ statusRegister) & (1 << SIGN_FLAG);			// set sign flag
			statusRegister ^= (-(dataRegister == 0 ? 1 : 0) ^ statusRegister) & (1 << ZERO_FLAG);		// set zero flag
		} else if(cyclesRemaining == 1) {
			memorySpace.write(dataCounter, dataRegister);
		}
	}
	
	private void INC() {
		if(cyclesRemaining == 2) {
			dataRegister = (dataRegister + 1) & 0xFF;
			statusRegister ^= (-((dataRegister >> 7) & 1) ^ statusRegister) & (1 << SIGN_FLAG);			// set sign flag
			statusRegister ^= (-(dataRegister == 0 ? 1 : 0) ^ statusRegister) & (1 << ZERO_FLAG);		// set zero flag
		} else if(cyclesRemaining == 1) {
			memorySpace.write(dataCounter, dataRegister);
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
			statusRegister ^= (-((accumulator >> 7) & 1) ^ statusRegister) & (1 << SIGN_FLAG);			// set sign flag
			statusRegister ^= (-(accumulator == 0 ? 1 : 0) ^ statusRegister) & (1 << ZERO_FLAG);		// set zero flag
		}
	}
	
	private void LDX() {
		if(cyclesRemaining == 1) {
			indexX = dataRegister;
			statusRegister ^= (-((indexX >> 7) & 1) ^ statusRegister) & (1 << SIGN_FLAG);			// set sign flag
			statusRegister ^= (-(indexX == 0 ? 1 : 0) ^ statusRegister) & (1 << ZERO_FLAG);		// set zero flag
		}
	}
	
	private void LDY() {
		if(cyclesRemaining == 1) {
			indexY = dataRegister;
			statusRegister ^= (-((indexY >> 7) & 1) ^ statusRegister) & (1 << SIGN_FLAG);			// set sign flag
			statusRegister ^= (-(indexY == 0 ? 1 : 0) ^ statusRegister) & (1 << ZERO_FLAG);		// set zero flag
		}
	}
	
	private void STA() {
		if(cyclesRemaining == 1) {
			memorySpace.write(dataCounter, accumulator);
		}
	}
	
	private void STX() {
		if(cyclesRemaining == 1) {
			memorySpace.write(dataCounter, indexX);
		}
	}
	
	private void STY() {
		if(cyclesRemaining == 1) {
			memorySpace.write(dataCounter, indexY);
		}
	}
	
	private void TAX() {
		if(cyclesRemaining == 1) {
			indexX = accumulator;
			statusRegister ^= (-((indexX >> 7) & 1) ^ statusRegister) & (1 << SIGN_FLAG);			// set sign flag
			statusRegister ^= (-(indexX == 0 ? 1 : 0) ^ statusRegister) & (1 << ZERO_FLAG);		// set zero flag
		}
	}
	
	private void TXA() {
		if(cyclesRemaining == 1) {
			accumulator = indexX;
			statusRegister ^= (-((accumulator >> 7) & 1) ^ statusRegister) & (1 << SIGN_FLAG);			// set sign flag
			statusRegister ^= (-(accumulator == 0 ? 1 : 0) ^ statusRegister) & (1 << ZERO_FLAG);		// set zero flag
		}
	}
	
	private void DEX() {
		if(cyclesRemaining == 1) {
			indexX = (indexX - 1) & 0xFF;
			statusRegister ^= (-((indexX >> 7) & 1) ^ statusRegister) & (1 << SIGN_FLAG);			// set sign flag
			statusRegister ^= (-(indexX == 0 ? 1 : 0) ^ statusRegister) & (1 << ZERO_FLAG);		// set zero flag
		}
	}
	
	private void INX() {
		if(cyclesRemaining == 1) {
			indexX = (indexX + 1) & 0xFF;
			statusRegister ^= (-((indexX >> 7) & 1) ^ statusRegister) & (1 << SIGN_FLAG);			// set sign flag
			statusRegister ^= (-(indexX == 0 ? 1 : 0) ^ statusRegister) & (1 << ZERO_FLAG);		// set zero flag
		}
	}
	
	private void TAY() {
		if(cyclesRemaining == 1) {
			indexY = accumulator;
			statusRegister ^= (-((indexY >> 7) & 1) ^ statusRegister) & (1 << SIGN_FLAG);			// set sign flag
			statusRegister ^= (-(indexY == 0 ? 1 : 0) ^ statusRegister) & (1 << ZERO_FLAG);		// set zero flag
		}
	}
	
	private void TYA() {
		if(cyclesRemaining == 1) {
			accumulator = indexY;
			statusRegister ^= (-((accumulator >> 7) & 1) ^ statusRegister) & (1 << SIGN_FLAG);			// set sign flag
			statusRegister ^= (-(accumulator == 0 ? 1 : 0) ^ statusRegister) & (1 << ZERO_FLAG);		// set zero flag
		}
	}
	
	private void DEY() {
		if(cyclesRemaining == 1) {
			indexY = (indexY - 1) & 0xFF;
			statusRegister ^= (-((indexY >> 7) & 1) ^ statusRegister) & (1 << SIGN_FLAG);			// set sign flag
			statusRegister ^= (-(indexY == 0 ? 1 : 0) ^ statusRegister) & (1 << ZERO_FLAG);		// set zero flag
		}
	}
	
	private void INY() {
		if(cyclesRemaining == 1) {
			indexY = (indexY + 1) & 0xFF;
			statusRegister ^= (-((indexY >> 7) & 1) ^ statusRegister) & (1 << SIGN_FLAG);			// set sign flag
			statusRegister ^= (-(indexY == 0 ? 1 : 0) ^ statusRegister) & (1 << ZERO_FLAG);		// set zero flag
		}
	}
	
	private void EOR() {
		if(cyclesRemaining == 1) {
			accumulator ^= dataRegister;
			statusRegister ^= (-((accumulator >> 7) & 1) ^ statusRegister) & (1 << SIGN_FLAG);			// set sign flag
			statusRegister ^= (-(accumulator == 0 ? 1 : 0) ^ statusRegister) & (1 << ZERO_FLAG);		// set zero flag
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
	
	private void LSR() {
		if(cyclesRemaining == 1) {
			statusRegister ^= (-((dataRegister) & 1) ^ statusRegister) & 1;			// set carry flag
			dataRegister = dataRegister >> 1;
			dataRegister &= 0xFF;
			if(instruction == 0x4A) {
				accumulator = dataRegister;
			} else {
				memorySpace.write(dataCounter, dataRegister);
			}
		}
	}
	
	private void BPL() {
		if(cyclesRemaining == instructionCycles - 1) {
			int postBranchPC = programCounter;
			if((statusRegister >> SIGN_FLAG & 1) == 0) {
				programCounter += (byte) (dataRegister);
				if((programCounter & 0xFF00) == (postBranchPC & 0xFF00)) {
					cyclesRemaining -= 1;
				}
			} else {
				cyclesRemaining -= 2;
			}
		}
	}
	
	private void BMI() {
		if(cyclesRemaining == instructionCycles - 1) {
			int postBranchPC = programCounter;
			if((statusRegister >> SIGN_FLAG & 1) == 1) {
				programCounter += (byte) (dataRegister);
				if((programCounter & 0xFF00) == (postBranchPC & 0xFF00)) {
					cyclesRemaining -= 1;
				}
			} else {
				cyclesRemaining -= 2;
			}
		}
	}
	
	private void BVC() {
		if(cyclesRemaining == instructionCycles - 1) {
			int postBranchPC = programCounter;
			if((statusRegister >> OVERFLOW_FLAG & 1) == 0) {
				programCounter += (byte) (dataRegister);
				if((programCounter & 0xFF00) == (postBranchPC & 0xFF00)) {
					cyclesRemaining -= 1;
				}
			} else {
				cyclesRemaining -= 2;
			}
		}
	}
	
	private void BVS() {
		if(cyclesRemaining == instructionCycles - 1) {
			int postBranchPC = programCounter;
			if((statusRegister >> OVERFLOW_FLAG & 1) == 1) {
				programCounter += (byte) (dataRegister);
				if((programCounter & 0xFF00) == (postBranchPC & 0xFF00)) {
					cyclesRemaining -= 1;
				}
			} else {
				cyclesRemaining -= 2;
			}
		}
	}
	
	private void BCC() {
		if(cyclesRemaining == instructionCycles - 1) {
			int postBranchPC = programCounter;
			if((statusRegister & 1) == 0) {
				programCounter += (byte) (dataRegister);
				if((programCounter & 0xFF00) == (postBranchPC & 0xFF00)) {
					cyclesRemaining -= 1;
				}
			} else {
				cyclesRemaining -= 2;
			}
		}
	}
	
	private void BCS() {
		if(cyclesRemaining == instructionCycles - 1) {
			int postBranchPC = programCounter;
			if((statusRegister & 1) == 1) {
				programCounter += (byte) (dataRegister);
				if((programCounter & 0xFF00) == (postBranchPC & 0xFF00)) {
					cyclesRemaining -= 1;
				}
			} else {
				cyclesRemaining -= 2;
			}
		}
	}
	
	private void BNE() {
		if(cyclesRemaining == instructionCycles - 1) {
			int postBranchPC = programCounter;
			if((statusRegister >> ZERO_FLAG & 1) == 0) {
				programCounter += (byte) (dataRegister);
				if((programCounter & 0xFF00) == (postBranchPC & 0xFF00)) {
					cyclesRemaining -= 1;
				}
			} else {
				cyclesRemaining -= 2;
			}
		}
	}
	
	private void BEQ() {
		if(cyclesRemaining == instructionCycles - 1) {
			int postBranchPC = programCounter;
			if((statusRegister >> ZERO_FLAG & 1) == 1) {
				programCounter += (byte) (dataRegister);
				if((programCounter & 0xFF00) == (postBranchPC & 0xFF00)) {
					cyclesRemaining -= 1;
				}
			} else {
				cyclesRemaining -= 2;
			}
		}
	}
	
	private void JMP() {
		if(cyclesRemaining == 1) {
			programCounter = dataCounter;
		}
	}
	
	private void JSR() {
		if(cyclesRemaining == 1) {
			final int pcToPush = programCounter - 1;
			memorySpace.write(0x100 | stackPointer, (pcToPush >> 8) & 0xFF);
			stackPointer = (stackPointer - 1) & 0xFF;
			memorySpace.write(0x100 | stackPointer, pcToPush & 0xFF);
			stackPointer = (stackPointer - 1) & 0xFF;
			programCounter = dataCounter;
		}
	}
	
	private void RTS() {
		if(cyclesRemaining == 1) {
			stackPointer = (stackPointer + 1) & 0xFF;
			int newAddress = memorySpace.read(0x100 | stackPointer);
			stackPointer = (stackPointer + 1) & 0xFF;
			newAddress |= memorySpace.read(0x100 | stackPointer) << 8;
			programCounter = newAddress + 1;
		}
	}
	
	private void RTI() {
		if(cyclesRemaining == 1) {
			stackPointer = (stackPointer + 1) & 0xFF;
			statusRegister = memorySpace.read(0x100 | stackPointer);
			stackPointer = (stackPointer + 1) & 0xFF;
			int newAddress = memorySpace.read(0x100 | stackPointer);
			stackPointer = (stackPointer + 1) & 0xFF;
			newAddress |= memorySpace.read(0x100 | stackPointer) << 8;
			programCounter = newAddress;
		}
	}
	
	private void NMI() {
		if(cyclesRemaining == 1) {
			memorySpace.write(0x100 | stackPointer, (programCounter >> 8) & 0xFF);
			stackPointer = (stackPointer - 1) & 0xFF;
			memorySpace.write(0x100 | stackPointer, programCounter & 0xFF);
			stackPointer = (stackPointer - 1) & 0xFF;
			memorySpace.write(0x100 | stackPointer, statusRegister);
			stackPointer = (stackPointer - 1) & 0xFF;
			int lowByte = memorySpace.read(0xFFFA);
			programCounter = lowByte | memorySpace.read(0xFFFB) << 8;
		}
	}
	
	private void BRK() {
		if(cyclesRemaining == 1) {
			NMIFlag = true;
			programCounter++;
		}
	}
	
	private void TXS() {
		if(cyclesRemaining == 1) {
			stackPointer = indexX;
		}
	}
	
	private void TSX() {
		if(cyclesRemaining == 1) {
			indexX = stackPointer;
		}
	}
	
	private void PHA() {
		if(cyclesRemaining == instructionCycles - 1) {
			memorySpace.write(0x100 | stackPointer, accumulator);
		} else if(cyclesRemaining == instructionCycles - 2) {
			stackPointer = (stackPointer - 1) & 0xFF;
		}
	}
	
	private void PLA() {
		if(cyclesRemaining == instructionCycles - 1) {
			stackPointer = (stackPointer + 1) & 0xFF;
		} else if(cyclesRemaining == instructionCycles - 2) {
			dataRegister = memorySpace.read(0x100 | stackPointer);
		} else if(cyclesRemaining == instructionCycles - 3) {
			accumulator = dataRegister;
		}
	}
	
	private void PHP() {
		if(cyclesRemaining == instructionCycles - 1) {
			memorySpace.write(0x100 | stackPointer, statusRegister);
		} else if(cyclesRemaining == instructionCycles - 2) {
			stackPointer = (stackPointer - 1) & 0xFF;
		}
	}
	
	private void PLP() {
		if(cyclesRemaining == instructionCycles - 1) {
			stackPointer = (stackPointer + 1) & 0xFF;
		} else if(cyclesRemaining == instructionCycles - 2) {
			dataRegister = memorySpace.read(0x100 | stackPointer);
		} else if(cyclesRemaining == instructionCycles - 3) {
			statusRegister = dataRegister;
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
		if(cyclesRemaining == instructionCycles - 1) {
			dataRegister = memorySpace.read(programCounter++);
		}
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
		if(cyclesRemaining == instructionCycles - 1) {
			dataCounter = memorySpace.read(programCounter++);
		} else if(cyclesRemaining == instructionCycles - 2) {
			dataCounter |= memorySpace.read(programCounter++) << 8;
		} else if(cyclesRemaining == instructionCycles - 3) {
			dataRegister = memorySpace.read(dataCounter);
			int nextAddress = dataCounter + 1;
			if((nextAddress & 0xFF00) != (dataCounter & 0xFF00)) {
				nextAddress &= dataCounter;
			}
			dataCounter = dataRegister | (memorySpace.read(nextAddress) <<8);
		}
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
	
	private void zeroPageIndexedY() {
		if(cyclesRemaining == instructionCycles - 1) {
			dataCounter = memorySpace.read(programCounter++);
		} else if(cyclesRemaining == instructionCycles - 2) {
			dataCounter = (dataCounter + indexY) & 0xFF;
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
			7,6,0,0,0,3,5,0,3,2,0,0,0,4,6,0,	// 0
			4,5,0,0,0,4,6,0,2,4,0,0,0,4,7,0,	// 1
			6,6,0,0,3,3,5,0,4,2,2,0,4,4,6,0,	// 2
			4,5,0,0,0,4,6,0,2,4,0,0,0,4,7,0,	// 3
			6,6,0,0,0,3,5,0,3,2,2,0,3,4,6,0,	// 4
			4,5,0,0,0,4,6,0,2,4,0,0,0,4,7,0,	// 5
			6,6,0,0,0,3,5,0,4,2,2,0,5,4,6,0,	// 6
			4,5,0,0,0,4,6,0,2,4,0,0,0,4,7,0,	// 7
			0,6,0,0,3,3,3,0,2,0,2,0,4,4,4,0,	// 8
			4,5,0,0,4,4,4,0,2,4,2,0,0,4,0,0,	// 9
			2,6,2,0,3,3,3,0,2,2,2,0,4,4,4,0,	// A
			4,5,0,0,4,4,4,0,2,4,2,0,4,4,4,0,	// B
			2,6,0,0,3,3,5,0,2,2,2,0,4,4,6,0,	// C
			4,5,0,0,0,4,6,0,2,4,0,0,0,4,7,0,	// D
			2,6,0,0,3,3,5,0,2,2,0,0,4,4,6,0,	// E
			4,5,0,0,0,4,6,0,2,4,0,0,0,4,7,0		// F
			
	};

}
