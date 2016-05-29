package com.bibler.awesome.bibness.assembler.tests;

import com.bibler.awesome.bibnes.assembler.Assembler;
import com.bibler.awesome.bibnes.utils.StringUtils;

import junit.framework.TestCase;

public class LabelTests extends TestCase {
	
	private Assembler assembler;
	
	public void testLabelParse() {
		assembler = new Assembler();
		String s;
		s = "LABEL";
		assertTrue(StringUtils.checkLabel(s).equals(s));
		s = "LABEL       ";
		assertTrue(StringUtils.checkLabel(s).equals("LABEL"));
		s = "LABEL:";
		assertTrue(StringUtils.checkLabel(s).equals("LABEL:"));
		s = "LA_@BEL0123456789:       ";
		assertTrue(StringUtils.checkLabel(s).equals("LA_@BEL01234"));
		s = "9Label";
		assertNull(StringUtils.checkLabel(s));
	}

}
