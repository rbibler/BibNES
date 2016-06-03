package com.bibler.awesome.bibnes.assembler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.bibler.awesome.bibnes.io.FileUtils;
import com.bibler.awesome.bibnes.systems.Memory;
import com.bibler.awesome.bibnes.utils.AssemblyUtils;
import com.bibler.awesome.bibnes.utils.DigitUtils;
import com.bibler.awesome.bibnes.utils.StringUtils;

public class Assembler {
	
	private File fileRoot;
	
	public static final int MAX_LABEL_LENGTH = 256;
	
	private StringBuilder listing = new StringBuilder();
	private Memory machineCode;
	
	private int locationCounter;
	
	// Label Tabels
	private ArrayList<String> labels = new ArrayList<String>();
	private ArrayList<Integer> labelAddresses = new ArrayList<Integer>();
	
	//Lines to check on Second Pass
	private ArrayList<Integer> secondPassLines = new ArrayList<Integer>();
	private ArrayList<Integer> secondPassAddress = new ArrayList<Integer>();
	private ArrayList<Integer> secondPassBanks = new ArrayList<Integer>();
	private ArrayList<Integer> secondPassBankSizes = new ArrayList<Integer>();
	
	private String[] linesToAssemble;
	
	private boolean  secondPass;
	
	
	String instruction;
	String addressString;
	int addressingMode;
	int opCode;
	int address;
	int bytes;
	int lineCount;
	int currentBank;
	int bankSize;
	
	private int inesPrgSize = 1;
	private int inesChrSize = 1;
	private int inesMapper;
	private int inesMirroring;
	
	public Assembler() {
		currentBank = 0;
		bankSize = AssemblyUtils.DEFAULT_BANK_SIZE;
		setByteSize(0x000);
	}
	
	public void setFileRoot(File fileRoot) {
		this.fileRoot = fileRoot;
	}
	
	public void setByteSize(int byteSize) {
		machineCode = new Memory(byteSize);
		for(int i = 0; i < machineCode.size(); i++) {
			machineCode.write(i, 0xFF);
		}
	}
	
	public Memory passOne(String[] lines) {
		checkForINESHeaderInfo(lines);
		secondPass = false;
		this.linesToAssemble = lines;
		lineCount = 0;
		for(String line : lines) {
			if(!line.trim().isEmpty()) {
				parseOpCode(line);
			}
			lineCount++;
		}
		passTwo(lines);
		return machineCode;
	}
	
	private void passTwo(String[] lines) {
		String line;
		secondPass = true;
		for(int i = 0; i < secondPassLines.size(); i++) {
			lineCount = secondPassLines.get(i);
			currentBank = secondPassBanks.get(i);
			bankSize = secondPassBankSizes.get(i);
			if(lineCount < lines.length) {
				line = lines[secondPassLines.get(i)];
				locationCounter = secondPassAddress.get(i);
				parseOpCode(line);
			} 
		}
	}
	
	private void checkForINESHeaderInfo(String[] lines) {
		for(String line : lines) {
			String directive = checkDirectives(line.trim());
			if(directive != null) {
				if(directive.contains("INES")) {
					processDirective(AssemblyUtils.getDirective(directive), 
							line.substring(line.toUpperCase().indexOf(directive) + directive.length()));
				}
			}
		}
		processHeaderInfo();
	}
	
