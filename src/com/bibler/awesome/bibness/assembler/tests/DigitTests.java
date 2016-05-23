package com.bibler.awesome.bibness.assembler.tests;

import com.bibler.awesome.bibnes.utils.DigitUtils;

import junit.framework.TestCase;

public class DigitTests extends TestCase {
	
	public void testHexDigits() {
		String s = "F2";
		assertTrue(DigitUtils.checkDigits(s, DigitUtils.HEX));
		s = "01";
		assertTrue(DigitUtils.checkDigits(s, DigitUtils.HEX));
		s = "K1";
		assertFalse(DigitUtils.checkDigits(s, DigitUtils.HEX));
	}
	
	public void testBinDigits() {
		String s = "11100";
		assertTrue(DigitUtils.checkDigits(s, DigitUtils.BIN));
		s = "1111";
		assertTrue(DigitUtils.checkDigits(s, DigitUtils.BIN));
		s = "21r";
		assertFalse(DigitUtils.checkDigits(s, DigitUtils.BIN));
	}
	
	public void testDecimalDigits() {
		String s = "12345";
		assertTrue(DigitUtils.checkDigits(s, DigitUtils.DECIMAL));
		s = "6789";
		assertTrue(DigitUtils.checkDigits(s, DigitUtils.DECIMAL));
		s = "F2";
		assertFalse(DigitUtils.checkDigits(s, DigitUtils.DECIMAL));
	}
	
	

}
