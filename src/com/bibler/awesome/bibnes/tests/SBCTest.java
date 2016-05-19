package com.bibler.awesome.bibnes.tests;

import com.bibler.awesome.bibnes.systems.CPU;
import com.bibler.awesome.bibnes.systems.Memory;

import junit.framework.TestCase;

public class SBCTest extends InstructionTest {
	
	
	private final int IMMEDIATE = 0xE9;
	private final int ZERO_PAGE = 0xE5;
	private final int ZERO_PAGE_X = 0xF5;
	private final int ABSOLUTE = 0xED;
	private final int ABSOLUTE_X = 0xFD;
	private final int ABSOLUTE_Y = 0xF9;
	private final int INDIRECT_X = 0xE1;
	private final int INDIRECT_Y = 0xF1;
	private final int SEC = 0x38;

	
	public void testImmediate() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, SEC);
		writeRom(0x41, IMMEDIATE);
		writeRom(0x42, 1);
		setAccumulator(0x55);
		runNCycles(4);
		assertEquals(0x54, getAccumulator());
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
		setAccumulator(0x55);
		runNCycles(5);
		assertEquals(0x54, getAccumulator());
		assertTrue(carryFlag());
		assertFalse(zeroFlag());
		assertFalse(signFlag());
		assertFalse(overflowFlag());
	}
	
	public void testZeroPageX() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, SEC);
		writeRom(0x41, ZERO_PAGE_X);
		writeRom(0x42, 0x44);
		writeRom(0x49, 0x1);
		setXIndex(5);
		setAccumulator(0x55);
		runNCycles(6);
		assertEquals(0x54, getAccumulator());
		assertTrue(carryFlag());
		assertFalse(zeroFlag());
		assertFalse(signFlag());
		assertFalse(overflowFlag());
	}
	
	public void testAbsolute() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, SEC);
		writeRom(0x41, ABSOLUTE);
		writeRom(0x42, 0x00);
		writeRom(0x43, 0x44);
		writeRom(0x4400, 1);
		setAccumulator(0x55);
		runNCycles(6);
		assertEquals(0x54, getAccumulator());
		assertTrue(carryFlag());
		assertFalse(zeroFlag());
		assertFalse(signFlag());
		assertFalse(overflowFlag());
	}
	
	public void testAbsoluteX() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, SEC);
		writeRom(0x41, ABSOLUTE_X);
		writeRom(0x42, 0x00);
		writeRom(0x43, 0x44);
		writeRom(0x4405, 1);
		setAccumulator(0x55);
		setXIndex(5);
		runNCycles(6);
		assertEquals(0x54, getAccumulator());
		assertTrue(carryFlag());
		assertFalse(zeroFlag());
		assertFalse(signFlag());
		assertFalse(overflowFlag());
	}
	
	public void testLDAAbsoluteY() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, SEC);
		writeRom(0x41, ABSOLUTE_Y);
		writeRom(0x42, 0x00);
		writeRom(0x43, 0x44);
		writeRom(0x4405, 1);
		setAccumulator(0x55);
		setYIndex(5);
		runNCycles(6);
		assertEquals(0x54, getAccumulator());
		assertTrue(carryFlag());
		assertFalse(zeroFlag());
		assertFalse(signFlag());
		assertFalse(overflowFlag());
	}
	
	public void testLDAIndirectX() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, SEC);
		writeRom(0x41, INDIRECT_X);
		writeRom(0x42, 0x44);
		writeRom(0x48, 0x00);
		writeRom(0x49,  0x44);
		writeRom(0x4400, 1);
		setXIndex(4);
		setAccumulator(0x55);
		runNCycles(8);
		assertEquals(0x54, getAccumulator());
		assertTrue(carryFlag());
		assertFalse(zeroFlag());
		assertFalse(signFlag());
		assertFalse(overflowFlag());
	}
	
	public void testLDAIndirectY() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, SEC);
		writeRom(0x41, INDIRECT_Y);
		writeRom(0x42, 0x44);
		writeRom(0x44, 0xC0);
		writeRom(0x45,  0x44);
		writeRom(0x4520, 1);
		setYIndex(0x60);
		setAccumulator(0x55);
		runNCycles(8);
		assertEquals(0x54, getAccumulator());
		assertTrue(carryFlag());
		assertFalse(zeroFlag());
		assertFalse(signFlag());
		assertFalse(overflowFlag());
	}

}
