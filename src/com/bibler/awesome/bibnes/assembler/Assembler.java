package com.bibler.awesome.bibnes.assembler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.bibler.awesome.bibnes.systems.Memory;
import com.bibler.awesome.bibnes.utils.AssemblyUtils;
import com.bibler.awesome.bibnes.utils.DigitUtils;
import com.bibler.awesome.bibnes.utils.StringUtils;

public class Assembler {
	
	
	private StringBuilder listing = new StringBuilder();
	private Memory machineCode;
	private int programCounter;
	
	
	String instruction;
	String addressString;
	int addressingMode;
	int opCode;
	int address;
	int bytes;
	
	public Assembler() {
		setByteSize(0x100);
	}
	
	public void setByteSize(int byteSize) {
		machineCode = new Memory(byteSize);
	}
	
	public void parseOpCode(String lineToParse) {
		String tmp = StringUtils.trimWhiteSpace(lineToParse);
		if(matchOpCode(tmp)) {
			tmp = tmp.substring(3);
			if(checkAddressingMode(tmp)) {
				opCode = findOpCode();
				processOpCode();
			}
		}
	}
	
	private void processOpCode() {
		machineCode.write(programCounter++, opCode);
		int[] operandBytes = DigitUtils.splitWord(address, AssemblyUtils.getBytes(opCode) - 1);
		for(int i = operandBytes.length - 1; i >= 0; i--) {
			machineCode.write(programCounter++, operandBytes[i]);
		}
	}
	
	/**
	 * Checks the first three characters of a string to find an instruction mnemonic.
	 * @param lineToParse - the string representing the presumptive instruction mnemonic
	 * @return - true if the string contains a mnemonic. False otherwise. 
	 */
	
	public boolean matchOpCode(String lineToParse) {
		boolean match = false;
		String tmp = lineToParse.replaceAll("\\s+", "");
		int firstSpaceChar = AssemblyUtils.findFirstSpaceChar(tmp.charAt(0));
		int secondSpaceChar = -1;
		int thirdSpaceChar = -1;
		if(firstSpaceChar >= 0) {
			secondSpaceChar = AssemblyUtils.findSecondSpaceChar(tmp.charAt(1), firstSpaceChar);
			if(secondSpaceChar >= 0) {
				thirdSpaceChar = AssemblyUtils.findThirdSpaceChar(tmp.charAt(2), firstSpaceChar, secondSpaceChar);
				if(thirdSpaceChar >= 0) {
					match = true;
					instruction = AssemblyUtils.getInstruction(firstSpaceChar, secondSpaceChar, thirdSpaceChar);
				}
			}
		}
		return match;
	}
	
	/**
	 * Checks a string to see if it contains a valid addressing mode.
	 * 
	 * @param lineToParse
	 * @return true if the string contains a valid address mode. False otherwise.
	 */
	
	public boolean checkAddressingMode(String lineToParse) {
		boolean match = false;
		String tmp = lineToParse.substring(0, lineToParse.contains(";") ? lineToParse.indexOf(';') : lineToParse.length());
		if(checkImmediate(tmp)) {
			match = true;
			addressingMode = AssemblyUtils.IMMEDIATE;
		} else if(checkAccumulator(tmp)) {
			match = true;
			addressingMode = AssemblyUtils.ACCUMULATOR;
		} else if(checkImplied(tmp)) {
			match = true;
			addressingMode = AssemblyUtils.IMPLIED;
		} else if(checkZP(tmp)) {
			match = true;
		} else if(checkAbsolute(tmp)) {
			match = true;
		}
		checkImmediate("");
		return match;
	}
	
	/**
	 * Checks the string for a valid immediate operand. Checks for disambiguating operators '$' for Hex 
	 * and '%' for bin. If none found, assumes decimal mode
	 * @param addressToCheck - The string containing the presumed address;
	 * @return true if valid immediate operand found. False otherwise. 
	 */
	
