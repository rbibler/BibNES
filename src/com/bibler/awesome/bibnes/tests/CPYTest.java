package com.bibler.awesome.bibnes.tests;

public class CPYTest extends InstructionTest {
	
	
	private final int IMMEDIATE = 0xC0;
	private final int ZERO_PAGE = 0xC4;
	private final int ABSOLUTE = 0xCC;
	private final int SEC = 0x38;
	
	
	public void testImmediate() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, SEC);
		writeRom(0x41, IMMEDIATE);
		writeRom(0x42, 1);
		setYIndex(0x55);
		runNCycles(4);
		assertTrue(carryFlag());
		assertFalse(zeroFlag());
		assertFalse(signFlag());
		assertFalse(overflowFlag());
	}
	
	public void testZeroPage() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, SEC);
		writeRom(0x41, ZERO_PAGE);
		writeRom(0x42, 0x23);
		writeRom(0x23, 1);
		setYIndex(0x55);
		runNCycles(5);
		assertTrue(carryFlag());
		assertFalse(zeroFlag());
		assertFalse(signFlag());
		assertFalse(overflowFlag());
	}
	
	public void testZeroPageGreater() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, SEC);
		writeRom(0x41, ZERO_PAGE);
		writeRom(0x42, 0x23);
		writeRom(0x23, 0x60);
		setYIndex(0x55);
		runNCycles(5);
		assertFalse(carryFlag());
		assertFalse(zeroFlag());
		assertTrue(signFlag());
	}
	
	public void testAbsolute() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, SEC);
		writeRom(0x41, ABSOLUTE);
		writeRom(0x42, 0x00);
		writeRom(0x43, 0x44);
		writeRom(0x4400, 1);
		setYIndex(0x55);
		runNCycles(6);
		assertTrue(carryFlag());
		assertFalse(zeroFlag());
		assertFalse(signFlag());
		assertFalse(overflowFlag());
	}

}
