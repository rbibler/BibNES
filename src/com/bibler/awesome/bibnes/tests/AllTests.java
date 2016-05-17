package com.bibler.awesome.bibnes.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {
	
	public static Test suite() {
		TestSuite suite = new TestSuite(AllTests.class.getName());
		suite.addTestSuite(ADCTest.class);
		suite.addTestSuite(AddressModeTest.class);
		suite.addTestSuite(ANDTest.class);
		suite.addTestSuite(ASLTest.class);
		suite.addTestSuite(LDATest.class);
		return suite;
	}

}
