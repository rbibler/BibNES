package com.bibler.awesome.bibnes.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DigitUtils {
	
	public final static int DECIMAL = 10;
	public final static int HEX = 0x10;
	public final static int BIN = 2;
	
	static Pattern hexValues = Pattern.compile("[0-9[A-F][a-f]]");
	static Pattern binValues = Pattern.compile("[10]");
	static Pattern decimalValues = Pattern.compile("[0-9]");
	static Matcher m;
	
	public static String getDigitString(String s) {
		int radix = s.charAt(0) == '$' ? HEX : (s.charAt(0) == '%' ? BIN : DECIMAL);
		String tmp = s.substring(radix == 10 ? 0 : 1);
		int lastDigit = checkDigits(tmp, radix) + (radix == 10 ? 1 : 2);
		if(lastDigit > 0) {
			return s.substring(0,  lastDigit);
		}
		return null;
	}
	
	public static int getDigits(String s) {
		int radix = s.charAt(0) == '$' ? HEX : (s.charAt(0) == '%' ? BIN : DECIMAL);
		s = s.substring(radix == 10 ? 0 : 1);
		int lastDigit = checkDigits(s, radix);
		int returnNumber = -1;
		if(lastDigit >= 0) {
			returnNumber = Integer.parseInt(s.substring(0, lastDigit + 1), radix);
		}
		return returnNumber;
	}
	
	public static int checkDigits(String s, int radix) {
		Pattern p = decimalValues;
		switch(radix) {
			case DECIMAL:
				p = decimalValues;
				break;
			case HEX:
				p = hexValues;
				break;
			case BIN:
				p = binValues;
				break;
		}
		int lastIndex = -1;
		for(int i = 0; i < s.length(); i++) {
			m = p.matcher(s.substring(i, i + 1));
			if(!m.matches()) {
				break;
			} else {
				lastIndex = i;
			}
		}
		return lastIndex;
	}
	
	public static boolean checkFirstDigit(String s) {
		int radix = s.charAt(0) == '$' ? HEX : (s.charAt(0) == '%' ? BIN : DECIMAL);
		s = s.substring(radix == 10 ? 0 : 1);
		return checkDigit(s.charAt(0), radix);
	}
	
	public static boolean checkDigit(char c, int radix) {
		Pattern p = decimalValues;
		switch(radix) {
			case DECIMAL:
				p = decimalValues;
				break;
			case HEX:
				p = hexValues;
				break;
			case BIN:
				p = binValues;
				break;
		}
		m = p.matcher("" + c);
		return m.matches();
	}
	
	public static int[] splitWord(int wordToSplit, int bytes) {
		int[] retArray = new int[bytes];
		int bytesMinusOne = bytes - 1;
		for(int i = 0; i < bytes; i++) {
			retArray[i] = wordToSplit >> ((bytesMinusOne - i) * 8) & 0xFF;
		}
		return retArray;
	}

}
