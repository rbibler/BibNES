package com.bibler.awesome.bibnes.utils.tests;

import com.bibler.awesome.bibnes.utils.StringUtils;

import junit.framework.TestCase;

public class StringUtilTests extends TestCase {
	
	public void testPatternCheck() {
		String pattern;
		String s;
		pattern = "~";
		s = "(LABEL + 3),x";
		String operand = StringUtils.checkAddressPattern(s, pattern);
		assertTrue(operand.equals("(LABEL + 3)"));
		
		s = "(LABEL + 3),x     ; comment";
		operand = StringUtils.checkAddressPattern(s, pattern);
		assertTrue(operand.equals("(LABEL + 3)"));
		
		s = "(LABEL + 3),xGWAR";
		operand = StringUtils.checkAddressPattern(s, pattern);
		assertNull(operand);
		
		s = "A";
		pattern = "~";
		operand = StringUtils.checkAddressPattern(s, pattern);
		assertTrue(operand.equals("A"));
	
	}

}
