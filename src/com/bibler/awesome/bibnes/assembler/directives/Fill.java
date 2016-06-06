package com.bibler.awesome.bibnes.assembler.directives;

import com.bibler.awesome.bibnes.assembler.Assembler;
import com.bibler.awesome.bibnes.assembler.ErrorHandler;
import com.bibler.awesome.bibnes.utils.DigitUtils;
import com.bibler.awesome.bibnes.utils.StringUtils;

public class Fill extends Directive {
	
	public Fill(Assembler assembler) {
		super(assembler);
	}

	@Override
	public int processDirective(String line) {
		int errorCode = -1;
		line = StringUtils.trimWhiteSpace(line);
		String[] params = StringUtils.trimWhiteSpace(line).split("[,]");
		if(params.length > 0) {
			int bytesToFill = DigitUtils.getDigits(params[0]);
			int fillByte = DigitUtils.getDigits(params[1]);
			if(fillByte <= 0xFF) {
				if(bytesToFill + assembler.getLocationCounterAndBank() < assembler.getRomSize()) {
					for(int i = 0; i < bytesToFill; i++) {
						assembler.writeCurrentLocationAndBank(fillByte);
					}
				} else {
					errorCode = ErrorHandler.OVERFLOW;
				}
			} else {
				errorCode = ErrorHandler.OPERAND_TOO_LARGE;
			}
		}
		return errorCode;
	}
	
	

}
