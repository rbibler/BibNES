package com.bibler.awesome.bibnes.tests;

public class JMPTest extends InstructionTest {
	
	final int ABSOLUTE = 0x4C;
	final int INDIRECT = 0x6C;
	
	public void testAbsolute() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, ABSOLUTE);
		writeRom(0x41, 0x00);
		writeRom(0x42, 0x44);
		runNCycles(3);
		assertEquals(0x4400, getProgramCounter());
	}
	
	public void testIndirect() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, INDIRECT);
		writeRom(0x41, 0x00);
		writeRom(0x42, 0x44);
		writeRom(0x4400, 0x00);
		writeRom(0x4401, 0x33);
		runNCycles(5);
		assertEquals(0x3300, getProgramCounter());
	}
	
	public void testIndirectPageBoundary() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, INDIRECT);
		writeRom(0x41, 0xFF);
		writeRom(0x42, 0x30);
		writeRom(0x30FF, 0x80);
		writeRom(0x3100, 0x50);
		writeRom(0x3000, 0x40);
		runNCycles(5);
		assertEquals(0x4080, getProgramCounter());
	}

}
