package com.bibler.awesome.bibnes.systems;

import java.util.ArrayList;
import com.bibler.awesome.bibnes.communications.Notifiable;
import com.bibler.awesome.bibnes.communications.Notifier;
import com.bibler.awesome.bibnes.utils.NESPalette;

public class PPU implements Notifier {
	
	
	// Flags
	private boolean NMIEnable;
	private boolean spriteHeight;
	private boolean bgTileSelect;
	private boolean spriteTileSelect;
	private boolean spritesEnabled;
	private boolean bgEnabled;
	private boolean spriteClip;
	private boolean bgClip;
	private boolean grayScale;
	private boolean vBlankFlag;
	private boolean sprite0Hit;
	private boolean spriteOverflow;
	private boolean oddFrame;
	
	private boolean showBG = true;
	private boolean showObjects = true;
	
	// Integer register values
	private int vRamInc;
	private int colorEmph;
	private int dataBus;
	
	//Address values
	private int vRamAddress;
	private int tempVRamAddress;
	private int fineX;
	private int writeToggle;
	private int readLatch;
	
	
	//OAM variables and arrays
	private int OAMAddress;
	private int[] OAMData = new int[256];
	private int[] tempOAMData = new int[32];;
	
	//Rendering variables
	private int scanline;
	private int cycle;
	private int frame;
	private int[] frameArray = new int[256 * 240];
	private int bgShiftHigh;
	private int bgShiftLow;
	private int bgLatchHigh;
	private int bgLatchLow;
	private int attrShiftHigh;
	private int attrShiftLow;
	private int attrLatchHigh;
	private int attrLatchLow;
	private int nametableByte;
	private int attributeByte;
	private int nextAttrByte;
	private int bgColorIndex;
	
	//Sprite registers and stuff
	private int[] spriteXPositions = new int[8];
	private int[] spriteAttributes = new int[8];
	private int[] spriteHighBytes = new int[8];
	private int[] spriteLowBytes = new int[8];
	private int[] spriteIndices = new int[8];
	
	// PPU Parameters
	//private int linesPerFrame;
	//private int visibleScanlines;

	
	//Power on Palette Values
	private int[] powerOnPalette = {
			0x09, 0x01, 0x00, 0x01, 0x00, 0x02, 0x02, 0x0D, 
			0x08, 0x10, 0x08, 0x24, 0x00, 0x00, 0x04, 0x2C, 
			0x09, 0x01, 0x34, 0x03, 0x00, 0x04, 0x00, 0x14, 
			0x08, 0x3A, 0x00, 0x02, 0x00, 0x20, 0x2C, 0x08	
	};
	
	//Other Systems
	private NES nes;
	
	//Communications
	private ArrayList<Notifiable> objectsToNotify = new ArrayList<Notifiable>();
	
	public PPU(NES nes) {
		this.nes = nes;
	}
	
	public void reset() {
		writePowerOnPalette();
		//oddFrame = false;
	}
	
	private void writePowerOnPalette() {
		for(int i = 0; i < powerOnPalette.length; i++) {
			nes.ppuWrite(0x3F00 + i, powerOnPalette[i]);
		}
	}
	
	public void cycle() {
		
		clock();
		spriteEvaluation();
		if(showObjects) {
			renderSprites();
		}
		cycle++;
		if(cycle == 339 && oddFrame && scanline == 261) {
			cycle = 0;
			frame++;
			scanline = 0;
			//nes.frame();
			oddFrame = !oddFrame;
			//notify("FRAME");
			return;
		} else if(cycle > 340) {
			cycle = 0;
			scanline++;
			if(scanline > 261) {
				frame++;
				scanline = 0;
				//nes.frame();
				//notify("FRAME");
			}
		}
	}
	
