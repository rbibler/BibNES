package com.bibler.awesome.bibnes.utils;

public class NESPalette {
	
	
	private static int[] pixels = new int[] {
			0xff7c7c7c, 0xff0000fc, 0xff0000bc, 0xff4428bc, 0xff940084, 0xffa80020, 0xffa81000, 0xff881400, 
			0xff503000, 0xff007800, 0xff006800, 0xff005800, 0xff004058, 0xff000000, 0xff000000, 0xff000000, 
			0xffbcbcbc, 0xff0078f8, 0xff0058f8, 0xff6844fc, 0xffd800cc, 0xffe40058, 0xfff83800, 0xffe45c10, 
			0xffac7c00, 0xff00b800, 0xff00a800, 0xff00a844, 0xff008888, 0xff000000, 0xff000000, 0xff000000, 
			0xfff8f8f8, 0xff3cbcfc, 0xff6888fc, 0xff9878f8, 0xfff878f8, 0xfff85898, 0xfff87858, 0xfffca044, 
			0xfff8b800, 0xffb8f818, 0xff58d854, 0xff58f898, 0xff00e8d8, 0xff787878, 0xff000000, 0xff000000, 
			0xfffcfcfc, 0xffa4e4fc, 0xffb8b8f8, 0xffd8b8f8, 0xfff8b8f8, 0xfff8a4c0, 0xfff0d0b0, 0xfffce0a8, 
			0xfff8d878, 0xffd8f878, 0xffb8f8b8, 0xffb8f8d8, 0xff00fcfc, 0xfff8d8f8, 0xff000000, 0xff000000

	};
		
	public static int getPixel(int pixelIndex) {
		return pixels[pixelIndex];
	}

	


}