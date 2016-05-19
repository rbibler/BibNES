package com.bibler.awesome.bibnes.tests;

import com.bibler.awesome.bibnes.systems.CPU;
import com.bibler.awesome.bibnes.systems.Memory;

import junit.framework.TestCase;

public class DECTest extends InstructionTest {
	
	
	private final int ZERO_PAGE = 0xC6;
	private final int ZERO_PAGE_X = 0xD6;
	private final int ABSOLUTE = 0xCE;
	private final int ABSOLUTE_X = 0xDE;
	
	
	public void testZeroPage() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, ZERO_PAGE);
		writeRom(0x41, 0x23);
		writeRom(0x23, 0x55);
		runNCycles(5);
		assertEquals(0x54, readRom(0x23));
		assertFalse(zeroFlag());
		assertFalse(signFlag());
	}
	
	public void testZeroPageWithUnderflow() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, ZERO_PAGE);
		writeRom(0x41, 0x23);
		writeRom(0x23, 0);
		runNCycles(5);
		assertEquals(0xFF, readRom(0x23));
		assertFalse(zeroFlag());
		assertTrue(signFlag());
	}
	
	public void testZeroPageX() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, ZERO_PAGE_X);
		writeRom(0x41, 0x44);
		writeRom(0x49, 0x55);
		setXIndex(5);
		runNCycles(6);
		assertEquals(0x54, readRom(0x49));
		assertFalse(zeroFlag());
		assertFalse(signFlag());
	}
	
	public void testAbsolute() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, ABSOLUTE);
		writeRom(0x41, 0x00);
		writeRom(0x42, 0x44);
		writeRom(0x4400, 0x55);
		runNCycles(6);
		assertEquals(0x54, readRom(0x4400));
		assertFalse(zeroFlag());
		assertFalse(signFlag());
	}
	
	public void testAbsoluteX() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, ABSOLUTE_X);
		writeRom(0x41, 0x00);
		writeRom(0x42, 0x44);
		writeRom(0x4405, 0x55);
		setXIndex(5);
		runNCycles(7);
		assertEquals(0x54, readRom(0x4405));
		assertFalse(zeroFlag());
		assertFalse(signFlag());
	}

}
