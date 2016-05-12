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
		memoryArray[addressToWrite % size] = data;
	}

}
