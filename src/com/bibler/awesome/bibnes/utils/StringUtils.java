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
	
	
	public static String insertStringAtIndex(int index, String toInsert, String baseString) {
		String ret = baseString;
		int diff = index - baseString.length();
		for(int i = 0; i < diff; i++) {
			ret += " ";
		}
		return ret + toInsert;
	}
	public static String intToPaddedString(int intToPad, int stringLength, int radix) {
		String digits = Integer.toString(intToPad, radix);
		int diff = stringLength - digits.length() - 1;
		String ret = "";
		for(int i = 0; i <= diff; i++) {
			ret += "0";
		}
		return (ret + digits).toUpperCase();
	}
	
	public static String trimComments(String s) {
		String ret = s;
		int index = s.indexOf(';');
		if(index >= 0) {
			ret = s.substring(0, index);
		}
		return ret;
	}
	
	public static String checkAddressPattern(String addressToCheck, String pattern) {
		String operand = "";
		int addressIndex = 0;
		char patternChar;
		char operandChar;
		String tmpAddress = addressToCheck.toLowerCase();
		for(int i = 0; i < pattern.length(); i++) {
			patternChar = pattern.charAt(i);
			if(patternChar == '~') {
				if(i == pattern.length() - 1) {
					operand = tmpAddress.substring(addressIndex, tmpAddress.contains(";") ? tmpAddress.indexOf(';') : tmpAddress.length());
					addressIndex += operand.length();
					break;
				} else {
					do {
						if(tmpAddress.length() == 0) {
							break;
						}
						operandChar = tmpAddress.charAt(addressIndex++);
						if(operandChar == ';') {
							break;
						}
						if(operandChar == pattern.charAt(i + 1)) {
							i++;
							break;
						} else {
							operand += operandChar;
						}
					} while(addressIndex < tmpAddress.length());
				}
			} else {
				if(addressIndex >= tmpAddress.length() || tmpAddress.charAt(addressIndex++) != patternChar ) {
					operand = null;
					break;
				}
			}
		}
		if(!validateEndOfLine(addressToCheck.substring(addressIndex))) {
			operand = null;
		}
		return operand;
	}
	
	private static boolean validateEndOfLine(String s) {
		boolean match = false;
		if(s.length() == 0 || s.trim().charAt(0) == ';') {
			match = true;
		}
		return match;
	}
	
	public static int stringToInt(String s, int radix) {
		return Integer.parseInt(s, radix);
	}
	
	public static byte[] stringToAsciiBytes(String s) {
		byte[] bytes = new byte[s.length()];
		for(int i = 0; i < s.length(); i++) {
			bytes[i] = (byte) s.charAt(i);
		}
		return bytes;
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
		while(s.length() > 2) {
			char c = s.charAt(0);
			if(c != '0') {
				break;
			}
			s = s.substring(1);
		}
		if(intToConvert == 0) {
			s = "00";
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
		return lineToTrim.replaceAll("\\s+", "").replaceAll("\t", "");
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
