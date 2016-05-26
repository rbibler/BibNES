package com.bibler.awesome.bibnes.utils.tests;

import com.bibler.awesome.bibnes.utils.AssemblyUtils;

import junit.framework.TestCase;

public class AssemblyUtilsTests extends TestCase {
	
	public void testDirectives() {
		String s;
		s = ".ALIGN";
		assertEquals(0, AssemblyUtils.findDirective(s));
		s = ".BYTE";
		assertEquals(1, AssemblyUtils.findDirective(s));
		s = ".DB";
		assertEquals(2, AssemblyUtils.findDirective(s));
		s = ".DW";
		assertEquals(3, AssemblyUtils.findDirective(s));
		s = ".EQU";
		assertEquals(4, AssemblyUtils.findDirective(s));
		s = ".FILL";
		assertEquals(5, AssemblyUtils.findDirective(s));
		s = ".INC";
		assertEquals(6, AssemblyUtils.findDirective(s));
		s = ".ORG";
		assertEquals(7, AssemblyUtils.findDirective(s));
		s = ".RS";
		assertEquals(8, AssemblyUtils.findDirective(s));
		s = ".WORD";
		assertEquals(9, AssemblyUtils.findDirective(s));
		s = "GALAPAGOS";
		assertEquals(-1, AssemblyUtils.findDirective(s));
	}

}
