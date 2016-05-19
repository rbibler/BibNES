package com.bibler.awesome.bibnes.tests;

public class JSRTest extends InstructionTest {
	
	final int ABSOLUTE = 0x20;
	
	public void testJSR() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, ABSOLUTE);
		writeRom(0x41, 0x00);
		writeRom(0x42, 0x44);
		runNCycles(6);
		assertEquals(0x4400, getProgramCounter());
		assertEquals(0x00, readRom(0x1FF));
		assertEquals(0x42, readRom(0x1FE));
	}

}
