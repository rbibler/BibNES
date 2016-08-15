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
	private int[] tempOAMData;
	
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
	
	// PPU Parameters
	//private int linesPerFrame;
	//private int visibleScanlines;

	
	//Power on Palette Values
	private int[] powerOnPalette = new int[] {
			
	};
	
	//Other Systems
	private NES nes;
	
	//Communications
	private ArrayList<Notifiable> objectsToNotify = new ArrayList<Notifiable>();
	
	public PPU(NES nes) {
		this.nes = nes;
	}
	
	public void cycle() {
		clock();
		cycle++;
		if(cycle > 340) {
			cycle = 0;
			scanline++;
			if(scanline > 261) {
				frame++;
				scanline = 0;
				nes.frame();
				notify("FRAME");
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
				OAMData[OAMAddress++] = data;
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
			readReturn = OAMData[OAMAddress++];
			break;
		case 7:
			readLatch = nes.ppuRead(vRamAddress);
			if(!renderingAndVisible()) {
				vRamAddress += vRamInc;
			} else {
				coarseXIncrement();
				YIncrement();
			}
			if(vRamAddress >= 0x3F00) {
				readReturn = readLatch;
			} 
			break;
		}
		return readReturn;
	}
	
	private void clock() {
		if(scanline < 240 || scanline == 261) {
			if(rendering()) {
				if(cycle > 0 && cycle <= 257 ) {
					if(cycle == 1 && scanline == 261) {
						vBlankFlag = false;
						sprite0Hit = false;
						spriteOverflow = false;
					}
					handleMemoryAccess();
					if(cycle == 257) {
						vRamAddress = (vRamAddress & ~0x41F) | (tempVRamAddress & 0x41F);
					}
				} else if(cycle > 257 && cycle <= 320) {
					
					if(cycle >= 280 && cycle <= 304 && scanline == 261) {
						vRamAddress = tempVRamAddress;
					}
					//handleSpriteFetches();
				} else if(cycle >= 321 && cycle <= 337) {
					handleMemoryAccess();
				} else if(cycle > 337 && cycle <= 340) {
					fetchNametableByte();
					fetchNametableByte();
				}
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
		switch(cycle % 8) {
		case 1:
			if((cycle >= 9 && cycle < 321) || (cycle > 321)) {
				attributeByte = nextAttrByte;
				loadLatches();
			} 
			break;
		case 2:
			fetchNametableByte();
			break;
		case 4:
			fetchAttributeByte();
			break;
		case 6:
			fetchLowBGByte();
			break;
		case 0:
			fetchHighBGByte();
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
	
	private void fetchNametableByte() {
		final int y = (vRamAddress & 0x3E0) >> 5;
		nametableByte = (0x2000 | (vRamAddress & 0xFFF));
		
		nametableByte = nes.ppuRead(nametableByte);
		/*if(scanline < 16) {
		System.out.println("S: " + scanline + " C: " + cycle + " Y: " + y + " NT: " + Integer.toHexString(nametableByte).toUpperCase());
		}*/
	}
	
	private void fetchAttributeByte() {
		attributeByte = 0x23C0 | (vRamAddress & 0xC00) | ((vRamAddress >> 4) & 0x38) | ((vRamAddress >> 2) & 0x7); 
		nextAttrByte = nes.ppuRead(attributeByte);
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
		//final int row = (((vRamAddress & 0x3E0) >> 5) % 4) / 2;
		//final int col = ((vRamAddress & 0x1F) % 4) / 2;
		
		final int row = (scanline % 32) / 16;
		final int col = ((cycle - 1) % 32) / 16;
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
			frameArray[offset] = NESPalette.getPixel(nes.ppuRead(0x3F00));
		} else {
			frameArray[offset] = NESPalette.getPixel(pixelValue);
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
	
	
	
}
