package com.bibler.awesome.bibnes.assembler.directives;

import com.bibler.awesome.bibnes.assembler.Assembler;
import com.bibler.awesome.bibnes.assembler.ErrorHandler;
import com.bibler.awesome.bibnes.utils.DigitUtils;
import com.bibler.awesome.bibnes.utils.StringUtils;

public class ReserveSpace extends Directive {

	public ReserveSpace(Assembler assembler) {
		super(assembler);
	}
	
	@Override
	public int processDirective(String line) {
		int errorCode = -1;
		line = StringUtils.trimWhiteSpace(line);
		int bytesToSkip = DigitUtils.getDigits(line);
		if(bytesToSkip >= 0) {
			int rsCounter = assembler.getRSCounter();
			//assembler.setLabelAddress(assembler.getLabelCount() - 1, rsCounter);
			assembler.setRSCounter(rsCounter += bytesToSkip);
			assembler.setLocationCounter(assembler.getLocationCounter() + bytesToSkip);
			
		} else {
			errorCode = ErrorHandler.MISSING_OPERAND;
		}
		return errorCode;
	}

}
