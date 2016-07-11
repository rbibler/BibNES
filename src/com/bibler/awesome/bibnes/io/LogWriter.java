package com.bibler.awesome.bibnes.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class LogWriter implements Runnable {
	
	private FileWriter writer;
	private String logFileBase;
	private File logFile;
	
	private String[] logBuffer = new String[0x100];
	private int addIndex;
	private int readIndex;
	private int linesWritten;
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
						writer.close();
					} catch(IOException e) {}
				}
			}
		});
		Thread t = new Thread(this);
		running = true;
		t.start();
		
	
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
	
	public void log(String lineToLog) {
		logBuffer[addIndex++] = lineToLog;
		if(addIndex >= logBuffer.length) {
			if(readIndex > 0) {
				addIndex = 0;
			}
		}
	}

	@Override
	public void run() {
		while(running) {
			if(readIndex < addIndex) {
				while(readIndex < addIndex) {
					try {
						writer.write(logBuffer[readIndex++] + "\n");
						if(readIndex >= logBuffer.length) {
							readIndex = 0;
						}
						linesWritten++;
						if(linesWritten >= 0x1000) {
							fileCount++;
							linesWritten = 0;
							initiateLogWriter();
						}
					} catch(IOException e) {}
				}
			}
			try {
				Thread.sleep(10);
			} catch(InterruptedException e) {}
		}
	}
	
}
