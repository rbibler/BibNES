package com.bibler.awesome.bibnes.tests;

import com.bibler.awesome.bibnes.systems.CPU;
import com.bibler.awesome.bibnes.systems.Memory;

import junit.framework.TestCase;

public class STYTest extends InstructionTest {
	
	
	private final int ZERO_PAGE = 0x84;
	private final int ZERO_PAGE_X = 0x94;
	private final int ABSOLUTE = 0x8C;
	
	public void testZeroPage() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, ZERO_PAGE);
		writeRom(0x41, 0x23);
		writeRom(0x23, 0xAA);
		setYIndex(0x55);
		runNCycles(3);
		assertEquals(0x55, readRom(0x23));
	}
	
	public void testZeroPageX() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, ZERO_PAGE_X);
		writeRom(0x41, 0x44);
		writeRom(0x49, 0xAA);
		setXIndex(5);
		setYIndex(0x55);
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
		setYIndex(0x55);
		runNCycles(4);
		assertEquals(0x55, readRom(0x4400));
	}

}