	private boolean checkImmediate(String addressToCheck) {
		boolean match = false;
		String tmp = ""; 
		if(addressToCheck.length() > 0 && addressToCheck.charAt(0) == '#') {
			switch(addressToCheck.charAt(1)) {
				case '$':
					tmp = addressToCheck.substring(2);
					match = DigitUtils.checkDigits(tmp, DigitUtils.HEX);
					address = StringUtils.hexStringToInt(tmp);
					addressString = tmp;
					break;
				case '%':
					tmp = addressToCheck.substring(2);
					match = DigitUtils.checkDigits(tmp, DigitUtils.BIN);
					address = StringUtils.binStringToInt(tmp);
					addressString = StringUtils.intToHexString(address, 2);
					break;
				default:
					tmp = addressToCheck.substring(1);
					match = DigitUtils.checkDigits(tmp, DigitUtils.DECIMAL);
					address = Integer.parseInt(tmp);
					addressString = StringUtils.intToHexString(address, 2);
					break;
			}
			
		}
		return match;
	}
	
	public boolean checkZP(String addressToCheck) {
		boolean match = false;
		int index = getIndex(addressToCheck);
		if(index >= 0) {
			match = checkZPIndex(addressToCheck.substring(0,index), addressToCheck.toLowerCase().contains("y"));
		} else {
			match = checkZeroPage(addressToCheck);
		}
		return match;
	}
	
	public boolean checkZeroPage(String addressToCheck) {
		boolean match = false;
		int radix = addressToCheck.charAt(0) == '$' ? DigitUtils.HEX : (addressToCheck.charAt(0) == '%' ? DigitUtils.BIN : DigitUtils.DECIMAL);
		String tmp = addressToCheck.substring(radix == DigitUtils.DECIMAL ? 0 : 1);
		match = DigitUtils.checkDigits(tmp, radix);
		if(match) {
			address = StringUtils.stringToInt(tmp, radix);
			if(address > 0xFF) {
				match = false;
			} else {
				addressingMode = AssemblyUtils.ZERO_PAGE;
				addressString = tmp;
			}
		}
		return match;
	}
	
	public boolean checkZPIndex(String addressToCheck, boolean yIndex) {
		boolean match = false;
		int radix;
		if(addressToCheck.charAt(addressToCheck.length() - 1) == ',') {
			radix = addressToCheck.charAt(0) == '$' ? DigitUtils.HEX : (addressToCheck.charAt(0) == '%' ? DigitUtils.BIN : DigitUtils.DECIMAL);
			String tmp = addressToCheck.substring(radix == DigitUtils.DECIMAL ? 0 : 1, addressToCheck.indexOf(','));
			match = DigitUtils.checkDigits(tmp, radix);
			if(match) {
				address = StringUtils.stringToInt(tmp, radix);
				if(address > 0xFF) {
					match = false;
				} else {
					addressingMode = yIndex ? AssemblyUtils.ZERO_PAGE_Y : AssemblyUtils.ZERO_PAGE_X;
					addressString = tmp;
				}
			}
		}
		return match;
	}
	
	public boolean checkAbsolute(String addressToCheck) {
		boolean match = false;
		int index = getIndex(addressToCheck);
		if(index >= 0) {
			match = checkAbsoluteIndex(addressToCheck.substring(0,index), addressToCheck.toLowerCase().contains("y"));
		} else {
			match = checkAbsoluteNoIndex(addressToCheck);
		}
		return match;
	}
	
	public boolean checkAbsoluteNoIndex(String addressToCheck) {
		boolean match = false;
		int radix = addressToCheck.charAt(0) == '$' ? DigitUtils.HEX : (addressToCheck.charAt(0) == '%' ? DigitUtils.BIN : DigitUtils.DECIMAL);
		String tmp = addressToCheck.substring(radix == DigitUtils.DECIMAL ? 0 : 1);
		match = DigitUtils.checkDigits(tmp, radix);
		if(match) {
			address = StringUtils.stringToInt(tmp, radix);
			if(address > 0xFFFF || address < 0x100) {
				match = false;
			} else {
				addressingMode = AssemblyUtils.ABSOLUTE;
				addressString = tmp;
			}
		}
		return match;
	}
	