	public void writeProgramRegister(int registerToWrite, int data) {
		dataBus = data;
		switch(registerToWrite) {
		case 0:
			//set register flags;
			NMIEnable = (data >> 7 & 1) == 1;
			spriteHeight = (data >> 5 & 1) == 1;
			bgTileSelect = (data >> 4 & 1) == 1;
			spriteTileSelect = (data >> 3 & 1) == 1;
			vRamInc = (data >> 2 & 1) == 0 ? 1 : 32;
			tempVRamAddress = (tempVRamAddress & ~0xC00) | ((data & 3) << 10);
			break;
		case 1:
			//set register flags
			colorEmph = (data >> 4) & 7;
			spritesEnabled = (data >> 4 & 1) == 1;
			bgEnabled = (data >> 3 & 1) == 1;
			spriteClip = (data >> 2 & 1) == 0;
			bgClip = (data >> 1 & 1) == 0;
			grayScale = (data & 1) == 1;
			break;
		case 3:
			OAMAddress = data;
			break;
		case 4: 
			if(!renderingAndVisible()) {
				OAMData[(OAMAddress & 0xFF)] = data;
				OAMAddress++;
			} else {
				// Bump only the high 6 bits
			}
			break;
		case 5:
			if(writeToggle == 0) {
				fineX = data & 7;
				tempVRamAddress = (tempVRamAddress & ~(0x1F)) | (data >> 3) & 0x1F;
				writeToggle = 1;
			} else {
				tempVRamAddress = (tempVRamAddress & ~(0x73E0)) | ((data & 7) << 12) | (((data >> 3) & 0x1F) << 5);
				writeToggle = 0;
			}
			break;
		case 6:
			if(writeToggle == 0) {
				tempVRamAddress = (tempVRamAddress & ~(0x7F00)) | (data & 0x3F) << 8;
				writeToggle = 1;
			} else {
				tempVRamAddress = (tempVRamAddress & ~(0xFF) | data & 0xFF);
				vRamAddress = tempVRamAddress;
				writeToggle = 0;
			}
			break;
		case 7:
			nes.ppuWrite(vRamAddress, data);
			if(!renderingAndVisible()) {
				vRamAddress += vRamInc;
			} else {
				coarseXIncrement();
				YIncrement();
			}
			break;
		default:
			System.out.println("WEIRDNESS!");
			break;
		}
	}
	
	public int readProgramRegister(int registerToRead) {
		int readReturn = readLatch;
		switch(registerToRead) {
		case 2:
			readReturn ^= (-(vBlankFlag ? 1 : 0) ^ (readLatch & 0xFF)) & 0x80;
			readReturn ^= (-(sprite0Hit ? 1 : 0) ^ (readLatch & 0xFF)) & 0x40;
			readReturn ^= (-(spriteOverflow ? 1 : 0) ^ (readLatch & 0xFF)) & 0x20;
			readReturn &= ~(dataBus & 0x1f);
			readReturn |= (dataBus & 0x1F);
			writeToggle = 0;
			vBlankFlag = false;
			break;
		case 4:
			readReturn = OAMData[OAMAddress];
			break;
		case 7:
			if(vRamAddress >= 0x3F00) {
				readReturn = nes.ppuRead(vRamAddress);
				readLatch = nes.ppuRead(vRamAddress - 0x1000);
			}  else {
				readLatch = nes.ppuRead(vRamAddress);
			}
			if(!renderingAndVisible()) {
				vRamAddress += vRamInc;
			} else {
				coarseXIncrement();
				YIncrement();
			}
			
			break;
		}
		return readReturn;
	}
	
