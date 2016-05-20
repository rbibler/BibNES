package com.bibler.awesome.bibnes.assembler;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Assembler {
	
	private HashMap<String,Integer> opCodes = new HashMap<String,Integer>();
	
	char[] firstSpaceChars = new char[]{'A', 'B', 'C', 'D', 'E', 'I', 'J', 'L', 'O', 'P', 'R', 'S', 'T'};
	char[][] secondSpaceChars = {{'D', 'N', 'S'}};
	char[][][] thirdSpaceChars = {{{'C'},{'D'},{'L'}}};
	
	Pattern hexValues = Pattern.compile("[0-9[A-F]]");
	Matcher m;
	String instruction;
	
	// first space chars: A, B, C, D, E, I, J, L, O, P, R, S, T
	
	public Assembler() {
		fillMap();
	}
	
	private void fillMap() {
		firstSpaceChars[0] = 'A';
		
	}
	
	public int parseOpCode(String lineToParse) {
		String tmp = lineToParse.replaceAll("\\s+", "");
		StringBuilder b = new StringBuilder();
		int returnInt = 0x100;
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
								checkAddressingMode(tmp.substring(3));
								return 0x29;
							}
						}
					}
				}
			}
		}
		return returnInt;
	}
	
	private int checkAddressingMode(String lineToParse) {
		boolean match = false;
		System.out.println(lineToParse);
		if(lineToParse.charAt(0) == '#') {
			if(lineToParse.charAt(1) == '$') {
				match = isHexDigit(lineToParse.substring(2));
			}
		}
		System.out.println(match);
		return 0;
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

}