	public boolean checkAbsoluteIndex(String addressToCheck, boolean yIndex) {
		boolean match = false;
		int radix;
		if(addressToCheck.charAt(addressToCheck.length() - 1) == ',') {
			radix = addressToCheck.charAt(0) == '$' ? DigitUtils.HEX : (addressToCheck.charAt(0) == '%' ? DigitUtils.BIN : DigitUtils.DECIMAL);
			String tmp = addressToCheck.substring(radix == DigitUtils.DECIMAL ? 0 : 1, addressToCheck.indexOf(','));
			match = DigitUtils.checkDigits(tmp, radix);
			if(match) {
				address = StringUtils.stringToInt(tmp, radix);
				if(address > 0xFFFF || address < 0x100) {
					match = false;
				} else {
					addressingMode = yIndex ? AssemblyUtils.ABSOLUTE_Y : AssemblyUtils.ABSOLUTE_X;
					addressString = tmp;
				}
			}
		}
		return match;
	}
	
	private int getIndex(String addressToCheck) {
		int index = addressToCheck.indexOf('X');
		if(index >= 0) {
			return index;
		} else {
			index = addressToCheck.indexOf('x');
			if(index >= 0) {
				return index;
			} else {
				index = addressToCheck.indexOf('Y');
				if(index >= 0) {
					return index;
				} else {
					index = addressToCheck.indexOf('y');
				}
			}
		}
		return index;
	}
	
	public boolean checkAccumulator(String addressToCheck) {
		boolean match = false;
		if(addressToCheck.length() > 0 && addressToCheck.charAt(0) == 'A') {
			if(addressToCheck.length() == 1 || addressToCheck.charAt(1) == ';') {
				match = true;
				address = -1;
				addressString = "";
			}
		}
		return match;
	}
	
	public boolean checkImplied(String addressToCheck) {
		boolean match = false;
		if(addressToCheck.length() == 0 || addressToCheck.charAt(0) == ';') {
			match = true;
			address = -1;
			addressString = "";
		}
		return match;
	}
	
	private int findOpCode() {
		return AssemblyUtils.getOpCode(instruction, addressingMode);
	}
	
	private void constructListing(int opCode, int bytes) {
		if(programCounter % 16 == 0) {
			if(programCounter != 0) {
				listing.append("\n");
			}
			listing.append(StringUtils.intToHexString(programCounter, 4));
			listing.append(" ");
		}
		listing.append(StringUtils.intToHexString(opCode, 2));
		listing.append(" ");
		machineCode.write(programCounter, opCode);
		programCounter++;
		
		final int endIndex = bytes - 1 <= addressString.length() ? bytes - 1 : addressString.length() - 1;
		for(int i = 0; i < endIndex; i++) {
			listing.append(addressString);
			addressString = addressString.substring(addressString.length() - 2);
			listing.append(" ");
			machineCode.write(programCounter, (address >> ((bytes - 1) - i) * 8) & 0xFF);
			programCounter++;
		}
	}
	
	public int getAddressMode() {
		return addressingMode;
	}
	
	public String getAddressString() {
		return StringUtils.intToHexString(address);
	}
	
	public int getAddress() {
		return address;
	}
	
	public int getOpCode() {
		return opCode;
	}
	
	public void printListing(File f) {
		System.out.println(listing.toString());
		writeMachineCodeToFile(f);
	}
	
	public void writeMachineCodeToFile(File f) {
		FileOutputStream stream = null;
		try {
			stream = new FileOutputStream(f);
			for(int i = 0; i < machineCode.size(); i++) {
				stream.write(machineCode.read(i));
			}
		} catch(IOException e) {}
		finally {
			if(stream != null) {
				try {
					stream.close();
				} catch(IOException e) {}
			}
		}
	}

}
