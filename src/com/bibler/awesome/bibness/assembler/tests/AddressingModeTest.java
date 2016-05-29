package com.bibler.awesome.bibness.assembler.tests;

import com.bibler.awesome.bibnes.assembler.Assembler;
import com.bibler.awesome.bibnes.utils.AssemblyUtils;
import junit.framework.TestCase;

public class AddressingModeTest extends TestCase {

	private Assembler assembler;
	
	public void testImmediate() {
		assembler = new Assembler();
		String s;
		s = "#$34";
		assertTrue(assembler.checkAddressMode(AssemblyUtils.IMMEDIATE, s));
		assertEquals(0x34, assembler.getAddress());
		
		s = "#52";
		assertTrue(assembler.checkAddressMode(AssemblyUtils.IMMEDIATE, s));
		assertEquals(52, assembler.getAddress());
		
		s = "#%00110100";
		assertTrue(assembler.checkAddressMode(AssemblyUtils.IMMEDIATE, s));
		assertEquals(52, assembler.getAddress());
	}
	
	public void testZeroPage() {
		assembler = new Assembler();
		String s;
		s = "$34";
		assertTrue(assembler.checkAddressMode(AssemblyUtils.ZERO_PAGE, s));
		assertEquals(0x34, assembler.getAddress());
		
		s = "52";
		assertTrue(assembler.checkAddressMode(AssemblyUtils.ZERO_PAGE, s));
		assertEquals(52, assembler.getAddress());
		
		s = "%00110100";
		assertTrue(assembler.checkAddressMode(AssemblyUtils.ZERO_PAGE, s));
		assertEquals(52, assembler.getAddress());
		
		s = "$34,x";
		assertFalse(assembler.checkAddressMode(AssemblyUtils.ZERO_PAGE, s));
		assertEquals(0x34, assembler.getAddress());
	}
	
	public void testZeroPageX() {
		assembler = new Assembler();
		String s;
		s = "$34,x";
		assertTrue(assembler.checkAddressMode(AssemblyUtils.ZERO_PAGE_X, s));
		assertEquals(0x34, assembler.getAddress());
		
		s = "52,X";
		assertTrue(assembler.checkAddressMode(AssemblyUtils.ZERO_PAGE_X, s));
		assertEquals(52, assembler.getAddress());
		
		s = "%00110100,x";
		assertTrue(assembler.checkAddressMode(AssemblyUtils.ZERO_PAGE_X, s));
		assertEquals(52, assembler.getAddress());
	}
	
	public void testZeroPageY() {
		assembler = new Assembler();
		String s;
		s = "$34,y";
		assertTrue(assembler.checkAddressMode(AssemblyUtils.ZERO_PAGE_Y, s));
		assertEquals(0x34, assembler.getAddress());
		
		s = "52,Y";
		assertTrue(assembler.checkAddressMode(AssemblyUtils.ZERO_PAGE_Y, s));
		assertEquals(52, assembler.getAddress());
		
		s = "%00110100,y";
		assertTrue(assembler.checkAddressMode(AssemblyUtils.ZERO_PAGE_Y, s));
		assertEquals(52, assembler.getAddress());
	}
	
	public void testAbsolute() {
		assembler = new Assembler();
		String s;
		s = "$4400";
		assertTrue(assembler.checkAddressMode(AssemblyUtils.ABSOLUTE, s));
		assertEquals(0x4400, assembler.getAddress());
		
		s = "17408";
		assertTrue(assembler.checkAddressMode(AssemblyUtils.ABSOLUTE, s));
		assertEquals(17408, assembler.getAddress());
		
		s = "%0100010000000000";
		assertTrue(assembler.checkAddressMode(AssemblyUtils.ABSOLUTE, s));
		assertEquals(0x4400, assembler.getAddress());
		
		s = "0x44";
		assertFalse(assembler.checkAddressMode(AssemblyUtils.ABSOLUTE, s));
	}
	
	public void testAbsoluteX() {
		assembler = new Assembler();
		String s;
		s = "$4400,x";
		assertTrue(assembler.checkAddressMode(AssemblyUtils.ABSOLUTE_X, s));
		assertEquals(0x4400, assembler.getAddress());
		
		s = "17408,X";
		assertTrue(assembler.checkAddressMode(AssemblyUtils.ABSOLUTE_X, s));
		assertEquals(0x4400, assembler.getAddress());
		
		s = "%0100010000000000,x";
		assertTrue(assembler.checkAddressMode(AssemblyUtils.ABSOLUTE_X, s));
		assertEquals(0x4400, assembler.getAddress());
	}
	
	public void testAbsoluteY() {
		assembler = new Assembler();
		String s;
		s = "$4400,y";
		assertTrue(assembler.checkAddressMode(AssemblyUtils.ABSOLUTE_Y, s));
		assertEquals(0x4400, assembler.getAddress());
		
		s = "17408,Y";
		assertTrue(assembler.checkAddressMode(AssemblyUtils.ABSOLUTE_Y, s));
		assertEquals(0x4400, assembler.getAddress());
		
		s = "%0100010000000000,y";
		assertTrue(assembler.checkAddressMode(AssemblyUtils.ABSOLUTE_Y, s));
		assertEquals(0x4400, assembler.getAddress());
	}
	
	
	public void testAccumulator() {
		assembler = new Assembler();
		String s = "A";
		assertTrue(assembler.checkAddressMode(AssemblyUtils.ACCUMULATOR, s));
		s = "A;FLOOOGIE";
		assertTrue(assembler.checkAddressMode(AssemblyUtils.ACCUMULATOR, s));
		s = "A3";
		assertTrue(assembler.checkAddressMode(AssemblyUtils.ACCUMULATOR, s));
		s = "#$32";
		assertFalse(assembler.checkAddressMode(AssemblyUtils.ACCUMULATOR, s));
	}
	
	public void testImplied() {
		assembler = new Assembler();
		String s = "";
		assertTrue(assembler.checkAddressMode(AssemblyUtils.IMPLIED, s));
		s = ";FLOOOOGIE";
		assertTrue(assembler.checkAddressMode(AssemblyUtils.IMPLIED, s));
		s = "A3";
		assertFalse(assembler.checkAddressMode(AssemblyUtils.IMPLIED, s));
		s = "#$32";
		assertFalse(assembler.checkAddressMode(AssemblyUtils.IMPLIED, s));
	}
	
	
	public void testIndirect() {
		assembler = new Assembler();
		String s;
		s = "($4400)";
		assertTrue(assembler.checkAddressMode(AssemblyUtils.INDIRECT, s));
		assertEquals(0x4400, assembler.getAddress());
		
		s =("($4400");
		assertTrue(assembler.checkAddressMode(AssemblyUtils.INDIRECT, s));
		
		s = "(17408)";
		assertTrue(assembler.checkAddressMode(AssemblyUtils.INDIRECT, s));
		assertEquals(17408, assembler.getAddress());
		
		s = "(%0100010000000000)";
		assertTrue(assembler.checkAddressMode(AssemblyUtils.INDIRECT, s));
		assertEquals(0x4400, assembler.getAddress());
	}

}
