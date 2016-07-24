package com.bibler.awesome.bibnes.systems;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import com.bibler.awesome.bibnes.communications.Notifiable;
import com.bibler.awesome.bibnes.communications.Notifier;
import com.bibler.awesome.bibnes.utils.NESPalette;

public class PPU implements Notifier {
	
	private final int CYCLES_PER_LINE = 340;
	private final int LINES_PER_FRAME = 261;
	private final int REGISTER_ADDRESS_WIDTH = 0x07;
	
	private final int X_HIGHLIGHT = 0;
	private final int Y_HIGHLIGHT = 2;
	
	private final int[] OAM = new int[256];
	private final int[] spriteTempMem = new int[32];
	private final int[] lowSpriteShift = new int[8];
	private final int[] highSpriteShift = new int[8];
	private final int[] spriteAttr = new int[8];
	private final int[] spriteXLatch = new int[8];
	private final boolean[] spritebgflags = new boolean[8];
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
	private int oamstart;
	private int oamData;
	private int ppuScroll;
	private int ppuAddr;
	private int ppuData;
	private int oamDMA;
	private int bgColorIndex;
	
	private int spriteRange;
	private int spriteRangeCount;
	
	private int v;
	private int t;
	private int x;
	private int w;
	
	private int ntByte;
	private int atByte;
	private int nextAtByte;
	private int lowBGByte;
	private int highBGByte;
	private int bgShiftOne;
	private int bgShiftTwo;
	private int paletteShiftOne;
	private int paletteShiftTwo;
	private int paletteLatchOne;
	private int paletteLatchTwo;
	
