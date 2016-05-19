package com.bibler.awesome.bibnes.tests;

public class StackTests extends InstructionTest {
	final int TXS = 0x9A;
	final int TSX = 0xBA;
	final int PHA = 0x48;
	final int PLA = 0x68;
	final int PHP = 0x08;
	final int PLP = 0x28;
	
	public void testTXS() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, TXS);
		setXIndex(4);
		runNCycles(2);
		assertEquals(4, getStackPointer());
	}
	
	public void testTSX() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, TSX);
		setXIndex(4);
		runNCycles(2);
		assertEquals(0xFF, getXIndex());
	}
	
	public void testPHA() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, PHA);
		setAccumulator(0x44);
		runNCycles(3);
		assertEquals(0xFE, getStackPointer());
		assertEquals(0x44, readRom(0x1FF));
	}
	
	public void testPLA() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, PLA);
		writeRom(0x1FF, 0x44);
		setAccumulator(0x33);
		setStackPointer(0xFE);
		runNCycles(4);
		assertEquals(0xFF, getStackPointer());
		assertEquals(0x44, getAccumulator());
	}
	
	public void testPHP() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, PHP);
		fillStatusRegister();
		runNCycles(3);
		assertEquals(0xFE, getStackPointer());
		assertEquals(0xFF, readRom(0x1FF));
	}
	
	public void testPLP() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, PLP);
		writeRom(0x1FF, 0x44);
		setAccumulator(0x33);
		setStackPointer(0xFE);
		runNCycles(4);
		assertEquals(0xFF, getStackPointer());
		assertEquals(0x44, getStatusRegister());
	}
	

}
