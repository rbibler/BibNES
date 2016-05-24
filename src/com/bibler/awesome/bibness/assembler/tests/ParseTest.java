package com.bibler.awesome.bibness.assembler.tests;

import java.io.File;

import com.bibler.awesome.bibnes.assembler.Assembler;
import com.bibler.awesome.bibnes.utils.AssemblyUtils;
import com.bibler.awesome.bibnes.utils.StringUtils;

import junit.framework.TestCase;

public class ParseTest extends TestCase {
	
	public void testInstructionParse() {
		Assembler assembler = new Assembler();
		String s = "ADC #$F2";
		assertTrue(assembler.matchOpCode(StringUtils.trimWhiteSpace(s)));
		s = "AND";
		assertTrue(assembler.matchOpCode(StringUtils.trimWhiteSpace(s)));
		s = "ASL";
		assertTrue(assembler.matchOpCode(StringUtils.trimWhiteSpace(s)));
		s = "BCC";
		assertTrue(assembler.matchOpCode(StringUtils.trimWhiteSpace(s)));
		s = "BCS";
		assertTrue(assembler.matchOpCode(StringUtils.trimWhiteSpace(s)));
		s = "BEQ";
		assertTrue(assembler.matchOpCode(StringUtils.trimWhiteSpace(s)));
		s = "BIT";
		assertTrue(assembler.matchOpCode(StringUtils.trimWhiteSpace(s)));
		s = "BMI";
		assertTrue(assembler.matchOpCode(StringUtils.trimWhiteSpace(s)));
		s = "BNE";
		assertTrue(assembler.matchOpCode(StringUtils.trimWhiteSpace(s)));
		s = "BPL";
		assertTrue(assembler.matchOpCode(StringUtils.trimWhiteSpace(s)));
		s = "BRK";
		assertTrue(assembler.matchOpCode(StringUtils.trimWhiteSpace(s)));
		s = "BVC";
		assertTrue(assembler.matchOpCode(StringUtils.trimWhiteSpace(s)));
		s = "BVS";
		assertTrue(assembler.matchOpCode(StringUtils.trimWhiteSpace(s)));
		s = "CLC";
		assertTrue(assembler.matchOpCode(StringUtils.trimWhiteSpace(s)));
		s = "CLD";
		assertTrue(assembler.matchOpCode(StringUtils.trimWhiteSpace(s)));
		s = "CLI";
		assertTrue(assembler.matchOpCode(StringUtils.trimWhiteSpace(s)));
		s = "CLV";
		assertTrue(assembler.matchOpCode(StringUtils.trimWhiteSpace(s)));
		s = "CMP";
		assertTrue(assembler.matchOpCode(StringUtils.trimWhiteSpace(s)));
		s = "CPX";
		assertTrue(assembler.matchOpCode(StringUtils.trimWhiteSpace(s)));
		s = "CPY";
		assertTrue(assembler.matchOpCode(StringUtils.trimWhiteSpace(s)));
		s = "DEC";
		assertTrue(assembler.matchOpCode(StringUtils.trimWhiteSpace(s)));
		s = "DEX";
		assertTrue(assembler.matchOpCode(StringUtils.trimWhiteSpace(s)));
		s = "DEY";
		assertTrue(assembler.matchOpCode(StringUtils.trimWhiteSpace(s)));
		s = "EOR";
		assertTrue(assembler.matchOpCode(StringUtils.trimWhiteSpace(s)));
		s = "INC";
		assertTrue(assembler.matchOpCode(StringUtils.trimWhiteSpace(s)));
		s = "INX";
		assertTrue(assembler.matchOpCode(StringUtils.trimWhiteSpace(s)));
		s = "INY";
		assertTrue(assembler.matchOpCode(StringUtils.trimWhiteSpace(s)));
		s = "JMP";
		assertTrue(assembler.matchOpCode(StringUtils.trimWhiteSpace(s)));
		s = "JSR";
		assertTrue(assembler.matchOpCode(StringUtils.trimWhiteSpace(s)));
		s = "LDA";
		assertTrue(assembler.matchOpCode(StringUtils.trimWhiteSpace(s)));
		s = "LDX";
		assertTrue(assembler.matchOpCode(StringUtils.trimWhiteSpace(s)));
		s = "LDY";
		assertTrue(assembler.matchOpCode(StringUtils.trimWhiteSpace(s)));
		s = "LSR";
		assertTrue(assembler.matchOpCode(StringUtils.trimWhiteSpace(s)));
		s = "ORA";
		assertTrue(assembler.matchOpCode(StringUtils.trimWhiteSpace(s)));
		s = "PHA";
		assertTrue(assembler.matchOpCode(StringUtils.trimWhiteSpace(s)));
		s = "PHP";
		assertTrue(assembler.matchOpCode(StringUtils.trimWhiteSpace(s)));
		s = "PLA";
		assertTrue(assembler.matchOpCode(StringUtils.trimWhiteSpace(s)));
		s = "PLP";
		assertTrue(assembler.matchOpCode(StringUtils.trimWhiteSpace(s)));
		s = "ROL";
		assertTrue(assembler.matchOpCode(StringUtils.trimWhiteSpace(s)));
		s = "ROR";
		assertTrue(assembler.matchOpCode(StringUtils.trimWhiteSpace(s)));
		s = "RTI";
		assertTrue(assembler.matchOpCode(StringUtils.trimWhiteSpace(s)));
		s = "RTS";
		assertTrue(assembler.matchOpCode(StringUtils.trimWhiteSpace(s)));
		s = "SBC";
		assertTrue(assembler.matchOpCode(StringUtils.trimWhiteSpace(s)));
		s = "SEC";
		assertTrue(assembler.matchOpCode(StringUtils.trimWhiteSpace(s)));
		s = "SED";
		assertTrue(assembler.matchOpCode(StringUtils.trimWhiteSpace(s)));
		s = "SEI";
		assertTrue(assembler.matchOpCode(StringUtils.trimWhiteSpace(s)));
		s = "STA";
		assertTrue(assembler.matchOpCode(StringUtils.trimWhiteSpace(s)));
		s = "STX";
		assertTrue(assembler.matchOpCode(StringUtils.trimWhiteSpace(s)));
		s = "STY";
		assertTrue(assembler.matchOpCode(StringUtils.trimWhiteSpace(s)));
		s = "TAX";
		assertTrue(assembler.matchOpCode(StringUtils.trimWhiteSpace(s)));
		s = "TAY";
		assertTrue(assembler.matchOpCode(StringUtils.trimWhiteSpace(s)));
		s = "TSX";
		assertTrue(assembler.matchOpCode(StringUtils.trimWhiteSpace(s)));
		s = "TXA";
		assertTrue(assembler.matchOpCode(StringUtils.trimWhiteSpace(s)));
		s = "TXS";
		assertTrue(assembler.matchOpCode(StringUtils.trimWhiteSpace(s)));
		s = "TYA";
		assertTrue(assembler.matchOpCode(StringUtils.trimWhiteSpace(s)));
		//Check some fake op codes
		s = "ANR";
		assertFalse(assembler.matchOpCode(StringUtils.trimWhiteSpace(s)));
		s = "BNR";
		assertFalse(assembler.matchOpCode(StringUtils.trimWhiteSpace(s)));
		s = "CNR";
		assertFalse(assembler.matchOpCode(StringUtils.trimWhiteSpace(s)));
		s = "DNR";
		assertFalse(assembler.matchOpCode(StringUtils.trimWhiteSpace(s)));
		s = "ENR";
		assertFalse(assembler.matchOpCode(StringUtils.trimWhiteSpace(s)));
		s = "INR";
		assertFalse(assembler.matchOpCode(StringUtils.trimWhiteSpace(s)));
		s = "JNR";
		assertFalse(assembler.matchOpCode(StringUtils.trimWhiteSpace(s)));
		s = "LNR";
		assertFalse(assembler.matchOpCode(StringUtils.trimWhiteSpace(s)));
		s = "ONR";
		assertFalse(assembler.matchOpCode(StringUtils.trimWhiteSpace(s)));
		s = "PNR";
		assertFalse(assembler.matchOpCode(StringUtils.trimWhiteSpace(s)));
		s = "RNR";
		assertFalse(assembler.matchOpCode(StringUtils.trimWhiteSpace(s)));
		s = "SNR";
		assertFalse(assembler.matchOpCode(StringUtils.trimWhiteSpace(s)));
		s = "TNR";
		assertFalse(assembler.matchOpCode(StringUtils.trimWhiteSpace(s)));
		
	}
	
