package com.bibler.awesome.bibnes.assembler;

import com.bibler.awesome.bibnes.systems.Memory;
import com.bibler.awesome.bibnes.utils.AssemblyUtils;

public class Disassembler {
	
	
	public String disassemble(Memory machineCode, int startIndex) {
		StringBuilder b = new StringBuilder();
		int opCode = 0;
		int length = 0;
		String inst = null;
		int operand = 0;
		int addressMode = 0;
		while(opCode != -1 && startIndex < machineCode.size()) {
			opCode = machineCode.read(startIndex++);
			inst = AssemblyUtils.getInstruction(opCode);
			if(inst != null) {
				length = AssemblyUtils.getBytes(opCode);
				addressMode = AssemblyUtils.lookupAddressMode(opCode);
				if(length == 2) {
					operand = machineCode.read(startIndex++);
				} else if(length == 3) {
					operand = machineCode.read(startIndex++) | machineCode.read(startIndex++) << 8;
				}
				b.append(formatString(inst, operand, addressMode));
				b.append("\n");
			}
		}
		return b.toString();
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
		String hexOperand = Integer.toHexString(operand);
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
