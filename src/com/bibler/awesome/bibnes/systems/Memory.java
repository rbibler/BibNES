package com.bibler.awesome.bibnes.systems;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Memory {
	
	private int[] memoryArray;
	
	private int size;
	
	public Memory(int size) {
		this.size = size;
		memoryArray = new int[size];
	}
	
	public int read(int addressToRead) {
		return memoryArray[addressToRead % size];
	}
	
	public void write(int addressToWrite, int data) {
		if(addressToWrite == 0x1C000) {
			System.out.println("here");
		}
		memoryArray[addressToWrite % size] = data & 0xFF;
	}
	
	public int size() {
		return size;
	}

	public void writeMachineCodeToFile(File f) {
		FileOutputStream stream = null;
		try {
			stream = new FileOutputStream(f);
			for(int i = 0; i < size(); i++) {
				stream.write(read(i));
			}
		} catch(IOException e) {}
		finally {
			if(stream != null) {
				try {
					stream.close();
				} catch(IOException e) {}
			}
		}
	}

}