	public void testAddressParse() {
		Assembler assembler = new Assembler();
		String s = "#$F2";
		assertTrue(assembler.checkAddressingMode(s));
		assertEquals(AssemblyUtils.IMMEDIATE, assembler.getAddressMode());
		assertEquals("F2", assembler.getAddressString());
	}
	
	public void testOpCodeParseImmediate() {
		Assembler assembler = new Assembler();
		String s = "AND #$32";
		assembler.parseOpCode(StringUtils.trimWhiteSpace(s));
		s = "ADC #32";
		assembler.parseOpCode(StringUtils.trimWhiteSpace(s));
		s = "CPX #$32";
		assembler.parseOpCode(StringUtils.trimWhiteSpace(s));
		s = "LDA #32";
		assembler.parseOpCode(StringUtils.trimWhiteSpace(s));
		s = "LDX #%01010101";
		assembler.parseOpCode(StringUtils.trimWhiteSpace(s));
		s = "LDY #32";
		assembler.parseOpCode(StringUtils.trimWhiteSpace(s));
		s = "CPX #%10101010";
		assembler.parseOpCode(StringUtils.trimWhiteSpace(s));
		s = "CPY #32";
		assembler.parseOpCode(StringUtils.trimWhiteSpace(s));
		s = "EOR #$32";
		assembler.parseOpCode(StringUtils.trimWhiteSpace(s));
		s = "AND #%10";
		assembler.parseOpCode(StringUtils.trimWhiteSpace(s));
		
		assembler.printListing(new File("C:/users/ryan/desktop/immediate.bin"));
	}
	
