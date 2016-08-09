package com.bibler.awesome.bibnes.systems;


import static java.awt.image.BufferedImage.TYPE_INT_BGR;
import static java.util.Arrays.fill;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;

import com.bibler.awesome.bibnes.communications.Notifiable;
import com.bibler.awesome.bibnes.communications.Notifier;
import com.bibler.awesome.bibnes.mappers.Mapper;
import com.bibler.awesome.bibnes.utils.NESPalette;


public class PPU implements Notifier {
	
	/*private final int CYCLES_PER_LINE = 340;
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
	
	private int emphBits;
	
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
	}*/
	
	public void registerObjectToNotify(Notifiable notifiable) {
		if(!objectsToNotify.contains(notifiable)) {
			objectsToNotify.add(notifiable);
		}
	}
	
	public void unregisterAll() {
		objectsToNotify.clear();
	}
	
	/*public void reset() {
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
		emphBits = (data & 0xe0) << 1;
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
							spriteRange = (scanline) - (spriteData);		// check range Should be scanline + 1 ?
							if(spriteRange >= 0 && spriteRange <=					// If in range 
									((ppuCtrl >> 5 & 1) == 1 ? 15 : 7)) {
								spriteMode = 1;		
								if(spriteFoundCount >= 8) {
									ppuStatus |= 1 << 5;							// Set sprite overflow
								}
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
				} else if(cycle == 257) {
					for(int i = spriteFoundCount; i < 8; i++) {
						lowSpriteShift[i] = 0;
						highSpriteShift[i] = 0;
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
		if((ppuMask >> 4 & 1) == 1 && scanline < 240 && cycle < 256) {
			for(int i = 0; i < 8; i++) {
				if(cycle >= spriteXLatch[i] && cycle < (spriteXLatch[i] + 8)) {
					if(!((ppuMask >> 2 & 1) == 1 && cycle < 8)) {
						renderSprite(i);
					}
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
	
	/*private void renderSprite(int index) {
		int shift = cycle - spriteXLatch[index];
		if((spriteAttr[index] >> 6 & 1) == 0) {
			shift = 7 - shift; 
		} 
		
		int pixel = (lowSpriteShift[index] >> (shift)) & 1;
		pixel |= ((highSpriteShift[index] >> (shift)) & 1) << 1;
		pixel |= (spriteAttr[index] & 3) << 2;
		final int bufferIndex = (scanline * 256) + (cycle - 1);
		final int palVal = nes.ppuRead(0x3F10 + pixel);
		final int spritePixel = NESPalette.getPixel( (((ppuMask & 1) == 1) ? palVal & 0x30 : palVal) & 0x3F | emphBits);
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
        val = (val & 0x3F) | emphBits;
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
		
	}*/
	
	 public Mapper mapper;
	    private int oamaddr, oamstart, readbuffer = 0;
	    private int loopyV = 0x0;//ppu memory pointer
	    private int loopyT = 0x0;//temp pointer
	    private int loopyX = 0;//fine x scroll
	    public int scanline = 0;
	    public int cycles = 0;
	    private int framecount = 0;
	    private int div = 2;
	    private final int[] OAM = new int[256], secOAM = new int[32],
	            spriteshiftregH = new int[8],
	            spriteshiftregL = new int[8], spriteXlatch = new int[8],
	            spritepals = new int[8], bitmap = new int[240 * 256];
	    private int found, bgShiftRegH, bgShiftRegL, bgAttrShiftRegH, bgAttrShiftRegL;
	    private final boolean[] spritebgflags = new boolean[8];
	    private boolean even = true, bgpattern = true, sprpattern, spritesize, nmicontrol,
	            grayscale, bgClip, spriteClip, bgOn, spritesOn,
	            vblankflag, sprite0hit, spriteoverflow;
	    private int emph;
	    public final int[] pal;
	    private int vraminc = 1;
	    private BufferedImage nametableView;
	    private final int[] bgcolors = new int[256];
	    private int openbus = 0; //the last value written to the PPU
	    private int nextattr;
	    private int linelowbits;
	    private int linehighbits;
	    private int penultimateattr;
	    private int numscanlines;
	    private int vblankline;
	    private int[] cpudivider;
	    private ArrayList<Notifiable> objectsToNotify = new ArrayList<Notifiable>();
	    private CPU cpu;
	    
