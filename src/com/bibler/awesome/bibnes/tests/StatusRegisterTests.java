package com.bibler.awesome.bibnes.tests;

public class StatusRegisterTests extends InstructionTest {
	
	final int CLC = 0x18;
	final int SEC = 0x38;
	final int CLI = 0x58;
	final int SEI = 0x78;
	final int CLV = 0xB8;
	final int CLD = 0xD8;
	final int SED = 0xF8;
	
	public void testCLC() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, CLC);
		fillStatusRegister();
		runNCycles(2);
		int statusRegister = getStatusRegister();
		assertEquals(0, statusRegister & 1);
	}
	
	public void testSEC() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, SEC);
		clearStatusRegister();
		runNCycles(2);
		int statusRegister = getStatusRegister();
		assertEquals(1, statusRegister & 1);
	}
	
	public void testCLI() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, CLI);
		fillStatusRegister();
		runNCycles(2);
		int statusRegister = getStatusRegister();
		assertEquals(0, (statusRegister >> INTERRUPT_FLAG) & 1);
	}
	
	public void testSEI() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, SEI);
		clearStatusRegister();
		runNCycles(2);
		int statusRegister = getStatusRegister();
		assertEquals(1, (statusRegister >> INTERRUPT_FLAG) & 1);
	}
	
	public void testCLV() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, CLV);
		fillStatusRegister();
		runNCycles(2);
		int statusRegister = getStatusRegister();
		assertEquals(0, (statusRegister >> OVERFLOW_FLAG) & 1);
	}
	
	public void testCLD() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, CLD);
		fillStatusRegister();
		runNCycles(2);
		int statusRegister = getStatusRegister();
		assertEquals(0, (statusRegister >> DECIMAL_FLAG) & 1);
	}
	
	public void testSED() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, SED);
		clearStatusRegister();
		runNCycles(2);
		int statusRegister = getStatusRegister();
		assertEquals(1, (statusRegister >> DECIMAL_FLAG) & 1);
	}

}