	public void testOpCodeParseAccumulator() {
		Assembler assembler = new Assembler();
		String s = "ASL A";
		assembler.parseOpCode(StringUtils.trimWhiteSpace(s));
		assertEquals(0x0A, assembler.getOpCode());
		assertEquals(-1, assembler.getAddress());
		s = "LSR A";
		assembler.parseOpCode(StringUtils.trimWhiteSpace(s));
		assertEquals(0x4A, assembler.getOpCode());
		assertEquals(-1, assembler.getAddress());
		s = "ROL A";
		assembler.parseOpCode(StringUtils.trimWhiteSpace(s));
		assertEquals(0x2A, assembler.getOpCode());
		assertEquals(-1, assembler.getAddress());
		s = "ROR A";
		assembler.parseOpCode(StringUtils.trimWhiteSpace(s));
		assertEquals(0x6A, assembler.getOpCode());
		assertEquals(-1, assembler.getAddress());
		
		assembler.printListing(new File("C:/users/ryan/desktop/accumulator.bin"));
	}
	
	public void testOpCodeParseImplied() {
		Assembler assembler = new Assembler();
		String s = "BRK";
		assembler.parseOpCode(StringUtils.trimWhiteSpace(s));
		assertEquals(0x00, assembler.getOpCode());
		assertEquals(-1, assembler.getAddress());
		s = "CLC";
		assembler.parseOpCode(StringUtils.trimWhiteSpace(s));
		assertEquals(0x18, assembler.getOpCode());
		assertEquals(-1, assembler.getAddress());
		s = "SEC";
		assembler.parseOpCode(StringUtils.trimWhiteSpace(s));
		assertEquals(0x38, assembler.getOpCode());
		assertEquals(-1, assembler.getAddress());
		s = "CLI";
		assembler.parseOpCode(StringUtils.trimWhiteSpace(s));
		assertEquals(0x58, assembler.getOpCode());
		assertEquals(-1, assembler.getAddress());
		s = "SEI";
		assembler.parseOpCode(StringUtils.trimWhiteSpace(s));
		assertEquals(0x78, assembler.getOpCode());
		assertEquals(-1, assembler.getAddress());
		s = "CLV";
		assembler.parseOpCode(StringUtils.trimWhiteSpace(s));
		assertEquals(0xB8, assembler.getOpCode());
		assertEquals(-1, assembler.getAddress());
		s = "CLD";
		assembler.parseOpCode(StringUtils.trimWhiteSpace(s));
		assertEquals(0xD8, assembler.getOpCode());
		assertEquals(-1, assembler.getAddress());
		s = "SED";
		assembler.parseOpCode(StringUtils.trimWhiteSpace(s));
		assertEquals(0xF8, assembler.getOpCode());
		assertEquals(-1, assembler.getAddress());
		s = "NOP";
		assembler.parseOpCode(StringUtils.trimWhiteSpace(s));
		assertEquals(0xEA, assembler.getOpCode());
		assertEquals(-1, assembler.getAddress());
		s = "TAX";
		assembler.parseOpCode(StringUtils.trimWhiteSpace(s));
		assertEquals(0xAA, assembler.getOpCode());
		assertEquals(-1, assembler.getAddress());
		s = "TXA";
		assembler.parseOpCode(StringUtils.trimWhiteSpace(s));
		assertEquals(0x8A, assembler.getOpCode());
		assertEquals(-1, assembler.getAddress());
		s = "DEX";
		assembler.parseOpCode(StringUtils.trimWhiteSpace(s));
		assertEquals(0xCA, assembler.getOpCode());
		assertEquals(-1, assembler.getAddress());
		s = "INX";
		assembler.parseOpCode(StringUtils.trimWhiteSpace(s));
		assertEquals(0xE8, assembler.getOpCode());
		assertEquals(-1, assembler.getAddress());
		s = "TAY";
		assembler.parseOpCode(StringUtils.trimWhiteSpace(s));
		assertEquals(0xA8, assembler.getOpCode());
		assertEquals(-1, assembler.getAddress());
		s = "TYA";
		assembler.parseOpCode(StringUtils.trimWhiteSpace(s));
		assertEquals(0x98, assembler.getOpCode());
		assertEquals(-1, assembler.getAddress());
		s = "DEY";
		assembler.parseOpCode(StringUtils.trimWhiteSpace(s));
		assertEquals(0x88, assembler.getOpCode());
		assertEquals(-1, assembler.getAddress());
		s = "INY";
		assembler.parseOpCode(StringUtils.trimWhiteSpace(s));
		assertEquals(0xC8, assembler.getOpCode());
		assertEquals(-1, assembler.getAddress());
		s = "RTI";
		assembler.parseOpCode(StringUtils.trimWhiteSpace(s));
		assertEquals(0x40, assembler.getOpCode());
		assertEquals(-1, assembler.getAddress());
		s = "RTS";
		assembler.parseOpCode(StringUtils.trimWhiteSpace(s));
		assertEquals(0x60, assembler.getOpCode());
		assertEquals(-1, assembler.getAddress());
		s = "TXS";
		assembler.parseOpCode(StringUtils.trimWhiteSpace(s));
		assertEquals(0x9A, assembler.getOpCode());
		assertEquals(-1, assembler.getAddress());
		s = "TSX";
		assembler.parseOpCode(StringUtils.trimWhiteSpace(s));
		assertEquals(0xBA, assembler.getOpCode());
		assertEquals(-1, assembler.getAddress());
		s = "PHA";
		assembler.parseOpCode(StringUtils.trimWhiteSpace(s));
		assertEquals(0x48, assembler.getOpCode());
		assertEquals(-1, assembler.getAddress());
		s = "PLA";
		assembler.parseOpCode(StringUtils.trimWhiteSpace(s));
		assertEquals(0x68, assembler.getOpCode());
		assertEquals(-1, assembler.getAddress());
		s = "PHP";
		assembler.parseOpCode(StringUtils.trimWhiteSpace(s));
		assertEquals(0x08, assembler.getOpCode());
		assertEquals(-1, assembler.getAddress());
		s = "PLP";
		assembler.parseOpCode(StringUtils.trimWhiteSpace(s));
		assertEquals(0x28, assembler.getOpCode());
		assertEquals(-1, assembler.getAddress());
		

		assembler.printListing(new File("C:/users/ryan/desktop/implied.bin"));
	}
	
