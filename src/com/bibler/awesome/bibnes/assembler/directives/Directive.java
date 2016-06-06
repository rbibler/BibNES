package com.bibler.awesome.bibnes.assembler.directives;

import com.bibler.awesome.bibnes.assembler.Assembler;

public class Directive implements IDirective {
	
	protected Assembler assembler;
	
	public Directive(Assembler assembler) {
		this.assembler = assembler;
	}

	@Override
	public int processDirective(String line) {
		// TODO Auto-generated method stub
		return 0;
	}

}
