package com.bibler.awesome.bibnes.assembler.directives;

import com.bibler.awesome.bibnes.assembler.Assembler;
import com.bibler.awesome.bibnes.assembler.ErrorHandler;
import com.bibler.awesome.bibnes.utils.DigitUtils;
import com.bibler.awesome.bibnes.utils.StringUtils;

public class INESCHR extends Directive {

	public INESCHR(Assembler assembler) {
		super(assembler);
	}
	
	@Override
	public int processDirective(String line) {
		int returnValue = -1;
		line = StringUtils.trimWhiteSpace(line);
		int chr = DigitUtils.getDigits(line);
		if(chr != -1) {
			returnValue = chr;
		} else {
			returnValue = ErrorHandler.MISSING_OPERAND;
		}
		return returnValue;
	}

}
