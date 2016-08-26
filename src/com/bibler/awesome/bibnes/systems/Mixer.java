package com.bibler.awesome.bibnes.systems;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

public class Mixer  {
	
	private byte[] sampleBuffer;
	private int sampleBufferIndex;
	private SourceDataLine player;
	
	
	public Mixer(APU apu) {
		sampleBuffer = new byte[(int) Math.ceil(44100.0 / 60) * 2];
		System.out.println("buffer length: " + sampleBuffer.length);
		openLine();
		player.start();
	}
	
	public void openLine() {
		AudioFormat[] format = new AudioFormat[] {
				new AudioFormat(
                44100,
                8,//bit
                1,//channel
                false,//unsigned
                false //little endian
        )};
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, format[0], sampleBuffer.length);
		if(!AudioSystem.isLineSupported(info)) {
			
		}
		try {
			player = (SourceDataLine) AudioSystem.getLine(info);
			player.open(format[0]);
		} catch(LineUnavailableException e) {};
	}
	
	public void outputSample(byte sample) {
		sampleBuffer[sampleBufferIndex++ % sampleBuffer.length] = sample;
	}

	public void flushSamples() {
		if(player.available() >= sampleBufferIndex) {
			player.write(sampleBuffer, 0, sampleBufferIndex);
		}
		sampleBufferIndex = 0;
	}
	
	public byte[] getFrame() {
		return sampleBuffer;
	}
	
}
