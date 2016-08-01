package com.bibler.awesome.bibnes.systems;

import java.util.ArrayList;
import com.bibler.awesome.bibnes.communications.Notifiable;
import com.bibler.awesome.bibnes.communications.Notifier;
import com.bibler.awesome.bibnes.utils.NESPalette;

public class PPU implements Notifier {
	
	private final int CYCLES_PER_LINE = 340;
	private final int LINES_PER_FRAME = 261;
	
	private final int[] OAM = new int[256];
	private final int[] spriteTempMem = new int[32];
	private final int[] lowSpriteShift = new int[8];
	private final int[] highSpriteShift = new int[8];
	private final int[] spriteAttr = new int[8];
	private final int[] spriteXLatch = new int[8];
	private Memory palette = new Memory(0x20);
	
	private int[] frameArray = new int[256 * 240];
	
	private int cycle;
	private int scanline;
	//Registers.
	private int ppuCtrl;
	private int ppuMask;
	private int ppuStatus;
	private int oamAddr;
	private int bgColorIndex;
	
	private int spriteRange;
	
	private int v;
	private int t;
	private int fineX;
	private int w;
	private int ppuData;
	private int ppuBuffer;
	
	private int ntByte;
	private int atByte;
	private int nextAtByte;
	private int penultimateattr;
	private int lowBGByte;
	private int highBGByte;
	private int bgShiftOne;
	private int bgShiftTwo;
	
	private int frameCount;
	
	private boolean NMIFlag;
	private boolean sprite0Found;

	
	private int vInc;
	private int bgTileLocation;
	public int currentXScroll;
	
	private boolean updateXScrollLine = true;
	private boolean oddFrame;
	
	private ArrayList<Notifiable> objectsToNotify = new ArrayList<Notifiable>();
	
	private int[] powerOnPalette = {
			0x09, 0x01, 0x00, 0x01, 0x00, 0x02, 0x02, 0x0D, 
			0x08, 0x10, 0x08, 0x24, 0x00, 0x00, 0x04, 0x2C, 
			0x09, 0x01, 0x34, 0x03, 0x00, 0x04, 0x00, 0x14, 
			0x08, 0x3A, 0x00, 0x02, 0x00, 0x20, 0x2C, 0x08
		};
		
	private NES nes;
	
	public PPU(NES nes) {
		this.nes = nes;
	}
	
	public void registerObjectToNotify(Notifiable notifiable) {
		if(!objectsToNotify.contains(notifiable)) {
			objectsToNotify.add(notifiable);
		}
	}
	
	public void unregisterAll() {
		objectsToNotify.clear();
	}
	
	public void reset() {
		writePowerOnPalette();
		oddFrame = false;
	}
	
	private void writePowerOnPalette() {
		for(int i = 0; i < powerOnPalette.length; i++) {
			nes.ppuWrite(0x3F00 + i, powerOnPalette[i]);
		}
	}
	
	
	public void write(int addressToWrite, int data) {
		ppuData = data;
		switch(addressToWrite % 8) {
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
		switch (addressToRead % 0x2000) {
			case 2:
				ppuData = readPPUStatus();
				break;
			case 4:
				ppuData = readOAMData();
			case 7:
				final int temp;
                if ((v & 0x3fff) < 0x3f00) {
                    temp = ppuBuffer;
                    ppuBuffer = nes.ppuRead(v & 0x3fff);
                } else {
                    ppuBuffer = nes.ppuRead((v & 0x3fff) - 0x1000);
                    temp = nes.ppuRead(v);
                }
                if (!rendering() || (scanline > 240 && scanline < (LINES_PER_FRAME - 1))) {
                    v += vInc;
                } else {
                    //if 2007 is read during rendering PPU increments both horiz
                    //and vert counters erroneously.
                    incHorizV();
                    incVertV();
                }
                ppuData = temp;
			default:
				return ppuData;
		}
		return ppuData;
	}
	
	private void writePPUCtrl(int data) {
		t = (t & ~0xC00) | ((data & 3) << 10);
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
		 if ((oamAddr & 3) == 2) {
             OAM[oamAddr++] = (data & 0xE3);
         } else {
             OAM[oamAddr++] = data;
         }
         oamAddr &= 0xff;
         // games don't usually write this directly anyway, it's unreliable
         
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
			fineX = data & 0x7;
			w = 1;
		}
		
	}
	
