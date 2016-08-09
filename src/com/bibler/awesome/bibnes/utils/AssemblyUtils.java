package com.bibler.awesome.bibnes.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import com.bibler.awesome.bibnes.io.TextReader;

public class AssemblyUtils {
	public final static int ADDRESS_MODE_COUNT = 13;
	
	public final static int ACCUMULATOR = 	0x00;
	public final static int ABSOLUTE    = 	0x01;
	public final static int ABSOLUTE_X  = 	0x02;
	public final static int ABSOLUTE_Y  = 	0x03;
	public final static int IMMEDIATE   = 	0x04;
	public final static int IMPLIED     = 	0x05;
	public final static int INDIRECT    = 	0x06;
	public final static int INDIRECT_X  = 	0x07;
	public final static int INDIRECT_Y  = 	0x08;
	public final static int RELATIVE    = 	0x09;
	public final static int ZERO_PAGE   = 	0x0A;
	public final static int ZERO_PAGE_X = 	0x0B;
	public final static int ZERO_PAGE_Y = 	0x0C;
	
	
	public final static int ALIGN = 0;
	public final static int BYTE = 1;
	public final static int DB = 2;
	public final static int DW = 3;
	public final static int EQU = 4;
	public final static int FILL = 5;
	public final static int INCLUDE = 6;
	public final static int INC = 7;
	public final static int ORG = 8;
	public final static int RSSET = 9;
	public final static int RS = 10;
	public final static int WORD = 11;
	public final static int BS = 12;
	public final static int BANK = 13;
	public final static int INES_PRG = 14;
	public final static int INES_CHR = 15;
	public final static int INES_MAP = 16;
	public final static int INES_MIRROR = 17;
	public final static int CHR_ZONE = 18;
	
	public final static int DEFAULT_BANK_SIZE = 0x2000;
	
	private static HashMap<String, Integer> immediateOpCodes;
	private static HashMap<String, Integer> accumulatorOpCodes;
	private static HashMap<String, Integer> impliedOpCodes;
	private static HashMap<String, Integer> zeroPageOpCodes;
	private static HashMap<String, Integer> zeroPageXOpCodes;
	private static HashMap<String, Integer> zeroPageYOpCodes;
	private static HashMap<String, Integer> absoluteOpCodes;
	private static HashMap<String, Integer> absoluteXOpCodes;
	private static HashMap<String, Integer> absoluteYOpCodes;
	private static HashMap<String, Integer> indirectOpCodes;
	private static HashMap<String, Integer> indirectXOpCodes;
	private static HashMap<String, Integer> indirectYOpCodes;
	private static HashMap<String, Integer> relativeOpCodes;
	
	private static ArrayList<String> addressModePatterns;
	
	private static ArrayList<HashMap<String, Integer>> addressModes;
	
	public final static int firstSpaceLength = 13;
	
	public static String getAddressModePattern(int addressMode) {
		if(addressModePatterns == null) {
			fillAddressModePatterns();
		}
		return addressModePatterns.get(addressMode);
	}
	
	private static void fillAddressModePatterns() {
		addressModePatterns = new ArrayList<String>();
		addressModePatterns.add("~");														// Accumulator
		addressModePatterns.add("~");								// Absolute
		addressModePatterns.add("~,x");					// Absolute X
		addressModePatterns.add("~,y");					// Absolute Y
		addressModePatterns.add("#~");						// Immediate
		addressModePatterns.add("");														// Implied
		addressModePatterns.add("(~)");					// Indirect
		addressModePatterns.add("(~,x)");						// Indirect, X
		addressModePatterns.add("(~),y");				// Indirect, Y
		addressModePatterns.add("~");							// Relative
		addressModePatterns.add("~");							// ZeroPage
		addressModePatterns.add("~,x");					// ZeroPage, X
		addressModePatterns.add("~,y");					// ZeroPage, Y
		
	}
	
