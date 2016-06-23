package com.bibler.awesome.bibnes.tests;

import com.bibler.awesome.bibnes.systems.PPU;

import junit.framework.TestCase;

public class PPUTest extends TestCase {
	
	private PPU ppu;
	
	public void testPPUWrites() {
		ppu = new PPU(null);
		ppu.write(0x2000, 0b11);
		ppu.write(0x2005, 0b10101);
		assertEquals(0b110000010101, ppu.getT());
	}

}
