package com.bibler.awesome.bibnes.systems;

import java.util.ArrayList;

import com.bibler.awesome.bibnes.communications.Notifiable;
import com.bibler.awesome.bibnes.communications.Notifier;

public class NES extends Motherboard implements Notifier, Runnable {
	
	private PPU ppu;
	private APU apu;
	private Memory cpuRam;
	private Memory ppuRam;
	private Memory cpuMem;
	private Cartridge cart;
	
	private int cycleCount;
	private boolean running;
	private boolean pause;
	private Object pauseLock = new Object();
	
	private ArrayList<Notifiable> objectsToNotify = new ArrayList<Notifiable>();
	
	public NES() {
		cpu = new CPU(this);
		ppu = new PPU();
		apu = new APU();
		cpuRam = new Memory(0x800);
	}
	
	@Override
	public void registerObjectToNotify(Notifiable objectToNotify) {
		cpu.registerObjectToNotify(objectToNotify);
		if(!objectsToNotify.contains(objectToNotify)) {
			objectsToNotify.add(objectToNotify);
		}
	}
	
	public void setCart(Cartridge cart) {
		this.cart = cart;
		setupCPUMem();
	}
	
	private void setupCPUMem() {
		Memory prgMem = cart.getPrgMem();
		cpuMem = new Memory(0x8000 + prgMem.size());
		for(int i = 0; i < cpuRam.size(); i++) {
			cpuMem.write(i, cpuRam.read(i));
		}
		for(int i = 0; i < prgMem.size(); i++) {
			cpuMem.write(0x8000 + i, prgMem.read(i));
		}
		notify("FILL_CPU_MEM");
	}
	
	public Memory getCPUMem() {
		return cpuMem;
	}
	
	@Override
	public void runSystem() {
		Thread t = new Thread(this);
		pause();
		running = true;
		t.start();
		resume();
	}
	
	@Override
	public void pause() {}
	
	@Override
	public void resume() {}
	
	@Override
	public void cycle() {
		//ppu.cycle();
		if(cycleCount % 3 == 0) {
			cpu.cycle();
		}
		cycleCount++;
	}
	
	@Override
	public void step() {
		do {
			cycle();
		} while(cpu.getCyclesRemaining() > 0);
		cycle();
		cycle();
		cycle();
		System.out.println("Stepped");
	}
	
	public void writeToRam(int addressToWrite, int data) {
		cpuRam.write(addressToWrite, data);
		notify("MEM" + addressToWrite + "," + data);
	}
	
	public void writeToPPU(int addressToWrite, int data) {
		ppu.write(addressToWrite, data);
	}
	
	public void writeToAPU(int addressToWrite, int data) {
		apu.write(addressToWrite, data);
	}
	
	public void writeToCart(int addressToWrite, int data) {
		cart.writePrg(addressToWrite, data);
		notify("MEM" + addressToWrite + "," + data);
	}
	
	public int readFromRam(int addressToRead) {
		return cpuRam.read(addressToRead);
	}
	
	public int readFromPPU(int addressToRead) {
		return ppu.read(addressToRead);
	}
	
	public int readFromAPU(int addressToRead) {
		return apu.read(addressToRead);
	}
	
	public int readFromCart(int addressToRead) {
		return cart.readPrg(addressToRead);
	}
	
	@Override
	public void write(int addressToWrite, int data) {
		if(addressToWrite < 0x1000) {
			writeToRam(addressToWrite, data);
		} else if(addressToWrite >= 0x2000 && addressToWrite < 0x4000) {
			writeToPPU(addressToWrite, data);
		} else if(addressToWrite >= 0x4000 && addressToWrite < 0x4020) {
			writeToAPU(addressToWrite, data);
		} else {
			writeToCart(addressToWrite, data);
		}
	}
	
	@Override
	public int read(int addressToRead) {
		int retVal = 0;
		if(addressToRead < 0x1000) {
			retVal = readFromRam(addressToRead);
		} else if(addressToRead >= 0x2000 && addressToRead < 0x4000) {
			retVal = readFromPPU(addressToRead);
		} else if(addressToRead >= 0x4000 && addressToRead < 0x4020) {
			retVal = readFromAPU(addressToRead);
		} else {
			retVal = readFromCart(addressToRead);
		}
		return retVal;
	}

	@Override
	public void notify(String messageToSend) {
		for(Notifiable notifiable : objectsToNotify) {
			notifiable.takeNotice(messageToSend, this);
		}		
	}

	@Override
	public void run() {
		while(running) {
			cycle();
		}
		
	}
	
	
}