	public static String getInstruction(int instruction) {
		if(immediateOpCodes == null) {
			fillMaps();
		}
		String ret = checkMapForInstruction(immediateOpCodes, instruction);
		if(ret == null) {
			ret = checkMapForInstruction(accumulatorOpCodes, instruction);
			if(ret == null) {
				ret = checkMapForInstruction(impliedOpCodes, instruction);
				if(ret == null) {
					ret = checkMapForInstruction(zeroPageOpCodes, instruction);
					if(ret == null) {
						ret = checkMapForInstruction(zeroPageXOpCodes, instruction);
						if(ret == null) {
							ret = checkMapForInstruction(zeroPageYOpCodes, instruction);
							if(ret == null) {
								ret = checkMapForInstruction(absoluteOpCodes, instruction);
								if(ret == null) {
									ret = checkMapForInstruction(absoluteXOpCodes, instruction);
									if(ret == null) {
										ret = checkMapForInstruction(absoluteYOpCodes, instruction);
										if(ret == null) {
											ret = checkMapForInstruction(indirectOpCodes, instruction);
											if(ret == null) {
												ret = checkMapForInstruction(indirectXOpCodes, instruction);
												if(ret == null) {
													ret = checkMapForInstruction(indirectYOpCodes, instruction);
													if(ret == null) {
														ret = checkMapForInstruction(relativeOpCodes, instruction);
													}
												}
											}
										}
									}	
								}
							}
						}
					}
				}
			}
		}
		return ret;
	}
	
	public static String checkMapForInstruction(HashMap<String, Integer> map, int instruction) {
		String ret = null;
		if(map != null && map.containsValue(instruction)) {
			Set<Entry<String, Integer>> set = map.entrySet();
			for(Entry<String, Integer> entry : set) {
				if(entry.getValue() == instruction) {
					ret = entry.getKey();
					break;
				}
			}		
		}
		return ret;
	}
	
	public static int lookupAddressMode(int opCode) {
		int addressMode = -1;
		int count = 0;
		if(addressModes == null) {
			fillMaps();
		}
		for(HashMap<String, Integer> map : addressModes) {
			if(map.containsValue(opCode)) {
				addressMode = count;
				break;
			} else {
				count++;
			}
		}
		return addressMode;
	}
	
	public static boolean noZeroPage(String instruction, int zpToCheck) {
		return !checkForAddressMode(zpToCheck, instruction);
	}
	
	public static boolean checkImplied(String s) {
		if(s.length() == 0) {
			return true;
		}
		if(s.charAt(0) == ';') {
			return true;
		}
		return false;
	}
	
	
	public static int findFirstSpaceChar(char charToFind) {
		int index = -1;
		for(int i = 0; i < firstSpaceChars.length; i++) {
			if(charToFind == firstSpaceChars[i]) {
				index = i;
				break;
			}
		}
		return index;
	}
	
	public static int findSecondSpaceChar(char charToFind, int firstSpaceChar) {
		int index = -1;
		for(int i = 0; i < secondSpaceChars[firstSpaceChar].length; i++) {
			if(charToFind == secondSpaceChars[firstSpaceChar][i]) {
				index = i;
				break;
			}
		}
		return index;
	}
	
	public static int findThirdSpaceChar(char charToFind, int firstSpaceChar, int secondSpaceChar) {
		int index = -1;
		for(int i = 0; i < thirdSpaceChars[firstSpaceChar][secondSpaceChar].length; i++) {
			if(charToFind == thirdSpaceChars[firstSpaceChar][secondSpaceChar][i]) {
				index = i;
				break;
			}
		}
		return index;
	}
	
	public static String getInstruction(int firstSpaceChar, int secondSpaceChar, int thirdSpaceChar) {
		StringBuilder b = new StringBuilder();
		b.append(firstSpaceChars[firstSpaceChar]);
		b.append(secondSpaceChars[firstSpaceChar][secondSpaceChar]);
		b.append(thirdSpaceChars[firstSpaceChar][secondSpaceChar][thirdSpaceChar]);
		return b.toString();
	}
	
	public static int getOpCode(String instruction, int addressingMode) {
		if(immediateOpCodes == null) {
			fillMaps();
		}
		int opCode = -1;
		HashMap<String, Integer> mapToCheck = addressModes.get(addressingMode);
		if(mapToCheck.containsKey(instruction)) {
			opCode = mapToCheck.get(instruction);
		}
		return opCode;
	}
	
	public static boolean checkForAddressMode(int addressMode, String instruction) {
		if(addressModes == null) {
			fillMaps();
		}
		return addressModes.get(addressMode).containsKey(instruction);
	}
	
