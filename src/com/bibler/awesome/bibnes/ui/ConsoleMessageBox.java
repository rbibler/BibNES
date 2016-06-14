package com.bibler.awesome.bibnes.ui;

import java.awt.Color;

public class ConsoleMessageBox extends MessageBox {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6714965922699796476L;
	
	public ConsoleMessageBox() {
		super();
	}
	
	public void displayErrorMessage(String errorMessage) {
		setTextColor(Color.RED);
		writeNewLineToBox(errorMessage);
		setTextColor(Color.BLACK);
	}

}
