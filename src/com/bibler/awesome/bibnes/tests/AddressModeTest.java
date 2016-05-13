package com.bibler.awesome.bibnes.tests;

import com.bibler.awesome.bibnes.systems.CPU;
import com.bibler.awesome.bibnes.systems.Memory;

import junit.framework.TestCase;

public class AddressModeTest extends TestCase {
	
	private CPU cpu;
	private Memory rom;
	
	public void testAbsolute() {
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
		assertEquals(0x4400, cpu.getDataCounter());
		assertEquals(0xFA, cpu.getDataRegister());
	}
	
	public void testAbsoluteIndexedX() {
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
		assertEquals(0x4520, cpu.getDataCounter());
		assertEquals(0xFA, cpu.getDataRegister());
	}
	
	public void testAbsoluteIndexedY() {
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
		assertEquals(0x4520, cpu.getDataCounter());
		assertEquals(0xFA, cpu.getDataRegister());
	}
	
	public void testAccumulator() {
		
	}
	
	public void testImmediate() {
		rom = new Memory(0x8000);
		cpu = new CPU(rom);	
		rom.write(0x40, 0xA9);
		rom.write(0x41, 0xFA);
		rom.write(0xFFFC, 0x40);
		rom.write(0xFFFD, 0);
		cpu.powerOn();
		cpu.cycle();
		cpu.cycle();
		assertEquals(0x41, cpu.getDataCounter());
		assertEquals(0xFA, cpu.getDataRegister());
		
	}
	
	public void testImplied() {
		
	}
	
	public void testIndexedIndirect() {
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
		assertEquals(0x4400, cpu.getDataCounter());
		assertEquals(0xFA, cpu.getDataRegister());
	}
	
	public void testIndirect() {
		
	}
	
	public void testIndirectIndexed() {
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
		assertEquals(0x4520, cpu.getDataCounter());
		assertEquals(0xFA, cpu.getDataRegister());
	}
	
	public void testRelative() {
		
	}
	
	public void testZeroPage() {
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
		assertEquals(0x23, cpu.getDataCounter());
		assertEquals(0xFA, cpu.getDataRegister());
	}
	
	public void testZeroPageIndexed() {
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
		assertEquals(0x20, cpu.getDataCounter());
		assertEquals(0xFA, cpu.getDataRegister());
	}

}
