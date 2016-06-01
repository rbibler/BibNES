package com.bibler.awesome.bibnes.assembler;

public class ErrorHandler {
	
	public static final int NO_OP_CODE = 0x01;
	public static final int ILLEGAL_LABEL = 0x02;
	public static final int ILLEGAL_DIRECTIVE = 0x03;
	public static final int MISSING_OPERAND = 0x04;
	public static final int OVERFLOW = 5;
	public static final int FILE_NOT_FOUND = 6;
	public static final int OPERAND_TOO_LARGE = 7;
	public static final int JUMP_OUT_OF_RANGE = 8;
	
	private static String[] errorMessages = new String[] {
			"", 
			"Syntax error! Expected Instruction, found ",
			"Illegal Label! Label must begin with Alpabetic Character.",
			"Illegal directive!",
			"Operand is either missing or incorrect!",
			"Memory overflow!",
			"File Not Found! Are you sure it exists?",
			"The specified operand is too large!",
			"Jump out of Range!"
	};
	
	
	public static void handleError(String errorText, int line, int errorCode) {
		System.out.println("Error at line " + (line + 1) + ": " + errorMessages[errorCode] + " " + errorText);

	}

}
