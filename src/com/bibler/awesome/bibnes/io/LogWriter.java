package com.bibler.awesome.bibnes.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

public class LogWriter {
	
	private FileWriter writer;
	private String logFileBase;
	private File logFile;
	
	private Queue<String> log = new LinkedList<String>();
	private int fileCount;
	
	boolean running;
	
	
	public LogWriter(String logFileBase) {
		this.logFileBase = logFileBase;
		initiateLogWriter();
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				System.out.println("Closing Writer");
				if(writer != null) {
					try {
						writeAll();
						writer.flush();
						writer.close();
					} catch(IOException e) {}
				}
			}
		});
		
	
	}
	
	private void writeAll() {
		String s = log.poll();
		do {
			try {
				writer.write(s + "\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			s = log.poll();
		} while(s != null);
	}
	
	private void initiateLogWriter() {
		logFile = new File(logFileBase + fileCount);
		openWriter();
	}
	
	private void openWriter() {
		if(writer != null) {
			try {
				writer.close();
			} catch(IOException e) {}
		}
		try {
			writer = new FileWriter(logFile);
		} catch(IOException e) {}
	}
	
	public synchronized void log(String lineToLog) {
		log.add(lineToLog);
	}

	/*@Override
	public void run() {
		while(running) {
			final String s = log.poll();
			if(s != null) {
				try {
					writer.write(s + "\n");
					//writer.flush();
				} catch (IOException e) {}
			}
			try {
				Thread.sleep(10);
			} catch(InterruptedException e) {}
		}
	}*/
	
}