	    private NES nes;

	    public PPU(NES nes) {
	        this.pal = new int[]{0x09, 0x01, 0x00, 0x01, 0x00, 0x02, 0x02, 0x0D,
	            0x08, 0x10, 0x08, 0x24, 0x00, 0x00, 0x04, 0x2C, 0x09, 0x01, 0x34,
	            0x03, 0x00, 0x04, 0x00, 0x14, 0x08, 0x3A, 0x00, 0x02, 0x00, 0x20,
	            0x2C, 0x08};
	        /*
	     power-up pallette checked by Blargg's power_up_palette test. Different
	     revs of NES PPU might give different initial results but there's a test
	     expecting this set of values and nesemu1, BizHawk, RockNES, MyNes use it
	         */
	        this.nes = nes;
	        cpu = nes.getCPU();
	        fill(OAM, 0xff);
	        setParameters();
	    }

	    final void setParameters() {
	       numscanlines = 262;
	       vblankline = 241;
	       cpudivider = new int[]{3, 3, 3, 3, 3};
	              
	    }

	    public void runFrame() {
	        for (int line = 0; line < numscanlines; ++line) {
	            clockLine(line);
	        }
	        notify("FRAME");
	    }

	    /**
	     * Performs a read from a PPU register, as well as causes any side effects
	     * of reading that specific register.
	     *
	     * @param regnum register to read (address with 0x2000 already subtracted)
	     * @return the data in the PPU register, or open bus (the last value written
	     * to a PPU register) if the register is read only
	     */
	    public final int read(int address) {
	    	final int regnum = address % 8;
	        switch (regnum) {
	            case 2:
	            	
	                even = true;
	                if (scanline == 241) {
	                    if (cycles == 1) {//suppress NMI flag if it was just turned on this same cycle
	                        vblankflag = false;
	                    }
	                    //OK, uncommenting this makes blargg's NMI suppression test
	                    //work but breaks Antarctic Adventure.
	                    //I'm going to need a cycle accurate CPU to fix that...
//	                    if (cycles < 4) {
//	                        //show vblank flag but cancel pending NMI before the CPU
//	                        //can actually do anything with it
//	                        //TODO: use proper interface for this
//	                        mapper.cpu.nmiNext = false;
//	                    }
	                }
	                openbus = (vblankflag ? 0x80 : 0)
	                        | (sprite0hit ? 0x40 : 0)
	                        | (spriteoverflow ? 0x20 : 0)
	                        | (openbus & 0x1f);
	                vblankflag = false;
	                break;
	            case 4:
	                // reading this is NOT reliable but some games do it anyways
	                openbus = OAM[oamaddr];
	                //System.err.println("codemasters?");
	                if (renderingOn() && (scanline <= 240)) {
	                    if (cycles < 64) {
	                        return 0xFF;
	                    } else if (cycles <= 256) {
	                        return 0x00;
	                    } //Micro Machines relies on this:
	                    else if (cycles < 320) {
	                        return 0xFF;
	                    } //and this:
	                    else {
	                        return secOAM[0]; //is this the right value @ the time?
	                    }
	                }
	                break;
	            case 7:
	                // PPUDATA
	                // correct behavior. read is delayed by one
	                // -unless- is a read from sprite pallettes
	                final int temp;
	                if ((loopyV & 0x3fff) < 0x3f00) {
	                    temp = readbuffer;
	                    readbuffer = nes.ppuRead(loopyV & 0x3fff);
	                } else {
	                    readbuffer = nes.ppuRead((loopyV & 0x3fff) - 0x1000);
	                    temp = nes.ppuRead(loopyV);
	                }
	                if (!renderingOn() || (scanline > 240 && scanline < (numscanlines - 1))) {
	                    loopyV += vraminc;
	                } else {
	                    //if 2007 is read during rendering PPU increments both horiz
	                    //and vert counters erroneously.
	                    incLoopyVHoriz();
	                    incLoopyVVert();
	                }
	                openbus = temp;
	                break;

	            // and don't increment on read
	            default:
	                return openbus; // last value written to ppu
	        }
	        return openbus;
	    }

