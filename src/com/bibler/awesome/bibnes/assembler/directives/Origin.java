package com.bibler.awesome.bibnes.assembler.directives;

import com.bibler.awesome.bibnes.assembler.Assembler;
import com.bibler.awesome.bibnes.assembler.ErrorHandler;
import com.bibler.awesome.bibnes.utils.DigitUtils;
import com.bibler.awesome.bibnes.utils.StringUtils;

public class Origin extends Directive {
	
	public Origin(Assembler assembler) {
		super(assembler);
	}

	@Override
	public int processDirective(String line) {
		int errorCode = -1;
		line = StringUtils.trimWhiteSpace(line);
		int newLocation = DigitUtils.getDigits(line);
		if(newLocation >= 0) {
			assembler.setLocationCounter(newLocation);
		} else {
			errorCode = ErrorHandler.MISSING_OPERAND;
		}
		return errorCode;
	}

}
