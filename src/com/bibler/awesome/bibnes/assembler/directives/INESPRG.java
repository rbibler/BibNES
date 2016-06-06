package com.bibler.awesome.bibnes.assembler.directives;

import com.bibler.awesome.bibnes.assembler.Assembler;
import com.bibler.awesome.bibnes.assembler.ErrorHandler;
import com.bibler.awesome.bibnes.utils.DigitUtils;
import com.bibler.awesome.bibnes.utils.StringUtils;

public class INESPRG extends Directive {

	public INESPRG(Assembler assembler) {
		super(assembler);
	}
	
	@Override
	public int processDirective(String line) {
		int returnValue = -1;
		line = StringUtils.trimWhiteSpace(line);
		int prg = DigitUtils.getDigits(line);
		if(prg != -1) {
			returnValue = prg;
		} else {
			returnValue = ErrorHandler.MISSING_OPERAND;
		}
		return returnValue;
	}

}