	    /**
	     * Performs a write to a PPU register
	     *
	     * @param regnum register number from 0 to 7, memory addresses are decoded
	     * to these elsewhere
	     * @param data the value to write to the register (0x00 to 0xff valid)
	     */
	    public final void write(int address, final int data) {
	    	final int regnum = address % 8;
//	        if (regnum != 4 /*&& regnum != 7*/) {
//	            System.err.println("PPU write - wrote " + utils.hex(data) + " to reg "
//	                    + utils.hex(regnum + 0x2000)
//	                    + " frame " + framecount + " scanline " + scanline);
//	        }
	        //debugdraw();
	        openbus = data;
	        switch (regnum) {
	            case 0: //PPUCONTROL (2000)
	                //set 2 bits of vram address (nametable select)
	                //bits 0 and 1 affect loopyT to change nametable start by 0x400
	                loopyT &= ~0xc00;
	                loopyT |= (data & 3) << 10;
	                /*
	                 SMB1 writes here at the end of its main loop and if this write
	                 lands on one exact PPU clock, the address bits are set to 0.
	                 This only happens on one CPU/PPU alignment of real hardware 
	                 though so it only shows up ~33% of the time.
	                 */
	                vraminc = (((data >> 2 & 1) != 0) ? 32 : 1);
	                sprpattern = ((data >> 3 & 1) != 0);
	                bgpattern = ((data >> 4 & 1) != 0);
	                spritesize = ((data >> 5 & 1) != 0);
	                /*bit 6 is kind of a halt and catch fire situation since it outputs
	                 ppu color data on the EXT pins that are tied to ground if set
	                 and that'll make the PPU get very hot from sourcing the current. 
	                 Only really useful for the NESRGB interposer board, kind of
	                 useless for emulators. I will ignore it.
	                 */
	                nmicontrol = ((data >> 7 & 1) != 0);

	                break;
	            case 1: //PPUMASK (2001)
	                grayscale = ((data & 1) != 0);
	                bgClip = !((data >> 1 & 1) != 0); //clip left 8 pixels when its on
	                spriteClip = !((data >> 2 & 1) != 0);
	                bgOn = ((data >> 3 & 1) != 0);
	                spritesOn = ((data >> 4 & 1) != 0);
	                emph = (data & 0xe0) << 1;
	                if (numscanlines == 312) {
	                    //if PAL switch position of red and green emphasis bits (6 and 5)
	                    //red is bit 6 -> bit 7
	                    //green is bit 7 -> bit 6
	                    int red = (emph >> 6) & 1;
	                    int green = (emph >> 7) & 1;
	                    emph &= 0xf3f;
	                    emph |= (red << 7) | (green << 6);
	                }
	                break;
	            case 3:
	                // PPUOAMADDR (2003)
	                // most games just write zero and use the dma
	                oamaddr = data & 0xff;
	                break;
	            case 4:
	                // PPUOAMDATA(2004)
	                if ((oamaddr & 3) == 2) {
	                    OAM[oamaddr++] = (data & 0xE3);
	                } else {
	                    OAM[oamaddr++] = data;
	                }
	                oamaddr &= 0xff;
	                // games don't usually write this directly anyway, it's unreliable
	                break;

	            // PPUSCROLL(2005)
	            case 5:
	                if (even) {
	                    // update horizontal scroll
	                    loopyT &= ~0x1f;
	                    loopyX = data & 7;
	                    loopyT |= data >> 3;

	                    even = false;
	                } else {
	                    // update vertical scroll
	                    loopyT &= ~0x7000;
	                    loopyT |= ((data & 7) << 12);
	                    loopyT &= ~0x3e0;
	                    loopyT |= (data & 0xf8) << 2;
	                    even = true;

	                }
	                break;

	            case 6:
	                // PPUADDR (2006)
	                if (even) {
	                    // high byte
	                    loopyT &= 0xc0ff;
	                    loopyT |= ((data & 0x3f) << 8);
	                    loopyT &= 0x3fff;
	                    even = false;
	                } else {
	                    loopyT &= 0xfff00;
	                    loopyT |= data;
	                    loopyV = loopyT;
	                    even = true;
	                }
	                break;
	            case 7:
	                // PPUDATA             
	                nes.ppuWrite((loopyV & 0x3fff), data);
	                if (!renderingOn() || (scanline > 240 && scanline < (numscanlines - 1))) {
	                    loopyV += vraminc;
	                } else if ((loopyV & 0x7000) == 0x7000) {
	                    int YScroll = loopyV & 0x3E0;
	                    loopyV &= 0xFFF;
	                    switch (YScroll) {
	                        case 0x3A0:
	                            loopyV ^= 0xBA0;
	                            break;
	                        case 0x3E0:
	                            loopyV ^= 0x3E0;
	                            break;
	                        default:
	                            loopyV += 0x20;
	                            break;
	                    }
	                } else {
	                    // while rendering, it seems to drop by 1 line, regardless of increment mode
	                    loopyV += 0x1000;
	                }
	                break;
	            default:
	                break;
	        }
	    }

