package com.bibler.awesome.bibnes.tests;

import com.bibler.awesome.bibnes.systems.CPU;
import com.bibler.awesome.bibnes.systems.Memory;

import junit.framework.TestCase;

public class ADCTest extends TestCase {
	
	private CPU cpu;
	private Memory rom;
	
	
	private final int IMMEDIATE = 0x69;
	private final int ZERO_PAGE = 0x65;
	private final int ZERO_PAGE_X = 0x75;
	private final int ABSOLUTE = 0x6D;
	private final int ABSOLUTE_X = 0x7D;
	private final int ABSOLUTE_Y = 0x79;
	private final int INDIRECT_X = 0x61;
	private final int INDIRECT_Y = 0x71;

	
	public void testLDAImmediate() {
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
		assertEquals(0x20, cpu.getAccumulator());
		assertEquals(1, (cpu.getStatusRegister() & 1));
	}
	
	public void testLDAImmediateOverflow() {
		rom = new Memory(0x8000);
		cpu = new CPU(rom);	
		rom.write(0x40, IMMEDIATE);
		rom.write(0x41, 0x92);
		rom.write(0xFFFC, 0x40);
		rom.write(0xFFFD, 0);
		cpu.setAccumulator(0xA4);
		cpu.powerOn();
		cpu.cycle();
		cpu.cycle();
		assertEquals(0x36, cpu.getAccumulator());
		final int statusRegister = cpu.getStatusRegister();
		assertEquals(1, statusRegister & 1);								// Check Carry Flag
		assertEquals(1, (statusRegister >> 5) & 1);							// Check Overflow
	}
	
	public void testLDAImmediateNoOverflow() {
		rom = new Memory(0x8000);
		cpu = new CPU(rom);	
		rom.write(0x40, IMMEDIATE);
		rom.write(0x41, 0xD8);
		rom.write(0xFFFC, 0x40);
		rom.write(0xFFFD, 0);
		cpu.setAccumulator(0x59);
		cpu.powerOn();
		cpu.cycle();
		cpu.cycle();
		assertEquals(0x31, cpu.getAccumulator());
		final int statusRegister = cpu.getStatusRegister();
		assertEquals(1, statusRegister & 1);								// Carry flag should be set
		assertEquals(0, (statusRegister >> 5) & 1);							// Overflow should not
	}
	
	public void testLDAImmediateZero() {
		rom = new Memory(0x8000);
		cpu = new CPU(rom);	
		rom.write(0x40, IMMEDIATE);
		rom.write(0x41, 0);
		rom.write(0xFFFC, 0x40);
		rom.write(0xFFFD, 0);
		cpu.setAccumulator(0);
		cpu.powerOn();
		cpu.cycle();
		cpu.cycle();
		assertEquals(0, cpu.getAccumulator());
		final int statusRegister = cpu.getStatusRegister();
		assertEquals(1, statusRegister>>1 & 1);								// Carry flag should be set
	}
	
	/*public void testLDAZeroPage() {
		rom = new Memory(0x8000);
		cpu = new CPU(rom);	
		rom.write(0x40, ZERO_PAGE);
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
		rom.write(0x40,  ZERO_PAGE_X);
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
		rom.write(0x40, ABSOLUTE);
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
		rom.write(0x40, ABSOLUTE_X);
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
		rom.write(0x40, ABSOLUTE_Y);
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
		rom.write(0x40, INDIRECT_X);
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
		rom.write(0x40, INDIRECT_Y);
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
	}*/

}
