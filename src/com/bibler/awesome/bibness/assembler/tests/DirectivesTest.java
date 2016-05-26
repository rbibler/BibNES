package com.bibler.awesome.bibness.assembler.tests;

import com.bibler.awesome.bibnes.assembler.Assembler;
import com.bibler.awesome.bibnes.assembler.InstructionLine;
import com.bibler.awesome.bibnes.assembler.Label;

import junit.framework.TestCase;

public class DirectivesTest extends TestCase {
	
	private Assembler assembler;
	
	public void testEQU() {
		assembler = new Assembler();
		String s;
		s = "LABEL  .EQU  #$04";
		assembler.parseOpCode(new InstructionLine(s, 0));
		Label l = assembler.getLabels().get(0);
		assertEquals(0x04, l.getAddress());
	}

}
