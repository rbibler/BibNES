package com.bibler.awesome.bibness.assembler.tests;

import java.io.File;
import com.bibler.awesome.bibnes.assembler.Assembler;
import com.bibler.awesome.bibnes.systems.Memory;
import com.bibler.awesome.bibnes.utils.AssemblyUtils;
import junit.framework.TestCase;

public class ParseTest extends TestCase {

	
	public void testFromFile() {
		String currentDirFile = System.getProperty("user.dir");
		Assembler assembler = new Assembler();
		File f = new File(currentDirFile + "/NES Files/test/allsuitea.asm");
		Memory machineCode = assembler.passOne(AssemblyUtils.processFile(f));
		machineCode.writeMachineCodeToFile(new File(currentDirFile + "/NES Files/test/AllSuiteA.bin"));
	}
	
	

}
