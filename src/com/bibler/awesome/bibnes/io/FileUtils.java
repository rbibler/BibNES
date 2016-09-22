package com.bibler.awesome.bibnes.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

public class FileUtils {
	
	private static JFileChooser fileChooser;
	
	public static byte[] readFile(File f) {
		byte[] fileBytes = new byte[(int) f.length()];
		FileInputStream stream = null;
		try {
			stream = new FileInputStream(f);
			stream.read(fileBytes);
		} catch(IOException e) {}
		finally {
			if(stream != null) {
				try {
					stream.close();
				} catch(IOException e) {}
			}
		}
		return fileBytes;
	}
	
	public static File getFileForInclusion(String line, File fileRoot) {
		String s = line.substring(line.indexOf('"') + 1, line.lastIndexOf('"')).trim();
		File f = null;
		if(s.charAt(0) == 'C' || s.charAt(0) == 'c') {
			f = new File(s);
		} else {
			f = new File(fileRoot.getAbsolutePath() + "/" + s);
		}
		return f;
	}

	private static boolean chooserMode = false;
	
	public static File loadFile(JFrame frame, boolean directoriesOnly) {
		if(fileChooser == null || (directoriesOnly != chooserMode)) {
			initiateFileChooser(directoriesOnly);
		}
		chooserMode = directoriesOnly;
		int retval = fileChooser.showOpenDialog(frame);
		if(retval == JFileChooser.APPROVE_OPTION) {
			return fileChooser.getSelectedFile();
		} else {
			return null;
		}
	}
	
	private static void initiateFileChooser(boolean chooserMode) {
		fileChooser = new JFileChooser(new File("C:/users/ryan/desktop/nes/roms"));
		if(chooserMode) {
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		}
	}

}
