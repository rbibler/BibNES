package com.bibler.awesome.bibnes.systems;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

public class Mixer {
	
	private byte[] sampleBuffer;
	private int sampleBufferIndex;
	private SourceDataLine line;
	
	public Mixer() {
		sampleBuffer = new byte[512];
		openLine();
		line.start();
	}
	
	public void output(double pulseOneValue, double pulseTwoValue, double triValue, double noiseValue, double DMCValue) {
		//if(pulseOneValue > 0 || pulseTwoValue > 0) {
			//System.out.println("Mixed!: " + "\n     Pulse 1: " + pulseOneValue + "\n     Pulse 2: " + pulseTwoValue);
		//}
		sampleBuffer[sampleBufferIndex++] = (byte) (pulseOneValue + pulseTwoValue);
		if(sampleBufferIndex >= sampleBuffer.length) {
			writeAudioBuffer();
			sampleBufferIndex = 0;
		}
	}
	
	private void writeAudioBuffer() {
		line.write(sampleBuffer, 0, sampleBuffer.length);
	}
	
	private void openLine() {
		AudioFormat format = new AudioFormat(44200, 8, 1, false, true);
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, 
			    format); // format is an AudioFormat object
			if (!AudioSystem.isLineSupported(info)) {
			    // Handle the error.
			    }
			    // Obtain and open the line.
			try {
			    line = (SourceDataLine) AudioSystem.getLine(info);
			    line.open(format);
			} catch (LineUnavailableException ex) {
			        // Handle the error.
			    //... 
			}
	}

}
