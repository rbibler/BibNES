package com.bibler.awesome.bibnes.tests;

import com.bibler.awesome.bibnes.systems.CPU;
import com.bibler.awesome.bibnes.systems.Memory;

import junit.framework.TestCase;

public class ASLTest extends TestCase {
	
	private CPU cpu;
	private Memory rom;
	
	private final int ACCUMULATOR = 0x0A;
	private final int ZERO_PAGE = 0x06;
	private final int ZERO_PAGE_X = 0x16;
	private final int ABSOLUTE = 0x0E;
	private final int ABSOLUTE_X = 0x1E;
	
	public void testASLZeroPage() {
		rom = new Memory(0x8000);
		cpu = new CPU(rom);	
		rom.write(0x40, ZERO_PAGE);
		rom.write(0x41, 0x23);
		rom.write(0x23, 0x44);
		rom.write(0xFFFC, 0x40);
		rom.write(0xFFFD, 0);
		cpu.powerOn();
		cpu.cycle();
		cpu.cycle();
		cpu.cycle();
		cpu.cycle();
		cpu.cycle();
		assertEquals(0x88, rom.read(0x23));
		int statusRegister = cpu.getStatusRegister();
		assertEquals(1, (statusRegister >> 6) & 1);
		assertEquals(0, statusRegister & 1);
		assertEquals(0, (statusRegister >> 1) & 1);
	}

	/*public void testANDZeroPageX() {
		rom = new Memory(0x8000);
		cpu = new CPU(rom);	
		rom.write(0x40, ZERO_PAGE_X);
		rom.write(0x41, 0xC0);
		rom.write(0x20, 0xC0);
		rom.write(0xFFFC, 0x40);
		rom.write(0xFFFD, 0);
		cpu.powerOn();
		cpu.setIndexX(0x60);											//Set high to test wrap around
		cpu.setAccumulator(0x60);
		cpu.cycle();
		cpu.cycle();
		cpu.cycle();
		cpu.cycle();
		assertEquals(0x40, cpu.getAccumulator());
	}

	public void testANDAbsolute() {
		rom = new Memory(0x8000);
		cpu = new CPU(rom);
		rom.write(0x40, ABSOLUTE);
		rom.write(0x41, 0x00);
		rom.write(0x42, 0x44);
		rom.write(0x4400, 0xC0);
		rom.write(0xFFFC, 0x40);
		rom.write(0xFFFD, 0);
		cpu.powerOn();
		cpu.setAccumulator(0x60);
		cpu.cycle();
		cpu.cycle();
		cpu.cycle();
		cpu.cycle();
		assertEquals(0x40, cpu.getAccumulator());
	}

	public void testANDAbsoluteX() {
		rom = new Memory(0x8000);
		cpu = new CPU(rom);
		rom.write(0x40, ABSOLUTE_X);
		rom.write(0x41, 0xC0);
		rom.write(0x42, 0x44);
		rom.write(0x4520, 0xC0);
		rom.write(0xFFFC, 0x40);
		rom.write(0xFFFD, 0);
		cpu.powerOn();
		cpu.setIndexX(0x60);
		cpu.setAccumulator(0x60);
		cpu.cycle();
		cpu.cycle();
		cpu.cycle();
		cpu.cycle();
		cpu.cycle();
		assertEquals(0x40, cpu.getAccumulator());
	}

	public void testANDAbsoluteY() {
		rom = new Memory(0x8000);
		cpu = new CPU(rom);
		rom.write(0x40, ABSOLUTE_Y);
		rom.write(0x41, 0xC0);
		rom.write(0x42, 0x44);
		rom.write(0x4520, 0xC0);
		rom.write(0xFFFC, 0x40);
		rom.write(0xFFFD, 0);
		cpu.powerOn();
		cpu.setIndexY(0x60);
		cpu.setAccumulator(0x60);
		cpu.cycle();
		cpu.cycle();
		cpu.cycle();
		cpu.cycle();
		cpu.cycle();
		assertEquals(0x40, cpu.getAccumulator());
		assertEquals(5, cpu.getTotalCycles());
	
	}

	public void testANDIndirectX() {
		rom = new Memory(0x8000);
		cpu = new CPU(rom);
		rom.write(0x40, INDIRECT_X);
		rom.write(0x41, 0x44);
		rom.write(0x48, 0x00);
		rom.write(0x49,  0x44);
		rom.write(0x4400, 0xC0);
		rom.write(0xFFFC, 0x40);
		rom.write(0xFFFD, 0);
		cpu.powerOn();
		cpu.setIndexX(0x4);
		cpu.setAccumulator(0x60);
		cpu.cycle();
		cpu.cycle();
		cpu.cycle();
		cpu.cycle();
		cpu.cycle();
		cpu.cycle();
		assertEquals(0x40, cpu.getAccumulator());
		assertEquals(6, cpu.getTotalCycles());
	
	}
	
	public void testANDIndirectY() {
		rom = new Memory(0x8000);
		cpu = new CPU(rom);
		rom.write(0x40, INDIRECT_Y);
		rom.write(0x41, 0x44);
		rom.write(0x44, 0xC0);
		rom.write(0x45,  0x44);
		rom.write(0x4520, 0xC0);
		rom.write(0xFFFC, 0x40);
		rom.write(0xFFFD, 0);
		cpu.powerOn();
		cpu.setIndexY(0x60);
		cpu.setAccumulator(0x60);
		cpu.cycle();
		cpu.cycle();
		cpu.cycle();
		cpu.cycle();
		cpu.cycle();
		cpu.cycle();
		assertEquals(0x40, cpu.getAccumulator());
		assertEquals(6, cpu.getTotalCycles());
		
	}*/

}
