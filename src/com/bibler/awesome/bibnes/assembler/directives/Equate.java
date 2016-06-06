package com.bibler.awesome.bibnes.assembler.directives;

import com.bibler.awesome.bibnes.assembler.Assembler;
import com.bibler.awesome.bibnes.assembler.ErrorHandler;
import com.bibler.awesome.bibnes.utils.StringUtils;

public class Equate extends Directive {

	public Equate(Assembler assembler) {
		super(assembler);
	}
	
	@Override
	public int processDirective(String line) {
		int errorCode = -1;
		line = StringUtils.trimWhiteSpace(line);
		line = StringUtils.trimComments(line);
		if(assembler.processOperand(line) && assembler.getAddress() != -1) {
			assembler.setLabelAddress(assembler.getLabelCount() - 1, assembler.getAddress());
		} else {
			errorCode = ErrorHandler.MISSING_OPERAND;
		}
		return errorCode;
	}

}
