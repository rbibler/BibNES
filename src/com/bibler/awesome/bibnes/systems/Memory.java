package com.bibler.awesome.bibnes.systems;

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

}
