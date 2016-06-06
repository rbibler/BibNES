package com.bibler.awesome.bibness.assembler.tests;

import com.bibler.awesome.bibnes.assembler.Assembler;

import junit.framework.TestCase;

public class ExpressionTest extends TestCase {
	
	public void testCheckHighExpression() {
		Assembler assembler = new Assembler();
		String s;
		s = "HIGH(#$3400)";
		assertTrue(assembler.processExpression(s));
		assertEquals(0x34, assembler.getAddress());
		
	}

}
