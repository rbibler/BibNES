package com.bibler.awesome.bibnes.tests;

public class BranchTests extends InstructionTest {
	
	final int BPL = 0x10;
	final int BMI = 0x30;
	final int BVC = 0x50;
	final int BVS = 0x70;
	final int BCC = 0x90;
	final int BCS = 0xB0;
	final int BNE = 0xD0;
	final int BEQ = 0xF0;
	
	public void testBPLNoBranch() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, BPL);
		writeRom(0x41, 0x5);
		fillStatusRegister();
		runNCycles(2);
		assertEquals(0x42, getProgramCounter());
	}
	
	public void testBPLBranch() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, BPL);
		writeRom(0x41, 0x5);
		clearStatusRegister();
		runNCycles(3);
		assertEquals(0x47, getProgramCounter());
	}
	
	public void testBMINoBranch() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, BMI);
		writeRom(0x41, 0x5);
		clearStatusRegister();
		runNCycles(2);
		assertEquals(0x42, getProgramCounter());
	}
	
	public void testBMIBranch() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, BMI);
		writeRom(0x41, 0x5);
		fillStatusRegister();
		runNCycles(3);
		assertEquals(0x47, getProgramCounter());
	}
	
	public void testBVCNoBranch() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, BVC);
		writeRom(0x41, 0x5);
		fillStatusRegister();
		runNCycles(2);
		assertEquals(0x42, getProgramCounter());
	}
	
	public void testBVCBranch() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, BVC);
		writeRom(0x41, 0x5);
		clearStatusRegister();
		runNCycles(3);
		assertEquals(0x47, getProgramCounter());
	}
	
	public void testBVSNoBranch() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, BVS);
		writeRom(0x41, 0x5);
		clearStatusRegister();
		runNCycles(2);
		assertEquals(0x42, getProgramCounter());
	}
	
	public void testBVSBranch() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, BVS);
		writeRom(0x41, 0x5);
		fillStatusRegister();
		runNCycles(3);
		assertEquals(0x47, getProgramCounter());
	}
	
	public void testBCCNoBranch() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, BCC);
		writeRom(0x41, 0x5);
		fillStatusRegister();
		runNCycles(2);
		assertEquals(0x42, getProgramCounter());
	}
	
	public void testBCCBranch() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, BCC);
		writeRom(0x41, 0x5);
		clearStatusRegister();
		runNCycles(3);
		assertEquals(0x47, getProgramCounter());
	}
	
	public void testBCSNoBranch() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, BCS);
		writeRom(0x41, 0x5);
		clearStatusRegister();
		runNCycles(2);
		assertEquals(0x42, getProgramCounter());
	}
	
	public void testBCSBranch() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, BCS);
		writeRom(0x41, 0x5);
		fillStatusRegister();
		runNCycles(3);
		assertEquals(0x47, getProgramCounter());
	}
	
	public void testBNENoBranch() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, BNE);
		writeRom(0x41, 0x5);
		clearStatusRegister();
		runNCycles(2);
		assertEquals(0x42, getProgramCounter());
	}
	
	public void testBNEBranch() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, BNE);
		writeRom(0x41, 0x5);
		fillStatusRegister();
		runNCycles(3);
		assertEquals(0x47, getProgramCounter());
	}
	
	public void testBEQNoBranch() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, BEQ);
		writeRom(0x41, 0x5);
		fillStatusRegister();
		runNCycles(2);
		assertEquals(0x42, getProgramCounter());
	}
	
	public void testBEQBranch() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, BEQ);
		writeRom(0x41, 0x5);
		clearStatusRegister();
		runNCycles(3);
		assertEquals(0x47, getProgramCounter());
	}
	
	public void testBVSBranchBackward() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, BVS);
		writeRom(0x41, 0xF0);
		fillStatusRegister();
		runNCycles(3);
		assertEquals(0x32, getProgramCounter());
	}
	
	public void testBVSBranchPageBoundary() {
		initializeCPU();
		resetCPU();
		writeRom(0xFC, BVS);
		writeRom(0xFD, 0x7F);
		setProgramCounter(0xFC);
		fillStatusRegister();
		runNCycles(4);
		assertEquals(0x17D, getProgramCounter());
	}

}
