package com.bibler.awesome.bibnes.systems;

import java.util.ArrayList;

import com.bibler.awesome.bibnes.assembler.BreakpointManager;
import com.bibler.awesome.bibnes.assembler.Disassembler;
import com.bibler.awesome.bibnes.communications.Notifiable;
import com.bibler.awesome.bibnes.communications.Notifier;
import com.bibler.awesome.bibnes.controllers.BaseController;
import com.bibler.awesome.bibnes.io.LogWriter;
import com.bibler.awesome.bibnes.mappers.Mapper;

public class NES implements Notifier, Runnable {
	
	private PPU ppu;
	private APU apu;
	private CPU cpu;
	private int[] cpuMem;
	private int[] ppuMem;
	
	private boolean frameBased;
	
	private Mapper mapper;
	
	private int cycleCount;
	private boolean running;
	private boolean pause;
	private boolean breakpointEngaged;
	private boolean stepped;
	private boolean frameByFrame;
	private Object pauseLock = new Object();
	
	private int mirrorType;
	
	public static final int HORIZ = 0;
	public static final int VERT = 1;
	public static final int SINGLE_SCREEN = 2;
	
	private Disassembler disassembler = new Disassembler();
	private BaseController controller;
	
	private BreakpointManager breakpointManager;
	
	public double averageFrameRate;
	private long lastFrameTime;
	private long frameRate = (long) (1000 / 59.9);
	private long totalFrameTime;
	private long frameCount;
	public long initialFrameTime;
	public long frameTimeAfterSleep;
	
	private int cpuCyclesToSkip;
	private boolean skipCPUCycles;
	
	private ArrayList<Notifiable> objectsToNotify = new ArrayList<Notifiable>();
	private LogWriter log = new LogWriter("C:/users/ryan/desktop/logs/log.txt");
	
	private long frameStartTime;
	
	public NES() {
		cpu = new CPU(this);
		ppu = new PPU(this);
		apu = new APU(this);
		cpuMem = new int[0x10000];
		ppuMem = new int[0x4000];
	}
	
	public void setMapper(Mapper mapper) {
		this.mapper = mapper;
		mapper.setNES(this);
	}
	
	public Mapper getMapper() {
		return mapper;
	}
	
	
	public void setPeripheral(BaseController controller) {
		this.controller = controller;
	}
	
	private int vblankStart;
	private int vblankStop;
	private int cycles;
	
	public void startVBlankClock() {
		vblankStart = cycles;
	}
	
	public void stopVBlankClock() {
		vblankStop = cycles;
		System.out.println("Vblank period: " + (vblankStop - vblankStart));
	}
	
	
	public void power() {
		cpu.powerOn(null);
		ppu.reset();
	}
	
	public void reset() {
		cpu.reset();
		apu.reset();
	}
	
	public void kill() {
		apu.kill();
	}
	
	
	public void registerObjectToNotify(Notifiable objectToNotify) {
		cpu.registerObjectToNotify(objectToNotify);
		ppu.registerObjectToNotify(objectToNotify);
		if(!objectsToNotify.contains(objectToNotify)) {
			objectsToNotify.add(objectToNotify);
		}
	}
	
	public void unregisterAll() {
		cpu.unregisterAll();
		ppu.unregisterAll();
		objectsToNotify.clear();
	}

	public int[] getCPUMem() {
		return cpuMem;
	}
	
	public int[] getPPUMem() {
		return ppuMem;
	}
	
	
	public void runSystem() {
		Thread t = new Thread(this);
		pause();
		running = true;
		t.start();
		resume();
	}
	
	
	public void pause() {
		synchronized(pauseLock) {
			pause = true;
		}
	}
	
	
	public void resume() {
		synchronized (pauseLock) {
            pause = false;
            pauseLock.notifyAll();
        }
	}
	
	private void setCPUSkip(int skip) {
		cpuCyclesToSkip = skip;
		skipCPUCycles = true;
	}	
	
	public void interrupt() {
		
	}
	
	
	private int totalClocks;
	public void cycle() {
		//if(totalClocks % 4 == 0) {
			
		//}
		ppu.cycle();
		if(totalClocks % 3 == 0) {
			if(!skipCPUCycles) {
				cpu.cycle();
			} else {
				cpuCyclesToSkip--;
				if(cpuCyclesToSkip == 0) {
					skipCPUCycles = false;
				}
			}
			apu.clock();
		}
		if(totalClocks == 89490 / 4) {
			apu.stepFrame();
			totalClocks = 0;
		}
		
		totalClocks++;
		cycles++;
	}
	
	private void runFrame() {
		for(int i = 0; i < 89342; i++) {
			cycle();
		}
		frame();
		totalClocks = 0;
	}
	
	public void runEmulator() {
		if(running) {
			breakpointEngaged = false;
			stepped = false;
			resume();
		} else {
			runSystem();
		}
	}
	
	public void nextFrame() {
		frameByFrame = true;
		resume();
		if(!running) {
			runSystem();
		}
	}
	