	private void processHeaderInfo() {
		int machineCodeSize = (inesPrgSize * 0x8000) + (inesChrSize * 0x4000);
		setByteSize(machineCodeSize);
		byte[] NESBytes = StringUtils.stringToAsciiBytes("NES");
		int index = 0;
		for(int i = 0; i < NESBytes.length; i++) {
			machineCode.write(index++, NESBytes[i]);
		}
		machineCode.write(index++, 0x1A);
		machineCode.write(index++, inesPrgSize);
		machineCode.write(index++, inesChrSize);
		int byte6 = (inesMapper & 0xF) << 4;
		int byte7 = inesMapper & 0xF0;
		machineCode.write(index++, byte6);
		machineCode.write(index++, byte7);
		for(int i = 0; i < 8; i++) {
			machineCode.write(index++, 0);
		}
	}

	
	public void parseOpCode(String lineToParse) {
		String tmp = StringUtils.trimWhiteSpace(lineToParse);
		String label = StringUtils.checkLabel(lineToParse);
		int additionalLabelChars = 0;
		if(label != null) {
			if(label.charAt(label.length() - 1) == ':') {
				label = label.substring(0, label.length() - 1);
				additionalLabelChars = 1;
			}
			labels.add(label);
			labelAddresses.add(locationCounter);
			tmp = StringUtils.trimWhiteSpace(lineToParse.substring(label.length() + additionalLabelChars));
		} else if(lineToParse.charAt(0) != ' ' && lineToParse.charAt(0) != ';') {
			ErrorHandler.handleError(lineToParse, lineCount, ErrorHandler.ILLEGAL_LABEL);
		}
		if(tmp.length() == 0) {
			return;
		}
		String directive = checkDirectives(tmp);
		if(directive != null) {
			processDirective(AssemblyUtils.getDirective(directive), lineToParse.substring(lineToParse.toUpperCase().indexOf(directive) + directive.length()));
		} else { 
			if(tmp.charAt(0) == '.') {
				ErrorHandler.handleError(tmp, lineCount, ErrorHandler.ILLEGAL_DIRECTIVE);
			}
			if(tmp.length() > 0 && matchOpCode(tmp)) {
				instruction = tmp.substring(0, 3);
				tmp = tmp.substring(3);
				processOpCode(instruction, tmp);
			} else {
				tmp = tmp.trim();
				if(tmp.length() != 0) {
					if(tmp.charAt(0) != ';') {
						ErrorHandler.handleError(lineToParse, lineCount, ErrorHandler.NO_OP_CODE);
					}
				}
			}
		}
	}
	
