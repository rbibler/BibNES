package com.bibler.awesome.bibnes.emulator.tests;

import java.io.File;

import com.bibler.awesome.bibnes.io.BinReader;
import com.bibler.awesome.bibnes.systems.CPU;
import com.bibler.awesome.bibnes.systems.Memory;
import junit.framework.TestCase;

public class EmulatorTest extends TestCase {
	
	private CPU cpu;
	private Memory memory;
	
	public void testFull() {
		String currentDirFile = System.getProperty("user.dir");
		File f = new File(currentDirFile + "/NES Files/test/allsuitea.bin");
		memory = BinReader.readBin(f);
		cpu = new CPU(memory);
		cpu.powerOn();
		for(int i = 0; i < 0x800; i++) {
			cpu.cycle();
		}
		
		assertEquals(0x55, memory.read(0x22A));
		assertEquals(0xAA, memory.read(0xA9));
		//assertEquals(0xFF, memory.read(0x71));
		assertEquals(0x6E, memory.read(0x1DD));
		//assertEquals(0x7F, memory.read(0x15));
		assertEquals(0xFF, memory.read(0x210));
	}

}
