package com.bibler.awesome.bibnes.assembler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.bibler.awesome.bibnes.systems.Memory;
import com.bibler.awesome.bibnes.utils.AssemblyUtils;
import com.bibler.awesome.bibnes.utils.DigitUtils;
import com.bibler.awesome.bibnes.utils.StringUtils;

public class Assembler {
	
	public static final int MAX_LABEL_LENGTH = 12;
	
	private StringBuilder listing = new StringBuilder();
	private Memory machineCode;
	private ArrayList<Label> labels = new ArrayList<Label>();
	private int[] linesToCheckOnSecondPass;
	private int[] pcAtLineToCheck;
	private int programCounter;
	
	
	String instruction;
	String addressString;
	int addressingMode;
	int opCode;
	int address;
	int bytes;
	int lineCount;
	
	public Assembler() {
		setByteSize(0x8000);
	}
	
	public void setByteSize(int byteSize) {
		machineCode = new Memory(byteSize);
	}
	
	public void passOne(String[] lines) {
		lineCount = 0;
		for(String line : lines) {
			parseOpCode(line);
			lineCount++;
		}
	}
	
	public void parseOpCode(String lineToParse) {
		String tmp;
		String label = StringUtils.checkLabel(lineToParse);
		if(label != null) {
			labels.add(new Label(label, programCounter));
			tmp = StringUtils.trimWhiteSpace(lineToParse.substring(label.length()));
		} else {
			tmp = StringUtils.trimWhiteSpace(lineToParse);
		}
		if(tmp.length() > 0 && matchOpCode(tmp)) {
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
		//String tmp = lineToParse.substring(0, lineToParse.contains(";") ? lineToParse.indexOf(';') : lineToParse.length());
		String tmp = lineToParse;
		if(checkRelative(tmp) && AssemblyUtils.checkForBranchInstruction(instruction)) {
			match = true;
		} else if(checkImmediate(tmp)) {
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
		} else if(checkIndirect(tmp)) {
			match = true;
		}
		return match;
	}
	
	/**
	 * Checks the string for a valid immediate operand. Checks for disambiguating operators '$' for Hex 
	 * and '%' for bin. If none found, assumes decimal mode. No labels allowed!
	 * @param addressToCheck - The string containing the presumed address;
	 * @return true if valid immediate operand found. False otherwise. 
	 */
	
	private boolean checkImmediate(String addressToCheck) {
		boolean match = false;
		String tmp = ""; 
		int lastIndex = -1;
		if(addressToCheck.length() > 0 && addressToCheck.charAt(0) == '#') {
			switch(addressToCheck.charAt(1)) {
				case '$':
					tmp = addressToCheck.substring(2);
					lastIndex = DigitUtils.checkDigits(tmp, DigitUtils.HEX);
					address = StringUtils.hexStringToInt(tmp.substring(0, lastIndex + 1));
					addressString = tmp;
					break;
				case '%':
					tmp = addressToCheck.substring(2);
					lastIndex = DigitUtils.checkDigits(tmp, DigitUtils.BIN);
					address = StringUtils.binStringToInt(tmp.substring(0, lastIndex + 1));
					addressString = StringUtils.intToHexString(address, 2);
					break;
				default:
					tmp = addressToCheck.substring(1);
					lastIndex = DigitUtils.checkDigits(tmp, DigitUtils.DECIMAL);
					address = Integer.parseInt(tmp.substring(0, lastIndex + 1));
					addressString = StringUtils.intToHexString(address, 2);
					break;
			}
		}
		if(lastIndex >= 0) {
			match = StringUtils.validateLine(tmp, lastIndex);
		} else {
			match = false;
		}
		return match;
	}
	
	public boolean checkZP(String addressToCheck) {
		boolean match = false;
		int index = getIndex(addressToCheck);
		if(index >= 0) {
			match = checkZPIndex(addressToCheck, index);
		} else {
			match = checkZeroPage(addressToCheck);
		}
		return match;
	}
	
	public boolean checkZeroPage(String addressToCheck) {
		boolean match = false;
		int radix = addressToCheck.charAt(0) == '$' ? DigitUtils.HEX : (addressToCheck.charAt(0) == '%' ? DigitUtils.BIN : DigitUtils.DECIMAL);
		String tmp = addressToCheck.substring(radix == DigitUtils.DECIMAL ? 0 : 1);
		int lastIndex = DigitUtils.checkDigits(tmp, radix);
		if(lastIndex >= 0) {
			address = StringUtils.stringToInt(tmp.substring(0,  lastIndex + 1), radix);
			if(address > 0xFF) {
				match = false;
			} else {
				match = StringUtils.validateLine(tmp, lastIndex);
				addressingMode = AssemblyUtils.ZERO_PAGE;
				addressString = tmp;
			}
		} else {
			Label l = checkLabels(tmp);
			if(l != null) {
				address = l.getAddress();
				if(address > 0xFF) {
					match = false;
				} else {
					match = StringUtils.validateLine(addressToCheck, l.getLength() - 1);
					addressingMode = AssemblyUtils.ZERO_PAGE;
					addressString = StringUtils.intToHexString(address);
				}
			}
		}
		return match;
	}
	
	public boolean checkZPIndex(String addressToCheck, int index) {
		boolean match = false;
		int radix;
		if(addressToCheck.charAt(index - 1) == ',') {
			radix = addressToCheck.charAt(0) == '$' ? DigitUtils.HEX : (addressToCheck.charAt(0) == '%' ? DigitUtils.BIN : DigitUtils.DECIMAL);
			String tmp = addressToCheck.substring(radix == DigitUtils.DECIMAL ? 0 : 1, addressToCheck.indexOf(','));
			int lastIndex = DigitUtils.checkDigits(tmp, radix);
			if(lastIndex >= 0) {
				address = StringUtils.stringToInt(tmp.substring(0, lastIndex + 1), radix);
				if(address > 0xFF) {
					match = false;
				} else {
					match = StringUtils.validateLine(addressToCheck, index);
					addressingMode = (addressToCheck.charAt(index) == 'Y' || addressToCheck.charAt(index) == 'y') ? AssemblyUtils.ZERO_PAGE_Y : AssemblyUtils.ZERO_PAGE_X;
					addressString = tmp;
				}
			} else {
				Label l = checkLabels(tmp);
				if(l != null) {
					address = l.getAddress();
					if(address > 0xFF) {
						match = false;
					} else {
						match = StringUtils.validateLine(addressToCheck, index);
						addressingMode = (addressToCheck.charAt(index) == 'Y' || addressToCheck.charAt(index) == 'y') ? AssemblyUtils.ZERO_PAGE_Y : AssemblyUtils.ZERO_PAGE_X;
						addressString = StringUtils.intToHexString(address);
					}
				}
			}
		}
		return match;
	}
	
	public boolean checkAbsolute(String addressToCheck) {
		boolean match = false;
		int index = getIndex(addressToCheck);
		if(index >= 0) {
			match = checkAbsoluteIndex(addressToCheck, index);
		} else {
			match = checkAbsoluteNoIndex(addressToCheck);
		}
		return match;
	}
	
	public boolean checkAbsoluteNoIndex(String addressToCheck) {
		boolean match = false;
		int radix = addressToCheck.charAt(0) == '$' ? DigitUtils.HEX : (addressToCheck.charAt(0) == '%' ? DigitUtils.BIN : DigitUtils.DECIMAL);
		String tmp = addressToCheck.substring(radix == DigitUtils.DECIMAL ? 0 : 1);
		int lastIndex = DigitUtils.checkDigits(tmp, radix);
		if(lastIndex >= 0) {
			address = StringUtils.stringToInt(tmp.substring(0, lastIndex + 1), radix);
			if(address > 0xFFFF || address < 0x100) {
				match = false;
			} else {
				match = StringUtils.validateLine(tmp, lastIndex);
				addressingMode = AssemblyUtils.ABSOLUTE;
				addressString = tmp;
			}
		} else {
			Label l = checkLabels(tmp);
			if(l != null) {
				address = l.getAddress();
				if(address > 0xFFFF || address < 0x100) {
					match = false;
				} else {
					match = StringUtils.validateLine(addressToCheck, l.getLength() - 1);
					addressingMode = AssemblyUtils.ABSOLUTE;
					addressString = StringUtils.intToHexString(address);
				}
			}
		}
		return match;
	}
	
	public boolean checkAbsoluteIndex(String addressToCheck, int index) {
		boolean match = false;
		int radix;
		if(addressToCheck.charAt(index - 1) == ',') {
			radix = addressToCheck.charAt(0) == '$' ? DigitUtils.HEX : (addressToCheck.charAt(0) == '%' ? DigitUtils.BIN : DigitUtils.DECIMAL);
			String tmp = addressToCheck.substring(radix == DigitUtils.DECIMAL ? 0 : 1, addressToCheck.indexOf(','));
			int lastIndex = DigitUtils.checkDigits(tmp, radix);
			if(lastIndex >= 0) {
				address = StringUtils.stringToInt(tmp.substring(0,  lastIndex + 1), radix);
				if(address > 0xFFFF || address < 0x100) {
					match = false;
				} else {
					match = StringUtils.validateLine(addressToCheck, index);
					addressingMode = (addressToCheck.charAt(index) == 'Y' || addressToCheck.charAt(index) == 'y') ? AssemblyUtils.ABSOLUTE_Y : AssemblyUtils.ABSOLUTE_X;
					addressString = tmp;
				}
			} else {
				Label l = checkLabels(tmp);
				if(l != null) {
					address = l.getAddress();
					if(address > 0xFFFF || address < 0x100) {
						match = false;
					} else {
						match = StringUtils.validateLine(addressToCheck, index);
						addressingMode = (addressToCheck.charAt(index) == 'Y' || addressToCheck.charAt(index) == 'y') ? AssemblyUtils.ABSOLUTE_Y : AssemblyUtils.ABSOLUTE_X;
						addressString = StringUtils.intToHexString(address);
					}
				}
			}
		}
		return match;
	}
	
	/**
	 * Validates whether an operand describes an Indirect addressing mode. 
	 * First examines string for an index (i.e. 'X' or 'Y') and calls the 
	 * appropriate helper function.
	 * @param addressToCheck - The string containing the operand
	 * @return True if operand describes an indirect addressing mode. False otherwise. 
	 */
	public boolean checkIndirect(String addressToCheck) {
		boolean match = false;
		int index = getIndex(addressToCheck);
		if(index >= 0) {
			match = checkIndirectIndex(addressToCheck, index);
		} else {
			match = checkIndirectNoIndex(addressToCheck);
		}
		return match;
	}
	
	/**
	 * Validates that the string represents either the Indexed Indirect or Indirect Indexed addressing  modes.
	 * Function parses the string to check for disambiguating identifiers: [(),].
	 * If appropriate identifiers are found, they are stripped and the remaining characters are checked for
	 * digit status according to any radix included in the string. 
	 * @param addressToCheck - String representing the operand to be checked.
	 * @param index - Location within the string of the index character (i.e. "X", "x", "Y", "y").
	 * @return True if the operand describes and indexed indirect address mode. False otherwise. 
	 */
	
	public boolean checkIndirectIndex(String addressToCheck, int index) {
		boolean match = false;
		//Store the index character for later comparison.
		char indexChar = addressToCheck.charAt(index);	
		// Stores the potential address mode after index character is checked. Primed at -1 for later validation.
		int potentialAddressMode = -1;
		int radix = -1;
		int lastValidChar = -1;
		String tmp = "";
		// Check if we have an "X" index
		if(indexChar == 'X' || indexChar == 'x') {
			// With whitespace trimmed, the ")" character must immediately follow the "X", and the "," character 
			// must immediately precede the X
			if(addressToCheck.length() > (index + 1) && addressToCheck.charAt(index + 1) == ')' && addressToCheck.charAt(index - 1) == ',') {
				//Trim everything but the (potential) address
				tmp = addressToCheck.substring(1, index - 1);
				// Set address mode now so we don't have to find it later.
				potentialAddressMode = AssemblyUtils.INDIRECT_X;
				lastValidChar = index + 1;
			} 
		} else if(indexChar == 'Y' || indexChar == 'y') {
			// With whitespace trimmed, the "," character must immediately precede the "Y", and the 
			// ")" must immediately precede the ",". 
			if(addressToCheck.charAt(index - 1) == ',' && addressToCheck.charAt(index - 2) == ')') {
				// Trim everything but the (potential) address
				tmp = addressToCheck.substring(1, index - 2);
				// Set address mode now so we don't have to find it later.
				potentialAddressMode = AssemblyUtils.INDIRECT_Y;
				lastValidChar = index;
			}
		}
		// If we found all our disambiguating identifiers, potential address will no longer be -1
		if(potentialAddressMode > 0) {
			// Check for a radix identifier to help with digit validation.
			radix = tmp.charAt(0) == '$' ? DigitUtils.HEX : (tmp.charAt(0) == '%' ? DigitUtils.BIN : DigitUtils.DECIMAL);
			// Trim radix identifier, if present
			tmp = tmp.substring(radix == DigitUtils.DECIMAL ? 0 : 1);
			int lastValidDigit = DigitUtils.checkDigits(tmp, radix);
			if(lastValidDigit >= 0) {
				// Convert string address to int
				address = StringUtils.stringToInt(tmp.substring(0, lastValidDigit + 1), radix);
				// Indexed indirect addresses can only be in Zero Page. 
				if(address > 0xFF) {
					match = false;
				} else {
					// Validate the rest of the line
					match = StringUtils.validateLine(addressToCheck, lastValidChar);
					// Set addressing mode using the mode we found earlier
					addressingMode = potentialAddressMode;
					addressString = tmp;
				}
			} else {
				Label l = checkLabels(tmp);
				if(l != null && potentialAddressMode != AssemblyUtils.INDIRECT_X) {
					address = l.getAddress();
					if(address > 0xFF) {
						match = false;
					} else {
						match = StringUtils.validateLine(addressToCheck, lastValidChar);
						addressingMode = potentialAddressMode;
						addressString = StringUtils.intToHexString(address);
					}
				}
			}
		}
		return match;
	}
	
	public boolean checkIndirectNoIndex(String addressToCheck) {
		boolean match = false;
		if(addressToCheck.charAt(0) != '(') {
			return false;
		}
		if(!addressToCheck.contains(")")) {
			return false;
		}
		String tmp = addressToCheck.substring(1, addressToCheck.indexOf(')'));
		int radix = tmp.charAt(0) == '$' ? DigitUtils.HEX : (tmp.charAt(0) == '%' ? DigitUtils.BIN : DigitUtils.DECIMAL);
		tmp = tmp.substring(radix == DigitUtils.DECIMAL ? 0 : 1);
		int lastValidDigit = DigitUtils.checkDigits(tmp, radix);
		if(lastValidDigit >= 0) {
			address = StringUtils.stringToInt(tmp.substring(0,  lastValidDigit + 1), radix);
			if(address > 0xFFFF) {
				match = false;
			} else {
				match = StringUtils.validateLine(tmp, lastValidDigit + 1);
				addressingMode = AssemblyUtils.INDIRECT;
				addressString = tmp;
			}
		} else {
			Label l = checkLabels(tmp);
			if(l != null) {
				address = l.getAddress();
				if(address > 0xFFFF) {
					match = false;
				} else {
					match = StringUtils.validateLine(addressToCheck, addressToCheck.indexOf(')'));
					addressingMode = AssemblyUtils.INDIRECT;
					addressString = StringUtils.intToHexString(address);
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
	
	public boolean checkRelative(String addressToCheck) {
		if(addressToCheck.length() <= 0) {
			return false;
		}
		boolean match = false;
		int radix = addressToCheck.charAt(0) == '$' ? DigitUtils.HEX : (addressToCheck.charAt(0) == '%' ? DigitUtils.BIN : DigitUtils.DECIMAL);
		String tmp = addressToCheck.substring(radix == DigitUtils.DECIMAL ? 0 : 1);
		int lastIndex = DigitUtils.checkDigits(tmp, radix);
		if(lastIndex >= 0) {
			address = StringUtils.stringToInt(tmp.substring(0,  lastIndex + 1), radix);
			if(address > 0xFF) {
				match = false;
			} else {
				match = StringUtils.validateLine(tmp, lastIndex);
				addressingMode = AssemblyUtils.RELATIVE;
				addressString = tmp;
			}
		} else {
			Label l = checkLabels(tmp);
			if(l != null) {
				address = l.getAddress();
				address = address - (programCounter + 2);
				if(address > 0xFF) {
					match = false;
				} else {
					match = StringUtils.validateLine(addressToCheck, l.getLength() - 1);
					addressingMode = AssemblyUtils.RELATIVE;
					addressString = StringUtils.intToHexString(address);
				}
			}
		}
		return match;
	}
	
	
	/**
	 * No Labels Allowed!
	 * @param addressToCheck
	 * @return
	 */
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
	
	/**
	 * No Labels Allowed!
	 * @param addressToCheck
	 * @return
	 */
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
	
	private Label checkLabels(String stringToCheck) {
		for(Label l : labels) {
			if(l.checkLabelAgainstString(stringToCheck)) {
				return l;
			}
		}
		return null;
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
	
	public ArrayList<Label> getLabels() {
		return labels;
	}
	
	public void addLabel(Label l) {
		labels.add(l);
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
