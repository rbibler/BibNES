package com.bibler.awesome.bibnes.systems;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
	private boolean bgOn;
	private boolean spritesOn;
	
	private ArrayList<Notifiable> objectsToNotify = new ArrayList<Notifiable>();
	private BufferedWriter writer;
		
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
		return palette.read(addressToRead % 0x3F00);
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
		bgOn = ((data >> 3 & 1) != 0);
        spritesOn = ((data >> 4 & 1) != 0);
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
		// Update vertical scroll
		if(w == 1) {
			t = (t & ~0b111001111100000) 
					| ((data & 7) << 12) 
					| ((data & 0x38) << 2) 
					| ((data & 0xC0) << 2);
			
			w = 0;
		// Update horizontal scroll
		} else {
			t = (t & ~0x1F) | ((data >> 3) & 0x1F);
			x = data & 0x7;
			w = 1;
		}
	}
	
	private void writePPUAddr(int data) {
		if(w == 1) {
			ppuAddr |= (data & 0xFF);
			t = (t & ~0xFF) | ((data & 0xFF));
			v = t;
			log("V = T\n" + "   V: " + v + "\n" + "   T: " + t);
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
			log("Increment v Not Rendering:\n" + "  v = " + v + "\n   Scanline: " + scanline + "\n   Cycle: " + cycle);
		} else {
			incHorizV();
			incVertV();
		}
	}
	
	public void cycle() {
		rendering = scanline < 240 && (ppuMask >> 3 & 3) > 0;
		//rendering = (ppuMask >> 3 & 3) > 0;
		renderPixel();
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
		 //cycle based ppu stuff will go here
        if (scanline < 240 || scanline == (LINES_PER_FRAME - 1)) {
            //on all rendering lines
            if (renderingOn()
                    && ((cycle >= 1 && cycle <= 256)
                    || (cycle >= 321 && cycle <= 336))) {
                //fetch background tiles, load shift registers
                processVisibleScanlinePixel();
            } else if (cycle == 257 && renderingOn()) {
                //x scroll reset
                //horizontal bits of loopyV = loopyT
                v &= ~0x41f;
                v |= t & 0x41f;

            } else if (cycle > 257 && cycle <= 341) {
                //clear the oam address from pxls 257-341 continuously
                oamAddr = 0;
            }
            if ((cycle == 340) && renderingOn()) {
                //read the same nametable byte twice
                //this signals the MMC5 to increment the scanline counter
                fetchNTByte();
                fetchNTByte();
            }
            if (cycle == 65 && renderingOn()) {
                //oamstart = oamaddr;
            }
            if (cycle == 260 && renderingOn()) {
                //evaluate sprites for NEXT scanline (as long as either background or sprites are enabled)
                //this does in fact happen on scanine 261 but it doesn't do anything useful
                //it's cycle 260 because that's when the first important sprite byte is read
                //actually sprite overflow should be set by sprite eval somewhat before
                //so this needs to be split into 2 parts, the eval and the data fetches
                //evalSprites();
            }
            if (scanline == (LINES_PER_FRAME - 1)) {
                if (cycle == 0) {// turn off vblank, sprite 0, sprite overflow flags
                    //vblankflag = false;
                    //sprite0hit = false;
                    //spriteoverflow = false;
                } else if (cycle >= 280 && cycle <= 304 && renderingOn()) {
                    //loopyV = (all of)loopyT for each of these cycles
                    v = t;
                }
            }
        } else if (scanline == 241 && cycle == 1) {
            //handle vblank on / off
           // vblankflag = true;
        }
        if (!renderingOn() || (scanline > 240 && scanline < (LINES_PER_FRAME - 1))) {
            //HACK ALERT
            //handle the case of MMC3 mapper watching A12 toggle
            //even when read or write aren't asserted on the bus
            //needed to pass Blargg's mmc3 tests
            //mapper.checkA12(loopyV & 0x3fff);
        }
        if (scanline < 240) {
            if (cycle >= 1 && cycle <= 256) {
                renderPixelToScreen();
            }
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
		if (cycle >= 321 && cycle <= 336) {
            shift();
        }
	}
	
	public void fetchNTByte() {
		ntByte = 0x2000 | (v & 0xFFF);
		ntByte = nes.ppuBusRead(ntByte);
		log("Fetch:\n" + "   V: " + Integer.toHexString(v) + "\n" +"   NTBYTE: " + Integer.toHexString(ntByte)
				+ "\n" + "   Scanline: " + scanline + "\n" + "   Cycle: " + cycle);
	}
	
	public void fetchATByte() {
		atByte = 0x23C0 | (v & 0xC00) | ((v >> 4) & 0x38) | ((v >> 2) & 0x07);
		atByte = nes.ppuBusRead(atByte);
	}
	
	public void fetchLowBGByte() {
		int col = ntByte % 16;
		int row = ntByte / 16;
		int fineY = ((v & 0x7000) >> 12) & 7;
		int address = (bgTileLocation << 0xC) | (row << 8) | (col << 4) | fineY; 
		lowBGByte = nes.ppuBusRead(address);
	}
	
	public void fetchHighBGByte() {
		int col = ntByte % 16;
		int row = ntByte / 16;
		int fineY = ((v & 0x7000) >> 12) & 7;
		int address = (bgTileLocation << 0xC) | (row << 8) | (col << 4) | (1 << 3) | fineY;
		highBGByte = nes.ppuBusRead(address);
	}
	
	private void incHorizV() {
		if ((v & 0x001F) == 31) { // if coarse X == 31
			  v &= ~0x001F;          // coarse X = 0
			  v ^= 0x0400;           // switch horizontal nametable
			  log("incHorizV mod 31:\n" + "  v = " + v + "\n   Scanline: " + scanline + "\n   Cycle: " + cycle);
		} else {
			  v += 1;                // increment coarse X
			  log("incHoirzV standard:\n" + "  v = " + v + "\n   Scanline: " + scanline + "\n   Cycle: " + cycle);
		}
	}
	
	private void incVertV() {
		if ((v & 0x7000) != 0x7000) {        	// if fine Y < 7
			  v += 0x1000;                      // increment fine Y
			  log("incVertV Fine Y < 7:\n" + "  v = " + v + "\n   Scanline: " + scanline + "\n   Cycle: " + cycle);
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
			  log("incVertV Fine Y > 7:\n" + "  v = " + v + "\n   Scanline: " + scanline + "\n   Cycle: " + cycle);
		}
	}
	
	private void equalizeHorizV() {
		v = (v & ~0x41F) | (t & 0x41F);
		log("Equalize horiz v:\n" + "  v = " + v + "\n   Scanline: " + scanline + "\n   Cycle: " + cycle);
	}
	
	private void equalizeVertV() {
		v = (v & ~ 0b111101111100000) | (t & 0b111101111100000);
		log("Equalize Vert v:\n" + "  v = " + v + "\n   Scanline: " + scanline + "\n   Cycle: " + cycle);
	}
	
	private void loadLatches() {
		bgShiftOne = (bgShiftOne & ~0xFF) | (highBGByte & 0xFF);
		bgShiftTwo = (bgShiftTwo & ~0xFF) | (lowBGByte & 0xFF);
	}
	
	private void renderPixelToScreen() {
		//int offset = (scanline * 256) + cycle;
		int offset = (scanline << 8) + (cycle);
		int pixel = 0;
		int fineX = x;
		pixel |= ( (((bgShiftOne & 0xFF00) >> 8) >> (7 - fineX) & 1) << 1) | ( (((bgShiftTwo & 0xFF00) >> 8) >> (7 - fineX) & 1)) | (3 << 2);
		if(offset < frameArray.length) {
			frameArray[offset] = NESPalette.getPixel(nes.ppuBusRead(0x3F00 + pixel));
		}
		shift();
	}
	
	private void shift() {
		bgShiftOne <<= 1;
		bgShiftTwo <<= 1;
		paletteShiftOne <<= 1;
		paletteShiftTwo <<= 1;
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
			//createFrame();
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
		int fineY;
		int x;
		int y;
		int attrX;
		int attrY;
		int curAttr;
		for(int i = 0; i < frameArray.length; i++) {
			x = i % 256;
			y = (i / 256);
			row = y / 8;
			col = x / 8;
			ntByte = nes.ppuBusRead(0x2000 + (row * 32) + col);
			curAttr = nes.ppuBusRead(0x23C0 + (((y / 32) * 8) + (x / 32))) & 0xFF;
			row = (ntByte / 16);
			col = ntByte % 16;
			fineY = (y % 8);
			address = (bgTileLocation  << 0xC) | (row << 8) | (col << 4) | fineY & 7; 
			lowBg = nes.ppuBusRead(address);
			address = (bgTileLocation << 0xC) | (row << 8) | (col << 4) | (1 << 3) | fineY & 7;
			highBg = nes.ppuBusRead(address);
			int attrStart = (((y / 32) * 32) * 256) + (((x / 32) * 32));
			attrX = (x / 32) * 4;
			attrY = (y / 32) * 4;
			int ntX = x / 8;
			int ntY = y / 8;
			attrStart = i - attrStart;
			int attrBitShift = (((ntX - attrX) / 2) * 2) + (((ntY - attrY) / 2) * 4);
			int palVal = ((curAttr >> attrBitShift) & 3) << 2;
			pixel = ((highBg >> (7 - (i % 8)) & 1) << 1 | (lowBg >> (7 -(i % 8)) & 1));
			frameArray[i] = NESPalette.getPixel(nes.ppuBusRead(0x3F00 + palVal + pixel));
		}
	}
	
	public boolean renderingOn() {
		return bgOn || spritesOn;
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

	private void log(String s) {
		return; /*
		if(writer == null) {
			setupLogWriter();
		}
		try {
			writer.write(s + "\n");
		} catch(IOException e) {}
		*/
	}
	
	private void setupLogWriter() {
		try {
			writer = new BufferedWriter(new FileWriter(new File("C:/users/ryan/documents/repos/BibNes/NESFiles/background/log.txt")));
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}
