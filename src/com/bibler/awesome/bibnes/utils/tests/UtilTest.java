package com.bibler.awesome.bibnes.utils.tests;

import com.bibler.awesome.bibnes.utils.DigitUtils;
import com.bibler.awesome.bibnes.utils.StringUtils;

import junit.framework.TestCase;

public class UtilTest extends TestCase {
	
	public void testGetDigits() {
		String s;
		s = "$34";
		assertEquals(0x34, DigitUtils.getDigits(s));
		s = "34";
		assertEquals(34, DigitUtils.getDigits(s));
		s = "%1110001";
		assertEquals(0b1110001, DigitUtils.getDigits(s));
		s = "34FLERGE";
		assertEquals(34, DigitUtils.getDigits(s));
	}
	
	public void testStringToInt() {
		String s = "FFFF5";
		assertEquals(0xFFFF5, StringUtils.hexStringToInt(s));
	}
	
	public void testBinStringToInt() {
		String s = "111";
		assertEquals(0b111, StringUtils.binStringToInt(s));
	}
	
	public void testWordSplitter() {
		int testInt = 0xFFAA;
		int[] resultInts = DigitUtils.splitWord(testInt, 2);
		assertEquals(0xFF, resultInts[0]);
		assertEquals(0xAA, resultInts[1]);
	}
	
	public void testLineValidate() {
		String s;
		s = "0x34";
		assertTrue(StringUtils.validateLine(s, s.indexOf('4')));
		assertFalse(StringUtils.validateLine(s,  s.indexOf('3')));
		s = "0x34			;";		
		assertTrue(StringUtils.validateLine(s, s.indexOf('4')));
		s = "0x34      The following is a comment;";
		assertFalse(StringUtils.validateLine(s, s.indexOf('4')));
	}

}
