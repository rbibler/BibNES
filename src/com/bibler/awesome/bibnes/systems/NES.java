package com.bibler.awesome.bibnes.systems;

import java.util.ArrayList;

import com.bibler.awesome.bibnes.assembler.BreakpointManager;
import com.bibler.awesome.bibnes.assembler.Disassembler;
import com.bibler.awesome.bibnes.communications.Notifiable;
import com.bibler.awesome.bibnes.communications.Notifier;
import com.bibler.awesome.bibnes.io.LogWriter;

public class NES extends Motherboard implements Notifier, Runnable {
	
	private PPU ppu;
	private APU apu;
	private int[] cpuMem;
	private int[] ppuMem;
	
	private int cycleCount;
	private boolean running;
	private boolean pause;
	private boolean breakpointEngaged;
	private boolean stepped;
	private Object pauseLock = new Object();
	
	private int mirrorType;
	
	private final int HORIZ = 0;
	private final int VERT = 1;
	
	private Disassembler disassembler = new Disassembler();
	private LogWriter logWriter = new LogWriter("C:/users/ryan/desktop/logs/NESLog");
	private Peripheral controller;
	
	private ArrayList<Notifiable> objectsToNotify = new ArrayList<Notifiable>();
	
	public NES() {
		cpu = new CPU(this);
		ppu = new PPU(this);
		apu = new APU();
		cpuMem = new int[0x10000];
		ppuMem = new int[0x4000];
	}
	
	@Override
	public void setPeripheral(Peripheral controller) {
		this.controller = controller;
	}
	
	@Override
	public void power() {
		cpu.powerOn();
		ppu.reset();
	}
	
	@Override
	public void registerObjectToNotify(Notifiable objectToNotify) {
		cpu.registerObjectToNotify(objectToNotify);
		ppu.registerObjectToNotify(objectToNotify);
		if(!objectsToNotify.contains(objectToNotify)) {
			objectsToNotify.add(objectToNotify);
		}
	}

	public int[] getCPUMem() {
		return cpuMem;
	}
	
	public int[] getPPUMem() {
		return ppuMem;
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
		checkCPUCycle();
		if(breakpointEngaged) {
			return;
		}
		ppu.cycle();
		cycleCount++;
	}

	private void checkCPUCycle() {
		if(cycleCount % 3 == 0) {
			checkForNewCPUInstruction();
			cpu.cycle();
			if(breakpointEngaged) {
				return;
			}
			
		}
	}

	private void checkForNewCPUInstruction() {
		if(cpu.getCyclesRemaining() == 0) {
			breakpointEngaged = checkForBreakpoint(cpu.getProgramCounter());
			notify("STEP" + cpu.getProgramCounter() % 0x2000);
		}
	}
	
	private boolean checkForBreakpoint(int pc) {
		return breakpoints.contains(pc % 0x2000);
	}
	
	@Override
	public void step() {
		do {
			cycle();
		} while(cpu.getCyclesRemaining() > 0);
		 while(cycleCount % 3 != 0) {
			cycle();
		};
		if(stepped) {
			breakpointEngaged = true;
			stepped = false;
		}
		
	}
	
	@Override
	public void stepNext() {
		stepped = true;
		breakpointEngaged = false;
		if(!running) {
			runSystem();
		}
	}
	
	@Override
	public void runEmulator() {
		if(running) {
			breakpointEngaged = false;
			stepped = false;
		} else {
			runSystem();
		}
	}
	
	
	public void writeToPPU(int addressToWrite, int data) {
		ppu.write(addressToWrite, data);
		
	}
	
	public void writeToAPU(int addressToWrite, int data) {
		if(addressToWrite == 0x4016) {
			controller.write();
		} else if(addressToWrite == 0x4014) {
			int n = data * 0x100;
			for(int i = 0; i < 0x100; i++) {
				ppu.write(0x2004, cpuMem[i + n]);
			}
		}
		apu.write(addressToWrite, data);
	}
	
	public int readFromPPU(int addressToRead) {
		return ppu.read(addressToRead);
	}
	
	public int readFromAPU(int addressToRead) {
		if(addressToRead == 0x4016) {
			return controller.read();
		}
		return apu.read(addressToRead);
	}
	
	public void NMI(boolean NMIFlag) {
		cpu.setNMI(NMIFlag);
	}
	
	public void setMirror(int mirrorType) {
		this.mirrorType = mirrorType;
	}
	
	public void cpuWrite(int address, int data) {
		if(address < 0x2000) {														// Write to CPU Ram
			address %= 0x800;
			cpuMem[address] = data;
			cpuMem[address + 0x800] = data;
			cpuMem[address + 0x1000] = data;
			cpuMem[address + 0x1800] = data;
			
			notify("CPUMEM" + address + "," + data);
			notify("CPUMEM" + (address + 0x800) + "," + data);
			notify("CPUMEM" + (address + 0x1000) + "," + data);
			notify("CPUMEM" + (address + 0x1800) + "," + data);
			
		} else if(address < 0x4000) {
			writeToPPU(address, data);
		} else if(address < 0x4020) {
			writeToAPU(address, data);
		} else {
			cpuMem[address % 0x10000] = data;
			notify("CPUMEM" + (address % 0x10000) + "," + data);
		}
	}
	
	public int cpuRead(int address) {
		int readData = 0;
		if(address < 0x2000) {														// Write to CPU Ram
			address %= 0x800;
			readData = cpuMem[address];
			
		} else if(address < 0x4000) {
			readData = readFromPPU(address);
		} else if(address < 0x4020) {
			readData = readFromAPU(address);
		} else {
			readData = cpuMem[address % 0x10000];
		}
		return readData;
	}
	
	public void ppuWrite(int address, int data) {
		if(address < 0x2000) {
			ppuMem[address] = data;
			notify("PPUMEM" + address + "," + data);
		} else if(address < 0x3000) {
			if(mirrorType == HORIZ) {
				ppuMem[address] = data;
				ppuMem[address + 0x400] = data;
				notify("PPUMEM" + address + "," + data);
				notify("PPUMEM" + (address + 0x400) +  "," + data);
			} else if(mirrorType == VERT) {
				ppuMem[address] = data;
				ppuMem[address + 0x800] = data; 
				notify("PPUMEM" + address + "," + data);
				notify("PPUMEM" + (address + 0x800) + "," + data);
			}
		} else if(address < 0x4000) {
			address = (0x3F00 + (address % 0x20));
			for(int i = 0; i < 8; i++) {
				ppuMem[address + (i * 0x20)] = data;
				notify("PPUM_MEM" + (address + (i * 0x20)) + "," + data);
			}
		}
	}
	
	public int ppuRead(int address) {
		return ppuMem[address % 0x4000];
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
			if(!breakpointEngaged) {
				step();
			} else {
				try {
					Thread.sleep(10);
				} catch(InterruptedException e) {}
			}
		}
		
	}
	
	
}
