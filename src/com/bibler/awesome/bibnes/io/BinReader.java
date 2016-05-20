package com.bibler.awesome.bibnes.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.bibler.awesome.bibnes.systems.Memory;

public class BinReader {
	
	public static Memory readBin(File f) {
		Memory rom = new Memory((int) f.length());
		FileInputStream stream = null;
		try {
			stream = new FileInputStream(f);
			int b = 0;
			int i = 0;
			while(b >= 0) {
				b = stream.read();
				if(b == -1) {
					break;
				}
				rom.write(i++, b);
			}
		} catch(IOException e) {}
		finally {
			if(stream != null) {
				try {
					stream.close();
				} catch(IOException e) {}
			}
		}
		return rom;
	}

}
