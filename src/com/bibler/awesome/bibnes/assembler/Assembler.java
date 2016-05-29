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
	
	private int locationCounter;
	
	// Label Tabels
	private ArrayList<String> labels = new ArrayList<String>();
	private ArrayList<Integer> labelAddresses = new ArrayList<Integer>();
	
	//Equate Tables
	private ArrayList<String> variables = new ArrayList<String>();
	private ArrayList<Integer> variableValues = new ArrayList<Integer>();
	
	//Lines to check on Second Pass
	private ArrayList<Integer> secondPassLines = new ArrayList<Integer>();
	private ArrayList<Integer> secondPassAddress = new ArrayList<Integer>();
	
	private String[] linesToAssemble;
	
	
	String instruction;
	String addressString;
	int addressingMode;
	int opCode;
	int address;
	int bytes;
	int lineCount;
	Label operandLabel;
	
	public Assembler() {
		setByteSize(0x8000);
	}
	
	public void setByteSize(int byteSize) {
		machineCode = new Memory(byteSize);
	}
	
	public Memory passOne(String[] lines) {
		this.linesToAssemble = lines;
		lineCount = 0;
		for(String line : lines) {
			parseOpCode(line);
			lineCount++;
		}
		passTwo(lines);
		return machineCode;
	}
	
	private void passTwo(String[] lines) {
		String line;
		for(int i = 0; i < secondPassLines.size(); i++) {
			line = lines[secondPassLines.get(i)];
			locationCounter = secondPassAddress.get(i);
			parseOpCode(line);
		}
	}

	
	public void parseOpCode(String lineToParse) {
		String tmp = StringUtils.trimWhiteSpace(lineToParse);
		String label = StringUtils.checkLabel(lineToParse);
		operandLabel = null;
		if(label != null) {
			labels.add(label);
			labelAddresses.add(locationCounter);
			tmp = StringUtils.trimWhiteSpace(lineToParse.substring(label.length()));
		} 
		int directive = checkDirectives(tmp);
		if(directive >= 0) {
			processDirective(directive, tmp);
		} else {
			if(tmp.length() > 0 && matchOpCode(tmp)) {
				instruction = tmp.substring(0, 3);
				tmp = tmp.substring(3);
				processOpCode(instruction, tmp);
			}
		}
	}
	
	private int checkDirectives(String lineToCheck) {
		return AssemblyUtils.findDirective(lineToCheck);
	}
	
	private void processEquate(String s) {
		String tmp = StringUtils.trimWhiteSpace(s.substring(s.indexOf(".EQU") + 4));
		
		if(checkImmediate(tmp)) {
			labels.get(labels.size() - 1).setAddress(address);
		}
	}
	
	/**
	 * Processes the directive defined by the parameter. Directives are as follows:
	 * ALIGN: Moves program counter to next power-of-two boundary
	 * BYTE; DB: Places the byte defined in the operand in memory at the current location
	 * WORD; DW: Places the word, or list of words, defined in the operand in memory at the current location
	 * EQU: Assigns the value in the operand to the label
	 * FILL: Fills specified number of bytes with the character specified
	 * INC: Includes content of specified file into memory at location
	 * ORG: Sets program counter to number specified
	 * RS: Reserves specified amount of space (increments location counter by that number)
	 * @param directive
	 */
	
	private void processDirective(int directive, String line) {
		switch(directive) {
		case AssemblyUtils.ALIGN:
			break;
		case AssemblyUtils.BYTE:
		case AssemblyUtils.DB:
			break;
		case AssemblyUtils.WORD:
		case AssemblyUtils.DW:
			break;
		case AssemblyUtils.EQU:
			break;
		case AssemblyUtils.FILL:
			break;
		case AssemblyUtils.INC:
			break;
		case AssemblyUtils.ORG:
			break;
		case AssemblyUtils.RS:
			break;
			
		}
	}
	
	private void processOpCode(String instruction, String operand) {
		boolean match = false;
		for(int i = 0; i < AssemblyUtils.ADDRESS_MODE_COUNT; i++) {
			if(AssemblyUtils.checkForAddressMode(i, instruction)) {
				if(checkAddressMode(i, operand)) {
					match = true;
					addressingMode = i;
					break;
				}
			}
		}
		if(!match) {
			secondPassLines.add(lineCount);
			secondPassAddress.add(locationCounter);
			locationCounter += 3;
		} else {
			opCode = AssemblyUtils.getOpCode(instruction, addressingMode);
			bytes = AssemblyUtils.getBytes(opCode);
			machineCode.write(locationCounter++, opCode);
			if(address >= 0) {
				int[] operandBytes = DigitUtils.splitWord(address, bytes - 1);
				for(int i = operandBytes.length - 1; i >= 0; i--) {
					machineCode.write(locationCounter++, operandBytes[i]);
				}
			}
			
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
				}
			}
		}
		return match;
	}
	
	
	public boolean checkAddressMode(int addressModeToCheck, String operand) {
		boolean match = false;
		switch(addressModeToCheck) {
		case AssemblyUtils.ABSOLUTE:
		case AssemblyUtils.ABSOLUTE_X:
		case AssemblyUtils.ABSOLUTE_Y:
			match = checkAddressMode(operand, AssemblyUtils.getAddressModePattern(addressModeToCheck)) && address >= 0xFF && address <= 0xFFFF;
			break;
		case AssemblyUtils.ACCUMULATOR:
		case AssemblyUtils.IMMEDIATE:
		case AssemblyUtils.INDIRECT:
			match = checkAddressMode(operand, AssemblyUtils.getAddressModePattern(addressModeToCheck));
			break;
		case AssemblyUtils.IMPLIED:
			match = AssemblyUtils.checkImplied(operand);
			break;
		case AssemblyUtils.INDIRECT_X:
		case AssemblyUtils.INDIRECT_Y:
		case AssemblyUtils.ZERO_PAGE:
		case AssemblyUtils.ZERO_PAGE_X:
		case AssemblyUtils.ZERO_PAGE_Y:
		case AssemblyUtils.RELATIVE:
			match = checkAddressMode(operand, AssemblyUtils.getAddressModePattern(addressModeToCheck)) && address <= 0xFF;
			break;
		}
		return match;
	}
	
	public boolean checkAddressMode(String addressToCheck, String pattern) {
		boolean match = false;
		String operand = StringUtils.checkAddressPattern(addressToCheck, pattern);
		if(operand != null) {
			address = DigitUtils.getDigits(operand);
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
	
	public boolean checkImmediate(String addressToCheck) {
		boolean match = false;
		String tmp = ""; 
		String operandDigits;
		if(addressToCheck.length() > 0 && addressToCheck.charAt(0) == '#') {
			tmp = addressToCheck.substring(1);
			operandDigits = DigitUtils.getDigitString(tmp);
			if(operandDigits != null && StringUtils.validateLine(addressToCheck, addressToCheck.indexOf(operandDigits) + operandDigits.length() - 1)) {
				match = true;
				address = DigitUtils.getDigits(tmp);
				addressString = operandDigits;
			} else {
				match = false;
			}
		}
		return match;
	}
	
	public boolean checkZeroPage(String addressToCheck) {
		return checkAbsOrZp(addressToCheck, 0, 0xFF);
	}
	
	public boolean checkZeroPageX(String addressToCheck) {
		return checkAbsOrZpIndexed(addressToCheck, 'x', 0, 0xFF);
	}
	
	public boolean checkZeroPageY(String addressToCheck) {
		return checkAbsOrZpIndexed(addressToCheck, 'y', 0, 0xFF);
	}
	
	public boolean checkAbsolute(String addressToCheck) {
		return checkAbsOrZp(addressToCheck, 0xFF, 0xFFFF);
	}
	
	public boolean checkAbsoluteX(String addressToCheck) {
		return checkAbsOrZpIndexed(addressToCheck, 'x', 0xFF, 0xFFFF);
	}
	
	public boolean checkAbsoluteY(String addressToCheck) {
		return checkAbsOrZpIndexed(addressToCheck, 'y', 0xFF, 0xFFFF);
	}
	
	public boolean checkAbsOrZp(String addressToCheck, int lowerBounds, int upperBounds) {
		boolean match = false;
		String operandDigits;
		if(addressToCheck.length() > 0) {
			operandDigits = DigitUtils.getDigitString(addressToCheck);
			if(operandDigits != null && StringUtils.validateLine(addressToCheck, addressToCheck.indexOf(operandDigits) + operandDigits.length() - 1)) {
				match = true;
				address = DigitUtils.getDigits(addressToCheck);
				addressString = operandDigits;
				match = address >= lowerBounds && address <= upperBounds;
			} else {
				match = false;
			}
		}
		return match;
	}
	
	public boolean checkAbsOrZpIndexed(String addressToCheck, char indexToCheck, int lowerBounds, int upperBounds) {
		boolean match = false;
		String operandDigits;
		if(addressToCheck.length() > 0) {
			operandDigits = DigitUtils.getDigitString(addressToCheck);
			int lastDigitIndex = addressToCheck.indexOf(operandDigits) + operandDigits.length();
			if(operandDigits != null) {
				if(addressToCheck.charAt(lastDigitIndex) == ',') {
					if(addressToCheck.toLowerCase().charAt(lastDigitIndex + 1) == indexToCheck) {
						match = StringUtils.validateLine(addressToCheck, lastDigitIndex + 1);
						if(match) {
							address = DigitUtils.getDigits(addressToCheck);
							addressString = operandDigits;
							match = address >= lowerBounds && address <= upperBounds;
						}
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
		String operandDigits;
		int lastValidIndex;
		if(addressToCheck.charAt(0) == '(') {
			operandDigits = DigitUtils.getDigitString(addressToCheck.substring(1));
			lastValidIndex = addressToCheck.indexOf(operandDigits) + operandDigits.length();
			if(lastValidIndex < addressToCheck.length() && addressToCheck.charAt(lastValidIndex) == ')') {
				match = StringUtils.validateLine(addressToCheck, lastValidIndex);
				if(match) {
					address = DigitUtils.getDigits(addressToCheck.substring(1));
					addressString = operandDigits;
				}
			}
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
						operandLabel = l;
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
				match = StringUtils.validateLine(addressToCheck, addressToCheck.indexOf(')'));
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
					operandLabel = l;
					match = StringUtils.validateLine(addressToCheck, addressToCheck.indexOf(')'));
					addressingMode = AssemblyUtils.INDIRECT;
					addressString = StringUtils.intToHexString(address);
				}
			}
		}
		return match;
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
				address = address - (locationCounter + 2);
				if(address > 0xFF) {
					match = false;
				} else {
					operandLabel = l;
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
		if(locationCounter % 16 == 0) {
			if(locationCounter != 0) {
				listing.append("\n");
			}
			listing.append(StringUtils.intToHexString(locationCounter, 4));
			listing.append(" ");
		}
		listing.append(StringUtils.intToHexString(opCode, 2));
		listing.append(" ");
		machineCode.write(locationCounter, opCode);
		locationCounter++;
		
		final int endIndex = bytes - 1 <= addressString.length() ? bytes - 1 : addressString.length() - 1;
		for(int i = 0; i < endIndex; i++) {
			listing.append(addressString);
			addressString = addressString.substring(addressString.length() - 2);
			listing.append(" ");
			machineCode.write(locationCounter, (address >> ((bytes - 1) - i) * 8) & 0xFF);
			locationCounter++;
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
	
	public void printPassTwoLines() {
		for(int i = 0; i < secondPassLines.size(); i++) {
			System.out.println(linesToAssemble[secondPassLines.get(i)]);
		}
	}
	
	public void writeMachineCodeToFile(File f, Memory codeToWrite) {
		FileOutputStream stream = null;
		try {
			stream = new FileOutputStream(f);
			for(int i = 0; i < codeToWrite.size(); i++) {
				stream.write(codeToWrite.read(i));
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
