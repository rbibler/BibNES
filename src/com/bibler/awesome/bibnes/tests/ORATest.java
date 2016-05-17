package com.bibler.awesome.bibnes.tests;

public class ORATest extends InstructionTest {
	
	private final int IMMEDIATE = 0x09;
	private final int ZERO_PAGE = 0x05;
	private final int ZERO_PAGE_X = 0x15;
	private final int ABSOLUTE = 0x0D;
	private final int ABSOLUTE_X = 0x1D;
	private final int ABSOLUTE_Y = 0x19;
	private final int INDIRECT_X = 0x01;
	private final int INDIRECT_Y = 0x11;
	
	public void testImmediate() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, IMMEDIATE);
		writeRom(0x41, 0x55);
		setAccumulator(0xAA);
		runNCycles(2);
		assertEquals(0xFF, getAccumulator());
		int statusRegister = getStatusRegister();
		assertEquals(1, (statusRegister >> SIGN_FLAG) & 1);
		assertEquals(0, (statusRegister >> ZERO_FLAG) & 1);
	}
	
	public void testZeroPage() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, ZERO_PAGE);
		writeRom(0x41, 0x23);
		writeRom(0x23, 0x55);
		setAccumulator(0xAA);
		runNCycles(3);
		assertEquals(0xFF, getAccumulator());
		int statusRegister = getStatusRegister();
		assertEquals(1, (statusRegister >> SIGN_FLAG) & 1);
		assertEquals(0, (statusRegister >> ZERO_FLAG) & 1);
	}
	
	public void testZeroPageX() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, ZERO_PAGE_X);
		writeRom(0x41, 0x23);
		writeRom(0x28, 0x55);
		setAccumulator(0xAA);
		setXIndex(5);
		runNCycles(4);
		assertEquals(0xFF, getAccumulator());
		int statusRegister = getStatusRegister();
		assertEquals(1, (statusRegister >> SIGN_FLAG) & 1);
		assertEquals(0, (statusRegister >> ZERO_FLAG) & 1);
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
		assertEquals(0xFF, getAccumulator());
		int statusRegister = getStatusRegister();
		assertEquals(1, (statusRegister >> SIGN_FLAG) & 1);
		assertEquals(0, (statusRegister >> ZERO_FLAG) & 1);
	}
	
	public void testAbsoluteX() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, ABSOLUTE_X);
		writeRom(0x41, 0x00);
		writeRom(0x42, 0x44);
		writeRom(0x4405, 0x55);
		setAccumulator(0xAA);
		setXIndex(5);
		runNCycles(4);
		assertEquals(0xFF, getAccumulator());
		int statusRegister = getStatusRegister();
		assertEquals(1, (statusRegister >> SIGN_FLAG) & 1);
		assertEquals(0, (statusRegister >> ZERO_FLAG) & 1);
	}
	
	public void testAbsoluteY() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, ABSOLUTE_Y);
		writeRom(0x41, 0x00);
		writeRom(0x42, 0x44);
		writeRom(0x4405, 0x55);
		setAccumulator(0xAA);
		setYIndex(5);
		runNCycles(4);
		assertEquals(0xFF, getAccumulator());
		int statusRegister = getStatusRegister();
		assertEquals(1, (statusRegister >> SIGN_FLAG) & 1);
		assertEquals(0, (statusRegister >> ZERO_FLAG) & 1);
	}
	
	public void testIndirectX() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, INDIRECT_X);
		writeRom(0x41, 0x44);
		writeRom(0x49, 0x00);
		writeRom(0x4A, 0x44);
		writeRom(0x4400, 0x55);
		setAccumulator(0xAA);
		setXIndex(5);
		runNCycles(6);
		assertEquals(0xFF, getAccumulator());
		int statusRegister = getStatusRegister();
		assertEquals(1, (statusRegister >> SIGN_FLAG) & 1);
		assertEquals(0, (statusRegister >> ZERO_FLAG) & 1);
	}
	
	public void testIndirectY() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, INDIRECT_Y);
		writeRom(0x41, 0x44);
		writeRom(0x44, 0x00);
		writeRom(0x45, 0x44);
		writeRom(0x4405, 0x55);
		setAccumulator(0xAA);
		setYIndex(5);
		runNCycles(5);
		assertEquals(0xFF, getAccumulator());
		int statusRegister = getStatusRegister();
		assertEquals(1, (statusRegister >> SIGN_FLAG) & 1);
		assertEquals(0, (statusRegister >> ZERO_FLAG) & 1);
	}

}