	private String checkDirectives(String lineToCheck) {
		return AssemblyUtils.findDirective(lineToCheck);
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
		boolean error = false;
		String label = null;
		int labelAddress = -1;
		int errorCode = -1;
		switch(directive) {
		case AssemblyUtils.ALIGN:
			break;
		case AssemblyUtils.BYTE:
		case AssemblyUtils.DB:
			line = StringUtils.trimWhiteSpace(line);
			int byteToWrite;
			String[] bytesToCheck = StringUtils.trimWhiteSpace(line).split("[,]");
			if(bytesToCheck.length > 1) {
				for(String s : bytesToCheck) {
					byteToWrite = DigitUtils.getDigits(s);
					if(byteToWrite == -1) {
						label = StringUtils.checkLabel(s);
						if(label != null) {
							labelAddress = this.getLabelAddress(label);
							if(labelAddress == -1) {
								error = true;
								errorCode = ErrorHandler.MISSING_OPERAND;
							} else {
								machineCode.write((currentBank * bankSize) + (locationCounter & 0x1FFF) + 16, labelAddress);
								locationCounter++;
							}
						} else {
							error = true;
							errorCode = ErrorHandler.MISSING_OPERAND;
						}
					} else {
						machineCode.write((currentBank * bankSize) + (locationCounter & 0x1FFF) + 16, byteToWrite);
						locationCounter++;
					}
				}
			} else {
				byteToWrite = DigitUtils.getDigits(line);
				if(byteToWrite == -1) {
					label = StringUtils.checkLabel(line);
					if(label != null) {
						labelAddress = this.getLabelAddress(label);
						if(labelAddress != -1) {
							machineCode.write((currentBank * bankSize) + (locationCounter & 0x1FFF) + 16, labelAddress);
							locationCounter++;
						} else {
							error = true;
							errorCode = ErrorHandler.MISSING_OPERAND;
						}
					} else {
						error = true;
						errorCode = ErrorHandler.MISSING_OPERAND;
					}
				} else {
					machineCode.write(locationCounter + 16, byteToWrite);
					locationCounter++;
				}
			}
			break;
		case AssemblyUtils.WORD:
		case AssemblyUtils.DW:
			line = StringUtils.trimWhiteSpace(line);
			label = StringUtils.checkLabel(line);
			if(label != null) {
				labelAddress = this.getLabelAddress(label);
				if(labelAddress == -1) {
					error = true;
					errorCode = ErrorHandler.MISSING_OPERAND;
				} else {
					machineCode.write((currentBank * bankSize) + (locationCounter & 0x1FFF) + 16, labelAddress & 0xFF);
					locationCounter++;
					machineCode.write((currentBank * bankSize) + (locationCounter & 0x1FFF) + 16, labelAddress >> 8 & 0xFF);
					locationCounter++;
				}
			} else {
				int wordToWrite;
				String[] wordsToCheck = StringUtils.trimWhiteSpace(line).split("[,]");
				if(wordsToCheck.length > 0) {
					for(String s : wordsToCheck) {
						wordToWrite = DigitUtils.getDigits(s);
						if(wordToWrite == -1) {
							error = true;
							errorCode = ErrorHandler.MISSING_OPERAND;
						} else {
							machineCode.write((currentBank * bankSize) + (locationCounter & 0x1FFF) + 16, wordToWrite & 0xFF);
							locationCounter++;
							machineCode.write((currentBank * bankSize) + (locationCounter & 0x1FFF) + 16, wordToWrite >> 8 & 0xFF);
							locationCounter++;
						}
					}
				} else {
					wordToWrite = DigitUtils.getDigits(line);
					if(wordToWrite == -1) {
						error = true;
						errorCode = ErrorHandler.MISSING_OPERAND;
					} else {
						machineCode.write((currentBank * bankSize) + (locationCounter & 0x1FFF) + 16, wordToWrite & 0xFF);
						locationCounter++;
						machineCode.write((currentBank * bankSize) + (locationCounter & 0x1FFF) + 16,  wordToWrite >> 8 & 0xFF);
						locationCounter++;
					}
				}
			}
			break;
		case AssemblyUtils.EQU:
			line = StringUtils.trimWhiteSpace(line);
			int value = processExpression(line);
			if(value == -1) {
				error = true;
				errorCode = ErrorHandler.MISSING_OPERAND;
			} else {
				labelAddresses.set(labelAddresses.size() - 1, value);
			}
			break;
		case AssemblyUtils.FILL:
			line = StringUtils.trimWhiteSpace(line);
			String[] params = StringUtils.trimWhiteSpace(line).split("[,]");
			if(params.length > 0) {
				int bytesToFill = DigitUtils.getDigits(params[0]);
				int fillByte = DigitUtils.getDigits(params[1]);
				if(fillByte <= 0xFF) {
					if(bytesToFill + locationCounter < machineCode.size()) {
						for(int i = 0; i < bytesToFill; i++) {
							machineCode.write((currentBank * bankSize) + (locationCounter & 0x1FFF) + 16, fillByte);
							locationCounter++;
						}
					} else {
						error = true;
						errorCode = ErrorHandler.OVERFLOW;
					}
				} else {
					error = true;
					errorCode = ErrorHandler.OPERAND_TOO_LARGE;
				}
			}
			break;
		case AssemblyUtils.INC:
			String s = line.substring(line.indexOf('"') + 1, line.lastIndexOf('"')).trim();
			File f = null;
			if(s.charAt(0) == 'C' || s.charAt(0) == 'c') {
				f = new File(s);
			} else {
				f = new File(fileRoot.getAbsolutePath() + "/" + s);
			}
			if(f.exists()) {
				byte[] fileBytes = FileUtils.readFile(f);
				for(Byte fileByte : fileBytes) {
					machineCode.write((currentBank * bankSize) + (locationCounter & 0x1FFF) + 16, fileByte);
					locationCounter++;
					if(locationCounter >= machineCode.size()) {
						error = true;
						errorCode = ErrorHandler.OVERFLOW;
						break;
					}
				}
			} else {
				error = true;
				errorCode = ErrorHandler.FILE_NOT_FOUND;
			}
			break;
		case AssemblyUtils.ORG:
			line = StringUtils.trimWhiteSpace(line);
			int newLocation = DigitUtils.getDigits(line);
			if(newLocation >= 0) {
				locationCounter = newLocation;
			} else {
				error = true;
				errorCode = ErrorHandler.MISSING_OPERAND;
			}
			break;
		case AssemblyUtils.RS:
			line = StringUtils.trimWhiteSpace(line);
			int bytesToSkip = DigitUtils.getDigits(line);
			if(bytesToSkip >= 0) {
				locationCounter += bytesToSkip;
			} else {
				error = true;
				errorCode = ErrorHandler.MISSING_OPERAND;
			}
			break;
		case AssemblyUtils.BS:
			line = StringUtils.trimWhiteSpace(line);
			bankSize = DigitUtils.getDigits(line);
			if(bankSize != -1) {
				bankSize *= AssemblyUtils.DEFAULT_BANK_SIZE;
			} else {
				error = true;
				errorCode = ErrorHandler.MISSING_OPERAND;
			}
			break;
		case AssemblyUtils.BANK:
			line = StringUtils.trimWhiteSpace(line);
			int bank = DigitUtils.getDigits(line);
			if(bank != -1) {
				currentBank = bank;
				locationCounter = bank * bankSize;
			} else {
				error = true;
				errorCode = ErrorHandler.MISSING_OPERAND;
			}
			break;
		case AssemblyUtils.INES_PRG:
			line = StringUtils.trimWhiteSpace(line);
			int prg = DigitUtils.getDigits(line);
			if(prg != -1) {
				inesPrgSize = prg;
			} else {
				error = true;
				errorCode = ErrorHandler.MISSING_OPERAND;
			}
			break;
		case AssemblyUtils.INES_CHR:
			line = StringUtils.trimWhiteSpace(line);
			int chr = DigitUtils.getDigits(line);
			if(chr != -1) {
				inesChrSize = chr;
			} else {
				error = true;
				errorCode = ErrorHandler.MISSING_OPERAND;
			}
			break;
		case AssemblyUtils.INES_MAP:
			line = StringUtils.trimWhiteSpace(line);
			int map = DigitUtils.getDigits(line);
			if(map != -1) {
				inesMapper = map;
			} else {
				error = true;
				errorCode = ErrorHandler.MISSING_OPERAND;
			}
			break;
		case AssemblyUtils.INES_MIRROR:
			break;
		}
		if(error) {
			ErrorHandler.handleError(line, lineCount, errorCode);
		}
	}
	
