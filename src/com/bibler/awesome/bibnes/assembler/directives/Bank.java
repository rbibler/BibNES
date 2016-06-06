package com.bibler.awesome.bibnes.assembler.directives;

import com.bibler.awesome.bibnes.assembler.Assembler;
import com.bibler.awesome.bibnes.assembler.ErrorHandler;
import com.bibler.awesome.bibnes.utils.DigitUtils;
import com.bibler.awesome.bibnes.utils.StringUtils;

public class Bank extends Directive {

	public Bank(Assembler assembler) {
		super(assembler);
	}
	
	@Override
	public int processDirective(String line) {
		int errorCode = -1;
		line = StringUtils.trimWhiteSpace(line);
		int bank = DigitUtils.getDigits(line);
		if(bank != -1) {
			assembler.setCurrentBank(bank);
			assembler.setLocationCounter(bank * assembler.getBankSize());
		} else {
			errorCode = ErrorHandler.MISSING_OPERAND;
		}
		return errorCode;
	}

}
