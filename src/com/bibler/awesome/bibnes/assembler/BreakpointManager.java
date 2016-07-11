package com.bibler.awesome.bibnes.assembler;

import java.util.ArrayList;

public class BreakpointManager {
	
	ArrayList<Integer> breakpoints = new ArrayList<Integer>();
	
	Assembler assembler = new Assembler();
	
	public void addBreakPoint(int breakpointLocation) {
		if(!breakpoints.contains(breakpointLocation)) {
			breakpoints.add(breakpointLocation);
			System.out.println("Adding breakpoint: " + Integer.toHexString(breakpointLocation));
		}
	}
	
	public void removeBreakpoint(int breakpointLocation) {
		breakpoints.remove(breakpoints.indexOf(breakpointLocation));
	}
	
	public boolean contains(int currentLocation) {
		return breakpoints.contains(currentLocation);
	}
	
	public boolean verifyLineAndAdd(String line, int lineNum) {
		boolean verify = false;
		verify = assembler.parseLine(line);
		if(verify) {
			addBreakPoint(lineNum);
		}
		return verify;
	}
	
	public void clearAllBreakpoints() {
		breakpoints.clear();
	}

}
