package com.bibler.awesome.bibnes.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.bibler.awesome.bibnes.assembler.Assembler;
import com.bibler.awesome.bibnes.assembler.ErrorHandler;

public class StringUtils {
	
	private static String[] hexValues = new String[] {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};
	private static Pattern alphabeticChars = Pattern.compile("[a-zA-Z]");
	private static Pattern labelChars = Pattern.compile("[a-zA-Z[0-9][@_?:]]");
	public static char digitChar = '@';
	public static char digitOrLabelChar = '!';
	public static char charToSave = '~';
	public static boolean saveNextMatch;
	
	public static String checkAddressPattern(String addressToCheck, String pattern) {
		String operand = null;
		int addressIndex = 0;
		char charToCheck;
		saveNextMatch = false;
		for(int i = 0; i < pattern.length(); i++) {
			charToCheck = pattern.charAt(i);
			if(charToCheck == charToSave) {
				saveNextMatch = true;
				continue;
			}
			if(charToCheck == digitChar) {
				operand = DigitUtils.getDigitString(addressToCheck.substring(addressIndex));
				if(operand == null) {
					break;
				} else {
					addressIndex += operand.length();
				}
			} else if(charToCheck == digitOrLabelChar) {
				if(DigitUtils.checkFirstDigit(addressToCheck.substring(addressIndex))) {
					operand = DigitUtils.getDigitString(addressToCheck.substring(addressIndex));
					if(operand == null) {
						break;
					} else {
						addressIndex += operand.length();
					}
				} else {
					operand = checkLabel(addressToCheck.substring(addressIndex));
					if(operand == null) {
						break;
					} else {
						addressIndex += operand.length();
						operand = "L" + operand;
					}
				}
			} else {
				if(addressIndex < addressToCheck.length() && addressToCheck.charAt(addressIndex) == charToCheck) {
					if(saveNextMatch) {
						operand = "" + addressToCheck.charAt(addressIndex);
					}
					addressIndex++;
				} else {
					break;
				}
			}
		}
		if(operand != null) {
			if(addressIndex < addressToCheck.length() - 1) {
				if(addressToCheck.charAt(addressIndex) != ';') {
					operand = null;
				}
			}
		}
		return operand;
	}
	
	public static int stringToInt(String s, int radix) {
		return Integer.parseInt(s, radix);
	}
	
	public static String intToHexString(int intToConvert) {
		StringBuilder sb = new StringBuilder();
		int temp = Integer.reverseBytes(intToConvert);
		byte b;
		for(int i = 0; i < Integer.BYTES; i++) {
			b = (byte) ( (temp >> (8 * i)) & 0xFF);
			sb.append(hexValues[(b >> 4) & 0xF]);
			sb.append(hexValues[b & 0xF]);
		}
		String s  = sb.toString();
		while(s.length() > 0) {
			char c = s.charAt(0);
			if(c != '0') {
				break;
			}
			s = s.substring(1);
		}
		return s;
	}
	
	public static String intToHexString(int intToConvert, int digits) {
		StringBuilder sb = new StringBuilder();
		int temp = Integer.reverseBytes(intToConvert);
		byte b;
		for(int i = 0; i < Integer.BYTES; i++) {
			b = (byte) ( (temp >> (8 * i)) & 0xFF);
			sb.append(hexValues[(b >> 4) & 0xF]);
			sb.append(hexValues[b & 0xF]);
		}
		String s  = sb.toString();
		s = s.substring(s.length() - digits);
		return s;
	}
	
	public static int hexStringToInt(String hexString) {
		return Integer.parseInt(hexString, 16);
	}
	
	public static int binStringToInt(String binString) {
		return Integer.parseInt(binString, 2);
	}
	
	public static String trimWhiteSpace(String lineToTrim) {
		return lineToTrim.replaceAll("\\s+", "");
	}
	
	public static boolean validateLine(String s, int lastLegitChar) {
		boolean legit = false;
		s = trimWhiteSpace(s);
		if(s.length() - 1 == lastLegitChar) {
			legit = true;
		} else if(s.charAt(lastLegitChar + 1) == ';') {
			legit = true;
		}
		return legit;
	}
	
	public static String checkLabel(String s) {
		String label = null;
		Matcher m = alphabeticChars.matcher(s.substring(0, 1));
		final int maxLength = Assembler.MAX_LABEL_LENGTH < s.length() ? Assembler.MAX_LABEL_LENGTH : s.length();
		if(m.matches()) {
			for(int i = 1; i < s.length(); i++) {
				m = labelChars.matcher(s.substring(i, i + 1));
				if(!m.matches()) {
					label = s.substring(0, i);
					break;
				} else {
					label = s.substring(0, i + 1);
				}
				
			}
		} 
		if(label != null && label.length() > maxLength) {
			label = label.substring(0, maxLength);
			
		}
		
		return label;
	}

}
