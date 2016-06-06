package com.bibler.awesome.bibnes.assembler.directives;

import java.io.File;

import com.bibler.awesome.bibnes.assembler.Assembler;
import com.bibler.awesome.bibnes.io.FileUtils;
import com.bibler.awesome.bibnes.io.TextReader;

public class Include extends Directive {
	
	public Include(Assembler assembler) {
		super(assembler);
	}

	@Override
	public int processDirective(String line) {
		int errorCode = -1;
		File f = FileUtils.getFileForInclusion(line, assembler.getFileRoot());
		if(f.exists()) {
			String[] newLines = TextReader.readTextFile(f);
			int index = 0;
			int lineCount = assembler.getLineCount();
			String[] linesToAssemble = assembler.getLinesToAssemble();
			if(newLines != null) {
				String[] tmp = new String[linesToAssemble.length + newLines.length];
				for(int i = 0; i < lineCount; i++) {
					tmp[index++] = linesToAssemble[i];
				}
				for(int i = 0; i < newLines.length; i++) {
					tmp[index++] = newLines[i];
				}
				for(int i = lineCount + 1; i < linesToAssemble.length; i++) {
					tmp[index++] = linesToAssemble[i];
				}
				assembler.setLinesToAssemble(tmp);
			}
		}
		return errorCode;
	}

}