	    /**
	     * PPU is on if either background or sprites are enabled
	     *
	     * @return true
	     */
	    public boolean renderingOn() {
	        return bgOn || spritesOn;
	    }

	    /**
	     * MMC3 scan line counter isn't clocked if background and sprites are using
	     * the same half of the pattern table
	     *
	     * @return true if PPU is rendering and BG and sprites are using different
	     * pattern tables
	     */
	    public final boolean mmc3CounterClocking() {
	        return (bgpattern != sprpattern) && renderingOn();
	    }

	    /**
	     * Runs the PPU emulation for one NES scan line.
	     */
	    public final void clockLine(int scanline) {
	        //skip a PPU clock on line 0 of odd frames when rendering is on
	        //and we are in NTSC mode (pal has no skip)
	        int skip = (numscanlines == 262
	                && scanline == 0
	                && renderingOn()
	                && !((framecount >> 1 & 1) != 0)) ? 1 : 0;
	        for (cycles = skip; cycles < 341; ++cycles) {
	            clock();
	        }
	    }

	    private int tileAddr = 0;
	    private int cpudividerctr = 0;

	    /**
	     * runs the emulation for one PPU clock cycle.
	     */
	    public final void clock() {

	        //cycle based ppu stuff will go here
	        if (cycles == 1) {
	            if (scanline == 0) {
	                dotcrawl = renderingOn();
	            }
	            if (scanline < 240) {
	                bgcolors[scanline] = pal[0];
	            }
	        }
	        if (scanline < 240 || scanline == (numscanlines - 1)) {
	            //on all rendering lines
	            if (renderingOn()
	                    && ((cycles >= 1 && cycles <= 256)
	                    || (cycles >= 321 && cycles <= 336))) {
	                //fetch background tiles, load shift registers
	                bgFetch();
	            } else if (cycles == 257 && renderingOn()) {
	                //x scroll reset
	                //horizontal bits of loopyV = loopyT
	                loopyV &= ~0x41f;
	                loopyV |= loopyT & 0x41f;

	            } else if (cycles > 257 && cycles <= 341) {
	                //clear the oam address from pxls 257-341 continuously
	                oamaddr = 0;
	            }
	            if ((cycles == 340) && renderingOn()) {
	                //read the same nametable byte twice
	                //this signals the MMC5 to increment the scanline counter
	                fetchNTByte();
	                fetchNTByte();
	            }
	            if (cycles == 65 && renderingOn()) {
	                oamstart = oamaddr;
	            }
	            if (cycles == 260 && renderingOn()) {
	                //evaluate sprites for NEXT scanline (as long as either background or sprites are enabled)
	                //this does in fact happen on scanline 261 but it doesn't do anything useful
	                //it's cycle 260 because that's when the first important sprite byte is read
	                //actually sprite overflow should be set by sprite eval somewhat before
	                //so this needs to be split into 2 parts, the eval and the data fetches
	                evalSprites();
	            }
	            if (scanline == (numscanlines - 1)) {
	                if (cycles == 0) {// turn off vblank, sprite 0, sprite overflow flags
	                    vblankflag = false;
	                    sprite0hit = false;
	                    spriteoverflow = false;
	                } else if (cycles >= 280 && cycles <= 304 && renderingOn()) {
	                    //loopyV = (all of)loopyT for each of these cycles
	                    loopyV = loopyT;
	                }
	            }
	        } else if (scanline == vblankline && cycles == 1) {
	            //handle vblank on / off
	            vblankflag = true;
	        }
	        if (!renderingOn() || (scanline > 240 && scanline < (numscanlines - 1))) {
	            //HACK ALERT
	            //handle the case of MMC3 mapper watching A12 toggle
	            //even when read or write aren't asserted on the bus
	            //needed to pass Blargg's mmc3 tests
	        }
	        if (scanline < 240 && cycles >= 1 && cycles <= 256) {
	            int bufferoffset = (scanline << 8) + (cycles - 1);
	            //bg drawing
	            if (bgOn) { //if background is on, draw a line of that
	                final boolean isBG = drawBGPixel(bufferoffset);
	                //sprite drawing
	                drawSprites(scanline << 8, cycles - 1, isBG);

	            } else if (spritesOn) {
	                //just the sprites then
	                int bgcolor = ((loopyV > 0x3f00 && loopyV < 0x3fff) ? nes.ppuRead(loopyV) : pal[0]);
	                bitmap[bufferoffset] = bgcolor;
	                drawSprites(scanline << 8, cycles - 1, true);
	            } else {
	                //rendering is off, so draw either the background color OR
	                //if the PPU address points to the palette, draw that color instead.
	                int bgcolor = ((loopyV > 0x3f00 && loopyV < 0x3fff) ? nes.ppuRead(loopyV) : pal[0]);
	                bitmap[bufferoffset] = bgcolor;
	            }
	            //deal with the grayscale flag
	            if (grayscale) {
	                bitmap[bufferoffset] &= 0x30;
	            }
	            //handle color emphasis
	            bitmap[bufferoffset] = (bitmap[bufferoffset] & 0x3f) | emph;

	        }
	        //handle nmi
	        if (vblankflag && nmicontrol) {
	            //pull NMI line on when conditions are right
	            nes.NMI(true);
	        } else {
	            nes.NMI(false);
	        }

	        //clock CPU, once every 3 ppu cycles
	        div = (div + 1) % cpudivider[cpudividerctr];
	        if (div == 0) {
	            cpu.cycle();
	            cpudividerctr = (cpudividerctr + 1) % cpudivider.length;
	        }
	        if (cycles == 257) {
	            
	        } else if (cycles == 340) {
	            scanline = (scanline + 1) % numscanlines;
	            if (scanline == 0) {
	                ++framecount;
	            }
	        }
	    }

