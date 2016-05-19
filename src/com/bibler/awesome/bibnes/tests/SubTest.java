package com.bibler.awesome.bibnes.tests;

import junit.framework.TestCase;

public class SubTest extends TestCase {
	
	public void testSub() {
		int testByte = 0x82;
		int accumulator = 0x7F;
		accumulator += (byte) testByte;
		assertEquals(1, accumulator);
	}

}
