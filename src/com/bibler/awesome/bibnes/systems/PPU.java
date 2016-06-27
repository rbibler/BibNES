package com.bibler.awesome.bibnes.systems;

import java.util.ArrayList;

import com.bibler.awesome.bibnes.communications.Notifiable;
import com.bibler.awesome.bibnes.communications.Notifier;
import com.bibler.awesome.bibnes.utils.NESPalette;

public class PPU implements Notifier {
	
	private final int CYCLES_PER_LINE = 340;
	private final int LINES_PER_FRAME = 261;
	private final int REGISTER_ADDRESS_WIDTH = 0x07;
	
	private Memory oamMem = new Memory(0x100);
	private Memory palette = new Memory(0x20);
	
	private int[] frameArray = new int[256 * 240];
	
	private int cycle;
	private int scanline;
	private int frameCount;
	
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
	
	private int ntByte;
	private int atByte;
	private int lowBGByte;
	private int highBGByte;
	private int bgShiftOne;
	private int bgShiftTwo;
	private int paletteShiftOne;
	private int paletteShiftTwo;
	
	
	private int vInc;
	private int bgTileLocation;
	
	private boolean rendering;
	
	private ArrayList<Notifiable> objectsToNotify = new ArrayList<Notifiable>();
		
	private NES nes;
	
	public PPU(NES nes) {
		this.nes = nes;
	}
	
	public void registerObjectToNotify(Notifiable notifiable) {
		if(!objectsToNotify.contains(notifiable)) {
			objectsToNotify.add(notifiable);
		}
	}
	
	public void reset() {
		//ppuStatus = 0x80;
	}
	
	public void writePalette(int addressToWrite, int data) {
		palette.write(addressToWrite % 0x3F00, data);
	}
	
	public int readPalette(int addressToRead) {
		return 0;
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
		int ret = 0;
		switch (addressToRead % 0x2000) {
		case 0:
			break;
		case 1:
			break;
		case 2:
			ret = readPPUStatus();
			break;
		case 4:
			ret = readOAMData();
		case 7:
			ret = readPPUData();
		}
		return ret;
	}
	
	private void writePPUCtrl(int data) {
		t = (t & ~3) | ((data & 3) << 10);
		ppuCtrl = data;
		vInc = (ppuCtrl >> 2 & 1) == 0 ? 1 : 32;
		bgTileLocation = ppuCtrl >> 4 & 1;
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
	
	private void writePPUData(int data) {
		nes.ppuBusWrite(v, data);
		incrementV();
	}
	
	private int readPPUStatus() {
		w = 0;
		int ret = ppuStatus;
		clearVBlankFlag();
		return ret;
	}
	
	private int readOAMData() {
		
		//ToDo should make sure this is correct.
		
		return oamMem.read(oamAddr);
	}
	private int readPPUData() {
		int ret = 0;
		ret = nes.ppuBusRead(v);
		incrementV();
		
		return ret;
	}
	
	private void incrementV() {
		if(!rendering) {
			v += vInc;
		} else {
			incHorizV();
			incVertV();
		}
	}
	
	public void cycle() {
		//rendering = scanline < 240 && (ppuMask >> 3 & 3) > 0;
		rendering = (ppuMask >> 3 & 3) > 0;
		//renderPixel();
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
		} else if(scanline == 261 && cycle == 340) {
			scanline = 0;
			cycle = 0;
			nextFrame();
		}
	}
	
	private void renderPixel() {
		if(scanline <= 239) {
			if(cycle < 257 || cycle > 320) {
				processVisibleScanlinePixel();
			} else if(cycle == 257) {
				equalizeHorizV();
			}
		} else if(scanline == 261) {
			if(cycle < 257 || cycle > 320) {
				processVisibleScanlinePixel();
			} else if(cycle == 257) {
				equalizeHorizV();
			} else if(cycle > 279 && cycle < 305) {
				//equalizeVertV();
				v = t;
			}
		} 
		if(rendering) {
			renderPixelToScreen();
		}
	}
	
	private void processVisibleScanlinePixel() {
		int cycleMod = cycle % 8; 
		switch(cycleMod) {
		case 2:
			fetchNTByte();
			break;
		case 4:
			fetchATByte();
			break;
		case 6:
			fetchLowBGByte();
			break;
		case 0:
			if(cycle != 0) {
				fetchHighBGByte();
				if(cycle == 256) {
					incVertV();
				} else {
					incHorizV();
				}
				loadLatches();
			} 
			break;
		}
	}
	
	public void fetchNTByte() {
		ntByte = 0x2000 | (v & 0xFFF);
		ntByte = nes.ppuBusRead(ntByte);
		System.out.println("Fetch: " + "V: " + Integer.toHexString(v) + " NTBYTE: " + Integer.toHexString(ntByte));
	}
	