	private void clock() {
		if(scanline < 240 || scanline == 261) {
			if(rendering()) {
				if(cycle > 0 && cycle <= 257 ) {
					handleMemoryAccess();
					if(cycle == 257) {
						vRamAddress = (vRamAddress & ~0x41F) | (tempVRamAddress & 0x41F);
					}
				} else if(cycle > 257 && cycle <= 320) {
					
					if(cycle >= 280 && cycle <= 304 && scanline == 261) {
						vRamAddress = tempVRamAddress;
					}
					handleSpriteFetches();
				} else if(cycle >= 321 && cycle <= 337) {
					handleMemoryAccess();
				} else if(cycle > 337 && cycle <= 340) {
					fetchNametableByte();
					fetchNametableByte();
				}
			}
			if(cycle == 1 && scanline == 261) {
				vBlankFlag = false;
				sprite0Hit = false;
				spriteOverflow = false;
			}
			
		} else if(scanline == 241) {
			if(cycle == 1) {
				vBlankFlag = true;
			}
		}
		if(scanline < 240) {
			if(cycle >= 1 && cycle <= 256) {
				if(bgEnabled) {
					renderBGPixel();
				} else {
					final int bgPixel = ((vRamAddress > 0x3F00 && vRamAddress <0x3FFF) ? nes.ppuRead(vRamAddress) : nes.ppuRead(0x3F00));
					frameArray[(scanline * 256) + (cycle - 1)] = NESPalette.getPixel(bgPixel);
					bgColorIndex = 0;
				}
			}
		}
		nes.NMI(vBlankFlag && NMIEnable);
	}
	
	private void coarseXIncrement() {
		if((vRamAddress & 0x1F) == 31) {
			vRamAddress &=  ~0x1F;
			vRamAddress ^= 0x400;
		} else {
			vRamAddress += 1;
		}
	}
	
	private void YIncrement() {
		if ((vRamAddress & 0x7000) != 0x7000) {        // if fine Y < 7
			  vRamAddress += 0x1000;                      // increment fine Y
		} else {
			vRamAddress &= ~0x7000;                     // fine Y = 0
			int y = (vRamAddress & 0x03E0) >> 5;        // let y = coarse Y
			if (y == 29) {
				y = 0;                          // coarse Y = 0
				vRamAddress ^= 0x0800;                    // switch vertical nametable
			} else if (y == 31) {
			    y = 0;                          // coarse Y = 0, nametable not switched
			} else {
			    y += 1;                         // increment coarse Y
			}
			vRamAddress = (vRamAddress & ~0x03E0) | (y << 5);     // put coarse Y back into v
		}
	}
	
	private void handleMemoryAccess() {
		switch((cycle - 1) & 7) {
		case 1:
			fetchNametableByte();
			break;
		case 3:
			fetchAttributeByte();
			break;
		case 5:
			fetchLowBGByte();
			break;
		case 7:
			fetchHighBGByte();
			loadLatches();
			if(cycle == 256) {
				YIncrement();
			} else {
				coarseXIncrement();
			} 
			
			break;
		}
		if(cycle >= 321 && cycle <= 336) {
			shiftRegisters();
		}
	}
	
	private int spriteToFetch;
	private int currentSpriteYOffset;
	private int currentSpriteTile;
	private int currentSpriteAddress;
	
	private void handleSpriteFetches() {
		spriteToFetch = (cycle - 257) / 8;
		switch((cycle - 1) & 7) {
		case 1:
			currentSpriteYOffset = tempOAMData[spriteToFetch * 4];
			break;
		case 2:
			currentSpriteTile = tempOAMData[spriteToFetch * 4 + 1];
			break;
		case 3:
			spriteAttributes[spriteToFetch] = tempOAMData[spriteToFetch * 4 + 2];
			break;
		case 4:
			spriteXPositions[spriteToFetch] = tempOAMData[spriteToFetch * 4 + 3];
			break;
		case 5:
			currentSpriteAddress = 0x1000 * (spriteTileSelect ? 1 : 0);
			if(((spriteAttributes[spriteToFetch] >> 7 & 1) == 1)) {
				currentSpriteYOffset = ~currentSpriteYOffset & (spriteHeight ? 0xF : 7);
			}
			if(spriteHeight) {
				currentSpriteAddress = ((currentSpriteTile & 1) * 0x1000) + (currentSpriteTile & 0xFE) * 16;
			} else {
				currentSpriteAddress += (currentSpriteTile * 16);
			}
			currentSpriteAddress += currentSpriteYOffset;
			spriteLowBytes[spriteToFetch] = nes.ppuRead(currentSpriteAddress);
			break;
		case 7:
			spriteHighBytes[spriteToFetch++] = nes.ppuRead(currentSpriteAddress + 8);
			break;
		}
	}
	
