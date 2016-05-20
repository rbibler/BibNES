package com.bibler.awesome.bibness.compiler.tests;

import com.bibler.awesome.bibnes.assembler.Assembler;

import junit.framework.TestCase;

public class ParseTest extends TestCase {
	
	public void testInstructionParse() {
		Assembler assembler = new Assembler();
		String s = "AND #$F2";
		assertTrue(assembler.matchOpCode(assembler.trimWhiteSpace(s)));
	}
	
	public void testAddressParse() {
		Assembler assembler = new Assembler();
		String s = "#$F2";
		assertTrue(assembler.checkAddressingMode(s));
		assertEquals(Assembler.IMMEDIATE, assembler.getAddressMode());
		assertEquals("F2", assembler.getAddress());
	}
	
	

}
