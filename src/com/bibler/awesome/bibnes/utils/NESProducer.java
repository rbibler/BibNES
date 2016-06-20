package com.bibler.awesome.bibnes.utils;

import com.bibler.awesome.bibnes.assembler.Assembler;
import com.bibler.awesome.bibnes.communications.MessageHandler;
import com.bibler.awesome.bibnes.systems.Cartridge;
import com.bibler.awesome.bibnes.systems.Memory;
import com.bibler.awesome.bibnes.systems.NES;

public class NESProducer {
	
	public static NES produceNES(String[] lines, MessageHandler messageHandler) {
		NES nes = new NES();
		nes.registerObjectToNotify(messageHandler);
		Assembler assembler = new Assembler();
		assembler.registerObjectToNotify(messageHandler);
		Memory prgRom = assembler.passOne(lines);
		prgRom.registerObject(messageHandler);
		nes.setCart(Cartridge.createCartridge(0, prgRom, null));
		return nes;
	}

}