	public void testOpCodeParseZeroPage() {
		Assembler assembler = new Assembler();
		String s = "ASL $45";
		assembler.parseOpCode(StringUtils.trimWhiteSpace(s));
		assertEquals(0x06, assembler.getOpCode());
		assertEquals(0x45, assembler.getAddress());
		s = "LSR $F2";
		assembler.parseOpCode(StringUtils.trimWhiteSpace(s));
		assertEquals(0x46, assembler.getOpCode());
		assertEquals(0xF2, assembler.getAddress());
		s = "LSR $F2,X";
		assembler.parseOpCode(StringUtils.trimWhiteSpace(s));
		assertEquals(0x56, assembler.getOpCode());
		assertEquals(0xF2, assembler.getAddress());
		s = "ROL 32";
		assembler.parseOpCode(StringUtils.trimWhiteSpace(s));
		assertEquals(0x26, assembler.getOpCode());
		assertEquals(32, assembler.getAddress());
		s = "ROR $ED";
		assembler.parseOpCode(StringUtils.trimWhiteSpace(s));
		assertEquals(0x66, assembler.getOpCode());
		assertEquals(0xED, assembler.getAddress());
		
		assembler.printListing(new File("C:/users/ryan/desktop/zeroPage.bin"));
	}
	
	public void testOpCodeParseAbsolute() {
		Assembler assembler = new Assembler();
		String s = "ASL $4400";
		assembler.parseOpCode(StringUtils.trimWhiteSpace(s));
		assertEquals(0x0E, assembler.getOpCode());
		assertEquals(0x4400, assembler.getAddress());
		s = "LSR $4200";
		assembler.parseOpCode(StringUtils.trimWhiteSpace(s));
		assertEquals(0x4E, assembler.getOpCode());
		assertEquals(0x4200, assembler.getAddress());
		s = "LSR $4400,X";
		assembler.parseOpCode(StringUtils.trimWhiteSpace(s));
		assertEquals(0x5E, assembler.getOpCode());
		assertEquals(0x4400, assembler.getAddress());
		s = "ROL 17408";
		assembler.parseOpCode(StringUtils.trimWhiteSpace(s));
		assertEquals(0x2E, assembler.getOpCode());
		assertEquals(0x4400, assembler.getAddress());
		s = "ROR $4400";
		assembler.parseOpCode(StringUtils.trimWhiteSpace(s));
		assertEquals(0x6E, assembler.getOpCode());
		assertEquals(0x4400, assembler.getAddress());
		
		assembler.printListing(new File("C:/users/ryan/desktop/absolute.bin"));
	}
	
