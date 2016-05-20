package com.bibler.awesome.bibnes.utils.tests;

import com.bibler.awesome.bibnes.utils.StringUtils;

import junit.framework.TestCase;

public class UtilTest extends TestCase {
	
	public void testStringToInt() {
		String s = "F2";
		assertEquals(242, StringUtils.hexStringToInt(s));
	}

}
