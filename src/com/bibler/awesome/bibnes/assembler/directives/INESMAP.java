package com.bibler.awesome.bibnes.assembler.directives;

import com.bibler.awesome.bibnes.assembler.Assembler;
import com.bibler.awesome.bibnes.assembler.ErrorHandler;
import com.bibler.awesome.bibnes.utils.DigitUtils;
import com.bibler.awesome.bibnes.utils.StringUtils;

public class INESMAP extends Directive {

	public INESMAP(Assembler assembler) {
		super(assembler);
	}
	
	@Override 
	public int processDirective(String line) {
		int returnValue = -1;
		line = StringUtils.trimWhiteSpace(line);
		int map = DigitUtils.getDigits(line);
		if(map != -1) {
			returnValue = map;
		} else {
			returnValue = ErrorHandler.MISSING_OPERAND;
		}
		return returnValue;
	}

}
