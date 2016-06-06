package com.bibler.awesome.bibness.assembler.tests;

import com.bibler.awesome.bibnes.assembler.Assembler;
import com.bibler.awesome.bibnes.utils.AssemblyUtils;

import junit.framework.TestCase;

public class DirectivesTest extends TestCase {
	
	public void testFindDirectives() {
		String s;
		s = ".ALIGN";
		assertEquals("ALIGN", AssemblyUtils.findDirective(s));
		s = ".BYTE";
		assertEquals("BYTE", AssemblyUtils.findDirective(s));
		s = ".DB";
		assertEquals("DB", AssemblyUtils.findDirective(s));
		s = ".DW";
		assertEquals("DW", AssemblyUtils.findDirective(s));
		s = ".EQU";
		assertEquals("EQU", AssemblyUtils.findDirective(s));
		s = ".FILL";
		assertEquals("FILL", AssemblyUtils.findDirective(s));
		s = ".INC";
		assertEquals("INC", AssemblyUtils.findDirective(s));
		s = ".ORG";
		assertEquals("ORG", AssemblyUtils.findDirective(s));
		s = ".RS";
		assertEquals("RS", AssemblyUtils.findDirective(s));
		s = ".WORD";
		assertEquals("WORD", AssemblyUtils.findDirective(s));
		s = "GALAPAGOS";
		assertEquals(null, AssemblyUtils.findDirective(s));
	}
	
	public void testGetDirectives() {
		String s;
		s = ".ALIGN";
		assertEquals(0, AssemblyUtils.getDirective(AssemblyUtils.findDirective(s)));
		s = ".BYTE";
		assertEquals(1, AssemblyUtils.getDirective(AssemblyUtils.findDirective(s)));
		s = ".DB";
		assertEquals(2, AssemblyUtils.getDirective(AssemblyUtils.findDirective(s)));
		s = ".DW";
		assertEquals(3, AssemblyUtils.getDirective(AssemblyUtils.findDirective(s)));
		s = ".EQU";
		assertEquals(4, AssemblyUtils.getDirective(AssemblyUtils.findDirective(s)));
		s = ".FILL";
		assertEquals(5, AssemblyUtils.getDirective(AssemblyUtils.findDirective(s)));
		s = ".INCLUDE";
		assertEquals(6, AssemblyUtils.getDirective(AssemblyUtils.findDirective(s)));
		s = ".INC";
		assertEquals(7, AssemblyUtils.getDirective(AssemblyUtils.findDirective(s)));
		s = ".ORG";
		assertEquals(8, AssemblyUtils.getDirective(AssemblyUtils.findDirective(s)));
		s = ".RSSET";
		assertEquals(9, AssemblyUtils.getDirective(AssemblyUtils.findDirective(s)));
		s = ".RS";
		assertEquals(10, AssemblyUtils.getDirective(AssemblyUtils.findDirective(s)));
		s = ".WORD";
		assertEquals(11, AssemblyUtils.getDirective(AssemblyUtils.findDirective(s)));
		s = "GALAPAGOS";
		assertEquals(-1, AssemblyUtils.getDirective(AssemblyUtils.findDirective(s)));
	}
	
	public void testDirectives() {
		Assembler assembler = new Assembler();
		String s;
		s = ".ORG $400";
		int result = assembler.processDirective(s);
		int locationCounter = assembler.getLocationCounter();
		assertEquals(-1, result);
		assertEquals(0x400, locationCounter);
		
		s = ".BYTE $44";
		result = assembler.processDirective(s);
		assertEquals(-1, result);
		assertEquals(0x44, assembler.getByteAt(locationCounter++));
		
		s = ".DB $44";
		result = assembler.processDirective(s);
		assertEquals(-1, result);
		assertEquals(0x44, assembler.getByteAt(locationCounter++));
		
		s = ".BYTE $44,$45,$46,$47";
		result = assembler.processDirective(s);
		assertEquals(-1, result);
		assertEquals(0x44, assembler.getByteAt(locationCounter++));
		assertEquals(0x45, assembler.getByteAt(locationCounter++));
		assertEquals(0x46, assembler.getByteAt(locationCounter++));
		assertEquals(0x47, assembler.getByteAt(locationCounter++));
		
		s = ".WORD $4400";
		result = assembler.processDirective(s);
		assertEquals(-1, result);
		assertEquals(0x00, assembler.getByteAt(locationCounter++));
		assertEquals(0x44, assembler.getByteAt(locationCounter++));
		
		s = ".DW $4400";
		result = assembler.processDirective(s);
		assertEquals(-1, result);
		assertEquals(0x00, assembler.getByteAt(locationCounter++));
		assertEquals(0x44, assembler.getByteAt(locationCounter++));
		
		s = ".WORD $4400,$4500,$4600,$4700";
		result = assembler.processDirective(s);
		assertEquals(-1, result);
		assertEquals(0x00, assembler.getByteAt(locationCounter++));
		assertEquals(0x44, assembler.getByteAt(locationCounter++));
		assertEquals(0x00, assembler.getByteAt(locationCounter++));
		assertEquals(0x45, assembler.getByteAt(locationCounter++));
		assertEquals(0x00, assembler.getByteAt(locationCounter++));
		assertEquals(0x46, assembler.getByteAt(locationCounter++));
		assertEquals(0x00, assembler.getByteAt(locationCounter++));
		assertEquals(0x47, assembler.getByteAt(locationCounter++));
		
		s = ".FILL $100, $FE";
		result = assembler.processDirective(s);
		assertEquals(-1, result);
		for(int i = 0; i < 0x100; i++) {
			assertEquals(0xFE, assembler.getByteAt(locationCounter++));
		}
		
		assembler.addLabel("LABEL", 0);
		s = ".EQU  $44";
		result = assembler.processDirective(s);
		assertEquals(-1, result);
		assertEquals(0x44, assembler.getLabelAddress("LABEL"));
		
		s = ".INC" + "\"C:/Users/Ryan/Desktop/testInc.bin\"";
		result = assembler.processDirective(s);
		assertEquals(-1, result);
		for(int i = 0; i < 0x100; i++) {
			assertEquals(0x44, assembler.getByteAt(locationCounter++));
		}
		
		s = ".BANK 10";
		result = assembler.processDirective(s);
		assertEquals(-1, result);
		locationCounter = assembler.getLocationCounter();
		assertEquals(10 * 0x2000, locationCounter);
		
		s = ".ORG $34";
		result = assembler.processDirective(s);
		assertEquals(-1, result);
		locationCounter = assembler.getLocationCounterAndBank();
		assertEquals((10 * 0x2000) + 0x34, locationCounter);
		
		s = ".ORG $C000";
		result = assembler.processDirective(s);
		assertEquals(-1, result);
		locationCounter = assembler.getLocationCounterAndBank();
		assertEquals(10 * 0x2000, locationCounter);
	}

}