	    private void bgFetch() {
	        //fetch tiles for background
	        //on real PPU this logic is repurposed for sprite fetches as well
	        //System.err.println(hex(loopyV));
	        bgAttrShiftRegH |= ((nextattr >> 1) & 1);
	        bgAttrShiftRegL |= (nextattr & 1);
	        //background fetches
	        switch ((cycles - 1) & 7) {
	            case 1:
	                fetchNTByte();
	                break;
	            case 3:
	                //fetch attribute (FIX MATH)
	                penultimateattr = getAttribute(((loopyV & 0xc00) + 0x23c0),
	                        (loopyV) & 0x1f,
	                        (((loopyV) & 0x3e0) >> 5));
	                break;
	            case 5:
	                //fetch low bg byte
	                linelowbits = nes.ppuRead((tileAddr)
	                        + ((loopyV & 0x7000) >> 12));
	                break;
	            case 7:
	                //fetch high bg byte
	                linehighbits = nes.ppuRead((tileAddr) + 8
	                        + ((loopyV & 0x7000) >> 12));
	                bgShiftRegL |= linelowbits;
	                bgShiftRegH |= linehighbits;
	                nextattr = penultimateattr;
	                if (cycles != 256) {
	                    incLoopyVHoriz();
	                } else {
	                    incLoopyVVert();
	                }
	                break;
	            default:
	                break;
	        }
	        if (cycles >= 321 && cycles <= 336) {
	            bgShiftClock();
	        }
	    }

	    private void incLoopyVVert() {
	        //increment loopy_v to next row of tiles
	        if ((loopyV & 0x7000) == 0x7000) {
	            //reset the fine scroll bits and increment tile address to next row
	            loopyV &= ~0x7000;
	            int y = (loopyV & 0x03E0) >> 5;
	            if (y == 29) {
	                //if row is 29 zero fine scroll and bump to next nametable
	                y = 0;
	                loopyV ^= 0x0800;
	            } else {
	                //increment (wrap to 5 bits) but if row is already over 29
	                //we don't bump loopyV to next nt.
	                y = (y + 1) & 31;
	            }
	            loopyV = (loopyV & ~0x03E0) | (y << 5);
	        } else {
	            //increment the fine scroll
	            loopyV += 0x1000;
	        }
	    }

