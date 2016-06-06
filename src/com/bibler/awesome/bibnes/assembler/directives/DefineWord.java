package com.bibler.awesome.bibnes.assembler.directives;

import com.bibler.awesome.bibnes.assembler.Assembler;
import com.bibler.awesome.bibnes.assembler.ErrorHandler;
import com.bibler.awesome.bibnes.utils.DigitUtils;
import com.bibler.awesome.bibnes.utils.StringUtils;

public class DefineWord extends Directive {
	
	public DefineWord(Assembler assembler) {
		super(assembler);
	}

	@Override
	public int processDirective(String line) {
		int errorCode = -1;
		line = StringUtils.trimWhiteSpace(line);
		String label = StringUtils.checkLabel(line);
		int labelAddress = -1;
		if(label != null) {
			labelAddress = assembler.getLabelAddress(label);
			if(labelAddress == -1) {
				errorCode = ErrorHandler.MISSING_OPERAND;
			} else {
				assembler.writeCurrentLocationAndBank(labelAddress & 0xFF);
				assembler.writeCurrentLocationAndBank(labelAddress >> 8 & 0xFF);
			}
		} else {
			int wordToWrite;
			String[] wordsToCheck = StringUtils.trimWhiteSpace(line).split("[,]");
			if(wordsToCheck.length > 0) {
				for(String s : wordsToCheck) {
					wordToWrite = DigitUtils.getDigits(s);
					if(wordToWrite == -1) {
						errorCode = ErrorHandler.MISSING_OPERAND;
					} else {
						assembler.writeCurrentLocationAndBank(wordToWrite & 0xFF);
						assembler.writeCurrentLocationAndBank(wordToWrite >> 8 & 0xFF);
					}
				}
			} else {
				wordToWrite = DigitUtils.getDigits(line);
				if(wordToWrite == -1) {
					errorCode = ErrorHandler.MISSING_OPERAND;
				} else {
					assembler.writeCurrentLocationAndBank(wordToWrite & 0xFF);
					assembler.writeCurrentLocationAndBank(wordToWrite >> 8 & 0xFF);
				}
			}
		}
		return errorCode;
	}
	

}
