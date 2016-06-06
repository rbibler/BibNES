package com.bibler.awesome.bibnes.assembler.directives;

import com.bibler.awesome.bibnes.assembler.Assembler;
import com.bibler.awesome.bibnes.assembler.ErrorHandler;
import com.bibler.awesome.bibnes.utils.AssemblyUtils;
import com.bibler.awesome.bibnes.utils.DigitUtils;
import com.bibler.awesome.bibnes.utils.StringUtils;

public class BankSize extends Directive {

	public BankSize(Assembler assembler) {
		super(assembler);
	}
	
	@Override
	public int processDirective(String line) {
		int errorCode = -1;
		line = StringUtils.trimWhiteSpace(line);
		int bankSize = DigitUtils.getDigits(line);
		if(bankSize != -1) {
			bankSize *= AssemblyUtils.DEFAULT_BANK_SIZE;
			assembler.setBankSize(bankSize);
		} else {
			errorCode = ErrorHandler.MISSING_OPERAND;
		}
		return errorCode;
	}

}