	    private void incLoopyVHoriz() {
	        //increment horizontal part of loopyv
	        if ((loopyV & 0x001F) == 31) // if coarse X == 31
	        {
	            loopyV &= ~0x001F; // coarse X = 0
	            loopyV ^= 0x0400;// switch horizontal nametable
	        } else {
	            loopyV += 1;// increment coarse X
	        }
	    }

	    private void fetchNTByte() {
	        //fetch nt byte
	        tileAddr = nes.ppuRead(
	                ((loopyV & 0xc00) | 0x2000) + (loopyV & 0x3ff)) * 16
	                + (bgpattern ? 0x1000 : 0);
	    }

	    private boolean drawBGPixel(int bufferoffset) {
	        //background drawing
	        //loopyX picks bits
	        final boolean isBG;
	        if (bgClip && (bufferoffset & 0xff) < 8) {
	            //left hand of screen clipping
	            //(needs to be marked as BG and not cause a sprite hit)
	            bitmap[bufferoffset] = pal[0];
	            isBG = true;
	        } else {
	            final int bgPix = (((bgShiftRegH >> -loopyX + 16) & 1) << 1)
	                    + ((bgShiftRegL >> -loopyX + 16) & 1);
	            final int bgPal = (((bgAttrShiftRegH >> -loopyX + 8) & 1) << 1)
	                    + ((bgAttrShiftRegL >> -loopyX + 8) & 1);
	            isBG = (bgPix == 0);
	            bitmap[bufferoffset] = isBG ? pal[0] : pal[(bgPal << 2) + bgPix];
	        }
	        bgShiftClock();
	        return isBG;
	    }

	    private void bgShiftClock() {
	        bgShiftRegH <<= 1;
	        bgShiftRegL <<= 1;
	        bgAttrShiftRegH <<= 1;
	        bgAttrShiftRegL <<= 1;
	    }

	    boolean dotcrawl = true;
	    private boolean sprite0here = false;

	    /**
	     * evaluates PPU sprites for the NEXT scanline
	     */
	    private void evalSprites() {
	        sprite0here = false;
	        int ypos, offset;
	        found = 0;
	        Arrays.fill(secOAM, 0xff);
	        //primary evaluation
	        //need to emulate behavior when OAM address is set to nonzero here
	        for (int spritestart = oamstart; spritestart < 255; spritestart += 4) {
	            //for each sprite, first we cull the non-visible ones
	            ypos = OAM[spritestart];
	            offset = scanline - ypos;
	            if (ypos > scanline || offset > (spritesize ? 15 : 7)) {
	                //sprite is out of range vertically
	                continue;
	            }
	            //if we're here it's a valid renderable sprite
	            if (spritestart == 0) {
	                sprite0here = true;
	            }
	            //actually which sprite is flagged for sprite 0 depends on the starting
	            //oam address which is, on the real thing, not necessarily zero.
	            if (found >= 8) {
	                //if more than 8 sprites, set overflow bit and STOP looking
	                //todo: add "no sprite limit" option back
	                spriteoverflow = true;
	                break; //also the real PPU does strange stuff on sprite overflow
	                //todo: emulate register trashing that happens when overflow
	            } else {
	                //set up ye sprite for rendering
	                secOAM[found * 4] = OAM[spritestart];
//	                secOAM[found * 4 + 1] = OAM[spritestart + 1];
//	                secOAM[found * 4 + 2] = OAM[spritestart + 2];
//	                secOAM[found * 4 + 3] = OAM[spritestart + 3];
	                final int oamextra = OAM[spritestart + 2];

	                //bg flag
	                spritebgflags[found] = ((oamextra >> 5 & 1) != 0);
	                //x value
	                spriteXlatch[found] = OAM[spritestart + 3];
	                spritepals[found] = ((oamextra & 3) + 4) * 4;
	                if (((oamextra >> 7 & 1) != 0)) {
	                    //if sprite is flipped vertically, reverse the offset
	                    offset = (spritesize ? 15 : 7) - offset;
	                }
	                //now correction for the fact that 8x16 tiles are 2 separate tiles
	                if (offset > 7) {
	                    offset += 8;
	                }
	                //get tile address (8x16 sprites can use both pattern tbl pages but only the even tiles)
	                final int tilenum = OAM[spritestart + 1];
	                spriteFetch(spritesize, tilenum, offset, oamextra);
	                ++found;
	            }
	        }
	        for (int i = found; i < 8; ++i) {
	            //fill unused sprite registers with zeros
	            spriteshiftregL[found] = 0;
	            spriteshiftregH[found] = 0;
	            //also, we need to do 8 reads no matter how many sprites we found
	            //dummy reads are to sprite 0xff
	            spriteFetch(spritesize, 0xff, 0, 0);
	        }
	    }

