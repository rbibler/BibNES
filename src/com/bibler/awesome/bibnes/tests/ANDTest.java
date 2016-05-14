package com.bibler.awesome.bibnes.tests;

import com.bibler.awesome.bibnes.systems.CPU;
import com.bibler.awesome.bibnes.systems.Memory;

import junit.framework.TestCase;

public class ANDTest extends TestCase {
	
	private CPU cpu;
	private Memory rom;
	
	private final int IMMEDIATE = 0x29;
	private final int ZERO_PAGE = 0x25;
	private final int ZERO_PAGE_X = 0x35;
	private final int ABSOLUTE = 0x2D;
	private final int ABSOLUTE_X = 0x3D;
	private final int ABSOLUTE_Y = 0x39;
	private final int INDIRECT_X = 0x21;
	private final int INDIRECT_Y = 0x31;
	
	public void testANDImmediate() {
		rom = new Memory(0x8000);
		cpu = new CPU(rom);	
		rom.write(0x40, IMMEDIATE);
		rom.write(0x41, 0xC0);
		rom.write(0xFFFC, 0x40);
		rom.write(0xFFFD, 0);
		cpu.setAccumulator(0x60);
		cpu.powerOn();
		cpu.cycle();
		cpu.cycle();
		assertEquals(0x40, cpu.getAccumulator());
		//assertEquals(1, (cpu.getStatusRegister() & 1));
	}
	
	public void testANDZeroPage() {
		rom = new Memory(0x8000);
		cpu = new CPU(rom);	
		rom.write(0x40, ZERO_PAGE);
		rom.write(0x41, 0x23);
		rom.write(0x23, 0xC0);
		rom.write(0xFFFC, 0x40);
		rom.write(0xFFFD, 0);
		cpu.powerOn();
		cpu.setAccumulator(0x60);
		cpu.cycle();
		cpu.cycle();
		cpu.cycle();
		assertEquals(0x40, cpu.getAccumulator());
	}

	public void testANDZeroPageX() {
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
		
	}

}