	private void fetchNametableByte() {
		nametableByte = (0x2000 | (vRamAddress & 0xFFF));
		nametableByte = nes.ppuRead(nametableByte);
	}
	
	private void fetchAttributeByte() {
		attributeByte = 0x23C0 | (vRamAddress & 0xC00) | ((vRamAddress >> 4) & 0x38) | ((vRamAddress >> 2) & 0x7); 
		attributeByte = nes.ppuRead(attributeByte);
	}
	
	public void fetchLowBGByte() {
		int col = nametableByte % 16;
		int row = nametableByte / 16;
		int fineY = ((vRamAddress & 0x7000) >> 12) & 7;
		int address = ((bgTileSelect ? 1 : 0) << 0xC) | (row << 8) | (col << 4) | fineY; 
		bgLatchLow = nes.ppuRead(address);
	}
	
	public void fetchHighBGByte() {
		int col = nametableByte % 16;
		int row = nametableByte / 16;
		int fineY = ((vRamAddress & 0x7000) >> 12) & 7;
		int address = ((bgTileSelect ? 1 : 0) << 0xC) | (row << 8) | (col << 4) | (1 << 3) | fineY;
		bgLatchHigh = nes.ppuRead(address);
	}
	
	private void loadLatches() {
		bgShiftHigh = (bgShiftHigh & ~0xFF) | (bgLatchHigh & 0xFF);
		bgShiftLow = (bgShiftLow & ~0xFF) | (bgLatchLow & 0xFF);
		final int row = ((((vRamAddress & 0x3E0) >> 5)) % 4) / 2;
		final int col = ((vRamAddress & 0x1F) % 4) / 2;
		int attrByte = 0;
		if(row == 0) {
			if(col == 0) {		
				//Top left
				attrByte = attributeByte & 3;
			} else {
				//Top Right
				attrByte = attributeByte >> 2 & 3;
			}
		} else {
			if(col == 0) {
				//Bottom left
				attrByte = attributeByte >> 4 & 3;
			} else {
				//Bottom right
				attrByte = attributeByte >> 6 & 3;
			}
		}
		attrLatchHigh = attrByte >> 1 & 1;
		attrLatchLow = attrByte & 1;
	}
	
	private void renderBGPixel() {
		final int offset = (scanline * 256) + (cycle - 1);
		int pixel = (((bgShiftHigh & 0xFF00) >> 8) >> (7 - fineX) & 1) << 1;
		pixel |= ((bgShiftLow & 0xFF00) >>8) >> (7 - fineX) & 1;
		bgColorIndex = pixel;
		pixel |= (attrShiftHigh >> (7 - fineX) & 1) << 3;
		pixel |= (attrShiftLow >> (7 - fineX) & 1) << 2;
		

		int pixelValue = nes.ppuRead(0x3F00 + pixel);
		if(grayScale) {
			pixelValue &= 0x30;
		}
		if(bgClip && cycle < 8) {
			bgColorIndex = 0;
			frameArray[offset] = NESPalette.getPixel(nes.ppuRead(0x3F00));
			
		} else {
			frameArray[offset] = NESPalette.getPixel(pixelValue);
			
		}
		if(!showBG) {
			frameArray[offset] = 0xFF << 24;
		}
		shiftRegisters();
	}
	
	private void shiftRegisters() {
		bgShiftHigh <<= 1;
		bgShiftLow <<= 1;
		attrShiftHigh = (attrShiftHigh << 1) | (attrLatchHigh & 1);
		attrShiftLow = (attrShiftLow << 1) | (attrLatchLow & 1);
	}
	
	private boolean renderingAndVisible() {
		return (spritesEnabled || bgEnabled) && (scanline <= 240 || scanline == 261);
	}
	
	private boolean rendering() {
		return (spritesEnabled || bgEnabled);
	}
	
	private int yDifference;
	private int inRangeCounter;
	private int spriteTempData;
	private int currentSpriteNum;
	private int evalState;
	private int evalStep;
	
