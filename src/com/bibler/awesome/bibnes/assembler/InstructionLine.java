package com.bibler.awesome.bibnes.assembler;

import java.util.ArrayList;

import com.bibler.awesome.bibnes.utils.AssemblyUtils;
import com.bibler.awesome.bibnes.utils.DigitUtils;

public class InstructionLine {
	
	private String line;
	private String instructionName;
	private String comment;
	private int lineNumber;
	private int opCode;
	private int operand;
	private int bytes;
	private Label label;
	private boolean checkOnSecondPass;
	
	
	/**
	 * Represent a complete or semi-parsed machine instruction that will ultimately be processed to create machine code.
	 * @param instructionName	String representing instruction mnemonic
	 * @param lineNumber	Line on which the instruction resides
	 * @param opCode	OpCode for instruction, if found
	 * @param operand	Operand for isntruction, if present
	 * @param bytes		Length of instruction in bytes
	 * @param checkOnSecondPass		Indicts if first pass was unable to find complete instruction. Needs second pass to check for lables. 
	 */
	public InstructionLine(String line, int lineNumber) {
		this.line = line;
		this.lineNumber = lineNumber;
		operand = -1;
	}
	
	public void setInstructionName(String instructionName) {
		this.instructionName = instructionName;
	}
	
	public void setOpCode(int opCode) {
		this.opCode = opCode;
	}
	
	public void setOperand(int operand) {
		this.operand = operand;
	}
	
	public void setBytes(int bytes) {
		this.bytes = bytes;
	}
	
	public void setCheckOnSecondPass(boolean checkOnSecondPass) {
		this.checkOnSecondPass = checkOnSecondPass;
	}
	
	
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public void setLabel(Label label) {
		this.label = label;
	}
	
	public void writeInstruction(ArrayList<Integer> machineCode) {
		machineCode.add(opCode);
		int[] operandBytes = DigitUtils.splitWord(operand, bytes - 1);
		for(int i = operandBytes.length - 1; i >= 0; i--) {
			machineCode.add(operandBytes[i]);
		}
	}
	
	public void update(InstructionLine inst) {
		this.line = inst.getLine();
		this.instructionName = inst.getInstructionName();
		this.comment = inst.getComment();
		this.lineNumber = inst.getLineNumber();
		this.opCode = inst.getOpCode();
		this.operand = inst.getOperand();
		this.label = inst.getLabel();
		this.bytes = inst.getBytes();
		this.checkOnSecondPass = inst.getCheckOnSecondPass();
	}
	
	public String getLine() {
		return line;
	}
	
	public String getInstructionName() {
		return instructionName;
	}
	
	public String getComment() {
		return comment;
	}
	
	public int getLineNumber() {
		return lineNumber;
	}
	
	public int getOpCode() {
		return opCode;
	}
	
	public int getOperand() {
		return operand;
	}
	
	public int getBytes() {
		return bytes;
	}
	
	public Label getLabel() {
		return label;
	}
	
	public boolean getCheckOnSecondPass() {
		return checkOnSecondPass;
	}
	

}
