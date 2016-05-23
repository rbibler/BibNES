package com.bibler.awesome.bibnes.utils;

public class StringUtils {
	
	private static String[] hexValues = new String[] {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};
	
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

}
