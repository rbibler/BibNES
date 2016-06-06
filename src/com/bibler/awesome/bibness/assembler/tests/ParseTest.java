package com.bibler.awesome.bibness.assembler.tests;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.bibler.awesome.bibnes.assembler.Assembler;
import com.bibler.awesome.bibnes.io.TextReader;
import com.bibler.awesome.bibnes.systems.Memory;
import com.bibler.awesome.bibnes.utils.AssemblyUtils;
import com.bibler.awesome.bibnes.utils.StringUtils;

import junit.framework.TestCase;

public class ParseTest extends TestCase {

	
	public void testFromFile() {
		String currentDirFile = System.getProperty("user.dir");
		Assembler assembler = new Assembler();
		File f = new File(currentDirFile + "/NES Files/test/test.asm");
		assembler.setFileRoot(f.getParentFile());
		Memory machineCode = assembler.passOne(AssemblyUtils.processFile(f));
		assembler.writeMachineCodeToFile(new File(currentDirFile + "/NES Files/test/test.nes"), machineCode);
	}
	
	

}
