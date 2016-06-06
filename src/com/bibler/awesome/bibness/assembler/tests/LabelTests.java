package com.bibler.awesome.bibness.assembler.tests;

import com.bibler.awesome.bibnes.assembler.Assembler;
import com.bibler.awesome.bibnes.utils.StringUtils;

import junit.framework.TestCase;

public class LabelTests extends TestCase {
	
	private Assembler assembler;
	
	public void testLabelParse() {
		String s;
		s = "LABEL";
		assertTrue(StringUtils.checkLabel(s).equals(s));
		s = "LABEL       ";
		assertTrue(StringUtils.checkLabel(s).equals("LABEL"));
		s = "LABEL:";
		assertTrue(StringUtils.checkLabel(s).equals("LABEL:"));
		s = "LA_@BEL0123456789:       ";
		assertTrue(StringUtils.checkLabel(s).equals("LA_@BEL0123456789:"));
		s = "9Label";
		assertNull(StringUtils.checkLabel(s));
	}
	
	public void testLabelProcess() {
		assembler = new Assembler();
		String s;
		String tmp = "";
		s = "LABEL: TEST";
		int returnCode = assembler.processLabel(s);
		String label = assembler.getLastLabel();
		int address = assembler.getLastLabelAddress();
		tmp = assembler.trimLineAfterLabel(s);
		assertEquals(-1, returnCode);
		assertTrue(label.equals("LABEL"));
		assertEquals(0, address);
		assertTrue(tmp.equals("TEST"));
	
	}

}
