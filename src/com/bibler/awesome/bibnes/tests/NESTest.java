package com.bibler.awesome.bibnes.tests;

import java.io.File;

import com.bibler.awesome.bibnes.io.BinReader;
import com.bibler.awesome.bibnes.systems.CPU;
import com.bibler.awesome.bibnes.systems.Memory;

import junit.framework.TestCase;

public class NESTest extends TestCase {

	private CPU cpu;
	private Memory rom;
	
	public void testNES() {
		rom = BinReader.readBin(new File("C:/users/ryan/desktop/test.bin"));
		cpu = new CPU(rom);
		cpu.powerOn();
		runNCycles(500);
		System.out.println(rom.read(0x210));
	}
	
	private void runNCycles(int cyclesToRun) {
		for(int i = 0; i < cyclesToRun; i++) {
			cpu.cycle();
		}
	}

}
