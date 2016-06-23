package com.bibler.awesome.bibnes.tests;

import com.bibler.awesome.bibnes.systems.PPU;

import junit.framework.TestCase;

public class PPUTest extends TestCase {
	
	private PPU ppu;
	
	public void testPPUWrites() {
		ppu = new PPU(null);
		ppu.write(0x2000, 0);
		assertEquals(0, ppu.getT());
		ppu.write(0x2005, 0x7D);
		assertEquals(0b1111,ppu.getT());
		assertEquals(0b101, ppu.getX());
		ppu.write(0x2005,  0x5E);
		assertEquals(0b110000101101111, ppu.getT());
		ppu.write(0x2006, 0x3D);
		assertEquals(0b011110101101111, ppu.getT());
		ppu.write(0x2006, 0xF0);
		assertEquals(0b011110111110000, ppu.getT());
		assertEquals(0b011110111110000, ppu.getV());
	}

}
