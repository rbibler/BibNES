package com.bibler.awesome.bibnes.ui;

import java.io.File;

public class NESFileTreeNode {
	
	private File file;
	
	public NESFileTreeNode(File file) {
		this.file = file;
	}
	
	@Override
	public String toString() {
		String name = file.getName();
		if(name.equals("")) {
			return file.getAbsolutePath();
		} else if(file.isFile()){
			return name.substring(0, name.length() - 4);
		} else {
			return name;
		}
	}

}
