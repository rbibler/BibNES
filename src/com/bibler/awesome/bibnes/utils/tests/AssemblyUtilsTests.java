package com.bibler.awesome.bibnes.utils.tests;

import com.bibler.awesome.bibnes.assembler.Assembler;
import com.bibler.awesome.bibnes.utils.AssemblyUtils;

import junit.framework.TestCase;

public class AssemblyUtilsTests extends TestCase {
	
	public void testProcessExpressionDigits() {
		Assembler assembler = new Assembler();
		String s;
		s = "$45";
		assertTrue(assembler.processOperand(s));
		assertEquals(0x45, assembler.getAddress());
		
		s = "45";
		assertTrue(assembler.processOperand(s));
		assertEquals(45, assembler.getAddress());
		
		s = "%010101";
		assertTrue(assembler.processOperand(s));
		assertEquals(0b010101, assembler.getAddress());
		
		s = "$x45";
		assertFalse(assembler.processOperand(s));
		assertEquals(-1, assembler.getAddress());
		
	}
	
	public void testProcessExpressionLabel() {
		Assembler assembler = new Assembler();
		String s;
		assembler.addLabel("Label", 0x44);
		s = "Label";
		assertTrue(assembler.processOperand(s));
		assertEquals(0x44, assembler.getAddress());
		
		s = "NO_LABEL!";
		assertFalse(assembler.processOperand(s));
		assertEquals(-1, assembler.getAddress());
	}
	
	

}
