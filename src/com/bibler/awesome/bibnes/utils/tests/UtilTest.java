package com.bibler.awesome.bibnes.utils.tests;

import com.bibler.awesome.bibnes.utils.DigitUtils;
import com.bibler.awesome.bibnes.utils.StringUtils;

import junit.framework.TestCase;

public class UtilTest extends TestCase {
	
	
	
	public void testStringToAscii() {
		String s;
		s = "NES";
		byte[] bytes = StringUtils.stringToAsciiBytes(s);
		assertEquals(0x4E, bytes[0]);
		assertEquals(0x45, bytes[1]);
		assertEquals(0x53, bytes[2]);
	}
	
	public void testGetDigitString() {
		String s;
		s = "$44";
		assertEquals("$44", DigitUtils.getDigitString(s));
	}
	
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
	
	public void testAddressPatternCheck() {
		String pattern;
		String addressToCheck;
		pattern = "#" + StringUtils.digitChar;
		addressToCheck = "#$44";
		assertEquals("$44", StringUtils.checkAddressPattern(addressToCheck, pattern));
		
		pattern = "" + StringUtils.digitChar;
		addressToCheck = "$44";
		assertEquals("$44", StringUtils.checkAddressPattern(addressToCheck, pattern));
		
		pattern += ",x";
		addressToCheck += ",x";
		assertEquals("$44", StringUtils.checkAddressPattern(addressToCheck, pattern));
		
		pattern = "(" + StringUtils.digitChar + ")";
		addressToCheck = "($4400)";
		assertEquals("$4400", StringUtils.checkAddressPattern(addressToCheck, pattern));
		
		pattern = "(" + StringUtils.digitChar + ",x)";
		addressToCheck = "($44,x)";
		assertEquals("$44", StringUtils.checkAddressPattern(addressToCheck, pattern));
		
		pattern = "(" + StringUtils.digitChar + "),y";
		addressToCheck = "($44),y";
		assertEquals("$44", StringUtils.checkAddressPattern(addressToCheck, pattern));
	}
	
	public void testLabelCheck() {
		String s;
		s = "LABEL:";
		assertTrue(s.equals(StringUtils.checkLabel(s)));
		s = "L13  ;more";
		assertTrue("L13".equals(StringUtils.checkLabel(s)));
		s = "Label,x";
		assertTrue("Label".equals(StringUtils.checkLabel(s)));
	}
	
	public void testFirstDigitCheck() {
		String s;
		s = "0134";
		assertTrue(DigitUtils.checkFirstDigit(s));
		
		s = "$FF";
		assertTrue(DigitUtils.checkFirstDigit(s));
		
		s = "%110";
		assertTrue(DigitUtils.checkFirstDigit(s));
		
		s = "Label";
		assertFalse(DigitUtils.checkFirstDigit(s));
	}

}
