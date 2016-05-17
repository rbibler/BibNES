package com.bibler.awesome.bibnes.tests;

public class BITTest extends InstructionTest {
	
	private final int ZERO_PAGE = 0x24;
	private final int ABSOLUTE = 0x2C;
	
	public void testBITZeroPage() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, ZERO_PAGE);
		writeRom(0x41, 0x23);
		writeRom(0x23, 0x44);
		setAccumulator(0);
		runNCycles(3);
		assertEquals(0, getAccumulator());
		int statusRegister = getStatusRegister();
		assertEquals(0, (statusRegister >> SIGN_FLAG) & 1);								// Sign Flag
		assertEquals(1, (statusRegister >> OVERFLOW_FLAG) & 1);								// Overflow Flag						
		assertEquals(1, (statusRegister >> ZERO_FLAG) & 1);								// Z Flag
	}
	
	public void testBITAbsolute() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, ABSOLUTE);
		writeRom(0x41, 0x00);
		writeRom(0x42, 0x44);
		writeRom(0x4400, 0x44);
		setAccumulator(0);
		runNCycles(4);
		assertEquals(0, getAccumulator());
		int statusRegister = getStatusRegister();
		assertEquals(0, (statusRegister >> SIGN_FLAG) & 1);								// Sign Flag
		assertEquals(1, (statusRegister >> OVERFLOW_FLAG) & 1);								// Overflow Flag						
		assertEquals(1, (statusRegister >> ZERO_FLAG) & 1);								// Z Flag
	}

}