	private int processExpression(String expression) {
		return DigitUtils.getDigits(expression);
	}
	
	private void processOpCode(String instruction, String operand) {
		boolean match = false;
		bytes = 3;
		if(instruction.equals("JMP")) {
			System.out.println("JUMP!");
		}
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
			if(secondPass){
				ErrorHandler.handleError(instruction + " " + operand, lineCount, ErrorHandler.MISSING_OPERAND);
			} else {
				secondPassLines.add(lineCount);
				secondPassAddress.add(locationCounter);
				secondPassBanks.add(currentBank);
				secondPassBankSizes.add(bankSize);
				locationCounter += bytes;
			}
		} else {
			System.out.println(instruction + " " + AssemblyUtils.getAddressModeName(addressingMode) + " " + StringUtils.intToHexString(address));
			opCode = AssemblyUtils.getOpCode(instruction, addressingMode);
			if(opCode == 0x10) {
				System.out.println("BPL");
			}
			bytes = AssemblyUtils.getBytes(opCode);
			machineCode.write((currentBank * bankSize) + (locationCounter & 0x1FFF) + 16, opCode);
			locationCounter++;
			//if(address >= 0) {
				int[] operandBytes = DigitUtils.splitWord(address, bytes - 1);
				for(int i = operandBytes.length - 1; i >= 0; i--) {
					machineCode.write((currentBank * bankSize) + (locationCounter & 0x1FFF) + 16, operandBytes[i]);
					locationCounter++;
				}
			//}
			
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
			match = checkAddressMode(operand, AssemblyUtils.getAddressModePattern(addressModeToCheck))  ;
			break;
		case AssemblyUtils.ACCUMULATOR:
		case AssemblyUtils.IMMEDIATE:
			match = checkAddressMode(operand, AssemblyUtils.getAddressModePattern(addressModeToCheck));
			break;
		case AssemblyUtils.INDIRECT:
			match = checkAddressMode(operand, AssemblyUtils.getAddressModePattern(addressModeToCheck));
			break;
		case AssemblyUtils.IMPLIED:
			match = AssemblyUtils.checkImplied(operand);
			bytes = 1;
			break;
		case AssemblyUtils.INDIRECT_X:
		case AssemblyUtils.INDIRECT_Y:
		case AssemblyUtils.ZERO_PAGE:
		case AssemblyUtils.ZERO_PAGE_X:
		case AssemblyUtils.ZERO_PAGE_Y:
			match = checkAddressMode(operand, AssemblyUtils.getAddressModePattern(addressModeToCheck)) && address <= 0xFF;
			break;
		case AssemblyUtils.RELATIVE:
			match = checkAddressMode(operand, AssemblyUtils.getAddressModePattern(addressModeToCheck));
			if(match) {
				//address = (address - (locationCounter + (address < locationCounter ? 2 : 1)));j
				address = address - (locationCounter + 2);
				if(Math.abs(address) <= 0xFF) {
					address = (byte) (address & 0xFF);
				} else {
					ErrorHandler.handleError(operand, lineCount, ErrorHandler.JUMP_OUT_OF_RANGE);
				}
			}
			bytes = 2; 
			break;
		}
		return match;
	}
	
	public boolean checkAddressMode(String addressToCheck, String pattern) {
		boolean match = false;
		String operand = StringUtils.checkAddressPattern(addressToCheck, pattern);
		if(operand != null) {
			if(operand.charAt(0) == 'L') {
				match = checkLabelsForAddress(operand.substring(1));
			} else {
				address = DigitUtils.getDigits(operand);
				match = true;
			}
		}
		return match;
	}
	
	private boolean checkLabelsForAddress(String labelToCheck) {
		for(int i = 0; i < labels.size(); i++) {
			if(labels.get(i).equals(labelToCheck)) {
				address = labelAddresses.get(i);
				return true;
			}
		}
		return false;
	}
	
	
	private int findOpCode() {
		return AssemblyUtils.getOpCode(instruction, addressingMode);
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
		machineCode.write(locationCounter + 16, opCode);
		locationCounter++;
		
		final int endIndex = bytes - 1 <= addressString.length() ? bytes - 1 : addressString.length() - 1;
		for(int i = 0; i < endIndex; i++) {
			listing.append(addressString);
			addressString = addressString.substring(addressString.length() - 2);
			listing.append(" ");
			machineCode.write(locationCounter + 16, (address >> ((bytes - 1) - i) * 8) & 0xFF);
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
	
	public int getLocationCounter() {
		return locationCounter;
	}
	
	public int getByteAt(int address) {
		return machineCode.read(address);
	}
	
	public int getLabelAddress(String label) {
		int index = labels.indexOf(label);
		if(index >= 0) {
			index = labelAddresses.get(index);
		}
		return index;
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
