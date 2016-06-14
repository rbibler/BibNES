package com.bibler.awesome.bibnes.assembler;

import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

import com.bibler.awesome.bibnes.assembler.directives.Bank;
import com.bibler.awesome.bibnes.assembler.directives.BankSize;
import com.bibler.awesome.bibnes.assembler.directives.DefineByte;
import com.bibler.awesome.bibnes.assembler.directives.DefineWord;
import com.bibler.awesome.bibnes.assembler.directives.Directive;
import com.bibler.awesome.bibnes.assembler.directives.Equate;
import com.bibler.awesome.bibnes.assembler.directives.Fill;
import com.bibler.awesome.bibnes.assembler.directives.INESCHR;
import com.bibler.awesome.bibnes.assembler.directives.INESMAP;
import com.bibler.awesome.bibnes.assembler.directives.INESPRG;
import com.bibler.awesome.bibnes.assembler.directives.Include;
import com.bibler.awesome.bibnes.assembler.directives.IncludeBinary;
import com.bibler.awesome.bibnes.assembler.directives.Origin;
import com.bibler.awesome.bibnes.assembler.directives.ReserveSpace;
import com.bibler.awesome.bibnes.assembler.directives.ReserveSpaceSet;
import com.bibler.awesome.bibnes.communications.Notifiable;
import com.bibler.awesome.bibnes.communications.Notifier;
import com.bibler.awesome.bibnes.systems.Memory;
import com.bibler.awesome.bibnes.utils.AssemblyUtils;
import com.bibler.awesome.bibnes.utils.DigitUtils;
import com.bibler.awesome.bibnes.utils.StringUtils;

public class Assembler implements Notifiable, Notifier{
	
	private File fileRoot;
	
	public static final int MAX_LABEL_LENGTH = 256;
	
	private String[] listing;
	private StringBuilder currentLine = new StringBuilder();
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
	
	private Directive[] directives;
	
	private String[] linesToAssemble;
	
	private boolean  secondPass;

	private ArrayList<Notifiable> objectsToNotify = new ArrayList<Notifiable>();
	
	String instruction;
	String addressString;
	int addressingMode;
	int opCode;
	int address;
	int bytes;
	int lineCount;
	int currentBank;
	int bankSize;
	int rsCounter;
	
	public Assembler() {
		currentBank = 0;
		bankSize = AssemblyUtils.DEFAULT_BANK_SIZE;
		setByteSize(0x8000);
		fillDirectives();
	}
	
