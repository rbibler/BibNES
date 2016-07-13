package com.bibler.awesome.bibnes.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import com.bibler.awesome.bibnes.systems.Memory;
import com.bibler.awesome.bibnes.utils.DigitUtils;
import com.bibler.awesome.bibnes.utils.StringUtils;

public class HexPane extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -756335891085926549L;
	
	private JScrollPane hexPane;
	private MessageBox hexText;
	
	private final String HEADER = "Offset(h)  00 01 02 03 04 05 06 07 08 09 0A 0B 0C 0D 0E 0F";
	private int[] memory;
	
	private final int ROW_WIDTH_IN_CHARS = 59;
	private final int ROW_START_WIDTH = 11;
	private final int BYTE_WIDTH = 3;
	
	private int currentIndex;
	
	public HexPane() {
		super();
		initialize();
	}
	
	private void initialize() {
		hexText = new MessageBox();
		hexText.writeNewLineToBox(HEADER);
		hexPane = new JScrollPane(hexText);
		hexPane.setPreferredSize(new Dimension(400, 400));
		setLayout(new BorderLayout());
		add(hexPane, BorderLayout.CENTER);
	}
	
	public void fillInValues(int valuesToFill, int valueToFill) {
		for(int i = 0; i < valuesToFill; i++) {
			updateHexValue(i, valueToFill);
		}
	}
	
	public void fillInValues(Memory memory) {
		this.memory = new int[memory.size()];
		Arrays.fill(this.memory, -1);
		for(int i = 0; i < memory.size(); i++) {
			updateHexValue(i, memory.read(i));
		}
	}
	
	public void parseMemUpdate(String s) {
		String[] values = s.split(",");
		updateHexValue(Integer.parseInt(values[0]), Integer.parseInt(values[1]));
	}
	
	public void updateHexValue(int index, int valueToUpdate) {
		
		currentIndex = index;
		//if(index % 16 == 0 && memory[index] == -1) {
			//String s = StringUtils.intToPaddedString(index, 8, DigitUtils.HEX).toUpperCase() + "  ";
			//hexText.writeNewStringToBox("\n" + s, hexText.getLength(), true);
		//} 
		int row = index / 16;
		int col = index % 16;
		int offset = (row + 1) * ROW_WIDTH_IN_CHARS;
		offset += ((col * BYTE_WIDTH) + ROW_START_WIDTH);
		//hexText.writeNewStringToBox(StringUtils.intToHexString(valueToUpdate).toUpperCase() + " ", offset, memory[index] != - 1);
		//memory[index] = valueToUpdate;
		
	}
}
