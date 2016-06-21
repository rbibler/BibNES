package com.bibler.awesome.bibnes.systems;

public class PPU {
	
	private final int CYCLES_PER_LINE = 340;
	private final int LINES_PER_FRAME = 261;
	
	private int cycle;
	private int scanline;
	
	//Registers.
	private int ppuCtrl;
	private int ppuStatus;
	
	private NES nes;
	
	public PPU(NES nes) {
		this.nes = nes;
	}
	
	public void reset() {
		//ppuStatus = 0x80;
	}
	
	
	public void write(int addressToWrite, int data) {
		if(addressToWrite == 0x2000) {
			ppuCtrl = data;
		}
	}
	
	public int read(int addressToRead) {
		if(addressToRead == 0x2002) {
			return ppuStatus;
		}
		return 0;
	}
	
	public void cycle() {
		updateCycleAndScanLine();
		checkForVBlankAndNMI();
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
