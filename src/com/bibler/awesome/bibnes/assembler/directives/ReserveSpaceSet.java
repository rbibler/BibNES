package com.bibler.awesome.bibnes.assembler.directives;

import com.bibler.awesome.bibnes.assembler.Assembler;
import com.bibler.awesome.bibnes.utils.DigitUtils;
import com.bibler.awesome.bibnes.utils.StringUtils;

public class ReserveSpaceSet extends Directive {

	public ReserveSpaceSet(Assembler assembler) {
		super(assembler);
	}
	
	@Override
	public int processDirective(String line) {
		int errorCode = -1;
		line = StringUtils.trimWhiteSpace(line);
		//TODO Added Error handling
		assembler.setRSCounter(DigitUtils.getDigits(line));
		return errorCode;
	}

}
