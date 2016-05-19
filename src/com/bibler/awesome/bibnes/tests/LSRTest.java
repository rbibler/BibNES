package com.bibler.awesome.bibnes.tests;

public class LSRTest extends InstructionTest {
	
	private final int ACCUMULATOR = 0x4A;
	private final int ZERO_PAGE = 0x46;
	private final int ZERO_PAGE_X = 0x56;
	private final int ABSOLUTE = 0x4E;
	private final int ABSOLUTE_X = 0x5E;
	
	public void testAccumulator() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, ACCUMULATOR);
		setAccumulator(0xAA);
		runNCycles(2);
		int statusRegister = getStatusRegister();
		assertEquals(0x55, getAccumulator());
		assertEquals(0, statusRegister & 1);
	}
	
	public void testAccumulatorWithCarry() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, ACCUMULATOR);
		setAccumulator(0x55);
		fillStatusRegister();
		runNCycles(2);
		int statusRegister = getStatusRegister();
		assertEquals(0x2A, getAccumulator());
		assertEquals(1, statusRegister & 1);
	}
	
	
	public void testZeroPage() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, ZERO_PAGE);
		writeRom(0x41, 0x23);
		writeRom(0x23, 0xAA);
		runNCycles(5);
		int statusRegister = getStatusRegister();
		assertEquals(0x55, readRom(0x23));
		assertEquals(0, statusRegister & 1);
	}
	
	public void testZeroPageX() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, ZERO_PAGE_X);
		writeRom(0x41, 0x23);
		writeRom(0x28, 0xAA);
		setXIndex(5);
		runNCycles(6);
		int statusRegister = getStatusRegister();
		assertEquals(0x55, readRom(0x28));
		assertEquals(0, statusRegister & 1);
	}
	
	public void testAbsolute() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, ABSOLUTE);
		writeRom(0x41, 0x00);
		writeRom(0x42, 0x44);
		writeRom(0x4400, 0xAA);
		runNCycles(6);
		int statusRegister = getStatusRegister();
		assertEquals(0x55, readRom(0x4400));
		assertEquals(0, statusRegister & 1);
	}
	
	public void testAbsoluteX() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, ABSOLUTE_X);
		writeRom(0x41, 0x00);
		writeRom(0x42, 0x44);
		writeRom(0x4405, 0xAA);
		setXIndex(5);
		runNCycles(7);
		int statusRegister = getStatusRegister();
		assertEquals(0x55, readRom(0x4405));
		assertEquals(0, statusRegister & 1);
	}

}
