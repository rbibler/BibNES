package com.bibler.awesome.bibnes;

import com.bibler.awesome.bibnes.systems.CPU;
import com.bibler.awesome.bibnes.systems.Memory;

public class Main {
	
	public static void main(String[] args) {
		Memory mem = new Memory(0x1000);
		mem.write(0, 0x44);
		mem.write(1, 0xFA);
		CPU cpu = new CPU(mem, 0x1000);
		cpu.cycle();
		cpu.cycle();
		cpu.cycle();
	}

}
