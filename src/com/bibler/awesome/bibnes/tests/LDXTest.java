package com.bibler.awesome.bibnes.tests;

public class LDXTest extends InstructionTest {
	
	private final int IMMEDIATE = 0xA2;				//2
	private final int ZERO_PAGE = 0xA6;				//3
	private final int ZERO_PAGE_Y = 0xB6;			//4
	private final int ABSOLUTE = 0xAE;				//4
	private final int ABSOLUTE_Y = 0xBE;			//4
	

	
	public void testImmediate() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, IMMEDIATE);
		writeRom(0x41, 0x44);
		runNCycles(2);
		int statusRegister = getStatusRegister();
		assertEquals(0x44, getXIndex());
		assertEquals(0, statusRegister >> SIGN_FLAG & 1);
		assertEquals(0, statusRegister >> ZERO_FLAG & 1);
	}
	
	public void testZeroPage() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, ZERO_PAGE);
		writeRom(0x41, 0x23);
		writeRom(0x23, 0x44);
		runNCycles(3);
		int statusRegister = getStatusRegister();
		assertEquals(0x44, getXIndex());
		assertEquals(0, statusRegister >> SIGN_FLAG & 1);
		assertEquals(0, statusRegister >> ZERO_FLAG & 1);
	}
	
	public void testZeroPageY() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, ZERO_PAGE_Y);
		writeRom(0x41, 0xC0);
		writeRom(0x20, 0x44);
		setYIndex(0x60);	
		runNCycles(4);
		int statusRegister = getStatusRegister();
		assertEquals(0x44, getXIndex());
		assertEquals(0, statusRegister >> SIGN_FLAG & 1);
		assertEquals(0, statusRegister >> ZERO_FLAG & 1);
	}
	
	public void testAbsolute() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, ABSOLUTE);
		writeRom(0x41, 0x00);
		writeRom(0x42, 0x44);
		writeRom(0x4400, 0x44);
		runNCycles(4);
		int statusRegister = getStatusRegister();
		assertEquals(0x44, getXIndex());
		assertEquals(0, statusRegister >> SIGN_FLAG & 1);
		assertEquals(0, statusRegister >> ZERO_FLAG & 1);
	}
	
	public void testAbsoluteY() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, ABSOLUTE_Y);
		writeRom(0x41, 0xC0);
		writeRom(0x42, 0x44);
		writeRom(0x4520, 0x44);
		setYIndex(0x60);
		runNCycles(5);
		int statusRegister = getStatusRegister();
		assertEquals(0x44, getXIndex());
		assertEquals(0, statusRegister >> SIGN_FLAG & 1);
		assertEquals(0, statusRegister >> ZERO_FLAG & 1);
	}

}