	public void registerObjectToNotify(Notifiable objectToNotify) {
		if(!objectsToNotify.contains(objectToNotify)) {
			objectsToNotify.add(objectToNotify);
		}
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
	
	public void fillDirectives() {
		directives = new Directive[] { null,
				new DefineByte(this), new DefineByte(this), new DefineWord(this), new Equate(this), new Fill(this),
				new Include(this), new IncludeBinary(this), new Origin(this), new ReserveSpaceSet(this), new ReserveSpace(this),
				new DefineWord(this), new BankSize(this), new Bank(this), new INESPRG(this), new INESCHR(this), 
				new INESMAP(this), new Directive(this)
		};
	}
	
	public Memory passOne(String[] lines) {
		this.linesToAssemble = lines;
		listing = new String[lines.length];
		secondPass = false;
		lineCount = 0;
		String line;
		for(int i = 0; i < linesToAssemble.length; i++) {
			if(i == 735) {
				System.out.println("Weird");
			}
			line = linesToAssemble[i];
			if(line != null && !line.trim().isEmpty()) {
				parseLine(line);
			}
			lineCount++;
		}
		passTwo(linesToAssemble);
		notify("DONE");
		return machineCode;
	}
	
	private void passTwo(String[] lines) {
		System.out.println("PASS TWO!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		String line;
		secondPass = true;
		for(int i = 0; i < secondPassLines.size(); i++) {
			lineCount = secondPassLines.get(i);
			currentBank = secondPassBanks.get(i);
			bankSize = secondPassBankSizes.get(i);
			if(lineCount < lines.length) {
				line = lines[secondPassLines.get(i)];
				locationCounter = secondPassAddress.get(i);
				parseLine(line);
			} 
		}
	}

	/**
	 * Scans a line of text, processing labels, directives, opcodes, and operands.
	 * 
	 * @param lineToParse
	 */
	public void parseLine(String lineToParse) {
		int errorCode = -1;
		String tmp = StringUtils.trimWhiteSpace(lineToParse);
		errorCode = processLabel(lineToParse);
		if(errorCode == -1) {											
			tmp = trimLineAfterLabel(lineToParse);
		} else if(errorCode != -1 && !confirmFirstCharEmpty(lineToParse)){
			ErrorHandler.handleError(tmp, lineCount, errorCode);
		}
		if(tmp.length() == 0) {
			return;
		}
		errorCode = processDirective(tmp); 
		if(errorCode != -1 && tmp.charAt(0) == '.') {
			ErrorHandler.handleError(tmp, lineCount, errorCode);
		}
		errorCode = processOpCode(tmp);
		if(errorCode == -1) {
			String lineAndLocation = StringUtils.intToPaddedString(lineCount, 6, DigitUtils.DECIMAL).toUpperCase();
			lineAndLocation += " " + StringUtils.intToPaddedString(locationCounter, 4, DigitUtils.HEX).toUpperCase() + " ";
			lineAndLocation += currentLine.toString();
			listing[lineCount] = StringUtils.insertStringAtIndex(26, lineToParse, lineAndLocation);
			currentLine.delete(0, currentLine.length());
		}
	}
	
	public boolean confirmFirstCharEmpty(String line) {
		char firstChar = line.charAt(0);
		return firstChar == ' ' || firstChar == ';' || firstChar == '\t';
	}
	
	public int processLabel(String lineToProcess) {
		int errorCode = -1;
		String label = StringUtils.checkLabel(lineToProcess);
		if(label != null) {
			if(label.charAt(label.length() - 1) == ':') {
				label = label.substring(0, label.length() - 1);
			}
			labels.add(label);
			labelAddresses.add(locationCounter);
			currentLine.append(label);
		} else if(lineToProcess.charAt(0) != ' ' && lineToProcess.charAt(0) != ';') {
			errorCode = ErrorHandler.ILLEGAL_LABEL;
		} else {
			errorCode = -2;
		}
		return errorCode;
	}
	
	public String trimLineAfterLabel(String lineToTrim) {
		String tmp = lineToTrim;
		String label = getLastLabel();
		if(label != null) {
			tmp = lineToTrim.substring(lineToTrim.indexOf(label) + label.length());
			if(tmp.length() > 0 && tmp.charAt(0) == ':') {
				tmp = tmp.substring(1);
			}
		}
		return StringUtils.trimWhiteSpace(tmp);
	}
	
	public int processDirective(String directiveToProcess) {
		int returnCode = -1;
		String directive = checkDirectives(directiveToProcess);
		if(directive != null) {
			returnCode = processDirective(AssemblyUtils.getDirective(directive), 
					directiveToProcess.substring(directiveToProcess.toUpperCase().indexOf(directive) + directive.length()));
		}
		return returnCode;
		
	}
	
	
	String checkDirectives(String lineToCheck) {
		return AssemblyUtils.findDirective(lineToCheck);
	}
	
	int processDirective(int directive, String line) {
		return directives[directive].processDirective(line);
	}
	
	public int processOpCode(String opCodeToProcess) {
		int errorCode = -1;
		if(opCodeToProcess.length() > 0 && matchOpCode(opCodeToProcess.toUpperCase())) {
			instruction = opCodeToProcess.substring(0, 3).toUpperCase();
			opCodeToProcess = opCodeToProcess.substring(3);
			errorCode = processOpCode(instruction, opCodeToProcess);
		} else {
			opCodeToProcess = opCodeToProcess.trim();
			if(opCodeToProcess.length() != 0) {
				if(opCodeToProcess.charAt(0) != ';') {
					errorCode = ErrorHandler.NO_OP_CODE;
				}
			}
		}
		return errorCode;
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
	
	private int processOpCode(String instruction, String operand) {
		int errorCode = -1;
		boolean foundAddressMode = false;
		bytes = 3;
		foundAddressMode = checkForAddressMode(instruction, operand);
		if(!foundAddressMode) {
			errorCode = addToSecondPass(instruction, operand);
		} else {
			writeOpCode(instruction);
		}
		return errorCode;
	}

	private void writeOpCode(String instruction) {
		System.out.println(instruction + " " + AssemblyUtils.getAddressModeName(addressingMode) + " " + StringUtils.intToHexString(address));
		opCode = AssemblyUtils.getOpCode(instruction, addressingMode);
		bytes = AssemblyUtils.getBytes(opCode);
		writeCurrentLocationAndBank(opCode);
		currentLine.append(StringUtils.intToHexString(opCode));
		int[] operandBytes = DigitUtils.splitWord(address, bytes - 1);
		for(int i = operandBytes.length - 1; i >= 0; i--) {
			currentLine.append(" " + StringUtils.intToHexString(operandBytes[i]));
			writeCurrentLocationAndBank(operandBytes[i]);
		}
		
	}

	private int addToSecondPass(String instruction, String operand) {
		int errorCode = -1;
		if(secondPass){
			errorCode = ErrorHandler.MISSING_OPERAND;
		} else {
			System.out.println(instruction + " bytes " + bytes + " Deferred to 2nd Pass");
			secondPassLines.add(lineCount);
			secondPassAddress.add(locationCounter);
			secondPassBanks.add(currentBank);
			secondPassBankSizes.add(bankSize);
			locationCounter += bytes;
		}
		return errorCode;
	}

	private boolean checkForAddressMode(String instruction, String operand) {
		boolean foundAddressMode = false;
		for(int i = 0; i < AssemblyUtils.ADDRESS_MODE_COUNT; i++) {
			if(AssemblyUtils.checkForAddressMode(i, instruction)) {
				if(checkAddressMode(i, operand)) {
					foundAddressMode = true;
					addressingMode = i;
					break;
				}
			}
		}
		return foundAddressMode;
	}
	
	public void writeCurrentLocationAndBank(int dataToWrite) {
		int addressToWrite = (currentBank * bankSize) + (locationCounter & 0x1FFF);
		machineCode.write(addressToWrite, dataToWrite);
		locationCounter++;
	}
	
	public boolean checkAddressMode(int addressModeToCheck, String operand) {
		boolean match = false;
		switch(addressModeToCheck) {
		case AssemblyUtils.ABSOLUTE:
		case AssemblyUtils.ABSOLUTE_X:
			match = checkAddressMode(operand, AssemblyUtils.getAddressModePattern(addressModeToCheck)) && address >= 0x100;
			break;
		case AssemblyUtils.ABSOLUTE_Y:
			match = checkAddressMode(operand, AssemblyUtils.getAddressModePattern(addressModeToCheck)) && checkForNoZeroPage(AssemblyUtils.ZERO_PAGE_Y);
			break;
		case AssemblyUtils.ACCUMULATOR:
			char first = operand.length() > 0 ? operand.charAt(0) : ' ';
			if(first == 'A' || first == 'a') {
				match = StringUtils.validateLine(operand, 0);
			}
			break;
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
				int additive = address < 0 ? 1 : 2;
				address = address - (locationCounter + additive);
				if(Math.abs(address) <= 0xFF) {
					address = (int) (address & 0xFF);					//Convert negative into positive 
				} else {
					ErrorHandler.handleError(operand, lineCount, ErrorHandler.JUMP_OUT_OF_RANGE);
				}
			}
			bytes = 2; 
			break;
		}
		return match;
	}
	
	private boolean checkForNoZeroPage(int zpToCheck) {
		if(AssemblyUtils.noZeroPage(instruction, zpToCheck)) {
			return true;
		} else {
			return address >= 0x100;
		}
	}
	
	public boolean checkAddressMode(String addressToCheck, String pattern) {
		boolean match = false;
		String operand = StringUtils.checkAddressPattern(addressToCheck, pattern);
		if(operand != null) {
			match = processOperand(operand);
		}
		return match;
	}
	
	public boolean processOperand(String operand) {
		if(instruction == "JMP" && secondPass) {
			System.out.println("Stopping here");
		}
		boolean match = false;
		if(DigitUtils.stringContainsOnlyDigits(operand)) {
			address = DigitUtils.getDigits(operand);
			match = true;
		} else {
			match = checkForLabel(operand);
			if(!match) {
				match = processExpression(operand);
			}
		} 
		return match;
	}
	
	public boolean processExpression(String expression) {
		boolean match = false;
		match = checkHigh(expression);
		if(!match) {
			match = checkLow(expression);
			if(!match) {
				match = checkMathExpression(expression);
			}
		}
		return match;
	}
	
	private boolean checkHigh(String expression) {
		boolean match = false;
		String operand = "";
		if(expression.startsWith("HIGH(")) {
			bytes = 2;
			for(int i = 5; i < expression.length(); i++) {
				if(expression.charAt(i) == ')') {
					break;
				} else {
					operand += expression.charAt(i);
				}
			}
			if(operand.charAt(0) == '#') {
				operand = operand.substring(1);
			}
			match = processOperand(operand);
			if(match) {
				address = address >> 8 & 0xFF;
			}
		}
		return match;
	}
	
	private boolean checkLow(String expression) {
		boolean match = false;
		String operand = "";
		if(expression.startsWith("LOW(")) {
			for(int i = 4; i < expression.length(); i++) {
				if(expression.charAt(i) == ')') {
					break;
				} else {
					operand += expression.charAt(i);
				}
			}
			if(operand.charAt(0) == '#') {
				operand = operand.substring(1);
			}
			match = processOperand(operand);
			if(match) {
				address = address & 0xFF;
			}
		}
		return match;
	}
	
	private boolean checkMathExpression(String expression) {
		if(expression.contains("+")) {
			String label = expression.substring(0, expression.indexOf('+'));
			if(checkForLabel(label)) {
				String operand = expression.substring(expression.indexOf('+') + 1);
				operand = StringUtils.trimComments(StringUtils.trimWhiteSpace(operand));
				int digits = DigitUtils.getDigits(operand);
				address += digits;
				return true;
			}
		}
		return false;
	}
	
	private boolean checkForLabel(String labelToCheck) {
		int index = checkLabelContains(labelToCheck);
		if(index != -1) {
			address = labelAddresses.get(index);
			return true;
		} else {
			address = -1;
			return false;
		}
	}
	
	private int checkLabelContains(String labelToCheck) {
		int index = -1;
		int count = 0;
		for(String s : labels) {
			if(s.equalsIgnoreCase(labelToCheck)) {
				index = count; 
				break;
			}
			count++;
		}
		return index;
	}
	
	public boolean checkForLocationOverflow() {
		return getLocationCounterAndBank() >= getRomSize();
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
	
	public void setLinesToAssemble(String[] linesToAssemble) {
		this.linesToAssemble = linesToAssemble;
	}
	
	public void setLocationCounter(int locationCounter) {
		this.locationCounter = locationCounter;
	}
	
	public void setBankSize(int bankSize) {
		this.bankSize = bankSize;
	}
	
	public int getBankSize() {
		return bankSize;
	}
	
	public void setCurrentBank(int currentBank) {
		this.currentBank = currentBank;
	}
	
	public void setRSCounter(int rsCounter) {
		this.rsCounter = rsCounter;
	}
	
	public int getRSCounter() {
		return rsCounter;
	}
	
	public void setLabelAddress(int labelAddressIndex, int address) {
		labelAddresses.set(labelAddressIndex, address);
	}
	
	public String getLastLabel() {
		return labels.size() > 0 ? labels.get(labels.size() - 1) : null;
	}
	
	public int getLastLabelAddress() {
		return labelAddresses.size() > 0 ? labelAddresses.get(labelAddresses.size() - 1) : -1;
	}
	
	public int getLabelCount() {
		return labelAddresses.size();
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
	
	public int getLineCount() {
		return lineCount;
	}
	
	public String[] getLinesToAssemble() {
		return linesToAssemble;
	}
	
	public int getLocationCounter() {
		return locationCounter;
	}
	
	public int getLocationCounterAndBank() {
		return (currentBank * bankSize) + (locationCounter & 0x1FFF);
	}
	
	public int getRomSize() {
		return machineCode.size();
	}
	
	public int getByteAt(int address) {
		return machineCode.read(address);
	}
	
	public File getFileRoot() {
		return fileRoot;
	}
	
	public int getLabelAddress(String label) {
		int index = labels.indexOf(label);
		if(index >= 0) {
			index = labelAddresses.get(index);
		}
		return index;
	}
	
	public void addLabel(String label, int labelAddress) {
		labels.add(label);
		labelAddresses.add(labelAddress);
	}
	
	public void printPassTwoLines() {
		for(int i = 0; i < secondPassLines.size(); i++) {
			System.out.println(linesToAssemble[secondPassLines.get(i)]);
		}
	}
	
	public String generateListing() {
		String s = "";
		int lineNum = 0;
		for(String line : listing) {
			if(line == null) {
				line = StringUtils.intToPaddedString(lineNum, 6, 10);
			}
			s += line + "\n";
			
			lineNum++;
		}
		return s;
	}
	
	public Memory getMachineCode() {
		return machineCode;
	}

	@Override
	public void notify(String messageToSend) {
		for(Notifiable notifiable : objectsToNotify) {
			notifiable.takeNotice(messageToSend, this);
		}
		
	}

	@Override
	public void takeNotice(String message, Object notifier) {
		// TODO Auto-generated method stub
		
	}

}
