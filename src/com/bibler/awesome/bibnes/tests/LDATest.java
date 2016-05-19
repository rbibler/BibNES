package com.bibler.awesome.bibnes.tests;

import com.bibler.awesome.bibnes.systems.CPU;
import com.bibler.awesome.bibnes.systems.Memory;

import junit.framework.TestCase;

public class LDATest extends InstructionTest {
	
	private final int IMMEDIATE = 0xA9;				//2
	private final int ZERO_PAGE = 0xA5;				//3
	private final int ZERO_PAGE_X = 0xB5;			//4
	private final int ABSOLUTE = 0xAD;				//4
	private final int ABSOLUTE_X = 0xBD;			//4
	private final int ABSOLUTE_Y = 0xB9;			//4
	private final int INDIRECT_X = 0xA1;			//6
	private final int INDIRECT_Y = 0xB1;			//5
	

	
	public void testLDAImmediate() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, IMMEDIATE);
		writeRom(0x41, 0x44);
		runNCycles(2);
		int statusRegister = getStatusRegister();
		assertEquals(0x44, getAccumulator());
		assertEquals(0, statusRegister >> SIGN_FLAG & 1);
		assertEquals(0, statusRegister >> ZERO_FLAG & 1);
	}
	
	public void testLDAZeroPage() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, ZERO_PAGE);
		writeRom(0x41, 0x23);
		writeRom(0x23, 0x44);
		runNCycles(3);
		int statusRegister = getStatusRegister();
		assertEquals(0x44, getAccumulator());
		assertEquals(0, statusRegister >> SIGN_FLAG & 1);
		assertEquals(0, statusRegister >> ZERO_FLAG & 1);
	}
	
	public void testLDAZeroPageX() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, ZERO_PAGE_X);
		writeRom(0x41, 0xC0);
		writeRom(0x20, 0x44);
		setXIndex(0x60);	
		runNCycles(4);
		int statusRegister = getStatusRegister();
		assertEquals(0x44, getAccumulator());
		assertEquals(0, statusRegister >> SIGN_FLAG & 1);
		assertEquals(0, statusRegister >> ZERO_FLAG & 1);
	}
	
	public void testLDAAbsolute() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, ABSOLUTE);
		writeRom(0x41, 0x00);
		writeRom(0x42, 0x44);
		writeRom(0x4400, 0x44);
		runNCycles(4);
		int statusRegister = getStatusRegister();
		assertEquals(0x44, getAccumulator());
		assertEquals(0, statusRegister >> SIGN_FLAG & 1);
		assertEquals(0, statusRegister >> ZERO_FLAG & 1);
	}
	
	public void testLDAAbsoluteX() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, ABSOLUTE_X);
		writeRom(0x41, 0xC0);
		writeRom(0x42, 0x44);
		writeRom(0x4520, 0x44);
		setXIndex(0x60);
		runNCycles(5);
		int statusRegister = getStatusRegister();
		assertEquals(0x44, getAccumulator());
		assertEquals(0, statusRegister >> SIGN_FLAG & 1);
		assertEquals(0, statusRegister >> ZERO_FLAG & 1);
	}
	
	public void testLDAAbsoluteY() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, ABSOLUTE_Y);
		writeRom(0x41, 0xC0);
		writeRom(0x42, 0x44);
		writeRom(0x4520, 0x44);
		setYIndex(0x60);
		runNCycles(5);
		int statusRegister = getStatusRegister();
		assertEquals(0x44, getAccumulator());
		assertEquals(0, statusRegister >> SIGN_FLAG & 1);
		assertEquals(0, statusRegister >> ZERO_FLAG & 1);
	}
	
	public void testLDAIndirectX() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, INDIRECT_X);
		writeRom(0x41, 0x44);
		writeRom(0x48, 0x00);
		writeRom(0x49,  0x44);
		writeRom(0x4400, 0x44);
		setXIndex(0x4);
		runNCycles(6);
		int statusRegister = getStatusRegister();
		assertEquals(0x44, getAccumulator());
		assertEquals(0, statusRegister >> SIGN_FLAG & 1);
		assertEquals(0, statusRegister >> ZERO_FLAG & 1);
	}
	
	public void testLDAIndirectY() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, INDIRECT_Y);
		writeRom(0x41, 0x44);
		writeRom(0x44, 0x00);
		writeRom(0x45,  0x44);
		writeRom(0x4404, 0x44);
		setYIndex(4);
		runNCycles(5);
		int statusRegister = getStatusRegister();
		assertEquals(0x44, getAccumulator());
		assertEquals(0, statusRegister >> SIGN_FLAG & 1);
		assertEquals(0, statusRegister >> ZERO_FLAG & 1);
	}

}