	private void writePPUAddr(int data) {
		if(w == 1) {
			t = (t & ~0xFF) | ((data & 0xFF));
			v = t;
			w = 0;
		} else {
			t = (t & ~0xFF00) | ((data & 0x3F) << 8);
			w = 1;
		}
		
	}
	
	private void writePPUData(int data) {
		nes.ppuWrite(v, data);
		if (!rendering() || (scanline > 240 && scanline < (LINES_PER_FRAME - 1))) {
            v += vInc;
        } else {
            // while rendering, it seems to drop by 1 scanline, regardless of increment mode
            if ((v & 0x7000) == 0x7000) {
                int YScroll = v & 0x3E0;
                v &= 0xFFF;
                if (YScroll == 0x3A0) {
                    v ^= 0xBA0;
                } else if (YScroll == 0x3E0) {
                    v ^= 0x3E0;
                } else {
                    v += 0x20;
                }
            } else {
                v += 0x1000;
            }
        }
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
	
	
	private void incrementV() {
		if(!rendering()) {
			v += vInc;
		} else {
			incHorizV();
			incVertV();
		}
	}
	
	public void cycle() {
		renderPixel();
		evaluateSprites();
		renderSprites();
		updateCycleAndScanLine();
	}
	
	private long lastFrame;
	private int bgAttrShiftRegH;
	private int bgAttrShiftRegL;
	
	private void updateCycleAndScanLine() {
		cycle++;
		if(cycle > CYCLES_PER_LINE) {
			cycle = 0;
			scanline++;
			if(scanline > LINES_PER_FRAME) {
				scanline = 0;
			}
		} else if(scanline == 261) {
			if(oddFrame) {
				if(cycle == 339) {
					nextFrame();
				}
			} else if(cycle == 340) {
				nextFrame();
			}
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
                if(updateXScrollLine) {
                	currentXScroll = ((v & 0x1F) * 8) + fineX;
                	currentXScroll += ((v >> 10 & 1) == 1) ? 256 : 0;
                	updateXScrollLine = false;
                }

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
            	if((ppuMask >> 3 & 1) == 1) {												//if BG on
            		if(rendering()) {
            			renderPixelToScreen();
            		}
            	} else {							
            		final int bgPixel = ( (v > 0x3F00 && v < 0x3FFF) ? nes.ppuRead(v) : nes.ppuRead(0x3F00));
            		frameArray[(scanline * 256) + (cycle - 1)] = NESPalette.getPixel(bgPixel);
            		bgColorIndex = 0;
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
		int cycleMod = cycle % 8; 
		bgAttrShiftRegH |= ((atByte >> 1) & 1);
        bgAttrShiftRegL |= (atByte & 1);
		switch(cycleMod) {
		case 2:
			fetchNTByte();
			break;
		case 4:
			 penultimateattr = getAttribute(((v & 0xc00) + 0x23c0),
                     (v) & 0x1f,
                     (((v) & 0x3e0) >> 5));
			break;
		case 6:
			fetchLowBGByte();
			break;
		case 0:
			
			if(cycle != 0) {
				fetchHighBGByte();
				atByte = penultimateattr;
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
		
		ntByte = nes.ppuRead(ntByte);
	}
	
	public int fetchATByte() {
		int atAddress = 0x23C0 | (v & 0xC00) | ((v >> 4) & 0x38) | ((v >> 2) & 0x7);
		return nes.ppuRead(atAddress);
		
	}
	
	public void fetchLowBGByte() {
		int col = ntByte % 16;
		int row = ntByte / 16;
		int fineY = ((v & 0x7000) >> 12) & 7;
		int address = (bgTileLocation << 0xC) | (row << 8) | (col << 4) | fineY; 
		lowBGByte = nes.ppuRead(address);
	}
	
	public void fetchHighBGByte() {
		int col = ntByte % 16;
		int row = ntByte / 16;
		int fineY = ((v & 0x7000) >> 12) & 7;
		int address = (bgTileLocation << 0xC) | (row << 8) | (col << 4) | (1 << 3) | fineY;
		highBGByte = nes.ppuRead(address);
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
							spriteRange = (scanline ) - (spriteData);		// check range
							if(spriteRange >= 0 && spriteRange <=					// If in range 
									((ppuCtrl >> 5 & 1) == 1 ? 15 : 7)) {
								spriteMode = 1;		
								if(spriteIndex == 0) {
									sprite0Found = true;
								}
								spriteTempMem[(spriteFoundCount * 4) + spriteEvalIndex++] = spriteRange;
								
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
						final boolean big = (ppuCtrl >> 5 & 1) == 1;
						if((spriteAttr[spriteShiftIndex] >> 7 & 1) == 1) {
							range = (big ? 15 : 7) - range;
							if(big) {
								if(range > 7) {
									range += 8;
								}
							}
						}
						if(big) {
							address = ((tileNum & 1) * 0x1000) + (tileNum & 0xfe) * 16;
						} else {
							address += (tileNum * 16);
						}
						address += range;
						
						lowSpriteShift[spriteShiftIndex] = nes.ppuRead(address );
						highSpriteShift[spriteShiftIndex++] = nes.ppuRead(address + 8);
						
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
		final int bufferIndex = (scanline * 256) + (cycle - 1);
		final int palVal = nes.ppuRead(0x3F10 + pixel);
		final int spritePixel = NESPalette.getPixel(((ppuMask & 1) == 1) ? palVal & 0x30 : palVal);
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
		int offset = (scanline * 256) + (cycle - 1)
				;
		int pixel = 0;
		//int x = (v & 0x1F) + (7 - fineX) + 8;
		//int y = ((v >> 5) & 0x1F);
		int x = cycle + (7 - fineX);
		int y = scanline;
		int shift = (x & 2) | ((y & 2) << 1);
		int pal = (atByte & (3 << shift)) >> shift;
		pixel = pal << 2; 
		pixel |= ( (((bgShiftOne & 0xFF00) >> 8) >> (7 - fineX) & 1) << 1) | ( (((bgShiftTwo & 0xFF00) >> 8) >> (7 - fineX) & 1));
		pixel += 0x3F00;
		bgColorIndex = pixel & 0x03;
		
		final int bgPix = (((bgShiftOne >> -fineX + 16) & 1) << 1)
                + ((bgShiftTwo >> -fineX + 16) & 1);
        final int bgPal = (((bgAttrShiftRegH >> -fineX + 8) & 1) << 1)
                + ((bgAttrShiftRegL >> -fineX + 8) & 1);
		
		
		
		//int val  = nes.ppuRead(pixel);
        int val = nes.ppuRead(0x3F00 + (bgPal << 2 | bgPix));
        if((ppuMask & 1) == 1) {
        	val &= 0x30;
        }
        if((ppuMask >> 1 & 1) == 0 && cycle < 8) {
        	frameArray[offset] = NESPalette.getPixel(nes.ppuRead(0x3F00));
        } else if(offset < frameArray.length && pixel > 0) {
			frameArray[offset] = NESPalette.getPixel(val);
		}
		shift();
	}
	
	private void shift() {
		bgShiftOne <<= 1;
		bgShiftTwo <<= 1;
		bgAttrShiftRegH <<= 1;
	    bgAttrShiftRegL <<= 1;
		
	}
	
	public int getT() {
		return t;
	}
	
	public int getV() {
		return v;
	}
	
	public int getX() {
		return fineX;
	}
	
	public int[] getFrameForPainting() {
		return frameArray;
	}
	
	private void nextFrame() {
		scanline = 0;
		cycle = 0;
		updateXScrollLine = true;
		oddFrame = !oddFrame;
		notify("FRAME");
		frameCount++;
		nes.frame();
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
	
	private void clearSpriteOverflowFlag() {
		ppuStatus &= ~(1 << 5);
	}
	
	private void setVBlankFlag() {
		ppuStatus |= (1 << 7);
	}
	
	private void clearVBlankFlag() {
		ppuStatus &= ~(1 << 7);
	}
	
	private int getAttribute(final int ntstart, final int tileX, final int tileY) {
        final int base = nes.ppuRead(ntstart + (tileX >> 2) + 8 * (tileY >> 2));
        if (((tileY >> 1 & 1) != 0)) {
            if (((tileX >> 1 & 1) != 0)) {
                return (base >> 6) & 3;
            } else {
                return (base >> 4) & 3;
            }
        } else {
            if (((tileX >> 1 & 1) != 0)) {
                return (base >> 2) & 3;
            } else {
                return base & 3;
            }
        }
    }
	
	public int[] getOamMem() {
		return OAM;
	}

	@Override
	public void notify(String messageToSend) {
		for(Notifiable notifiable : objectsToNotify) {
			if(notifiable != null) {
				notifiable.takeNotice(messageToSend, this);
			}
		}
		
	}
	
}
