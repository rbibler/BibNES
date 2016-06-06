package com.bibler.awesome.bibnes.assembler;

import java.io.File;

import com.bibler.awesome.bibnes.io.TextReader;
import com.bibler.awesome.bibnes.systems.Memory;
import com.bibler.awesome.bibnes.utils.AssemblyUtils;
import com.bibler.awesome.bibnes.utils.StringUtils;

public class NESCreator {
	
	private Assembler assembler;
	private Memory machineCode;
	private int inesPrgSize;
	private int inesChrSize;
	private int inesMapper;
	private int inesMirror;
	
	private final int PRG_BANK_SIZE = 0x4000;
	private final int CHR_BANK_SIZE = 0x2000;
	
	public NESCreator() {
		assembler = new Assembler();
	}
	
	public Memory createNESFile(File f) {
		if(f.exists() && f.isFile()) {
			String[] lines = TextReader.readTextFile(f);
			checkForINESHeaderInfo(lines);
			machineCode = assembler.passOne(TextReader.readTextFile(f));
			writeHeaderInfo();
			return machineCode;
		}
		return null;
	}
	
	private void checkForINESHeaderInfo(String[] lines) {
		for(String line : lines) {
			String directive = assembler.checkDirectives(line.trim());
			if(directive != null) {
				if(directive.contains("INES")) {
					processDirective(directive, line);
				}
			}
		}
		processHeaderInfo();
	}
	
	private void processDirective(String directiveToProcess, String line) {
		int directive = AssemblyUtils.getDirective(directiveToProcess);
		int directiveResult = assembler.processDirective(directive, 
				line.substring(line.toUpperCase().indexOf(directiveToProcess) + directiveToProcess.length()));
		switch(directive) {
		case AssemblyUtils.INES_PRG:
			inesPrgSize = directiveResult;
			break;
		case AssemblyUtils.INES_CHR:
			inesChrSize = directiveResult;
			break;
		case AssemblyUtils.INES_MAP:
			inesMapper = directiveResult;
			break;
		case AssemblyUtils.INES_MIRROR:
			inesMirror = directiveResult;
			break;
		}
	}
	
	private void processHeaderInfo() {
		int machineCodeSize = (inesPrgSize * PRG_BANK_SIZE) + (inesChrSize * CHR_BANK_SIZE);
		assembler.setByteSize(machineCodeSize);
	}
	
	private void writeHeaderInfo() {
		Memory tmp = new Memory(machineCode.size() + 16);
		byte[] NESBytes = StringUtils.stringToAsciiBytes("NES");
		int index = 0;
		for(int i = 0; i < NESBytes.length; i++) {
			tmp.write(index++, NESBytes[i]);
		}
		tmp.write(index++, 0x1A);
		tmp.write(index++, inesPrgSize);
		tmp.write(index++, inesChrSize);
		int byte6 = (inesMapper & 0xF) << 4;
		int byte7 = inesMapper & 0xF0;
		tmp.write(index++, byte6);
		tmp.write(index++, byte7);
		for(int i = 0; i < 8; i++) {
			tmp.write(index++, 0);
		}
		machineCode = copyAssembledCode(tmp, index);
	}
	
	private Memory copyAssembledCode(Memory tmp, int index) {
		for(int i = 0; i < machineCode.size(); i++) {
			tmp.write(i + index, machineCode.read(i));
		}
		return tmp;
	}
	
	public int getINESPrgSize() {
		return inesPrgSize;
	}
	
	public int getINESChrSize() {
		return inesChrSize;
	}
	
	public int getINESMapper() {
		return inesMapper;
	}
	
}
