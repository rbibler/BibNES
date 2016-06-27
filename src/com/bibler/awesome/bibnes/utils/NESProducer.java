package com.bibler.awesome.bibnes.utils;

import com.bibler.awesome.bibnes.assembler.Assembler;
import com.bibler.awesome.bibnes.communications.MessageHandler;
import com.bibler.awesome.bibnes.communications.Notifier;
import com.bibler.awesome.bibnes.systems.Cartridge;
import com.bibler.awesome.bibnes.systems.Memory;
import com.bibler.awesome.bibnes.systems.NES;

public class NESProducer {
	
	private Assembler assembler;
	private Memory machineCode;
	private int inesPrgSize;
	private int inesChrSize;
	private int inesMapper;
	private int inesMirror;
	
	final int PRG_BANK_SIZE = 0x4000;
	final int CHR_BANK_SIZE = 0x2000;
	
	public NES produceNES(String[] lines, MessageHandler messageHandler) {
		NES nes = new NES();
		nes.registerObjectToNotify(messageHandler);
		assembler = new Assembler();
		checkForINESHeaderInfo(lines);
		assembler.registerObjectToNotify(messageHandler);
		machineCode = assembler.passOne(lines);
		Cartridge cart = Cartridge.createCartridge(inesMapper, inesPrgSize * PRG_BANK_SIZE, inesChrSize * CHR_BANK_SIZE, machineCode);
		cart.getPrgMem().registerObject(messageHandler);
		nes.setCart(cart);
		machineCode = cart.getCombinedRoms();
		messageHandler.takeNotice("DONE", this);
		return nes;
	}
	
	public Memory getMachineCode() {
		return machineCode;
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

}