	    private void spriteFetch(final boolean spritesize, final int tilenum, int offset, final int oamextra) {
	        int tilefetched;
	        if (spritesize) {
	            tilefetched = ((tilenum & 1) * 0x1000)
	                    + (tilenum & 0xfe) * 16;
	        } else {
	            tilefetched = tilenum * 16
	                    + ((sprpattern) ? 0x1000 : 0);
	        }
	        tilefetched += offset;
	        //now load up the shift registers for said sprite
	        final boolean hflip = ((oamextra >> 6 & 1) != 0);
	        if (!hflip) {
	            spriteshiftregL[found] = reverseByte(nes.ppuRead(tilefetched));
	            spriteshiftregH[found] = reverseByte(nes.ppuRead(tilefetched + 8));
	        } else {
	            spriteshiftregL[found] = nes.ppuRead(tilefetched);
	            spriteshiftregH[found] = nes.ppuRead(tilefetched + 8);
	        }
	    }

	    /**
	     * draws appropriate pixel of the sprites selected by sprite evaluation
	     */
	    private void drawSprites(int bufferoffset, int x, boolean bgflag) {
	        final int startdraw = !spriteClip ? 0 : 8;//sprite left 8 pixels clip
	        int sprpxl = 0;
	        int index = 7;
	        //per pixel in de line that could have a sprite
	        for (int y = found - 1; y >= 0; --y) {
	            int off = x - spriteXlatch[y];
	            if (off >= 0 && off <= 8) {
	                if ((spriteshiftregH[y] & 1) + (spriteshiftregL[y] & 1) != 0) {
	                    index = y;
	                    sprpxl = 2 * (spriteshiftregH[y] & 1) + (spriteshiftregL[y] & 1);
	                }
	                spriteshiftregH[y] >>= 1;
	                spriteshiftregL[y] >>= 1;
	            }
	        }
	        if (sprpxl == 0 || x < startdraw || !spritesOn) {
	            //no opaque sprite pixel here
	            return;
	        }

	        if (sprite0here && (index == 0) && !bgflag
	                && x < 255) {
	            //sprite 0 hit!
	            sprite0hit = true;
	        }
	        //now, FINALLY, drawing.
	        if (!spritebgflags[index] || bgflag) {
	            bitmap[bufferoffset + x] = pal[spritepals[index] + sprpxl];
	        }
	    }

	    /**
	     * Read the appropriate color attribute byte for the current tile. this is
	     * fetched 2x as often as it really needs to be, the MMC5 takes advantage of
	     * that for ExGrafix mode.
	     *
	     * @param ntstart //start of the current attribute table
	     * @param tileX //x position of tile (0-31)
	     * @param tileY //y position of tile (0-29)
	     * @return attribute table value (0-3)
	     */
	    private int getAttribute(final int ntstart, final int tileX, final int tileY) {
	        final int base = nes.ppuRead(ntstart + (tileX >> 2) + 8 * (tileY >> 2));
	        if (((tileY >> 1 & 1) != 0)) {
	            if (((tileX >> 1 & 1) != 0)) {
	                return (base >> 6) & 3;
	            } else {
	                return (base >> 4) & 3;
	            }
	        } else if (((tileX >> 1 & 1) != 0)) {
	            return (base >> 2) & 3;
	        } else {
	            return base & 3;
	        }
	    }

	    @Override
		public void notify(String messageToSend) {
			for(Notifiable notifiable : objectsToNotify) {
				if(notifiable != null) {
					notifiable.takeNotice(messageToSend, this);
				}
			}
			
		}
	    
	    public int[] getFrameForPainting() {
	    	return bitmap;
	    }
	    
	    private int reverseByte(int nibble) {
	        //reverses 8 bits packed into int.
	        return (Integer.reverse(nibble) >> 24) & 0xff;
	    }
	
	
}
