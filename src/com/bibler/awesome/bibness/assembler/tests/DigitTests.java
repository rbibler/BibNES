package com.bibler.awesome.bibness.assembler.tests;

import com.bibler.awesome.bibnes.utils.DigitUtils;

import junit.framework.TestCase;

public class DigitTests extends TestCase {
	
	public void testHexDigits() {
		String s = "F2";
		assertTrue(DigitUtils.checkDigits(s, DigitUtils.HEX) == 1);
		s = "01";
		assertTrue(DigitUtils.checkDigits(s, DigitUtils.HEX)  == 1);
		s = "K1";
		assertFalse(DigitUtils.checkDigits(s, DigitUtils.HEX) == 1);
	}
	
	public void testBinDigits() {
		String s = "11100";
		assertTrue(DigitUtils.checkDigits(s, DigitUtils.BIN) == 4);
		s = "1111";
		assertTrue(DigitUtils.checkDigits(s, DigitUtils.BIN) == 3);
		s = "21r";
		assertFalse(DigitUtils.checkDigits(s, DigitUtils.BIN) == 2);
	}
	
	public void testDecimalDigits() {
		String s = "12345";
		assertTrue(DigitUtils.checkDigits(s, DigitUtils.DECIMAL) == 4);
		s = "6789";
		assertTrue(DigitUtils.checkDigits(s, DigitUtils.DECIMAL) == 3);
		s = "F2";
		assertFalse(DigitUtils.checkDigits(s, DigitUtils.DECIMAL) == 2);
	}
	
	

}
