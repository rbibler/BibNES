package com.bibler.awesome.bibnes.tests;

import com.bibler.awesome.bibnes.systems.CPU;
import com.bibler.awesome.bibnes.systems.Memory;

import junit.framework.TestCase;

public class EORTest extends InstructionTest
{
	
	private final int IMMEDIATE = 0x49;				//2
	private final int ZERO_PAGE = 0x45;				//3
	private final int ZERO_PAGE_X = 0x55;			//4
	private final int ABSOLUTE = 0x4D;				//4
	private final int ABSOLUTE_X = 0x5D;			//4
	private final int ABSOLUTE_Y = 0x59;			//4
	private final int INDIRECT_X = 0x41;			//6
	private final int INDIRECT_Y = 0x51;			//5
	
	public void testImmediate() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, IMMEDIATE);
		writeRom(0x41, 0x55);
		setAccumulator(0xAA);
		runNCycles(2);
		int statusRegister = getStatusRegister();
		assertEquals(0xFF, getAccumulator());
		assertEquals(1, statusRegister >> SIGN_FLAG & 1);
		assertEquals(0, statusRegister >> ZERO_FLAG & 1);
	}
	
	public void testZeroPage() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, ZERO_PAGE);
		writeRom(0x41, 0x44);
		writeRom(0x44, 0x55);
		setAccumulator(0xAA);
		runNCycles(3);
		int statusRegister = getStatusRegister();
		assertEquals(0xFF, getAccumulator());
		assertEquals(1, statusRegister >> SIGN_FLAG & 1);
		assertEquals(0, statusRegister >> ZERO_FLAG & 1);
	}

	public void testZeroPageX() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, ZERO_PAGE_X);
		writeRom(0x41, 0x44);
		writeRom(0x49, 0x55);
		setXIndex(5);
		setAccumulator(0xAA);
		runNCycles(4);
		int statusRegister = getStatusRegister();
		assertEquals(0xFF, getAccumulator());
		assertEquals(1, statusRegister >> SIGN_FLAG & 1);
		assertEquals(0, statusRegister >> ZERO_FLAG & 1);
	}

	public void testAbsolute() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, ABSOLUTE);
		writeRom(0x41, 0x00);
		writeRom(0x42, 0x44);
		writeRom(0x4400, 0x55);
		setAccumulator(0xAA);
		runNCycles(4);
		int statusRegister = getStatusRegister();
		assertEquals(0xFF, getAccumulator());
		assertEquals(1, statusRegister >> SIGN_FLAG & 1);
		assertEquals(0, statusRegister >> ZERO_FLAG & 1);
	}

	public void testAbsoluteX() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, ABSOLUTE_X);
		writeRom(0x41, 0x00);
		writeRom(0x42, 0x44);
		writeRom(0x4405, 0x55);
		setXIndex(5);
		setAccumulator(0xAA);
		runNCycles(4);
		int statusRegister = getStatusRegister();
		assertEquals(0xFF, getAccumulator());
		assertEquals(1, statusRegister >> SIGN_FLAG & 1);
		assertEquals(0, statusRegister >> ZERO_FLAG & 1);
	}

	public void testAbsoluteY() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, ABSOLUTE_Y);
		writeRom(0x41, 0x00);
		writeRom(0x42, 0x44);
		writeRom(0x4405, 0x55);
		setYIndex(5);
		setAccumulator(0xAA);
		runNCycles(4);
		int statusRegister = getStatusRegister();
		assertEquals(0xFF, getAccumulator());
		assertEquals(1, statusRegister >> SIGN_FLAG & 1);
		assertEquals(0, statusRegister >> ZERO_FLAG & 1);
	
	}

	public void testIndirectX() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, INDIRECT_X);
		writeRom(0x41, 0x44);
		writeRom(0x48, 0x00);
		writeRom(0x49,  0x44);
		writeRom(0x4400, 0x55);
		setXIndex(4);
		setAccumulator(0xAA);
		runNCycles(6);
		int statusRegister = getStatusRegister();
		assertEquals(0xFF, getAccumulator());
		assertEquals(1, statusRegister >> SIGN_FLAG & 1);
		assertEquals(0, statusRegister >> ZERO_FLAG & 1);
	
	}
	
	public void testANDIndirectY() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, INDIRECT_Y);
		writeRom(0x41, 0x44);
		writeRom(0x44, 0xC0);
		writeRom(0x45,  0x44);
		writeRom(0x4520, 0x55);
		setYIndex(0x60);
		setAccumulator(0xAA);
		runNCycles(6);
		int statusRegister = getStatusRegister();
		assertEquals(0xFF, getAccumulator());
		assertEquals(1, statusRegister >> SIGN_FLAG & 1);
		assertEquals(0, statusRegister >> ZERO_FLAG & 1);
		
	}

}
