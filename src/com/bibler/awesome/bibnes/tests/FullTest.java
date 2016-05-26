package com.bibler.awesome.bibnes.tests;

import java.io.File;

import com.bibler.awesome.bibnes.io.BinReader;
import com.bibler.awesome.bibnes.systems.CPU;
import com.bibler.awesome.bibnes.systems.Memory;

import junit.framework.TestCase;

public class FullTest extends TestCase {
	
	private CPU cpu;
	private Memory memory;
	
	public void testFull() {
		memory = BinReader.readBin(new File("C:/users/ryan/desktop/textTest.bin"));
		cpu = new CPU(memory);
		cpu.powerOn();
		cpu.setProgramCounter(0);
		for(int i = 0; i < 0x200; i++) {
			cpu.cycle();
		}
		
		assertEquals(0x09, cpu.getXIndex());
	}
	
	

}