	private boolean NMIFlag;
	private boolean spriteSize;
	private boolean spriteOverflow;
	private boolean sprite0Found;
	private boolean even = true, bgpattern = true, sprpattern, nmicontrol,
	            grayscale, bgClip, spriteClip, bgOn, spritesOn,
	            vblankflag, sprite0hit;
	
	
	private int vInc;
	private int bgTileLocation;
	
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
		if((data >> 7 & 1) == 1) {
			NMIFlag = true;
		} else {
			NMIFlag = false;
		}
	}
	
	private void writePPUMask(int data) {
		ppuMask = data;
	}
	
	private void writePPUStatus(int data) {}
	
	private void writeOAMAddr(int data) {
		oamAddr = data;
	}
	
	private void writeOAMData(int data) {
		OAM[oamAddr] = data;
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
		notify("NT");
	}
	
	private int readPPUStatus() {
		w = 0;
		int ret = ppuStatus;
		clearVBlankFlag();
		return ret;
	}
	
	private int readOAMData() {
		
		//ToDo should make sure this is correct.
		
		return OAM[oamAddr];
	}
	private int readPPUData() {
		int ret = 0;
		ret = nes.ppuBusRead(v);
		incrementV();
		
		return ret;
	}
	
	private void incrementV() {
		if(!rendering()) {
			v += vInc;
		} else {
			incHorizV();
			incVertV();
		}
	}
	
	public void cycle() {
		//rendering = (ppuMask >> 3 & 3) > 0;
		renderPixel();
		evaluateSprites();
		renderSprites();
		updateCycleAndScanLine();
	}
	
	private long lastFrame;
	
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
			System.out.println("frame time: " + (System.currentTimeMillis() - lastFrame));
			lastFrame = System.currentTimeMillis();
		}
	}
	
	private void renderPixel() {	
		 //cycle based ppu stuff will go here
        if (scanline < 240 || scanline == (LINES_PER_FRAME - 1)) {
            //on all rendering lines
            if (rendering()
                    && ((cycle >= 1 && cycle <= 256)
                    || (cycle >= 321 && cycle <= 336))) {
                //fetch background tiles, load shift registers
                processVisibleScanlinePixel();
            } else if (cycle == 257 && rendering()) {
                //x scroll reset
                //horizontal bits of loopyV = loopyT
                v &= ~0x41f;
                v |= t & 0x41f;

            } 
            if ((cycle == 340) && rendering()) {
                //read the same nametable byte twice
                //this signals the MMC5 to increment the scanline counter
                fetchNTByte();
                fetchNTByte();
            }
            if (scanline == (LINES_PER_FRAME - 1)) {
                if (cycle == 0) {// turn off vblank, sprite 0, sprite overflow flags
                    clearVBlankFlag();
                    clearSprite0HitFlag();
                    clearSpriteOverflowFlag();
                } else if (cycle >= 280 && cycle <= 304 && rendering()) {
                    //loopyV = (all of)loopyT for each of these cycles
                    v = t;
                }
            }
        } else if (scanline == 241 && cycle == 1) {
            //handle vblank on / off
            setVBlankFlag();
        }
        if (!rendering() || (scanline > 240 && scanline < (LINES_PER_FRAME - 1))) {
            //HACK ALERT
            //handle the case of MMC3 mapper watching A12 toggle
            //even when read or write aren't asserted on the bus
            //needed to pass Blargg's mmc3 tests
            //mapper.checkA12(loopyV & 0x3fff);
        }
        if (scanline < 240) {
            if (cycle >= 1 && cycle <= 256) {
            	if(rendering()) {
            		renderPixelToScreen();
            	}
            }
        }
        if (((ppuStatus >> 7 & 1) == 1) && NMIFlag) {
            //pull NMI line on when conditions are right
            nes.NMI(true);
        } else {
           nes.NMI(false);
        }
		
	}
	
	private void processVisibleScanlinePixel() {
		paletteShiftOne |= ((atByte >> 1) & 1);
        paletteShiftTwo |= (atByte & 1);
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
				atByte = nextAtByte;
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
		//int atAddress = 0x23C0 | (v & 0xC00) | ((v >> 4) & 0x38) | ((v >> 2) & 0x07);
		int atAddress = calculateAtAddress();
		nextAtByte = nes.ppuBusRead(atAddress);
		
	}
	
	private int calculateAtAddress() {
		final int x = v & 0b00011111;
		final int y = (v >> 5) & 0b00011111;
        final int row = (v >> 12) & 0b00000111;

        final int attributeX = x / 4;
        final int attributeY = ((y * 8) + row) / 32;
        return 32*30 + (attributeY * (256 / 32)) + attributeX + 0x2000;
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
	
	private int spriteTempIndex;
	private int spriteMode;
	private int spriteData;
	private int spriteIndex;
	private int spriteEvalIndex;
	private int spriteFoundCount;
	private int spriteShiftIndex;
	
	private void evaluateSprites() {
		if(rendering()) {
			if(scanline < 240) {
				if(cycle == 0) {
					spriteTempIndex = 0;
					spriteMode = 0;
					spriteIndex = 0;
					spriteEvalIndex = 0;
					spriteFoundCount = 0;
					spriteShiftIndex = 0;
					sprite0Found = false;
				}
				if(cycle <= 64) {
					if(cycle % 2 == 0 && spriteTempIndex < spriteTempMem.length) {											// write on even cycles
						spriteTempMem[spriteTempIndex++] = 0xFF;
					}
				} else if(cycle < 257 && spriteIndex < 64) {
					if(cycle % 2 == 1) {											// read on odd cycles
						spriteData = OAM[(spriteIndex * 4) + spriteEvalIndex];
					} else if(spriteFoundCount < 8) {
						if(spriteMode == 0) {										// If in range evaluation mode
							spriteRange = (scanline + 1) - (spriteData + 1);		// check range
							if(spriteRange >= 0 && spriteRange <=					// If in range 
									((ppuCtrl >> 5 & 1) == 1 ? 15 : 7)) {
								spriteMode = 1;		
								if(spriteIndex == 0) {
									sprite0Found = true;
								}
								try {// Set to sprite data copy mode
									spriteTempMem[(spriteFoundCount * 4) + spriteEvalIndex++] = spriteRange;
								} catch(ArrayIndexOutOfBoundsException e ) {
									System.out.println("Out of bounds:\n    spriteIndex: " + spriteIndex + "\n    spriteEvalIndex: " + spriteEvalIndex);
								}
							} else {
								spriteIndex++;										// If not in range, go to next sprite
							}
						} else if(spriteMode == 1) {								// Sprite data copy mode
							spriteTempMem[(spriteFoundCount * 4) + spriteEvalIndex++] = spriteData;
							if(spriteEvalIndex >= 4) {								// if all data for this sprite is copied, go to next sprite
								spriteIndex++;
								spriteMode = 0;
								spriteEvalIndex = 0;
								spriteFoundCount++;
							}
						}
					}
				} else if(cycle < 321) {
					if(spriteShiftIndex < 8) {
						
						int address = 0x1000 * (ppuCtrl >> 3) & 1;
						int tileNum = spriteTempMem[(spriteShiftIndex * 4) + 1];
						int range = spriteTempMem[spriteShiftIndex * 4];
						spriteAttr[spriteShiftIndex] = spriteTempMem[(spriteShiftIndex * 4) + 2];
						spriteXLatch[spriteShiftIndex] = spriteTempMem[(spriteShiftIndex * 4) + 3];
						//if(cycle % 8 == 5) {								// low tile byte
							
							lowSpriteShift[spriteShiftIndex] = nes.ppuBusRead(address + (tileNum * 16) + range);
						//} else if(cycle % 8 == 7) {							// high tile byte
							highSpriteShift[spriteShiftIndex++] = nes.ppuBusRead(address + (tileNum * 16) + 8 + range);
							
						//}
					}
				}
			}
		}
	}
	
	private void renderSprites() {
		if(rendering() && scanline < 240 && cycle < 256) {
			for(int i = 0; i < 8; i++) {
				if(cycle >= spriteXLatch[i] && cycle < (spriteXLatch[i] + 8)) {
					renderSprite(i);
				}
			}
		}
	}
	
	/*
	 * Priority Decision Table
	 * BG Pixel | Sprite Pixel | Priority | Output
	 * 0			0				X		BG ($3F00)
	 * 0			1-3				X		Sprite
	 * 1-3			0				X		BG
	 * 1-3			1-3				0		Sprite
	 * 1-3			1-3				1		BG
	 */
	
	private void renderSprite(int index) {
		int shift = cycle - spriteXLatch[index];
		if((spriteAttr[index] >> 6 & 1) == 0) {
			shift = 7 - shift; 
		} 
		
		int pixel = (lowSpriteShift[index] >> (shift)) & 1;
		pixel |= ((highSpriteShift[index] >> (shift)) & 1) << 1;
		pixel |= (spriteAttr[index] & 3) << 2;
		final int bufferIndex = (scanline * 256) + cycle;
		final int spritePixel = NESPalette.getPixel(nes.ppuBusRead(0x3F10 + pixel));
		final int pixelIndex = pixel & 0x03;
		
		final int priority = spriteAttr[index] >> 5 & 1;
		if(bgColorIndex == 0) {							// If BG Pixel = 0
			if((pixelIndex) != 0) {																// If Sprite Pixel is not 0, sprite pixel wins
				frameArray[bufferIndex] = spritePixel;
			}
		} else {																				// If BG Pixel is not 0
			if((pixelIndex) != 0) {																// If Sprite Pixel is not 0, sprite pixel wins
				if(sprite0Found && index == 0) {
					setSprite0HitFlag();
				}
				if(priority == 0) {
					frameArray[bufferIndex] = spritePixel;
				}
			}
		}
	}
	
	private void renderPixelToScreen() {
		int offset = (scanline * 256) + cycle;
		int pixel = 0;
		int fineX = x;
		int x = (v & 0x1F) - 2;
		int y = ((v >> 5) & 0x1F);
		int shift = (x & 2) | ((y & 2) << 1);
		int pal = (atByte & (3 << shift)) >> shift;
		pixel = pal << 2; 
		pixel |= ( (((bgShiftOne & 0xFF00) >> 8) >> (7 - fineX) & 1) << 1) | ( (((bgShiftTwo & 0xFF00) >> 8) >> (7 - fineX) & 1));
		pixel += 0x3F00;
		bgColorIndex = pixel & 0x03;
		//int val  = nes.ppuBusRead(pixel);
		int val = nes.ppuBusRead(0x3F00);
		if(offset < frameArray.length && pixel > 0) {
			frameArray[offset] = NESPalette.getPixel(val);
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
	
	public int[] getCurrentNameTable() {
		return createFrame();
	}
	
	private void nextFrame() {
		notify("FRAME");
		frameCount++;
	}
	
	private int[] createFrame() {
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
		int[] frame = new int[256 * 240];
		for(int i = 0; i < frame.length; i++) {
			x = i % 256;
			y = (i / 256);
			row = y / 8;
			col = x / 8;
			ntByte = nes.ppuBusRead(0x2000 + (row * 32) + col);
			curAttr = nes.ppuBusRead(0x23C0 + (((y / 32) * 8) + (x / 32))) & 0xFF;
			row = (ntByte / 16);
			col = ntByte % 16;
			fineY = (y % 8);
			address = (1  << 0xC) | (row << 8) | (col << 4) | fineY & 7; 
			lowBg = nes.ppuBusRead(address);
			address = (1 << 0xC) | (row << 8) | (col << 4) | (1 << 3) | fineY & 7;
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
			frame[i] = NESPalette.getPixel(nes.ppuBusRead(0x3F00 + palVal + pixel));
		}
		return frame;
	}
	
	public boolean rendering() {
		return (ppuMask >> 3 & 1) == 1 || (ppuMask >> 4 & 1) == 1;
	}
	
	private void setSprite0HitFlag() {
		ppuStatus |= (1 << 6);
	}
	
	private void clearSprite0HitFlag() {
		ppuStatus &= ~(1 << 6);
	}
	
	private void setSpriteOverflowFlag() {
		ppuStatus |= (1 << 5);
	}
	
	private void clearSpriteOverflowFlag() {
		ppuStatus &= ~(1 << 5);
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