	private void spriteEvaluation() {
		if(!rendering()) {
			return;
		}
		if(cycle >= 1 && cycle <= 64) {
			if(cycle == 1) {
				inRangeCounter = 0;
				currentSpriteNum = 0;
				evalState = 0;
				evalStep = 0;
			}
			if((cycle & 1) == 1) {
				tempOAMData[cycle >> 1] = 0xFF;
			}
		} else if(cycle >= 65 && cycle <= 256 && currentSpriteNum < 64) {
			if((cycle & 1) == 1) {
				spriteTempData = OAMData[(currentSpriteNum * 4) + evalStep];
			} else {
				if(evalState == 0) {
					yDifference = (scanline - spriteTempData);
					if(yDifference >= 0 && yDifference <= (spriteHeight ? 15 : 7)) {
						if(inRangeCounter < 8) {
							tempOAMData[inRangeCounter * 4 + evalStep] = yDifference;
							evalState++;
							evalStep++;
							spriteIndices[inRangeCounter] = currentSpriteNum;
						} else {
							spriteOverflow = true;
						}
					} else {
						currentSpriteNum++;
					}
				} else if(evalState == 1) {
					tempOAMData[inRangeCounter * 4 + evalStep] = spriteTempData;
					evalStep++;
					if(evalStep == 4) {
						currentSpriteNum++;
						inRangeCounter++;
						evalState = 0;
						evalStep = 0;
					}
				}
			}
		}
	}
	
	private void renderSprites() {
		if(spritesEnabled && scanline < 240 && cycle > 0 && cycle < 256) {
			for(int i = 0; i < 8; i++) {
				if(cycle >= spriteXPositions[i] && cycle < (spriteXPositions[i] + 8)) {
					if( !(spriteClip && cycle < 8) ) {
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
	
	private void renderSprite(int index) {
		int shift = cycle - spriteXPositions[index];
		if((spriteAttributes[index] >> 6 & 1) == 0) {
			shift = 7 - shift; 
		} 
		
		int pixel = (spriteLowBytes[index] >> (shift)) & 1;
		pixel |= ((spriteHighBytes[index] >> (shift)) & 1) << 1;
		pixel |= (spriteAttributes[index] & 3) << 2;
		final int bufferIndex = (scanline * 256) + (cycle - 1);
		final int palVal = nes.ppuRead(0x3F10 + pixel);
		final int spritePixel = NESPalette.getPixel((grayScale ? palVal & 0x30 : palVal));
		final int pixelIndex = pixel & 0x03;
		
		final int priority = spriteAttributes[index] >> 5 & 1;
		if(bgColorIndex == 0) {							// If BG Pixel = 0
			if((pixelIndex) != 0 && cycle < 255) {																// If Sprite Pixel is not 0, sprite pixel wins
				frameArray[bufferIndex] = spritePixel;
			}
		} else {																				// If BG Pixel is not 0
			if((pixelIndex) != 0) {																// If Sprite Pixel is not 0, sprite pixel wins
				if(spriteIndices[index] == 0 && cycle < 255) {
					sprite0Hit = true;
				}
				if(priority == 0 || !showBG && cycle < 255) {
					frameArray[bufferIndex] = spritePixel;
				}
			}
		}
	}
	

	@Override
	public void notify(String messageToSend) {
		for(Notifiable notifiable : objectsToNotify) {
			notifiable.takeNotice(messageToSend, this);
		}
	}

	public void registerObjectToNotify(Notifiable objectToNotify) {
		objectsToNotify.add(objectToNotify);
		
	}

	public void unregisterAll() {
		objectsToNotify.clear();
		
	}
	
	public int[] getFrameForPainting() {
		return frameArray;
	}

	public int[] getOamMem() {
		return OAMData;
	}
	
	public void toggleDisplay(int bgOrObject, boolean display) {
		if(bgOrObject == 0) {
			showBG = display;
		} else {
			showObjects = display;
		}
	}
	
}
