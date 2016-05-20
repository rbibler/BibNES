package com.bibler.awesome.bibness.compiler.tests;

import com.bibler.awesome.bibnes.assembler.Assembler;

import junit.framework.TestCase;

public class ParseTest extends TestCase {
	
	public void testInstructionParse() {
		Assembler assembler = new Assembler();
		String s = "AND #$F2";
		int opCode = assembler.parseOpCode(s);
		assertEquals(0x29, opCode);
	}

}
