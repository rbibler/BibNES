package com.bibler.awesome.bibnes.assembler.directives;

import com.bibler.awesome.bibnes.assembler.Assembler;
import com.bibler.awesome.bibnes.assembler.ErrorHandler;
import com.bibler.awesome.bibnes.utils.DigitUtils;
import com.bibler.awesome.bibnes.utils.StringUtils;

public class DefineByte extends Directive {
	
	public DefineByte(Assembler assembler) {
		super(assembler);
	}

	@Override
	public int processDirective(String line) {
		int errorCode = -1;
		String label;
		int labelAddress;
		line = StringUtils.trimWhiteSpace(line);
		int byteToWrite;
		String[] bytesToCheck = StringUtils.trimWhiteSpace(line).split("[,]");
		if(bytesToCheck.length > 1) {
			for(String s : bytesToCheck) {
				byteToWrite = DigitUtils.getDigits(s);
				if(byteToWrite == -1) {
					label = StringUtils.checkLabel(s);
					if(label != null) {
						labelAddress = assembler.getLabelAddress(label);
						if(labelAddress == -1) {
							errorCode = ErrorHandler.MISSING_OPERAND;
						} else {
							assembler.writeCurrentLocationAndBank(labelAddress);
						}
					} else {
						errorCode = ErrorHandler.MISSING_OPERAND;
					}
				} else {
					assembler.writeCurrentLocationAndBank(byteToWrite);
				}
			}
		} else {
			byteToWrite = DigitUtils.getDigits(line);
			if(byteToWrite == -1) {
				label = StringUtils.checkLabel(line);
				if(label != null) {
					labelAddress = assembler.getLabelAddress(label);
					if(labelAddress != -1) {
						assembler.writeCurrentLocationAndBank(byteToWrite);
					} else {
						errorCode = ErrorHandler.MISSING_OPERAND;
					}
				} else {
					errorCode = ErrorHandler.MISSING_OPERAND;
				}
			} else {
				assembler.writeCurrentLocationAndBank(byteToWrite);
			}
				
		}
		return errorCode;
	}
	
	

}
