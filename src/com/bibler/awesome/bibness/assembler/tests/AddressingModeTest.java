package com.bibler.awesome.bibness.assembler.tests;

import com.bibler.awesome.bibnes.assembler.Assembler;
import com.bibler.awesome.bibnes.utils.AssemblyUtils;
import junit.framework.TestCase;

public class AddressingModeTest extends TestCase {

	private Assembler assembler;
	
	public void testAccumulator() {
		assembler = new Assembler();
		String s = "A";
		assertTrue(assembler.checkAccumulator(s));
		s = "A;FLOOOGIE";
		assertTrue(assembler.checkAccumulator(s));
		s = "A3";
		assertFalse(assembler.checkAccumulator(s));
		s = "#$32";
		assertFalse(assembler.checkAccumulator(s));
	}
	
	public void testImplied() {
		assembler = new Assembler();
		String s = "";
		assertTrue(assembler.checkImplied(s));
		s = ";FLOOOOGIE";
		assertTrue(assembler.checkImplied(s));
		s = "A3";
		assertFalse(assembler.checkImplied(s));
		s = "#$32";
		assertFalse(assembler.checkImplied(s));
	}
	
	public void testZeroPage() {
		assembler = new Assembler();
		String s = "$34";
		assertTrue(assembler.checkZP(s));
		s = ";FLOOOOGIE";
		assertFalse(assembler.checkZP(s));
		s = "A3";
		assertFalse(assembler.checkZP(s));
		s = "32";
		assertTrue(assembler.checkZP(s));
		s = "%10101";
		assertTrue(assembler.checkZP(s));
		s = "$FFE8";
		assertFalse(assembler.checkZP(s));
		s = "%1111111111111111";
		assertFalse(assembler.checkZP(s));
	}
	
	public void testCheckZeroPageIndex() {
		assembler = new Assembler();
		String s = "34,X";
		assertTrue(assembler.checkZP(s));
		assertEquals(34, assembler.getAddress());
		assertEquals("22", assembler.getAddressString());
		assertEquals(AssemblyUtils.ZERO_PAGE_X, assembler.getAddressMode());
		s = "$F4,X";
		assertTrue(assembler.checkZP(s));
		assertEquals(0xF4, assembler.getAddress());
		assertEquals("F4", assembler.getAddressString());
		assertEquals(AssemblyUtils.ZERO_PAGE_X, assembler.getAddressMode());
		s = "%01010101,X";
		assertTrue(assembler.checkZP(s));
		assertEquals(0x55, assembler.getAddress());
		assertEquals("55", assembler.getAddressString());
		assertEquals(AssemblyUtils.ZERO_PAGE_X, assembler.getAddressMode());
		
		s = "34,x";
		assertTrue(assembler.checkZP(s));
		assertEquals(34, assembler.getAddress());
		assertEquals("22", assembler.getAddressString());
		assertEquals(AssemblyUtils.ZERO_PAGE_X, assembler.getAddressMode());
		s = "$F4,x";
		assertTrue(assembler.checkZP(s));
		assertEquals(0xF4, assembler.getAddress());
		assertEquals("F4", assembler.getAddressString());
		assertEquals(AssemblyUtils.ZERO_PAGE_X, assembler.getAddressMode());
		s = "%01010101,x";
		assertTrue(assembler.checkZP(s));
		assertEquals(0x55, assembler.getAddress());
		assertEquals("55", assembler.getAddressString());
		assertEquals(AssemblyUtils.ZERO_PAGE_X, assembler.getAddressMode());
		
		s = "34,Y";
		assertTrue(assembler.checkZP(s));
		assertEquals(34, assembler.getAddress());
		assertEquals("22", assembler.getAddressString());
		assertEquals(AssemblyUtils.ZERO_PAGE_Y, assembler.getAddressMode());
		s = "$F4,Y";
		assertTrue(assembler.checkZP(s));
		assertEquals(0xF4, assembler.getAddress());
		assertEquals("F4", assembler.getAddressString());
		assertEquals(AssemblyUtils.ZERO_PAGE_Y, assembler.getAddressMode());
		s = "%01010101,Y";
		assertTrue(assembler.checkZP(s));
		assertEquals(0x55, assembler.getAddress());
		assertEquals("55", assembler.getAddressString());
		assertEquals(AssemblyUtils.ZERO_PAGE_Y, assembler.getAddressMode());
		
		s = "34,y";
		assertTrue(assembler.checkZP(s));
		assertEquals(34, assembler.getAddress());
		assertEquals("22", assembler.getAddressString());
		assertEquals(AssemblyUtils.ZERO_PAGE_Y, assembler.getAddressMode());
		s = "$F4,y";
		assertTrue(assembler.checkZP(s));
		assertEquals(0xF4, assembler.getAddress());
		assertEquals("F4", assembler.getAddressString());
		assertEquals(AssemblyUtils.ZERO_PAGE_Y, assembler.getAddressMode());
		s = "%01010101,y";
		assertTrue(assembler.checkZP(s));
		assertEquals(0x55, assembler.getAddress());
		assertEquals("55", assembler.getAddressString());
		assertEquals(AssemblyUtils.ZERO_PAGE_Y, assembler.getAddressMode());
		
		s = "asdf,X";
		assertFalse(assembler.checkZP(s));
		s = "FFFF,y";
		assertFalse(assembler.checkZP(s));
		s = "1234,x";
		assertFalse(assembler.checkZP(s));
		
	}
	
	public void testAbsolute() {
		assembler = new Assembler();
		String s;
		s = "$FF34";
		assertTrue(assembler.checkAbsolute(s));
		assertEquals(0xFF34, assembler.getAddress());
		s = "$FF";
		assertFalse(assembler.checkAbsolute(s));
		s = "1000";
		assertTrue(assembler.checkAbsolute(s));
		assertEquals(1000, assembler.getAddress());
		s = "%111111111";
		assertTrue(assembler.checkAbsolute(s));
		assertEquals(0b111111111, assembler.getAddress());
		s = "$DEDEE";
		assertFalse(assembler.checkAbsolute(s));
	}

}
