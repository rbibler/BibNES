package com.bibler.awesome.bibnes.tests;

import com.bibler.awesome.bibnes.systems.CPU;
import com.bibler.awesome.bibnes.systems.Memory;

import junit.framework.TestCase;

public class STATest extends InstructionTest {
	
	
	private final int ZERO_PAGE = 0x85;
	private final int ZERO_PAGE_X = 0x95;
	private final int ABSOLUTE = 0x8D;
	private final int ABSOLUTE_X = 0x9D;
	private final int ABSOLUTE_Y = 0x99;
	private final int INDIRECT_X = 0x81;
	private final int INDIRECT_Y = 0x91;

	
	public void testZeroPage() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, ZERO_PAGE);
		writeRom(0x41, 0x23);
		writeRom(0x23, 0xAA);
		setAccumulator(0x55);
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
		setAccumulator(0x55);
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
		setAccumulator(0x55);
		runNCycles(4);
		assertEquals(0x55, readRom(0x4400));
	}
	
	public void testAbsoluteX() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, ABSOLUTE_X);
		writeRom(0x41, 0x00);
		writeRom(0x42, 0x44);
		writeRom(0x4405, 0xAA);
		setAccumulator(0x55);
		setXIndex(5);
		runNCycles(4);
		assertEquals(0x55, readRom(0x4405));
	}
	
	public void testLDAAbsoluteY() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, ABSOLUTE_Y);
		writeRom(0x41, 0x00);
		writeRom(0x42, 0x44);
		writeRom(0x4405, 0xAA);
		setAccumulator(0x55);
		setYIndex(5);
		runNCycles(4);
		assertEquals(0x55, readRom(0x4405));
	}
	
	public void testLDAIndirectX() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, INDIRECT_X);
		writeRom(0x41, 0x44);
		writeRom(0x48, 0x00);
		writeRom(0x49,  0x44);
		writeRom(0x4400, 0xAA);
		setXIndex(4);
		setAccumulator(0x55);
		runNCycles(6);
		assertEquals(0x55, readRom(0x4400));
	}
	
	public void testLDAIndirectY() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, INDIRECT_Y);
		writeRom(0x41, 0x44);
		writeRom(0x44, 0xC0);
		writeRom(0x45,  0x44);
		writeRom(0x4520, 0xAA);
		setYIndex(0x60);
		setAccumulator(0x55);
		runNCycles(6);
		assertEquals(0x55, readRom(0x4520));
	}

}
