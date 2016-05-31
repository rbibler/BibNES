package com.bibler.awesome.bibnes.assembler;

public class ErrorHandler {
	
	public static final int NO_OP_CODE = 0x01;
	public static final int ILLEGAL_LABEL = 0x02;
	public static final int ILLEGAL_DIRECTIVE = 0x03;
	
	private static String[] errorMessages = new String[] {
			"", 
			"Syntax error! Expected Instruction, found ",
			"Illegal Label! Label must begin with Alpabetic Character.",
			"Illegal directive!"
	};
	
	
	public static void handleError(String errorText, int line, int errorCode) {
		switch(errorCode) {
		case NO_OP_CODE:
			System.out.println(errorMessages[errorCode] + errorText + " at line " + line + ".");
			break;
		case ILLEGAL_LABEL:
			System.out.println("Error: " + line + ": " + errorMessages[errorCode] + " " + errorText);
			break;
		case ILLEGAL_DIRECTIVE:
			System.out.println("Error at " + line + ": " + errorMessages[errorCode] + " " + errorText);
			break;
		}
	}

}
