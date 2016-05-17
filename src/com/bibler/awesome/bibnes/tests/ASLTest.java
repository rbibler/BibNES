package com.bibler.awesome.bibnes.tests;

public class ASLTest extends InstructionTest {
	
	private final int ACCUMULATOR = 0x0A;
	private final int ZERO_PAGE = 0x06;
	private final int ZERO_PAGE_X = 0x16;
	private final int ABSOLUTE = 0x0E;
	private final int ABSOLUTE_X = 0x1E;
	
	public void testASLZeroPage() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, ZERO_PAGE);
		writeRom(0x41, 0x23);
		writeRom(0x23, 0x44);
		runNCycles(5);
		assertEquals(0x88, readRom(0x23));
		int statusRegister = getStatusRegister();
		assertEquals(1, (statusRegister >> SIGN_FLAG) & 1);
		assertEquals(0, statusRegister & 1);
		assertEquals(0, (statusRegister >> ZERO_FLAG) & 1);
	}

	public void testASLZeroPageX() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, ZERO_PAGE_X);
		writeRom(0x41, 0xC0);
		writeRom(0x20, 0x44);
		setXIndex(0x60);											//Set high to test wrap around
		runNCycles(6);
		assertEquals(0x88, readRom(0x20));
	}

	public void testASLAbsolute() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, ABSOLUTE);
		writeRom(0x41, 0x00);
		writeRom(0x42, 0x44);
		writeRom(0x4400, 0x44);
		runNCycles(6);
		assertEquals(0x88, readRom(0x4400));
	}

	public void testASLAbsoluteX() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, ABSOLUTE_X);
		writeRom(0x41, 0xC0);
		writeRom(0x42, 0x44);
		writeRom(0x4520, 0x44);
		setXIndex(0x60);
		runNCycles(7);
		assertEquals(0x88, readRom(0x4520));
	}

}
