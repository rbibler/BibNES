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
	private int samplesPerFrame;
	private SourceDataLine player;
	
	private float sampleRate;
	private int bitRate;
	private int channels;
	private int bytesPerSample;
	
	private int[] sampleRates = new int[] {
		6000, 7333, 8000, 11025, 16000, 22050, 24000, 32000, 44100,
		48000, 64000, 88200, 96000, 192000
	};
	
	public void updateParameters(int bitRate) {
		sampleRate = 44100;
		this.bitRate = bitRate;
		if(bitRate == 8) {
			channels = 1;
		} else {
			channels = 2;
		}
		samplesPerFrame = (int) Math.ceil((sampleRate * channels) / 59.9);
		sampleBuffer = new byte[(samplesPerFrame * (bitRate / 8)) *  2];
		bytesPerSample = channels * (bitRate / 8);
		if(player != null) {
			closeLine();
		}		
		openLine();
	}
	
	public void closeLine() {
		player.flush();
		player.close();
	}

	
	public void openLine() {
		AudioFormat[] format = new AudioFormat[] {
				new AudioFormat(
                sampleRate,
                bitRate,//bit
                channels,//channel
                bitRate == 8 ? false : true,//unsigned
                false //little endian
        )};
		try {
			player = AudioSystem.getSourceDataLine(format[0]);
			player.open(format[0], samplesPerFrame * 32 * channels /*ch*/ * (bitRate / 8) /*bytes/sample*/); 
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
         //create 4 frame audio buffer
        player.start();
	}
	
	public void outputSample(int sample) {
		if(bitRate == 16) {
			if (sample < -32768) {
				sample = -32768;
            //System.err.println("clip");
			}
			if (sample > 32767) {
				sample = 32767;
            //System.err.println("clop");
			}
		}
		if(sampleBufferIndex + bytesPerSample < sampleBuffer.length) {
			for(int i = 0; i < channels; i++) {
				if(bitRate == 8) {
					sampleBuffer[sampleBufferIndex++] = (byte) (sample & 0xFF);
				} else {
					sampleBuffer[sampleBufferIndex++] = (byte) (sample & 0xFF);
					sampleBuffer[sampleBufferIndex++] = (byte) ((sample >> 8) & 0xFF);
				}
			}
		}
	}
	
	public void flushSamples() {
		if(player.available() >= sampleBufferIndex) {
			player.write(sampleBuffer, 0, sampleBufferIndex);
		} else {
			System.out.println("OVERRUN!!!!");
		}
		sampleBufferIndex = 0;
	}
	
	public byte[] getFrame() {
		return sampleBuffer;
	}
	
	 public final boolean bufferHasLessThan(final int samples) {
		 //returns true if the audio buffer has less than the specified amt of samples remaining in it
	     return (player == null) ? false : ((player.getBufferSize() - player.available()) <= samples);
	 }
	 
	public float getBufferUsage() {
		final float size = player.getBufferSize();
		final float avail = player.available();
		final float used = size - avail;
		return used / size;
	}
	
}
