package com.bibler.awesome.bibnes.ui;

import java.awt.Color;
import java.awt.Font;

import javax.swing.text.AttributeSet;

public class LookAndFeel {
	
	Font currentFont;
	Color backgroundColor;
	Color standardTextColor;
	Color commentColor;
	Color labelColor;
	Color instructionTextColor;
	Color operandTextColor;
	AttributeSet currentAttrSet;
	
	public LookAndFeel() {
		
	}
	
	public void setCurrentFont(Font currentFont) {
		this.currentFont = currentFont;
	}
	
	public Font getCurrentFont() {
		return currentFont;
	}
	
	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}
	
	public Color getBackgroundColor() {
		return backgroundColor;
	}
	
	public void setStandardTextColor(Color standardTextColor) {
		this.standardTextColor = standardTextColor;
	}
	
	public Color getStandardTextColor() {
		return standardTextColor;
	}
	
	public void setinstructionTextColor(Color instructionTextColor) {
		this.instructionTextColor = instructionTextColor;
	}
	
	public Color getinstructionTextColor() {
		return instructionTextColor;
	}
	
	public void setoperandTextColor(Color operandTextColor) {
		this.operandTextColor = operandTextColor;
	}
	
	public Color getoperandTextColor() {
		return operandTextColor;
	}
	
	
	
	

}
