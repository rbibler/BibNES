package com.bibler.awesome.bibnes.assembler;

public class Label {
	
	String labelName;
	int labelLine;
	
	public Label(String labelName, int labelLine) {
		this.labelName = labelName;
		this.labelLine = labelLine;
	}
	
	public int getLineNumber() {
		return labelLine;
	}
	
	public boolean checkLabelAgainstString(String s) {
		for(int i = 0; i < labelName.length(); i++) {
			if(s.charAt(i) != labelName.charAt(i)) {
				return false;
			}
		}
		if(s.length() > labelName.length()) {
			char c = s.charAt(labelName.length());
			if(c != ';' && c != ',') {
				return false;
			}
		}
		return true;
	}
	
	public int getAddress() {
		return labelLine;
	}
	
	public int getLength() {
		return labelName.length();
	}

}
