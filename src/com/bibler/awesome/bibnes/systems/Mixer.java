package com.bibler.awesome.bibnes.systems;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

public class Mixer implements Runnable {
	
	private byte[] sampleBuffer;
	private int sampleBufferIndex;
	private SourceDataLine player;
	
	private APU apu;
	
	public Mixer(APU apu) {
		this.apu = apu;
		sampleBuffer = new byte[512];
		openLine();
		player.start();
		Thread t = new Thread(this);
		t.start();
	}
	
	public void openLine() {
		AudioFormat[] format = new AudioFormat[] {new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100.0F, 8, 1, 1, 44100.0F, false)};
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, format, 512, 512);
		if(!AudioSystem.isLineSupported(info)) {
			
		}
		try {
			player = (SourceDataLine) AudioSystem.getLine(info);
			player.open(format[0]);
		} catch(LineUnavailableException e) {};
	}

	@Override
	public void run() {
		while(!Thread.interrupted()) {
			final int bytesRead = apu.getSamples(sampleBuffer);
			if(bytesRead > 0) {
				player.write(sampleBuffer,  0,  bytesRead);
			}
		}
	}
}
