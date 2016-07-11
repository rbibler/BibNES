package com.bibler.awesome.bibnes.assembler;

import com.bibler.awesome.bibnes.systems.Memory;
import com.bibler.awesome.bibnes.utils.AssemblyUtils;
import com.bibler.awesome.bibnes.utils.DigitUtils;
import com.bibler.awesome.bibnes.utils.StringUtils;

public class Disassembler {
	
	private static int address;
	private static int opCode;
	
	
	public String disassemble(Memory machineCode, int startIndex) {
		StringBuilder b = new StringBuilder();
		address = startIndex;
		while(opCode != -1 && address < machineCode.size()) {
			b.append(disassembleInstruction(machineCode, address));
			b.append("\n");
		}
		return b.toString();
	}
	
	public String disassembleInstruction(Memory machineCode, int startIndex) {
		String ret = "";
		address = startIndex;
		int operandHigh = -1;
		int operandLow = -1;
		opCode = machineCode.read(startIndex++);
		final String inst = AssemblyUtils.getInstruction(opCode);
		//if(inst != null) {
			final int length = AssemblyUtils.getBytes(opCode);
			final int addressMode = AssemblyUtils.lookupAddressMode(opCode);
			if(length == 2) {
				operandLow = machineCode.read(startIndex++);
			} else if(length == 3) {
				operandLow = machineCode.read(startIndex++);
				operandHigh = machineCode.read(startIndex++);
			}
			ret = createLine(address, opCode, operandHigh, operandLow, addressMode, inst);
			
		//}
		address = startIndex;
		return ret;
	}
	
	private String createLine(int address, int opCode, int operandHigh, int operandLow, int addressMode, String instruction) {
		String s = null;
		int operand = operandHigh >= 0  ? operandLow | operandHigh << 8 : operandLow;
		s = StringUtils.intToPaddedString(address, 4, DigitUtils.HEX) + " " + StringUtils.intToPaddedString(opCode, 2, DigitUtils.HEX) + " ";
		if(operandLow != -1) {
			s += StringUtils.intToPaddedString(operandLow, 2, DigitUtils.HEX) + " ";
		}
		if(operandHigh != -1) {
			s += StringUtils.intToPaddedString(operandHigh, 2, DigitUtils.HEX) + " ";
		}
		s = alignRight(s, formatString(instruction, operand, addressMode));
		return s;
	}
	
	private String alignRight(String left, String right) {
		int diff = 14 - left.length();
		for(int i = 0; i < diff; i++) {
			left += " ";
		}
		return left + right;
	}
	
	
	public String lookupInstruction(int opCode) {
		return AssemblyUtils.getInstruction(opCode);
	}
	
	public int lookupLength(int opCode) {
		return AssemblyUtils.getBytes(opCode);
	}
	
	public int lookupAddressMode(int opCode) {
		return AssemblyUtils.lookupAddressMode(opCode);
	}
	
	public String formatString(String instruction, int operand, int addressMode) {
		String line = null;
		String hexOperand = Integer.toHexString(operand).toUpperCase();
		switch(addressMode) {
			case AssemblyUtils.ACCUMULATOR:
				line = String.format("%s A", instruction);
				break;
			case AssemblyUtils.IMMEDIATE:
				line = String.format("%s #$%s", instruction, hexOperand);
				break;
			case AssemblyUtils.ABSOLUTE:
			case AssemblyUtils.RELATIVE:
			case AssemblyUtils.ZERO_PAGE:
				line = String.format("%s $%s", instruction, hexOperand);
				break;
			case AssemblyUtils.ABSOLUTE_X:
			case AssemblyUtils.ZERO_PAGE_X:
				line = String.format("%s $%s, x", instruction, hexOperand);
				break;
				
			case AssemblyUtils.ABSOLUTE_Y:
			case AssemblyUtils.ZERO_PAGE_Y:
				line = String.format("%s $%s, y", instruction, hexOperand);
				break;
				
			case AssemblyUtils.IMPLIED:
				line = String.format("%s", instruction);
				break;
				
			case AssemblyUtils.INDIRECT:
				line = String.format("%s ($%s)", instruction, hexOperand);
				break;
				
			case AssemblyUtils.INDIRECT_X:
				line = String.format("%s ($%s, x)", instruction, hexOperand);
				break;
				
			case AssemblyUtils.INDIRECT_Y:
				line = String.format("%s ($%s), y", instruction, hexOperand);
				break;
		}
		return line;
	}

}
