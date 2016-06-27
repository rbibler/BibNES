package com.bibler.awesome.bibnes.assembler.directives;

import java.io.File;

import com.bibler.awesome.bibnes.assembler.Assembler;
import com.bibler.awesome.bibnes.assembler.ErrorHandler;
import com.bibler.awesome.bibnes.io.FileUtils;

public class IncludeBinary extends Directive {
	
	public IncludeBinary(Assembler assembler) {
		super(assembler);
	}

	@Override
	public int processDirective(String line) {
		int errorCode = -1;
		File f = FileUtils.getFileForInclusion(line, assembler.getFileRoot());
		if(f.exists()) {
			byte[] fileBytes = FileUtils.readFile(f);
			for(Byte fileByte : fileBytes) {
				assembler.writeCurrentLocationAndBank(fileByte);
				//if(assembler.checkForLocationOverflow()) {
					//errorCode = ErrorHandler.OVERFLOW;
					//break;
				//}
			}
		} else {
			errorCode = ErrorHandler.FILE_NOT_FOUND;
		}
		return errorCode;
	}

}
