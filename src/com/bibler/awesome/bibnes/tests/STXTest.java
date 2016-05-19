package com.bibler.awesome.bibnes.tests;

public class STXTest extends InstructionTest {
	
	
	private final int ZERO_PAGE = 0x86;
	private final int ZERO_PAGE_Y = 0x96;
	private final int ABSOLUTE = 0x8E;
	
	public void testZeroPage() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, ZERO_PAGE);
		writeRom(0x41, 0x23);
		writeRom(0x23, 0xAA);
		setXIndex(0x55);
		runNCycles(3);
		assertEquals(0x55, readRom(0x23));
	}
	
	public void testZeroPageY() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, ZERO_PAGE_Y);
		writeRom(0x41, 0x44);
		writeRom(0x49, 0xAA);
		setYIndex(5);
		setXIndex(0x55);
		runNCycles(4);
		assertEquals(0x55, readRom(0x49));
	}
	
	public void testAbsolute() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, ABSOLUTE);
		writeRom(0x41, 0x00);
		writeRom(0x42, 0x44);
		writeRom(0x4400, 0xAA);
		setXIndex(0x55);
		runNCycles(4);
		assertEquals(0x55, readRom(0x4400));
	}

}
