package com.bibler.awesome.bibnes.tests;

public class RegisterInstructionTests extends InstructionTest {
	
	final int TAX = 0xAA;
	final int TXA = 0x8A;
	final int DEX = 0xCA;
	final int INX = 0xE8;
	final int TAY = 0xA8;
	final int TYA = 0x98;
	final int DEY = 0x88;
	final int INY = 0xC8;
		
	public void testTAX() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, TAX);
		setAccumulator(0x44);
		runNCycles(2);
		int statusRegister = getStatusRegister();
		assertEquals(0x44, getXIndex());
		assertEquals(0, statusRegister >> SIGN_FLAG & 1);
		assertEquals(0, statusRegister >> ZERO_FLAG & 1);
	}
	
	public void testTXA() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, TXA);
		setXIndex(0x44);
		runNCycles(2);
		int statusRegister = getStatusRegister();
		assertEquals(0x44, getAccumulator());
		assertEquals(0, statusRegister >> SIGN_FLAG & 1);
		assertEquals(0, statusRegister >> ZERO_FLAG & 1);
	}
	
	public void testDEX() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, DEX);
		setXIndex(0x44);
		runNCycles(2);
		int statusRegister = getStatusRegister();
		assertEquals(0x43, getXIndex());
		assertEquals(0, statusRegister >> SIGN_FLAG & 1);
		assertEquals(0, statusRegister >> ZERO_FLAG & 1);
	}
	
	public void testINX() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, INX);
		setXIndex(0x44);
		runNCycles(2);
		int statusRegister = getStatusRegister();
		assertEquals(0x45, getXIndex());
		assertEquals(0, statusRegister >> SIGN_FLAG & 1);
		assertEquals(0, statusRegister >> ZERO_FLAG & 1);
	}
	
	public void testTAY() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, TAY);
		setAccumulator(0x44);
		runNCycles(2);
		int statusRegister = getStatusRegister();
		assertEquals(0x44, getYIndex());
		assertEquals(0, statusRegister >> SIGN_FLAG & 1);
		assertEquals(0, statusRegister >> ZERO_FLAG & 1);
	}
	
	public void testTYA() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, TYA);
		setYIndex(0x44);
		runNCycles(2);
		int statusRegister = getStatusRegister();
		assertEquals(0x44, getAccumulator());
		assertEquals(0, statusRegister >> SIGN_FLAG & 1);
		assertEquals(0, statusRegister >> ZERO_FLAG & 1);
	}
	
	public void testDEY() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, DEY);
		setYIndex(0x44);
		runNCycles(2);
		int statusRegister = getStatusRegister();
		assertEquals(0x43, getYIndex());
		assertEquals(0, statusRegister >> SIGN_FLAG & 1);
		assertEquals(0, statusRegister >> ZERO_FLAG & 1);
	}
	
	public void testINY() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, INY);
		setYIndex(0x44);
		runNCycles(2);
		int statusRegister = getStatusRegister();
		assertEquals(0x45, getYIndex());
		assertEquals(0, statusRegister >> SIGN_FLAG & 1);
		assertEquals(0, statusRegister >> ZERO_FLAG & 1);
	}

}