	public static int getBytes(int opCode) {
		return opCodeLengths[opCode];
	}
	
	/**
	 * Fills the different hash maps with Instruction mnemonic and opcode for use with the getOpCode function.
	 */
	
	private static void fillMaps() {
		immediateOpCodes = new HashMap<String, Integer>();
		immediateOpCodes.put("AND", 0x29);
		immediateOpCodes.put("ADC", 0x69);
		immediateOpCodes.put("CMP", 0xC9);
		immediateOpCodes.put("CPX", 0xE0);
		immediateOpCodes.put("CPY", 0xC0);
		immediateOpCodes.put("EOR", 0x49);
		immediateOpCodes.put("LDA", 0xA9);
		immediateOpCodes.put("LDX", 0xA2);
		immediateOpCodes.put("LDY", 0xA0);
		immediateOpCodes.put("ORA", 0x09);
		immediateOpCodes.put("SBC", 0xE9);
		accumulatorOpCodes = new HashMap<String, Integer>();
		accumulatorOpCodes.put("ASL", 0x0A);
		accumulatorOpCodes.put("LSR", 0x4A);
		accumulatorOpCodes.put("ROL", 0x2A);
		accumulatorOpCodes.put("ROR", 0x6A);
		impliedOpCodes = new HashMap<String, Integer>();
		impliedOpCodes.put("BRK", 0x00);
		impliedOpCodes.put("CLC", 0x18);
		impliedOpCodes.put("SEC", 0x38);
		impliedOpCodes.put("CLI", 0x58);
		impliedOpCodes.put("SEI", 0x78);
		impliedOpCodes.put("CLV", 0xB8);
		impliedOpCodes.put("CLD", 0xD8);
		impliedOpCodes.put("SED", 0xF8);
		impliedOpCodes.put("NOP", 0xEA);
		impliedOpCodes.put("TAX", 0xAA);
		impliedOpCodes.put("TXA", 0x8A);
		impliedOpCodes.put("DEX", 0xCA);
		impliedOpCodes.put("INX", 0xE8);
		impliedOpCodes.put("TAY", 0xA8);
		impliedOpCodes.put("TYA", 0x98);
		impliedOpCodes.put("DEY", 0x88);
		impliedOpCodes.put("INY", 0xC8);
		impliedOpCodes.put("RTI", 0x40);
		impliedOpCodes.put("RTS", 0x60);
		impliedOpCodes.put("TXS", 0x9A);
		impliedOpCodes.put("TSX", 0xBA);
		impliedOpCodes.put("PHA", 0x48);
		impliedOpCodes.put("PLA", 0x68);
		impliedOpCodes.put("PHP", 0x08);
		impliedOpCodes.put("PLP", 0x28);
		
		zeroPageOpCodes = new HashMap<String, Integer>();
		zeroPageOpCodes.put("ADC", 0x65);
		zeroPageOpCodes.put("AND", 0x25);
		zeroPageOpCodes.put("ASL", 0x06);
		zeroPageOpCodes.put("BIT", 0x24);
		zeroPageOpCodes.put("CMP", 0xC5);
		zeroPageOpCodes.put("CPX", 0xE4);
		zeroPageOpCodes.put("CPY", 0xC4);
		zeroPageOpCodes.put("DEC", 0xC6);
		zeroPageOpCodes.put("EOR", 0x45);
		zeroPageOpCodes.put("INC", 0xE6);
		zeroPageOpCodes.put("LDA", 0xA5);
		zeroPageOpCodes.put("LDX", 0xA6);
		zeroPageOpCodes.put("LDY", 0xA4);
		zeroPageOpCodes.put("LSR", 0x46);
		zeroPageOpCodes.put("ORA", 0x05);
		zeroPageOpCodes.put("ROL", 0x26);
		zeroPageOpCodes.put("ROR", 0x66);
		zeroPageOpCodes.put("SBC", 0xE5);
		zeroPageOpCodes.put("STA", 0x85);
		zeroPageOpCodes.put("STX", 0x86);
		zeroPageOpCodes.put("STY", 0x84);
		
		zeroPageXOpCodes = new HashMap<String, Integer>();
		zeroPageXOpCodes.put("ADC", 0x75);
		zeroPageXOpCodes.put("AND", 0x35);
		zeroPageXOpCodes.put("ASL", 0x16);
		zeroPageXOpCodes.put("CMP", 0xD5);
		zeroPageXOpCodes.put("DEC", 0xD6);
		zeroPageXOpCodes.put("EOR", 0x55);
		zeroPageXOpCodes.put("INC", 0xF6);
		zeroPageXOpCodes.put("LDA", 0xB5);
		zeroPageXOpCodes.put("LDY", 0xB4);
		zeroPageXOpCodes.put("LSR", 0x56);
		zeroPageXOpCodes.put("ORA", 0x15);
		zeroPageXOpCodes.put("ROL", 0x36);
		zeroPageXOpCodes.put("ROR", 0x76);
		zeroPageXOpCodes.put("SBC", 0xF5);
		zeroPageXOpCodes.put("STA", 0x95);
		zeroPageXOpCodes.put("STY", 0x94);
		
		zeroPageYOpCodes = new HashMap<String, Integer>();
		zeroPageYOpCodes.put("LDX", 0xB6);
		zeroPageYOpCodes.put("STX", 0x96);
		
		absoluteOpCodes = new HashMap<String, Integer>();
		absoluteOpCodes.put("ADC", 0x6D);
		absoluteOpCodes.put("AND", 0x2D);
		absoluteOpCodes.put("ASL", 0x0E);
		absoluteOpCodes.put("BIT", 0x2C);
		absoluteOpCodes.put("CMP", 0xCD);
		absoluteOpCodes.put("CPX", 0xEC);
		absoluteOpCodes.put("CPY", 0xCC);
		absoluteOpCodes.put("DEC", 0xCE);
		absoluteOpCodes.put("EOR", 0x4D);
		absoluteOpCodes.put("INC", 0xEE);
		absoluteOpCodes.put("JMP", 0x4C);
		absoluteOpCodes.put("JSR", 0x20);
		absoluteOpCodes.put("LDA", 0xAD);
		absoluteOpCodes.put("LDX", 0xAE);
		absoluteOpCodes.put("LDY", 0xAC);
		absoluteOpCodes.put("LSR", 0x4E);
		absoluteOpCodes.put("ORA", 0x0D);
		absoluteOpCodes.put("ROL", 0x2E);
		absoluteOpCodes.put("ROR", 0x6E);
		absoluteOpCodes.put("SBC", 0xED);
		absoluteOpCodes.put("STA", 0x8D);
		absoluteOpCodes.put("STX", 0x8E);
		absoluteOpCodes.put("STY", 0x8C);
		
		absoluteXOpCodes = new HashMap<String, Integer>();
		absoluteXOpCodes.put("ADC", 0x7D);
		absoluteXOpCodes.put("AND", 0x3D);
		absoluteXOpCodes.put("ASL", 0x1E);
		absoluteXOpCodes.put("CMP", 0xDD);
		absoluteXOpCodes.put("DEC", 0xDE);
		absoluteXOpCodes.put("EOR", 0x5D);
		absoluteXOpCodes.put("INC", 0xFE);
		absoluteXOpCodes.put("LDA", 0xBD);
		absoluteXOpCodes.put("LDY", 0xBC);
		absoluteXOpCodes.put("LSR", 0x5E);
		absoluteXOpCodes.put("ORA", 0x1D);
		absoluteXOpCodes.put("ROL", 0x3E);
		absoluteXOpCodes.put("ROR", 0x7E);
		absoluteXOpCodes.put("SBC", 0xFD);
		absoluteXOpCodes.put("STA", 0x9D);
		
		absoluteYOpCodes = new HashMap<String, Integer>();
		absoluteYOpCodes.put("ADC", 0x79);
		absoluteYOpCodes.put("AND", 0x39);
		absoluteYOpCodes.put("CMP", 0xD9);
		absoluteYOpCodes.put("EOR", 0x59);
		absoluteYOpCodes.put("LDA", 0xB9);
		absoluteYOpCodes.put("LDX", 0xBE);
		absoluteYOpCodes.put("ORA", 0x19);
		absoluteYOpCodes.put("SBC", 0xF9);
		absoluteYOpCodes.put("STA", 0x99);
		
		indirectXOpCodes = new HashMap<String, Integer>();
		indirectXOpCodes.put("ADC", 0x61);
		indirectXOpCodes.put("AND", 0x21);
		indirectXOpCodes.put("CMP", 0xC1);
		indirectXOpCodes.put("EOR", 0x41);
		indirectXOpCodes.put("LDA", 0xA1);
		indirectXOpCodes.put("ORA", 0x01);
		indirectXOpCodes.put("SBC", 0xE1);
		indirectXOpCodes.put("STA", 0x81);
		
		indirectYOpCodes = new HashMap<String, Integer>();
		indirectYOpCodes.put("ADC", 0x71);
		indirectYOpCodes.put("AND", 0x31);
		indirectYOpCodes.put("CMP", 0xD1);
		indirectYOpCodes.put("EOR", 0x51);
		indirectYOpCodes.put("LDA", 0xB1);
		indirectYOpCodes.put("ORA", 0x11);
		indirectYOpCodes.put("SBC", 0xF1);
		indirectYOpCodes.put("STA", 0x91);
		
		indirectOpCodes = new HashMap<String, Integer>();
		indirectOpCodes.put("JMP", 0x6C);
		
		relativeOpCodes = new HashMap<String, Integer>();
		relativeOpCodes.put("BPL", 0x10);
		relativeOpCodes.put("BMI", 0x30);
		relativeOpCodes.put("BVC", 0x50);
		relativeOpCodes.put("BVS", 0x70);
		relativeOpCodes.put("BCC", 0x90);
		relativeOpCodes.put("BCS", 0xB0);
		relativeOpCodes.put("BNE", 0xD0);
		relativeOpCodes.put("BEQ", 0xF0);
		
		addressModes = new ArrayList<HashMap<String, Integer>>();
		addressModes.add(accumulatorOpCodes);
		addressModes.add(absoluteOpCodes);
		addressModes.add(absoluteXOpCodes);
		addressModes.add(absoluteYOpCodes);
		addressModes.add(immediateOpCodes);
		addressModes.add(impliedOpCodes);
		addressModes.add(indirectOpCodes);
		addressModes.add(indirectXOpCodes);
		addressModes.add(indirectYOpCodes);
		addressModes.add(relativeOpCodes);
		addressModes.add(zeroPageOpCodes);
		addressModes.add(zeroPageXOpCodes);
		addressModes.add(zeroPageYOpCodes);
		
	}
	