	public void testMixedBag() {
		Assembler assembler = new Assembler();
		String s;
		s = "LDX #$01         ; Comment goes here";
		assembler.parseOpCode(s);
		assertEquals(0xA2, assembler.getOpCode());
		s = "LDA #$05; Comment goes here";
		assembler.parseOpCode(s);
		assertEquals(0xA9, assembler.getOpCode());
		s = "STA $01; Comment goes here";
		assembler.parseOpCode(s);
		assertEquals(0x85, assembler.getOpCode());
		s = "LDA #$06; Comment goes here";
		assembler.parseOpCode(s);
		assertEquals(0xA9, assembler.getOpCode());
		s = "STA $02; Comment goes here";
		assembler.parseOpCode(s);
		assertEquals(0x85, assembler.getOpCode());
		s = "LDY #$0A; Comment goes here";
		assembler.parseOpCode(s);
		assertEquals(0xA0, assembler.getOpCode());
		s = "STY $0605; Comment goes here";
		assembler.parseOpCode(s);
		assertEquals(0x8C, assembler.getOpCode());
		s = "LDA ($00,X); Comment goes here";
		assembler.parseOpCode(s);
		assertEquals(0xA1, assembler.getOpCode());
		assembler.printListing(new File("C:/users/ryan/desktop/mix.bin"));
	}
	
	

}
