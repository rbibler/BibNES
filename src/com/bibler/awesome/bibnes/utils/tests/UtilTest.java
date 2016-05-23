package com.bibler.awesome.bibnes.utils.tests;

import com.bibler.awesome.bibnes.utils.DigitUtils;
import com.bibler.awesome.bibnes.utils.StringUtils;

import junit.framework.TestCase;

public class UtilTest extends TestCase {
	
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

}