	public static boolean checkForBranchInstruction(String instructionToCheck) {
		return relativeOpCodes.containsKey(instructionToCheck);
	}
	
	public static String[] processFile(File f) {
		String[] lines = TextReader.readTextFile(f);
		return lines;
	}
	
	public static String findDirective(String s) {
		if(s.length() == 0 || s.charAt(0) != '.') {
			return null;
		}
		String directive;
		String testString = s.toUpperCase().substring(1);
		int directiveIndex = -1;
		for(int i = 0; i < directives.length; i++) {
			directive = directives[i];
			if(directive.length() > s.length()) {
				continue;
			}
			boolean match = true;
			for(int j = 0; j < directive.length(); j++) {
				if(testString.charAt(j) != directive.charAt(j)) {
					match = false;
					break;
				}
			}
			if(match) {
				directiveIndex = i;
				break;
			}
		}
		if(directiveIndex != -1) {
			return directives[directiveIndex];
		} else {
			return null;
		}
	}
	
	public static int getDirective(String directive) {
		if(directive == null) {
			return -1;
		}
		for(int i = 0; i < directives.length; i++) {
			if(directive.equals(directives[i])) {
				return i;
			}
		}
		return -1;
	}
	
	private static String[] directives = new String[] {
			"ALIGN", "BYTE", "DB", "DW", "EQU", "FILL", "INCLUDE", "INC",
			"ORG", "RSSET", "RS", "WORD", "BS", "BANK", "INESPRG", "INESCHR", "INESMAP", "INESMIR"
			
	};
	
	
	private static int[] opCodeLengths = new int[] {
			//  0 1 2 3 4 5 6 7 8 9 A B C D E F	
				1,2,0,0,0,2,2,0,1,2,1,0,0,3,3,0,	// 0
				2,2,0,0,0,2,2,0,1,3,0,0,0,3,3,0,	// 1
				3,2,0,0,2,2,2,0,1,2,1,0,3,3,3,0,	// 2
				2,2,0,0,0,2,2,0,1,3,0,0,0,3,3,0,	// 3
				1,2,0,0,0,2,2,0,1,2,1,0,3,3,3,0,	// 4
				2,2,0,0,0,2,2,0,1,3,0,0,0,3,3,0,	// 5
				1,2,0,0,0,2,2,0,1,2,1,0,3,3,3,0,	// 6
				2,2,0,0,0,2,2,0,1,3,0,0,0,3,3,0,	// 7
				0,2,0,0,2,2,2,0,1,0,1,0,3,3,3,0,	// 8
				2,2,0,0,2,2,2,0,1,3,1,0,0,3,0,0,	// 9
				2,2,2,0,2,2,2,0,1,2,1,0,3,3,3,0,	// A
				2,2,0,0,2,2,2,0,1,3,1,0,3,3,3,0,	// B
				2,2,0,0,2,2,2,0,1,2,1,0,3,3,3,0,	// C
				2,2,0,0,0,2,2,0,1,3,0,0,0,3,3,0,	// D
				2,2,0,0,2,2,2,0,1,2,1,0,3,3,3,0,	// E
				2,2,0,0,0,2,2,0,1,3,0,0,0,3,3,0		// F
				
		};

