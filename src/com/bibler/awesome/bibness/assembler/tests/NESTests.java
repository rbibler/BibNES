package com.bibler.awesome.bibness.assembler.tests;

import java.io.File;

import com.bibler.awesome.bibnes.assembler.NESCreator;
import com.bibler.awesome.bibnes.io.FileUtils;
import com.bibler.awesome.bibnes.systems.Memory;

import junit.framework.TestCase;

public class NESTests extends TestCase {
	
	public void testINESHeaderParse() {
		String currentDirFile = System.getProperty("user.dir");
		File f = new File(currentDirFile + "/NES Files/background/background_test.asm");
		NESCreator creator = new NESCreator();
		Memory result = creator.createNESFile(f);
		result.writeMachineCodeToFile(new File(currentDirFile + "/NES Files/background/background_test.nes"));
		assertEquals(1, creator.getINESPrgSize());
		assertEquals(1, creator.getINESChrSize());
		assertEquals(0, creator.getINESMapper());
		assertEquals(0x6010, result.size());
		assertTrue(compareFiles(result, 
				new File("C:/users/ryan/desktop/NES/nerdy nights tutorials/background/background/background_test.nes")));
		
	}
	
	private boolean compareFiles(Memory result, File testCase) {
		boolean match = true;
		byte[] bytes = FileUtils.readFile(testCase);
		if(bytes != null) {
			for(int i = 0; i < bytes.length; i++) {
				if(((int) bytes[i] & 0xFF) != result.read(i) && i > 0x10) {
					System.out.println("No match at " + i);
					match = false;
					break;
				}
			}
		}
		return match;
	}

}
