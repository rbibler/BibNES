package com.bibler.awesome.bibnes.systems;

import java.util.ArrayList;

import com.bibler.awesome.bibnes.assembler.Disassembler;
import com.bibler.awesome.bibnes.communications.Notifiable;
import com.bibler.awesome.bibnes.communications.Notifier;
import com.bibler.awesome.bibnes.io.LogWriter;
import com.bibler.awesome.bibnes.utils.AssemblyUtils;
import com.bibler.awesome.bibnes.utils.StringUtils;

public class CPU implements Notifier {
	
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

	private NES board;
	
	//Control variables
	private int cyclesRemaining;
	private int instructionCycles;
	private boolean pageBoundaryFlag;
	private boolean NMIFlag;
	private boolean NMINext;
	private boolean NMIPrev;
	
	//Debug
	private int totalCycles;
	
	private ArrayList<Notifiable> objectsToNotify = new ArrayList<Notifiable>();
	
	public CPU() {
		
	}
	
	public CPU(NES board) {
		this.board = board;
	}
	
	
	public void registerObjectToNotify(Notifiable objectToNotify) {
		if(!objectsToNotify.contains(objectToNotify)) {
			objectsToNotify.add(objectToNotify);
		}
	}
	
	public void unregisterAll() {
		objectsToNotify.clear();
	}
	
