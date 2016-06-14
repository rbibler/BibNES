package com.bibler.awesome.bibnes.ui;

import com.bibler.awesome.bibnes.systems.Memory;
import com.bibler.awesome.bibnes.utils.DigitUtils;
import com.bibler.awesome.bibnes.utils.StringUtils;

public class HexPanel extends MessageBox {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1989546159905835737L;
	
	public HexPanel() {
		super();
	}
	
	public void displayMachineCode(Memory machineCode) {
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				int b;
				String s = "Offset(h) 00 01 02 03 04 05 06 07 08 09 0A 0B 0C 0D 0E 0F";

				for(int i = 0; i < machineCode.size(); i++) {
					if(i % 16 == 0) {
						writeNewLineToBox(s);
						s = StringUtils.intToPaddedString(i, 9, DigitUtils.HEX).toUpperCase() + " ";
					}
					b = machineCode.read(i);
					s += " " + StringUtils.intToHexString(b).toUpperCase();
				}
			}
		});
		t.start();
		
		
	}

}
