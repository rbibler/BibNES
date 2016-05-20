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
		suite.addTestSuite(BITTest.class);
		suite.addTestSuite(BranchTests.class);
		suite.addTestSuite(CMPTest.class);
		suite.addTestSuite(CPXTest.class);
		suite.addTestSuite(CPYTest.class);
		suite.addTestSuite(DECTest.class);
		suite.addTestSuite(EORTest.class);
		suite.addTestSuite(INCTest.class);
		suite.addTestSuite(InterruptTest.class);
		suite.addTestSuite(JMPTest.class);
		suite.addTestSuite(JSRTest.class);
		suite.addTestSuite(LDATest.class);
		suite.addTestSuite(LDXTest.class);
		suite.addTestSuite(LDYTest.class);
		suite.addTestSuite(LSRTest.class);
		suite.addTestSuite(ORATest.class);
		suite.addTestSuite(RegisterInstructionTests.class);
		suite.addTestSuite(ROLTest.class);
		suite.addTestSuite(RORTest.class);
		suite.addTestSuite(SBCTest.class);
		suite.addTestSuite(StackTests.class);
		suite.addTestSuite(STATest.class);
		suite.addTestSuite(StatusRegisterTests.class);
		suite.addTestSuite(STXTest.class);
		suite.addTestSuite(STYTest.class);
		return suite;
	}

}
