package com.bibler.awesome.bibnes.systems;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.bibler.awesome.bibnes.communications.Notifiable;
import com.bibler.awesome.bibnes.communications.Notifier;

public class Memory implements Notifier {
	
	private int[] memoryArray;
	private ArrayList<Notifiable> objectsToNotify = new ArrayList<Notifiable>();
	
	private int size;
	
	public Memory(int size) {
		this.size = size;
		memoryArray = new int[size];
	}
	
	public void registerObject(Notifiable objectToNotify) {
		if(!objectsToNotify.contains(objectToNotify)) {
			objectsToNotify.add(objectToNotify);
		}
	}
	
	public int read(int addressToRead) {
		return memoryArray[addressToRead % size];
	}
	
	public void write(int addressToWrite, int data) {
		memoryArray[addressToWrite % size] = data & 0xFF;
		notify("MEM" + (addressToWrite % size) + "," + data);
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

	@Override
	public void notify(String messageToSend) {
		for(Notifiable notifiable : objectsToNotify) {
			notifiable.takeNotice(messageToSend, this);
		}
	}

}
