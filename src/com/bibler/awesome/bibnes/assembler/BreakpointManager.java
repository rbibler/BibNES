package com.bibler.awesome.bibnes.assembler;

import java.util.ArrayList;

public class BreakpointManager {
	
	ArrayList<Integer> breakPoints = new ArrayList<Integer>();
	
	public void addBreakPoint(int breakPointLocation) {
		breakPoints.add(breakPointLocation);
	}
	
	public boolean checkForBreakPoint(int currentLocation) {
		return breakPoints.contains(currentLocation);
	}

}