	public void frame() {
		apu.finishFrame();
		notify("FRAME");
		cycles = 0;
		initialFrameTime = System.currentTimeMillis() - lastFrameTime;
		if(initialFrameTime < frameRate) {
			try {
				Thread.sleep((long) (frameRate - initialFrameTime));
			} catch(InterruptedException e) {}
		}
		if(lastFrameTime != 0) {
			frameTimeAfterSleep = System.currentTimeMillis() - lastFrameTime;
			totalFrameTime += frameTimeAfterSleep;
			frameCount++;
			averageFrameRate = 1000.0 / (totalFrameTime / frameCount);
			
		}
		lastFrameTime = System.currentTimeMillis();
		//printStats();
		
		if(frameByFrame) {
			pause();
		}
	}
	
	private void printStats() {
		System.out.println("Frame time: " + initialFrameTime);
	}
	
	
	public void writeToPPU(int addressToWrite, int data) {
		ppu.writeProgramRegister(addressToWrite & 7, data);
		
	}
	
	public void writeToAPU(int addressToWrite, int data) {
		if(addressToWrite == 0x4016) {
			controller.write();
		} else if(addressToWrite == 0x4014) {
			int n = data * 0x100;
			for(int i = 0; i < 0x100; i++) {
				ppu.writeProgramRegister(4, cpuMem[i + n]);
			}
		} else {
			apu.write(addressToWrite, data);
		}
	}
	
	public int readFromPPU(int addressToRead) {
		return ppu.readProgramRegister(addressToRead & 7);
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
	
	public void fillCPU(int address, int data) {
		cpuMem[address] = data;
	}
	
	public void cpuWrite(int address, int data) {
		if(address < 0x2000) {														// Write to CPU Ram
			address %= 0x800;
			cpuMem[address] = data;
			cpuMem[address + 0x800] = data;
			cpuMem[address + 0x1000] = data;
			cpuMem[address + 0x1800] = data;
			
		} else if(address < 0x4000) {
			writeToPPU(address, data);
		} else if(address < 0x4020) {
			writeToAPU(address, data);
		} else if(address >= 0x8000) {
			mapper.writePrg(address, data);
		}
	}
	
	public int cpuRead(int address) {
		int readData = 0;
		if(address < 0x2000) {														// Write to CPU Ram
			readData = cpuMem[address];
		} else if(address < 0x4000) {
			readData = readFromPPU(address);
		} else if(address < 0x4020) {
			readData = readFromAPU(address);
		} else if(address >= 0x8000){
			readData = mapper.readPrg(address % 0x10000);
		}
		return readData;
	}
	
	public int readDMCByte(int address) {
		setCPUSkip(4);
		return cpuRead(address);
	}
	
	public void fillPPURom(int address, int data) {
		ppuMem[address] = data;
	}
	
	public void ppuWrite(int address, int data) {
		if(address < 0x2000) {
			mapper.writeChr(address, data);
		} else if(address < 0x3000) {
			if(mirrorType == HORIZ) {
				ppuMem[address] = data;
				if(address < 0x2400 || (address >= 0x2800 && address < 0x2C00)) {
					ppuMem[address + 0x400] = data;
				} else {
					ppuMem[address - 0x400] = data;
				}
			} else if(mirrorType == VERT) {
				ppuMem[address] = data;
				if(address < 0x2800) {
					ppuMem[address + 0x800] = data;
				} else {
					ppuMem[address - 0x800] = data;
				}
			}
		} else if(address < 0x4000) {
			final int baseAddress = address % 0x20;
			for(int i = 0; i < 8; i++) {
				ppuMem[(i * 0x20) + baseAddress + 0x3F00] = data;
			}
			if(address % 4 == 0) {
				ppuMem[address & ~(1 << 4)] = data;
				ppuMem[address | (1 << 4)] = data;
			}
			
		}
	}
	
	public int ppuRead(int address) {
		int ret = 0;
		if(address < 0x2000) {
			ret = mapper.readChr(address);
		} else {
		    ret = ppuMem[address % 0x4000];
		// Returns the background color for all palette 0 cases. Might not be the right place to do this. 
		} 
		if(address >= 0x3F00) {
			if(address % 4 == 0) {
				ret = ppuMem[0x3F00];
			}
		} 
		return ret;
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
			synchronized(pauseLock) {
                if (pause) {
                    while (pause) {
                        try {
                        	pauseLock.wait();
                        } catch (InterruptedException e) {}
                    }
                }
            }
			runFrame();
		}
	}

	public PPU getPPU() {
		return ppu;
	}
	
	public CPU getCPU() {
		return cpu;
	}
	
	public APU getAPU() {
		return apu;
	}

	public void setBreakpointManager(BreakpointManager bpManager) {
		this.breakpointManager = bpManager;
		
	}
	
	public void enableAudio(boolean enable) {
		apu.enableAudio(enable);
	}
	
	public void setAudioChannelEnable(int audioChannel, boolean enable) {
		apu.setChannelEnabled(audioChannel, enable);
	}
	
	public void updateAudioParams(int paramNum) {
		apu.updateAudioParams(paramNum);
	}
	
}
