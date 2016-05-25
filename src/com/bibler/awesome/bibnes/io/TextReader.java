package com.bibler.awesome.bibnes.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class TextReader {
	
	public static String[] readTextFile(File f) {
		if(!f.getName().endsWith(".bns") && !f.getName().endsWith(".asm")) {
			return null;
		}
		ArrayList<String> lines = new ArrayList<String>();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(f));
			String s;
			do {
				s = reader.readLine();
				if(s == null) {
					break;
				}
				lines.add(s);
			} while(s != null);
			
		} catch(IOException e) {}
		finally {
			if(reader != null) {
				try {
					reader.close();
				} catch(IOException e) {}
			}
		}
		String[] lineArray = new String[lines.size()];
		return lines.toArray(lineArray);
	}

}