	private static char[] firstSpaceChars = new char[]{'A', 'B', 'C', 'D', 'E', 'I', 'J', 'L', 'O', 'P', 'R', 'S', 'T', 'N'};
	private static char[][] secondSpaceChars = {
			{'D', 'N', 'S'},									// Axx 
			{'C', 'E', 'I', 'M', 'N', 'P', 'R', 'V'}, 			// Bxx
			{'L', 'M', 'P'},									// Cxx 
			{'E'},												// Dxx 
			{'O'},												// Exx 
			{'N'},												// Ixx 
			{'M', 'S'},											// Jxx
			{'D', 'S'},											// Lxx 
			{'R'},												// Oxx 
			{'H', 'L'},											// Pxx 
			{'O', 'T'},											// Rxx 
			{'B', 'E', 'T'},									// Sxx 
			{'A', 'S', 'X', 'Y'},								// Txx
			{'O'}};												// NOP	
	private static char[][][] thirdSpaceChars = {
			{{'C'},{'D'},{'L'}},
			{{'C', 'S'}, {'Q'}, {'T'}, {'I'}, {'E'}, {'L'}, {'K'}, {'C', 'S'}},
			{{'C', 'D', 'I', 'V'}, {'P'}, {'X', 'Y'}},
			{{'C', 'X', 'Y'}},
			{{'R'}},
			{{'C', 'X', 'Y'}},
			{{'P'}, {'R'}},
			{{'A', 'X', 'Y'}, {'R'}},
			{{'A'}},
			{{'A', 'P'}, {'A', 'P'}},
			{{'L', 'R'}, {'I', 'S'}},
			{{'C'}, {'C', 'D', 'I'}, {'A', 'X', 'Y'}},
			{{'X', 'Y'}, {'X'}, {'A', 'S'}, {'A'}},
			{{'P'}}
	};

	public static String getAddressModeName(int addressingMode) {
		switch(addressingMode) {
		case ACCUMULATOR:
			return "Accumulator";
		case ABSOLUTE:
			return "Absolute";
		case ABSOLUTE_X:
			return "Absolute X";
		case ABSOLUTE_Y:
			return "Absolute Y";
		case IMMEDIATE:
			return "Immediate";
		case IMPLIED:
			return "Implied";
		case INDIRECT:
			return "Indirect";
		case INDIRECT_X:
			return "Indirect X";
		case INDIRECT_Y:
			return "Indirect Y";
		case RELATIVE:
			return "Relative";
		case ZERO_PAGE:
			return "Zero Page";
		case ZERO_PAGE_X:
			return "Zero Page X";
		case ZERO_PAGE_Y:
			return "Zero Page Y";
		}
		return null;
	}
}
