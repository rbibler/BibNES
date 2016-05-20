package com.bibler.awesome.bibnes.assembler;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Assembler {
	
	private HashMap<String,Integer> opCodes = new HashMap<String,Integer>();
	
	char[] firstSpaceChars = new char[]{'A', 'B', 'C', 'D', 'E', 'I', 'J', 'L', 'O', 'P', 'R', 'S', 'T'};
	char[][] secondSpaceChars = {{'D', 'N', 'S'}};
	char[][][] thirdSpaceChars = {{{'C'},{'D'},{'L'}}};
	
	public final static int ACCUMULATOR = 0x00;
	public final static int IMMEDIATE = 0x01;
	public final static int ABSOLUTE = 0x02;
	public final static int ABSOLUTE_X = 0x03;
	public final static int ABSOLUTE_Y = 0x04;
	
	Pattern hexValues = Pattern.compile("[0-9[A-F]]");
	Matcher m;
	String instruction;
	String address;
	int addressingMode;
	
	// first space chars: A, B, C, D, E, I, J, L, O, P, R, S, T
	
	public Assembler() {
		fillMap();
	}
	
	private void fillMap() {
		firstSpaceChars[0] = 'A';
		
	}
	
	public boolean matchOpCode(String lineToParse) {
		boolean match = false;
		String tmp = lineToParse.replaceAll("\\s+", "");
		StringBuilder b = new StringBuilder();
		for(int i = 0; i < firstSpaceChars.length; i++) {
			if(tmp.charAt(0) == firstSpaceChars[i]) {
				for(int j = 0; j < secondSpaceChars[i].length; j++) {
					if(tmp.charAt(1) == secondSpaceChars[i][j]) {
						for(int k = 0; k < thirdSpaceChars[i][j].length; k++) {
							if(tmp.charAt(2) == thirdSpaceChars[i][j][k]) {
								b.append(firstSpaceChars[i]);
								b.append(secondSpaceChars[i][j]);
								b.append(thirdSpaceChars[i][j][k]);
								instruction = b.toString();
								System.out.println(instruction);
								match = true;
							}
						}
					}
				}
			}
		}
		return match;
	}
	
	public int parseOpCode(String lineToParse) {
		int opCode = 0x100;
		String tmp = trimWhiteSpace(lineToParse);
		if(matchOpCode(tmp)) {
			tmp = tmp.substring(3);
			if(checkAddressingMode(tmp)) {
				opCode = constructOpCode();
			}
		}
		return opCode;
	}
	
	public String trimWhiteSpace(String lineToTrim) {
		return lineToTrim.replaceAll("\\s+", "");
	}
	
	public boolean checkAddressingMode(String lineToParse) {
		boolean match = false;
		if(checkImmediate(lineToParse)) {
			match = true;
			addressingMode = IMMEDIATE;
			address = lineToParse.substring(2);
		}
		return match;
	}
	
	private boolean checkImmediate(String addressToCheck) {
		System.out.println(addressToCheck);
		boolean match = false;
		if(addressToCheck.charAt(0) == '#') {
			if(addressToCheck.charAt(1) == '$') {
				match = isHexDigit(addressToCheck.substring(2));
			}
		}
		return match;
	}
	
	private int constructOpCode() {
		return ACCUMULATOR;
	}
	
	private boolean isHexDigit(String s) {
		for(int i = 0; i < s.length(); i++) {
			System.out.println(s.substring(i,i + 1));
			m = hexValues.matcher(s.substring(i, i + 1));
			if(!m.matches()) {
				return false;
			}
		}
		return true;
	}
	
	public int getAddressMode() {
		return addressingMode;
	}
	
	public String getAddress() {
		return address;
	}

}
