package com.bibler.awesome.bibnes.tests;

import com.bibler.awesome.bibnes.systems.CPU;
import com.bibler.awesome.bibnes.systems.Memory;

import junit.framework.TestCase;

public class LDATest extends TestCase {
	
	private CPU cpu;
	private Memory rom;
	

	
	public void testLDAImmediate() {
		rom = new Memory(0x8000);
		cpu = new CPU(rom);	
		rom.write(0x40, 0xA9);
		rom.write(0x41, 0xFA);
		rom.write(0xFFFC, 0x40);
		rom.write(0xFFFD, 0);
		cpu.powerOn();
		cpu.cycle();
		cpu.cycle();
		assertEquals(0xFA, cpu.getAccumulator());
	}
	
	public void testLDAZeroPage() {
		rom = new Memory(0x8000);
		cpu = new CPU(rom);	
		rom.write(0x40, 0xA5);
		rom.write(0x41, 0x23);
		rom.write(0x23, 0xFA);
		rom.write(0xFFFC, 0x40);
		rom.write(0xFFFD, 0);
		cpu.powerOn();
		cpu.cycle();
		cpu.cycle();
		cpu.cycle();
		assertEquals(0xFA, cpu.getAccumulator());
	}
	
	public void testLDAZeroPageX() {
		rom = new Memory(0x8000);
		cpu = new CPU(rom);	
		rom.write(0x40,  0xB5);
		rom.write(0x41, 0xC0);
		rom.write(0x20, 0xFA);
		rom.write(0xFFFC, 0x40);
		rom.write(0xFFFD, 0);
		cpu.powerOn();
		cpu.setIndexX(0x60);											//Set high to test wrap around
		cpu.cycle();
		cpu.cycle();
		cpu.cycle();
		cpu.cycle();
		assertEquals(0xFA, cpu.getAccumulator());
	}
	
	public void testLDAAbsolute() {
		rom = new Memory(0x8000);
		cpu = new CPU(rom);
		rom.write(0x40, 0xAD);
		rom.write(0x41, 0x00);
		rom.write(0x42, 0x44);
		rom.write(0x4400, 0xFA);
		rom.write(0xFFFC, 0x40);
		rom.write(0xFFFD, 0);
		cpu.powerOn();
		cpu.cycle();
		cpu.cycle();
		cpu.cycle();
		cpu.cycle();
		assertEquals(0xFA, cpu.getAccumulator());
	}
	
	public void testLDAAbsoluteX() {
		System.out.println("ABSX");
		rom = new Memory(0x8000);
		cpu = new CPU(rom);
		rom.write(0x40, 0xBD);
		rom.write(0x41, 0xC0);
		rom.write(0x42, 0x44);
		rom.write(0x4520, 0xFA);
		rom.write(0xFFFC, 0x40);
		rom.write(0xFFFD, 0);
		cpu.powerOn();
		cpu.setIndexX(0x60);
		cpu.cycle();
		cpu.cycle();
		cpu.cycle();
		cpu.cycle();
		cpu.cycle();
		assertEquals(0xFA, cpu.getAccumulator());
		assertEquals(5, cpu.getTotalCycles());
	}
	
	public void testLDAAbsoluteY() {
		rom = new Memory(0x8000);
		cpu = new CPU(rom);
		rom.write(0x40, 0xB9);
		rom.write(0x41, 0xC0);
		rom.write(0x42, 0x44);
		rom.write(0x4520, 0xFA);
		rom.write(0xFFFC, 0x40);
		rom.write(0xFFFD, 0);
		cpu.powerOn();
		cpu.setIndexY(0x60);
		cpu.cycle();
		cpu.cycle();
		cpu.cycle();
		cpu.cycle();
		cpu.cycle();
		assertEquals(0xFA, cpu.getAccumulator());
		assertEquals(5, cpu.getTotalCycles());
	}
	
	public void testLDAIndirectX() {
		rom = new Memory(0x8000);
		cpu = new CPU(rom);
		rom.write(0x40, 0xA1);
		rom.write(0x41, 0x44);
		rom.write(0x48, 0x00);
		rom.write(0x49,  0x44);
		rom.write(0x4400, 0xFA);
		rom.write(0xFFFC, 0x40);
		rom.write(0xFFFD, 0);
		cpu.powerOn();
		cpu.setIndexX(0x4);
		cpu.cycle();
		cpu.cycle();
		cpu.cycle();
		cpu.cycle();
		cpu.cycle();
		cpu.cycle();
		assertEquals(0xFA, cpu.getAccumulator());
		assertEquals(6, cpu.getTotalCycles());
	}
	
	public void testLDAIndirectY() {
		rom = new Memory(0x8000);
		cpu = new CPU(rom);
		rom.write(0x40, 0xB1);
		rom.write(0x41, 0x44);
		rom.write(0x44, 0xC0);
		rom.write(0x45,  0x44);
		rom.write(0x4520, 0xFA);
		rom.write(0xFFFC, 0x40);
		rom.write(0xFFFD, 0);
		cpu.powerOn();
		cpu.setIndexY(0x60);
		cpu.cycle();
		cpu.cycle();
		cpu.cycle();
		cpu.cycle();
		cpu.cycle();
		cpu.cycle();
		assertEquals(0xFA, cpu.getAccumulator());
		assertEquals(6, cpu.getTotalCycles());
	}

}
