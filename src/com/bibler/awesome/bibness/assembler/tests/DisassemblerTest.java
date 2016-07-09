package com.bibler.awesome.bibness.assembler.tests;

import com.bibler.awesome.bibnes.assembler.Disassembler;
import com.bibler.awesome.bibnes.utils.AssemblyUtils;

import junit.framework.TestCase;

public class DisassemblerTest extends TestCase {
	
	public void testInstructionLookup() {
		Disassembler disassembler = new Disassembler();
		int opCode = 0xA9;
		String instruction = disassembler.lookupInstruction(opCode);
		assertTrue(instruction.equalsIgnoreCase("LDA"));
		opCode = 0x999;
		instruction = disassembler.lookupInstruction(opCode);
		assertNull(instruction);
	}
	
	public void testLengthLookup() {
		Disassembler disassembler = new Disassembler();
		int opCode = 0xA9;
		int length = disassembler.lookupLength(opCode);
		assertEquals(2, length);
	}
	
	public void testAddressModeLookup() {
		Disassembler disassembler = new Disassembler();
		int opCode = 0xA9;
		int addressMode = disassembler.lookupAddressMode(opCode);
		assertEquals(addressMode, AssemblyUtils.IMMEDIATE);
	}
	
	public void testStringFormation() {
		Disassembler disassembler = new Disassembler();
		String instruction = "LDA";
		int addressMode = AssemblyUtils.IMMEDIATE;
		int operand = 0x44;
		String line = disassembler.formatString(instruction, operand, addressMode);
		assertTrue(line.equalsIgnoreCase("LDA #$44"));
		
		
		operand = -1;
		addressMode = AssemblyUtils.ACCUMULATOR;
		instruction = "LSR";
		line = disassembler.formatString(instruction, operand, addressMode);
		assertTrue(line.equalsIgnoreCase("LSR A"));
		
		instruction = "LDA";
		operand = 0x4400;
		addressMode = AssemblyUtils.ABSOLUTE;
		line = disassembler.formatString(instruction, operand, addressMode);
		assertTrue(line.equalsIgnoreCase("LDA $4400"));
		
		instruction = "LDA";
		operand = 0x4400;
		addressMode = AssemblyUtils.ABSOLUTE_X;
		line = disassembler.formatString(instruction, operand, addressMode);
		assertTrue(line.equalsIgnoreCase("LDA $4400, x"));
		

		instruction = "LDA";
		operand = 0x4400;
		addressMode = AssemblyUtils.ABSOLUTE_Y;
		line = disassembler.formatString(instruction, operand, addressMode);
		assertTrue(line.equalsIgnoreCase("LDA $4400, y"));
		
		instruction = "INX";
		addressMode = AssemblyUtils.IMPLIED;
		line = disassembler.formatString(instruction, operand, addressMode);
		assertTrue(line.equalsIgnoreCase("INX"));
		
		instruction = "JMP";
		addressMode = AssemblyUtils.INDIRECT;
		operand = 0x5597;
		line = disassembler.formatString(instruction, operand, addressMode);
		assertTrue(line.equalsIgnoreCase("JMP ($5597)"));
		
		instruction = "LDA";
		addressMode = AssemblyUtils.INDIRECT_X;
		operand = 0x44;
		line = disassembler.formatString(instruction, operand, addressMode);
		assertTrue(line.equalsIgnoreCase("LDA ($44, x)"));
		
		instruction = "LDA";
		addressMode = AssemblyUtils.INDIRECT_Y;
		operand = 0x44;
		line = disassembler.formatString(instruction, operand, addressMode);
		assertTrue(line.equalsIgnoreCase("LDA ($44), y"));
		
		
	}

}