	public void run() {
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				while(totalCycles < 0x1000) {
					cycle();
				}
			}
		});
		t.start();
	}

	public void cycle() {
		if(cyclesRemaining == 0) {
			if(NMINext) {
				NMINext = false;
				cyclesRemaining = 6;
				instruction = NMI;
			} else {
				fetch();
			}
			if(NMIFlag && !NMIPrev) {
				NMINext = true;
			}
			NMIPrev = NMIFlag;
		} else {
			execute();
		}
		statusRegister |= 1 << 5;
		cyclesRemaining--;
		totalCycles++;
		notify("STEP");
	}
	
	
	 public void powerOn(Integer initialPC) {// different than reset
	        // puts RAM in NES poweron state
	        for (int i = 0; i < 0x800; ++i) {
	            writeMemory(i, 0xFF);
	        }

	        writeMemory(0x0008, 0xF7);
	        writeMemory(0x0009, 0xEF);
	        writeMemory(0x000A, 0xDF);
	        writeMemory(0x000F, 0xBF);

	        for (int i = 0x4000; i <= 0x400F; ++i) {
	            writeMemory(i, 0x00);
	        }

	        writeMemory(0x4015, 0x00);
	        writeMemory(0x4017, 0x00);

	        //clocks = 27393; //correct for position we start vblank in
	        accumulator = 0;
	        indexX = 0;
	        indexY = 0;
	        stackPointer = 0xFD;
	        if (initialPC == null) {
	            programCounter = readMemory(0xFFFD) * 256 + readMemory(0xFFFC);
	        } else {
	            programCounter = initialPC;
	        }
	    }

	    public void reset() {
	        programCounter = readMemory(0xFFFD) * 256 + readMemory(0xFFFC);
	        writeMemory(0x4015, 0);
	        writeMemory(0x4017, readMemory(0x4017));
	        //disable audio on reset
	        stackPointer -= 3;
	        stackPointer &= 0xff;
	    }
	
	//public void powerOn() {
		//programCounter = board.cpuRead(0xFFFC) |board.cpuRead(0xFFFD) << 8;
		//resetCPU();
	//}
	
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
	
	public int getCyclesRemaining() {
		return cyclesRemaining;
	}
	
	public void clearStatusRegister() {
		statusRegister = 0;
	}
	
	public void fillStatusRegister() {
		statusRegister = 0xFF;
	}
	
	public String getCurrentInstruction() {
		return AssemblyUtils.getInstruction(instruction);
	}
	
	public void setNMI(boolean NMIFlag) {
		this.NMIFlag = NMIFlag;
	}
	
	protected void writeMemory(int addressToWrite, int data) {
		board.cpuWrite(addressToWrite, data);
	}
	
	protected int readMemory(int addressToRead) {
		return board.cpuRead(addressToRead);
	}
	
	private void execute() {
		switch(instruction) {
			case 0x00:
				BRK();
				break;
			case 0x01:
				indexedIndirect(true);
				ORA();
				break;
			case 0x05:
				zeroPage(true);
				ORA();
				break;
			case 0x06:
				zeroPage(true);
				ASL();
				break;
			case 0x08:
				PHP();
				break;
			case 0x09:
				immediate();
				ORA();
				break;
			case 0x0A:
				accumulator();
				ASL();
				break;
			case 0x0D:
				absolute(true);
				ORA();
				break;
			case 0x0E:
				absolute(true);
				ASL();
				break;
			case 0x10:
				relative();
				BPL();
				break;
			case 0x11:
				indirectIndexed(true);
				ORA();
				break;
			case 0x15:
				zeroPageIndexed(true);
				ORA();
				break;
			case 0x16:
				zeroPageIndexed(true);
				ASL();
				break;
			case 0x18:
				CLC();
				break;
			case 0x19:
				absoluteIndexedY(true);
				ORA();
				break;
			case 0x1D:
				absoluteIndexedX(true);
				ORA();
				break;
			case 0x1E:
				absoluteIndexedX(true);
				ASL();
				break;
			case 0x20:
				absolute(false);
				JSR();
				break;
			case 0x21:									// AND Indexed Indirect
				indexedIndirect(true);
				AND();
				break;
			case 0x24:									// BIT Zero Page
				zeroPage(true);
				BIT();
				break;
			case 0x25:									
				zeroPage(true);
				AND();
				break;
			case 0x26:
				zeroPage(true);
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
				absolute(true);
				BIT();
				break;
			case 0x2D:
				absolute(true);
				AND();
				break;
			case 0x2E:
				absolute(true);
				ROL();
				break;
			case 0x30:
				relative();
				BMI();
				break;
			case 0x31:
				indirectIndexed(true);
				AND();
				break;
			case 0x35:
				zeroPageIndexed(true);
				AND();
				break;
			case 0x36:
				zeroPageIndexed(true);
				ROL();
				break;
			case 0x38:
				SEC();
				break;
			case 0x39:
				absoluteIndexedY(true);
				AND();
				break;
			case 0x3D:
				absoluteIndexedX(true);
				AND();
				break;
			case 0x3E:
				absoluteIndexedX(true);
				ROL();
				break;
			case 0x40:
				RTI();
				break;
			case 0x41:
				indexedIndirect(true);
				EOR();
				break;
			case 0x45:
				zeroPage(true);
				EOR();
				break;
			case 0x46:
				zeroPage(true);
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
				absolute(true);
				JMP();
				break;
			case 0x4D:
				absolute(true);
				EOR();
				break;
			case 0x4E:
				absolute(true);
				LSR();
				break;
			case 0x50:
				relative();
				BVC();
				break;
			case 0x51:
				indirectIndexed(true);
				EOR();
				break;
			case 0x55:
				zeroPageIndexed(true);
				EOR();
				break;
			case 0x56:
				zeroPageIndexed(true);
				LSR();
				break;
			case 0x58:
				CLI();
				break;
			case 0x59:
				absoluteIndexedY(true);
				EOR();
				break;
			case 0x5D:
				absoluteIndexedX(true);
				EOR();
				break;
			case 0x5E:
				absoluteIndexedX(true);
				LSR();
				break;
			case 0x60:
				RTS();
				break;
			case 0x61:
				indexedIndirect(true);
				ADC();
				break;
			case 0x65:
				zeroPage(true);
				ADC();
				break;
			case 0x66:
				zeroPage(true);
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
				absolute(true);
				ADC();
				break;
			case 0x6E:
				absolute(true);
				ROR();
				break;
			case 0x70:
				relative();
				BVS();
				break;
			case 0x71:
				indirectIndexed(true);
				ADC();
				break;
			case 0x75:
				zeroPageIndexed(true);
				ADC();
				break;
			case 0x76:
				zeroPageIndexed(true);
				ROR();
				break;
			case 0x78:
				SEI();
				break;
			case 0x79:
				absoluteIndexedY(true);
				ADC();
				break;
			case 0x7D:
				absoluteIndexedX(true);
				ADC();
				break;
			case 0x7E:
				absoluteIndexedX(true);
				ROR();
				break;
			case 0x81:
				indexedIndirect(false);
				STA();
				break;
			case 0x84:
				zeroPage(false);
				STY();
				break;
			case 0x85:
				zeroPage(false);
				STA();
				break;
			case 0x86:
				zeroPage(false);
				STX();
				break;
			case 0x88:
				DEY();
				break;
			case 0x8A:
				TXA();
				break;
			case 0x8C:
				absolute(false);
				STY();
				break;
			case 0x8D:
				absolute(false);
				STA();
				break;
			case 0x8E:
				absolute(false);
				STX();
				break;
			case 0x90:
				relative();
				BCC();
				break;
			case 0x91:
				indirectIndexed(false);
				STA();
				break;
			case 0x94:
				zeroPageIndexed(false);
				STY();
				break;
			case 0x95:
				zeroPageIndexed(false);
				STA();
				break;
			case 0x96:
				zeroPageIndexedY(false);
				STX();
				break;
			case 0x98:
				TYA();
				break;
			case 0x99:
				absoluteIndexedY(false);
				STA();
				break;
			case 0x9A:
				TXS();
				break;
			case 0x9D:
				absoluteIndexedX(false);
				STA();
				break;
			case 0xA0:
				immediate();
				LDY();
				break;
			case 0xA1:
				indexedIndirect(true);
				LDA();
				break;
			case 0xA2:
				immediate();
				LDX();
				break;
			case 0xA4:
				zeroPage(true);
				LDY();
				break;
			case 0xA5:									// LDA Zero Page
				zeroPage(true);
				LDA();
				break;
			case 0xA6:
				zeroPage(true);
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
				absolute(true);
				LDY();
				break;
			case 0xAD:									// LDA Absolute
				absolute(true);
				LDA();
				break;
			case 0xAE:
				absolute(true);
				LDX();
				break;
			case 0xB0:
				relative();
				BCS();
				break;
			case 0xB1:
				indirectIndexed(true);
				LDA();
				break;
			case 0xB4:
				zeroPageIndexed(true);
				LDY();
				break;
			case 0xB5:									// LDA Zero Page X
				zeroPageIndexed(true);
				LDA();
				break;
			case 0xB6:
				zeroPageIndexedY(true);
				LDX();
				break;
			case 0xB8:
				CLV();
				break;
			case 0xB9:									// LDA Absolute Y
				absoluteIndexedY(true);
				LDA();
				break;
			case 0xBA:
				TSX();
				break;
			case 0xBC:
				absoluteIndexedX(true);
				LDY();
				break;
			case 0xBD:									// LDA Absolute X
				absoluteIndexedX(true);
				LDA();
				break;
			case 0xBE:
				absoluteIndexedY(true);
				LDX();
				break;
			case 0xC0:
				immediate();
				CPY();
				break;
			case 0xC1:
				indexedIndirect(true);
				CMP();
				break;
			case 0xC4:
				zeroPage(true);
				CPY();
				break;
			case 0xC5:
				zeroPage(true);
				CMP();
				break;
			case 0xC6:
				zeroPage(true);
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
				absolute(true);
				CPY();
				break;
			case 0xCD:
				absolute(true);
				CMP();
				break;
			case 0xCE:
				absolute(true);
				DEC();
				break;
			case 0xD0:
				relative();
				BNE();
				break;
			case 0xD1:
				indirectIndexed(true);
				CMP();
				break;
			case 0xD5:
				zeroPageIndexed(true);
				CMP();
				break;
			case 0xD6:
				zeroPageIndexed(true);
				DEC();
				break;
			case 0xD8:
				CLD();
				break;
			case 0xD9:
				absoluteIndexedY(true);
				CMP();
				break;
			case 0xDD:
				absoluteIndexedX(true);
				CMP();
				break;
			case 0xDE:
				absoluteIndexedX(true);
				DEC();
				break;
			case 0xE0:
				immediate();
				CPX();
				break;
			case 0xE1:
				indexedIndirect(true);
				SBC();
				break;
			case 0xE4:
				zeroPage(true);
				CPX();
				break;
			case 0xE5:
				zeroPage(true);
				SBC();
				break;
			case 0xE6:
				zeroPage(true);
				INC();
				break;
			case 0xE8:
				INX();
				break;
			case 0xE9:
				immediate();
				SBC();
				break;
			case 0xEA:
				NOP();
				break;
			case 0xEC:
				absolute(true);
				CPX();
				break;
			case 0xED:
				absolute(true);
				SBC();
				break;
			case 0xEE:
				absolute(true);
				INC();
				break;
			case 0xF0:
				relative();
				BEQ();
				break;
			case 0xF1:
				indirectIndexed(true);
				SBC();
				break;
			case 0xF5:
				zeroPageIndexed(true);
				SBC();
				break;
			case 0xF6:
				zeroPageIndexed(true);
				INC();
				break;
			case 0xF8:
				SED();
				break;
			case 0xF9:
				absoluteIndexedY(true);
				SBC();
				break;
			case 0xFD:
				absoluteIndexedX(true);
				SBC();
				break;
			case 0xFE:
				absoluteIndexedX(true);
				INC();
				break;
			case NMI:
				NMI();
				
				break;
		}	
		
	}
	
	
	private void fetch() {
		instruction = readMemory(programCounter++);
		instructionCycles = instructionTimes[instruction];
		cyclesRemaining += instructionCycles;
	}
	
	//Instructions
	
	private void ADC() {
		if(cyclesRemaining == 1) {
			final int addResult = (accumulator + dataRegister) + (statusRegister & 1);
			final boolean overflowFlag = ((addResult ^ accumulator) & (addResult ^ dataRegister) & 0x080) != 0;
			if(overflowFlag) {
				statusRegister |= 1 << OVERFLOW_FLAG;
			} else {
				statusRegister &= ~(1 << OVERFLOW_FLAG);
			}
			
			accumulator = (addResult & 0xFF);
			statusRegister ^= (-((addResult >> 7) & 1) ^ statusRegister) & (1 << SIGN_FLAG);			// set sign flag
			statusRegister ^= (-((addResult >> 8) & 1) ^ statusRegister) & 1;					// set carry flag
			statusRegister ^= (-(accumulator == 0 ? 1 : 0) ^ statusRegister) & (1 << ZERO_FLAG);		// set zero flag
			
		}
	}
	
	private void SBC() {
		if(cyclesRemaining == 1) {
			final int subtractResult = (accumulator - dataRegister - (((statusRegister & 1) == 1 ? 0 : 1)));
			statusRegister ^= (-((subtractResult >> 7) & 1) ^ statusRegister) & (1 << SIGN_FLAG);			// set sign flag
			statusRegister ^= (-(subtractResult == 0 ? 1 : 0) ^ statusRegister) & (1 << ZERO_FLAG);		// set zero flag
			final boolean carryFlag = (subtractResult >> 8 == 0);
			if(!carryFlag) {
				statusRegister &= ~1;
			} else {
				statusRegister |= 1;
			}
			final boolean overflowFlag = (((accumulator ^ dataRegister) & 0x80) != 0)
		                && (((accumulator ^ subtractResult) & 0x80) != 0);
			statusRegister &= ~(1 << OVERFLOW_FLAG);
			if(overflowFlag) {
				statusRegister |= 1 << OVERFLOW_FLAG;
			}
			accumulator = (subtractResult & 0xFF);
		}
	}
	
	private void CMP() {
		if(cyclesRemaining == 1) {
			final int result = accumulator - dataRegister;
			boolean negativeFlag;
			boolean carryFlag;
			boolean zeroFlag;
			if (result < 0) {
	            negativeFlag = ((result >> 7 & 1) != 0);
	            carryFlag = false;
	            zeroFlag = false;
	        } else if (result == 0) {
	            negativeFlag = false;
	            carryFlag = true;
	            zeroFlag = true;
	        } else {
	            negativeFlag = ((result >> 7 & 1) != 0);
	            carryFlag = true;
	            zeroFlag = false;
	        }
			if(negativeFlag) {
				statusRegister |= (1 << SIGN_FLAG);
			} else {
				statusRegister &= ~(1 << SIGN_FLAG);
			}
			if(carryFlag) {
				statusRegister |= 1;
			} else {
				statusRegister &= ~1;
			}
			if(zeroFlag) {
				statusRegister |= (1 << 1);
			} else {
				statusRegister &= ~(1 << 1);
			}
			
			
			
			/*int carryFlag = 0;
			if(result >= 0 && result <= 0xFF) {
				carryFlag = 1;
			}
			statusRegister ^= (-carryFlag ^ statusRegister) & 1;
			statusRegister ^= (-((result >> 7) & 1) ^ statusRegister) & (1 << SIGN_FLAG);			// set sign flag
			statusRegister ^= (-(result == 0 ? 1 : 0) ^ statusRegister) & (1 << ZERO_FLAG);		// set zero flag
			*/
		}
	}
	
	private void CPX() {
		if(cyclesRemaining == 1) {
			final int result = indexX - dataRegister;
			boolean negativeFlag;
			boolean carryFlag;
			boolean zeroFlag;
			if (result < 0) {
	            negativeFlag = ((result >> 7 & 1) != 0);
	            carryFlag = false;
	            zeroFlag = false;
	        } else if (result == 0) {
	            negativeFlag = false;
	            carryFlag = true;
	            zeroFlag = true;
	        } else {
	            negativeFlag = ((result >> 7 & 1) != 0);
	            carryFlag = true;
	            zeroFlag = false;
	        }
			if(negativeFlag) {
				statusRegister |= (1 << SIGN_FLAG);
			} else {
				statusRegister &= ~(1 << SIGN_FLAG);
			}
			if(carryFlag) {
				statusRegister |= 1;
			} else {
				statusRegister &= ~1;
			}
			if(zeroFlag) {
				statusRegister |= (1 << 1);
			} else {
				statusRegister &= ~(1 << 1);
			}
			/*int carryFlag = 0;
			if(result >= 0 && result <= 0xFF) {
				carryFlag = 1;
			}
			statusRegister ^= (-carryFlag ^ statusRegister) & 1;
			statusRegister ^= (-((result >> 7) & 1) ^ statusRegister) & (1 << SIGN_FLAG);			// set sign flag
			statusRegister ^= (-(result == 0 ? 1 : 0) ^ statusRegister) & (1 << ZERO_FLAG);		// set zero flag
			*/
		}
	}
	
	private void CPY() {
		if(cyclesRemaining == 1) {
			final int result = indexY - dataRegister;
			boolean negativeFlag;
			boolean carryFlag;
			boolean zeroFlag;
			if (result < 0) {
	            negativeFlag = ((result >> 7 & 1) != 0);
	            carryFlag = false;
	            zeroFlag = false;
	        } else if (result == 0) {
	            negativeFlag = false;
	            carryFlag = true;
	            zeroFlag = true;
	        } else {
	            negativeFlag = ((result >> 7 & 1) != 0);
	            carryFlag = true;
	            zeroFlag = false;
	        }
			if(negativeFlag) {
				statusRegister |= (1 << SIGN_FLAG);
			} else {
				statusRegister &= ~(1 << SIGN_FLAG);
			}
			if(carryFlag) {
				statusRegister |= 1;
			} else {
				statusRegister &= ~1;
			}
			if(zeroFlag) {
				statusRegister |= (1 << 1);
			} else {
				statusRegister &= ~(1 << 1);
			}
			/*int carryFlag = 0;
			if(result >= 0 && result <= 0xFF) {
				carryFlag = 1;
			}
			statusRegister ^= (-carryFlag ^ statusRegister) & 1;
			statusRegister ^= (-((result >> 7) & 1) ^ statusRegister) & (1 << SIGN_FLAG);			// set sign flag
			statusRegister ^= (-(result == 0 ? 1 : 0) ^ statusRegister) & (1 << ZERO_FLAG);		// set zero flag
			*/
		}
	}
	
	private void DEC() {
		if(cyclesRemaining == 2) {
			dataRegister = (dataRegister - 1) & 0xFF;
			statusRegister ^= (-((dataRegister >> 7) & 1) ^ statusRegister) & (1 << SIGN_FLAG);			// set sign flag
			statusRegister ^= (-(dataRegister == 0 ? 1 : 0) ^ statusRegister) & (1 << ZERO_FLAG);		// set zero flag
		} else if(cyclesRemaining == 1) {
			writeMemory(dataCounter, dataRegister);
		}
	}
	
	private void INC() {
		if(cyclesRemaining == 2) {
			dataRegister = (dataRegister + 1) & 0xFF;
			statusRegister ^= (-((dataRegister >> 7) & 1) ^ statusRegister) & (1 << SIGN_FLAG);			// set sign flag
			statusRegister ^= (-(dataRegister == 0 ? 1 : 0) ^ statusRegister) & (1 << ZERO_FLAG);		// set zero flag
		} else if(cyclesRemaining == 1) {
			writeMemory(dataCounter, dataRegister);
		}
	}
	
	private void AND() {
		if(cyclesRemaining == 1) {
			accumulator &= dataRegister;
			accumulator &= 0xFF;
			statusRegister ^= (-((accumulator >> 7) & 1) ^ statusRegister) & (1 << SIGN_FLAG);			// set sign flag
			statusRegister ^= (-(accumulator == 0 ? 1 : 0) ^ statusRegister) & (1 << ZERO_FLAG);		// set zero flag
		}
	}
	
	private void ASL() {
		if(cyclesRemaining == 1) {
			dataRegister = dataRegister << 1;
			statusRegister ^= (-((dataRegister >> 7) & 1) ^ statusRegister) & (1 << SIGN_FLAG);			// set sign flag
			statusRegister ^= (-(dataRegister == 0 ? 1 : 0) ^ statusRegister) & (1 << ZERO_FLAG);		// set zero flag
			statusRegister ^= (-((dataRegister >> 8) & 1) ^ statusRegister) & 1;					// set carry flag
			dataRegister = dataRegister & 0xFF;
			if(instruction == 0x0A) {
				accumulator = dataRegister;
				statusRegister ^= (-((accumulator >> 7) & 1) ^ statusRegister) & (1 << SIGN_FLAG);			// set sign flag
				statusRegister ^= (-(accumulator == 0 ? 1 : 0) ^ statusRegister) & (1 << ZERO_FLAG);		// set zero flag
			} else {
				writeMemory(dataCounter, dataRegister);
			}
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
	
	private void NOP() {
		
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
			writeMemory(dataCounter, accumulator);
		}
	}
	
	private void STX() {
		if(cyclesRemaining == 1) {
			writeMemory(dataCounter, indexX);
		}
	}
	
	private void STY() {
		if(cyclesRemaining == 1) {
			writeMemory(dataCounter, indexY);
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
			accumulator &= 0xFF;
			statusRegister ^= (-((accumulator >> 7) & 1) ^ statusRegister) & (1 << SIGN_FLAG);			// set sign flag
			statusRegister ^= (-(accumulator == 0 ? 1 : 0) ^ statusRegister) & (1 << ZERO_FLAG);		// set zero flag
		}
	}
	
	private void ORA() {
		if(cyclesRemaining == 1) {
			accumulator |= dataRegister;
			accumulator &= 0xFF;
			statusRegister ^= (-((accumulator >> 7) & 1) ^ statusRegister) & (1 << SIGN_FLAG);			// set sign flag
			statusRegister ^= (-(accumulator == 0 ? 1 : 0) ^ statusRegister) & (1 << ZERO_FLAG);		// set zero flag
		}
	}
	
	private void ROL() {
		if(cyclesRemaining == 1) {
			dataRegister = (dataRegister << 1) | (statusRegister & 1);
			statusRegister ^= (-((dataRegister >> 8) & 1) ^ statusRegister) & 1;			// set carry flag
			dataRegister &= 0xFF;
			if(instruction == 0x2A) {
				accumulator = dataRegister;
				statusRegister ^= (-((accumulator >> 7) & 1) ^ statusRegister) & (1 << SIGN_FLAG);			// set sign flag
				statusRegister ^= (-(accumulator == 0 ? 1 : 0) ^ statusRegister) & (1 << ZERO_FLAG);		// set zero flag
			} else {
				writeMemory(dataCounter, dataRegister);
			}
		}
	}
	
	private void ROR() {
		if(cyclesRemaining == 1) {
			int carryFlag = statusRegister & 1;
			statusRegister ^= (-((dataRegister) & 1) ^ statusRegister) & 1;			// set carry flag
			dataRegister = dataRegister >> 1;
			dataRegister &= 0x7F;
			dataRegister |= (carryFlag << 7);
			//dataRegister ^= (-carryFlag ^ dataRegister) & (1 << 7);
			dataRegister &= 0xFF;
			if(instruction == 0x6A) {
				accumulator = dataRegister;
				statusRegister ^= (-((accumulator >> 7) & 1) ^ statusRegister) & (1 << SIGN_FLAG);			// set sign flag
				statusRegister ^= (-(accumulator == 0 ? 1 : 0) ^ statusRegister) & (1 << ZERO_FLAG);		// set zero flag
			} else {
				writeMemory(dataCounter, dataRegister);
			}
		}
	}
	
	private void LSR() {
		if(cyclesRemaining == 1) {
			statusRegister ^= (-((dataRegister) & 1) ^ statusRegister) & 1;			// set carry flag
			dataRegister = dataRegister >> 1;
			//dataRegister &= 0xFF;
			dataRegister &= 0x7F;
			if(instruction == 0x4A) {
				accumulator = dataRegister;
				statusRegister ^= (-((accumulator >> 7) & 1) ^ statusRegister) & (1 << SIGN_FLAG);			// set sign flag
				statusRegister ^= (-(accumulator == 0 ? 1 : 0) ^ statusRegister) & (1 << ZERO_FLAG);		// set zero flag
			} else {
				writeMemory(dataCounter, dataRegister);
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
			push(pcToPush >> 8);
			push(pcToPush & 0xFF);
			programCounter = dataCounter;
		}
	}
	
	private void push(int data) {
		writeMemory(0x100 + (stackPointer & 0xFF), data);
		stackPointer = (stackPointer - 1) & 0xFF;
	}
	
	private int pop() {
		stackPointer = (stackPointer + 1) & 0xFF;
		return readMemory(0x100 + (stackPointer & 0xFF));
	}
	
	private void RTS() {
		if(cyclesRemaining == 1) {
			
			int newAddress = pop() & 0xFF;
			newAddress |= (pop()) << 8;
			programCounter = newAddress + 1;
		}
	}
	
	private void RTI() {
		if(cyclesRemaining == 1) {
			statusRegister = pop();
			int newAddress = pop() & 0xFF;
			newAddress |= pop() << 8;
			programCounter = newAddress;
		}
	}
	
	private void NMI() {
		if(cyclesRemaining == 1) {
			push(programCounter >> 8 & 0xFF);
			push(programCounter & 0xFF);
			push(statusRegister);
			int lowByte = readMemory(0xFFFA);
			programCounter = lowByte | readMemory(0xFFFB) << 8;
		}
	}
	/* 
	 * To Do:
	 * Must Fix the brk command. 
	 */
	private void BRK() {
		if(cyclesRemaining == 1) {
			
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
			statusRegister ^= (-((indexX >> 7) & 1) ^ statusRegister) & (1 << SIGN_FLAG);			// set sign flag
			statusRegister ^= (-(indexX == 0 ? 1 : 0) ^ statusRegister) & (1 << ZERO_FLAG);		// set zero flag
		}
	}
	
	private void PHA() {
		if(cyclesRemaining == instructionCycles - 1) {
			writeMemory(0x100 | stackPointer, accumulator);
		} else if(cyclesRemaining == instructionCycles - 2) {
			stackPointer = (stackPointer - 1) & 0xFF;
		}
	}
	
	private void PLA() {
		if(cyclesRemaining == instructionCycles - 1) {
			stackPointer = (stackPointer + 1) & 0xFF;
		} else if(cyclesRemaining == instructionCycles - 2) {
			dataRegister = readMemory(0x100 | stackPointer);
		} else if(cyclesRemaining == instructionCycles - 3) {
			accumulator = dataRegister;
			statusRegister ^= (-((accumulator >> 7) & 1) ^ statusRegister) & (1 << SIGN_FLAG);			// set sign flag
			statusRegister ^= (-(accumulator == 0 ? 1 : 0) ^ statusRegister) & (1 << ZERO_FLAG);		// set zero flag
			
		}
	}
	
	private void PHP() {
		if(cyclesRemaining == instructionCycles - 1) {
			statusRegister |= (1 << 4);
			statusRegister |= (1 << 5);
			writeMemory(0x100 | stackPointer, statusRegister);
		} else if(cyclesRemaining == instructionCycles - 2) {
			stackPointer = (stackPointer - 1) & 0xFF;
		}
	}
	
	private void PLP() {
		if(cyclesRemaining == instructionCycles - 1) {
			stackPointer = (stackPointer + 1) & 0xFF;
		} else if(cyclesRemaining == instructionCycles - 2) {
			dataRegister = readMemory(0x100 | stackPointer);
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
			dataRegister = readMemory(dataCounter);
		}
	}

	
	private void relative() {
		if(cyclesRemaining == instructionCycles - 1) {
			dataRegister = readMemory(programCounter++);
		}
	}
	
	private void absolute(boolean read) {
		if(cyclesRemaining == instructionCycles - 1) {
			dataCounter = readMemory(programCounter++);
		} else if(cyclesRemaining == instructionCycles - 2) {
			dataCounter |= readMemory(programCounter++) << 8;
		} else if(cyclesRemaining == instructionCycles - 3 && read) {
			dataRegister = readMemory(dataCounter);
		}
	}
	
	private void zeroPage(boolean read) {
		if(cyclesRemaining == instructionCycles - 1) {
			dataCounter = readMemory(programCounter++);
		} else if(cyclesRemaining == instructionCycles - 2 && read) {
			dataRegister = readMemory(dataCounter);
		}
	}
	
	private void indirect() {
		if(cyclesRemaining == instructionCycles - 1) {
			dataCounter = readMemory(programCounter++);
		} else if(cyclesRemaining == instructionCycles - 2) {
			dataCounter |= readMemory(programCounter++) << 8;
		} else if(cyclesRemaining == instructionCycles - 3) {
			dataRegister = readMemory(dataCounter);
			int nextAddress = dataCounter + 1;
			if((nextAddress & 0xFF00) != (dataCounter & 0xFF00)) {
				nextAddress &= dataCounter;
			}
			dataCounter = dataRegister | (readMemory(nextAddress) << 8);
		}
	}
	
	private void absoluteIndexedX(boolean read) {
		if(cyclesRemaining == instructionCycles - 1) {
			dataCounter = readMemory(programCounter++);
		} else if(cyclesRemaining == instructionCycles - 2) {
			dataCounter |= readMemory(programCounter++) << 8;
		} else if(cyclesRemaining == instructionCycles - 3) {
			if((dataCounter + indexX & 0xFF00) != (dataCounter & 0xFF00) && !pageBoundaryFlag && instruction != 0x1E) {
				cyclesRemaining++;
				pageBoundaryFlag = true;
			} else {
				pageBoundaryFlag = false;
				dataCounter = dataCounter + indexX;
				if(read) {
					dataRegister = readMemory(dataCounter);
				}
			}
		}
		
	}
	
	private void absoluteIndexedY(boolean read) {
		if(cyclesRemaining == instructionCycles - 1) {
			dataCounter = readMemory(programCounter++);
		} else if(cyclesRemaining == instructionCycles - 2) {
			dataCounter |= readMemory(programCounter++) << 8;
		} else if(cyclesRemaining == instructionCycles - 3) {
			if((dataCounter + indexY & 0xFF00) != (dataCounter & 0xFF00) && !pageBoundaryFlag) {
				cyclesRemaining++;
				pageBoundaryFlag = true;
			} else {
				pageBoundaryFlag = false;
				dataCounter = (dataCounter + indexY) & 0xFFFF;
				if(read) {
					dataRegister = readMemory(dataCounter);
				}
			}
		}
	}
	
	private void zeroPageIndexed(boolean read) {
		if(cyclesRemaining == instructionCycles - 1) {
			dataCounter = readMemory(programCounter++);
		} else if(cyclesRemaining == instructionCycles - 2) {
			dataCounter = (dataCounter + indexX) & 0xFF;
		} else if(cyclesRemaining == instructionCycles - 3 && read) {
			dataRegister = readMemory(dataCounter);
		}
	}
	
	private void zeroPageIndexedY(boolean read) {
		if(cyclesRemaining == instructionCycles - 1) {
			dataCounter = readMemory(programCounter++);
		} else if(cyclesRemaining == instructionCycles - 2) {
			dataCounter = (dataCounter + indexY) & 0xFF;
		} else if(cyclesRemaining == instructionCycles - 3 && read) {
			dataRegister = readMemory(dataCounter);
		}
	}
	
	private void indexedIndirect(boolean read) {
		if(cyclesRemaining == instructionCycles - 1) {
			dataCounter = readMemory(programCounter++);
		} else if(cyclesRemaining == instructionCycles - 2) {
			dataRegister = (dataCounter + indexX) & 0xFF;
		} else if(cyclesRemaining == instructionCycles - 3) {
			dataCounter = readMemory(dataRegister);
		} else if(cyclesRemaining == instructionCycles - 4) {
			dataCounter |= readMemory((dataRegister + 1) & 0xFF) << 8;
			dataCounter &= 0xFFFF;
		} else if(cyclesRemaining == instructionCycles - 5 && read) {
			dataRegister = readMemory(dataCounter);
		}
	}
	
	private void indirectIndexed(boolean read) {
		if(cyclesRemaining == instructionCycles - 1) {
			dataRegister = readMemory(programCounter++);
		} else if(cyclesRemaining == instructionCycles - 2) {
			dataCounter = readMemory(dataRegister);
		} else if(cyclesRemaining == instructionCycles - 3) {
			dataCounter |= readMemory((dataRegister + 1) & 0xFF) << 8;
		} else if(cyclesRemaining == instructionCycles - 4) {
			if((dataCounter + indexY & 0xFF00) != (dataCounter & 0xFF00) && !pageBoundaryFlag) {
				cyclesRemaining++;
				pageBoundaryFlag = true;
			} else {
				pageBoundaryFlag = false;
				dataCounter = (dataCounter + indexY) & 0xFFFF;
				if(read) {
					dataRegister = readMemory(dataCounter);
				}
			}
		}
	}
	
	private int[] instructionTimes = new int[] {
		//  0 1 2 3 4 5 6 7 8 9 A B C D E F	
			7,6,0,0,0,3,5,0,3,2,2,0,0,4,6,0,	// 0
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
			2,6,0,0,3,3,5,0,2,2,2,0,4,4,6,0,	// E
			4,5,0,0,0,4,6,0,2,4,0,0,0,4,7,0		// F
			
	};

	@Override
	public void notify(String messageToSend) {
		for(Notifiable notifiable : objectsToNotify) {
			if(notifiable != null) {
				notifiable.takeNotice(messageToSend, this);
			}
		}
	}

}