	public void fetchATByte() {
		atByte = 0x23C0 | (v & 0xC00) | ((v >> 4) & 0x38) | ((v >> 2) & 0x07);
		atByte = nes.ppuBusRead(atByte);
	}
	
	public void fetchLowBGByte() {
		int col = ntByte % 16;
		int row = ntByte / 16;
		int address = (bgTileLocation << 0xC) | (row << 8) | (col << 4) | (v >> 12) & 7; 
		lowBGByte = nes.ppuBusRead(address);
	}
	
	public void fetchHighBGByte() {
		int col = ntByte % 16;
		int row = ntByte / 16;
		int address = (bgTileLocation << 0xC) | (row << 8) | (col << 4) | (1 << 3) | (v >> 12) & 7; 
		highBGByte = nes.ppuBusRead(address);
	}
	
	private void incHorizV() {
		if ((v & 0x001F) == 31) { // if coarse X == 31
			  v &= ~0x001F;          // coarse X = 0
			  v ^= 0x0400;           // switch horizontal nametable
		} else {
			  v += 1;                // increment coarse X
		}
	}
	
	private void incVertV() {
		if ((v & 0x7000) != 0x7000) {        	// if fine Y < 7
			  v += 0x1000;                      // increment fine Y
		} else {
			  v &= ~0x7000;                     // fine Y = 0
			  int y = (v & 0x03E0) >> 5;        // let y = coarse Y
			  if (y == 29) {
			    y = 0;                          // coarse Y = 0
			    v ^= 0x0800;                    // switch vertical nametable
			  } else if (y == 31) {
			    y = 0;                          // coarse Y = 0, nametable not switched
			  } else {
			    y += 1;							// increment coarse Y
			  }	
			  v = (v & ~0x03E0) | (y << 5);     // put coarse Y back into v
		}
	}
	
	private void equalizeHorizV() {
		v = (v & ~0x41F) | (t & 0x41F);
	}
	
	private void equalizeVertV() {
		v = (v & ~ 0b111101111100000) | (t & 0b111101111100000);
	}
	
	private void loadLatches() {
		bgShiftOne = (bgShiftOne & ~0xFF00) | (highBGByte << 8 & 0xFF00);
		bgShiftTwo = (bgShiftTwo & ~0xFF00) | (lowBGByte << 8 & 0xFF00);
	}
	
	private void renderPixelToScreen() {
		int offset = (scanline * 256) + cycle;
		int pixel = 0;
		int fineX = 0;
		pixel |= ((bgShiftOne >> fineX & 1) << 1) | ((bgShiftTwo >> fineX & 1)) | (3 << 2);
		if(offset < frameArray.length) {
			frameArray[offset] = NESPalette.getPixel(pixel);
		}
		shift();
	}
	
	private void shift() {
		bgShiftOne >>= 1;
		bgShiftTwo >>= 1;
		paletteShiftOne >>= 1;
		paletteShiftTwo >>= 1;
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
	
	public int[] getFrameForPainting() {
		return frameArray;
	}
	
	private void nextFrame() {
		if(rendering) {
			createFrame();
		}
		notify("FRAME");
		frameCount++;
	}
	
	private void createFrame() {
		int pixel;
		int row;
		int col;
		int address;
		int lowBg;
		int highBg;
		int ntByte;
		int atByte;
		int fineY;
		for(int i = 0; i < frameArray.length; i++) {
			row = (i / 256) / 8;
			col = i % 256 / 8;
			ntByte = nes.ppuBusRead(0x2000 + (row * 32) + col);
			row = (ntByte / 16);
			col = ntByte % 16;
			fineY = ((i / 256) % 8);
			address = (bgTileLocation  << 0xC) | (row << 8) | (col << 4) | fineY & 7; 
			lowBg = nes.ppuBusRead(address);
			address = (bgTileLocation << 0xC) | (row << 8) | (col << 4) | (1 << 3) | fineY & 7;
			highBg = nes.ppuBusRead(address);
			row = (i / 256) / 32;
			col = (i % 256) / 32;
			atByte = nes.ppuBusRead(0x23C0 + (row * 8) + col);
			System.out.println("ATBYTE: " + Integer.toBinaryString(atByte));
			pixel = (atByte & 3) << 2 | ((highBg >> (7 - (i % 8)) & 1) << 1 | (lowBg >> (7 -(i % 8)) & 1));
			frameArray[i] = NESPalette.getPixel(pixel);
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

	@Override
	public void notify(String messageToSend) {
		for(Notifiable notifiable : objectsToNotify) {
			notifiable.takeNotice(messageToSend, this);
		}
		
	}

}
