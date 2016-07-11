package com.bibler.awesome.bibnes.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.bibler.awesome.bibnes.assembler.Assembler;
import com.bibler.awesome.bibnes.assembler.Disassembler;
import com.bibler.awesome.bibnes.communications.MessageHandler;
import com.bibler.awesome.bibnes.communications.Notifier;
import com.bibler.awesome.bibnes.systems.Cartridge;
import com.bibler.awesome.bibnes.systems.Memory;
import com.bibler.awesome.bibnes.systems.Motherboard;
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

	public Motherboard produceNES(File f, MessageHandler messageHandler) {
		if(f == null || !f.exists()) {
			return null;
		}
		BufferedInputStream input = openStream(f);
		processHeaderInfo(input);
		machineCode = processRom(input);
		Cartridge cart = Cartridge.createCartridge(inesMapper, inesPrgSize * PRG_BANK_SIZE, inesChrSize * CHR_BANK_SIZE, machineCode);
		cart.getPrgMem().registerObject(messageHandler);
		Disassembler disassembler = new Disassembler();
		messageHandler.takeNotice("LISTING" + disassembler.disassemble(cart.getPrgMem(), 0), this);
		NES nes = new NES();
		nes.registerObjectToNotify(messageHandler);
		nes.setCart(cart);
		machineCode = cart.getCombinedRoms();
		messageHandler.takeNotice("DONE", this);
		
		return nes;
	}
	
	private BufferedInputStream openStream(File f) {
		BufferedInputStream input = null;
		try {
			input = new BufferedInputStream(new FileInputStream(f));
		} catch(IOException e) {}
		return input;
	}
	
	private void processHeaderInfo(BufferedInputStream input) {
		byte[] headerBytes = new byte[16];
		try {
			input.read(headerBytes);
		} catch(IOException e) {}
		inesPrgSize = headerBytes[4];
		inesChrSize = headerBytes[5];
		inesMapper = headerBytes[6] >> 4 & 0xF | headerBytes[7] & 0xF0;
		inesMirror = headerBytes[6] & 1;
	}
	
	private Memory processRom(BufferedInputStream input) {
		Memory mem = new Memory((inesPrgSize * PRG_BANK_SIZE) + (inesChrSize * CHR_BANK_SIZE));
		int index = 0;
		int read = 0;
		while(read != -1 && index < mem.size()) {
			try {
				read = input.read();
				mem.write(index++, read);
			} catch(IOException e) {}
		}
		try {
			input.close();
		} catch(IOException e) {}
		return mem;
	}

}