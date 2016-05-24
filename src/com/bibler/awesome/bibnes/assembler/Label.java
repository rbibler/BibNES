package com.bibler.awesome.bibnes.assembler;

public class Label {
	
	String labelName;
	int labelAddress;
	
	public Label(String labelName, int labelAddress) {
		this.labelName = labelName;
		this.labelAddress = labelAddress;
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
		return labelAddress;
	}

}
