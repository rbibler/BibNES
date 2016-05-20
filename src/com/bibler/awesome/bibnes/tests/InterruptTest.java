package com.bibler.awesome.bibnes.tests;

public class InterruptTest extends InstructionTest {

	final int RTI = 0x40;
	
	
	public void testInterrupt() {
		initializeCPU();
		resetCPU();
		writeRom(0x40, 0x2A);
		writeRom(0xFFFA, 0x00);
		writeRom(0xFFFB, 0x44);
		writeRom(0x4400, RTI);
		setAccumulator(0xFF);
		runNCycles(1);
		setNMI();
		runNCycles(13);
		assertEquals(0x41, getProgramCounter());
	}
}
