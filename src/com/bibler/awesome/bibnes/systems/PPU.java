package com.bibler.awesome.bibnes.systems;

public class PPU {
	
	private final int CYCLES_PER_LINE = 340;
	private final int LINES_PER_FRAME = 261;
	private final int REGISTER_ADDRESS_WIDTH = 0x07;
	
	private Memory oamMem = new Memory(0x100);
	
	private int cycle;
	private int scanline;
	
	//Registers.
	private int ppuCtrl;
	private int ppuMask;
	private int ppuStatus;
	private int oamAddr;
	private int oamData;
	private int ppuScroll;
	private int ppuAddr;
	private int ppuData;
	private int oamDMA;
	
	private int v;
	private int t;
	private int x;
	private int w;
		
	private NES nes;
	
	public PPU(NES nes) {
		this.nes = nes;
	}
	
	public void reset() {
		//ppuStatus = 0x80;
	}
	
	
	public void write(int addressToWrite, int data) {
		switch(addressToWrite % (0x2000)) {
		case 0:
			writePPUCtrl(data);
			break;
		case 1:
			writePPUMask(data);
			break;
		case 2:
			writePPUStatus(data);
			break;
		case 3:
			writeOAMAddr(data);
			break;
		case 4:
			writeOAMData(data);
			break;
		case 5:
			writePPUScroll(data);
			break;
		case 6: 
			writePPUAddr(data);
			break;
		case 7:
			writePPUData(data);
			break;
		}
	}
	
	public int read(int addressToRead) {
		if(addressToRead == 0x2002) {
			return ppuStatus;
		}
		return 0;
	}
	
	private void writePPUCtrl(int data) {
		t = (t & ~3) | ((data & 3) << 10);
		ppuCtrl = data;
	}
	
	private void writePPUMask(int data) {
		ppuMask = data;
	}
	
	private void writePPUStatus(int data) {}
	
	private void writeOAMAddr(int data) {
		oamAddr = data;
	}
	
	private void writeOAMData(int data) {
		oamMem.write(oamAddr, data);
		oamAddr++;
	}
	
	private void writePPUScroll(int data) {
		if(w == 1) {
			ppuScroll |= (data & 0xFF);
			t = (t & ~0b111001111100000) 
					| ((data & 7) << 12) 
					| ((data & 0x38) << 2) 
					| ((data & 0xC0) << 2);
			w = 0;
		} else {
			ppuScroll |= (data << 8);
			t = (t & ~0x1F) | ((data >> 3) & 0x1F);
			x = data & 0x7;
			w = 1;
		}
	}
	
	private void writePPUAddr(int data) {
		if(w == 1) {
			ppuAddr |= (data & 0xFF);
			t = (t & ~0xFF) | ((data & 0xFF));
			v  = t;
			w = 0;
		} else {
			t = (t & ~0xFF00) | ((data & 0x3F) << 8);
			w = 1;
		}
	}
	
	private void writePPUData(int data) {}
	
	public void cycle() {
		updateCycleAndScanLine();
		checkForVBlankAndNMI();
	}
	
	public int getT() {
		return t;
	}
	
	public int getV() {
		return v;
	}
	
	public int getX() {
		return x;
	}

	private void updateCycleAndScanLine() {
		cycle++;
		if(cycle > CYCLES_PER_LINE) {
			cycle = 0;
			scanline++;
			if(scanline > LINES_PER_FRAME) {
				scanline = 0;
			}
		}
	}
	
	private void checkForVBlankAndNMI() {
		if(cycle == 1) { 
			if(scanline == 241) {
				setVBlankFlag();
				if((ppuCtrl >> 7 & 1) == 1) {
					nes.NMI();
				}
				
			} else if(scanline == 261) {
				clearVBlankFlag();
			}
		} 
	}
	
	private void setVBlankFlag() {
		ppuStatus |= (1 << 7);
	}
	
	private void clearVBlankFlag() {
		ppuStatus &= ~(1 << 7);
	}

}
