/*******************************************************************************
 * Copyright (c)2014 Prometheus Consulting
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package nz.co.senanque.parser;

import static org.junit.Assert.*;

import java.io.InputStream;

import org.junit.Test;

/**
 * @author Roger Parkinson
 *
 */
public class InputStreamParserSourceTest {

	/**
	 * Test method for {@link nz.co.senanque.parser.InputStreamParserSource#getNextChar()}.
	 */
	@Test
	public void testGetNextChar1() {
		InputStream is = this.getClass().getResourceAsStream("TestRules.txt");
		// Force the buffer to be very small to check it switches correctly.
		ParserSource parserSource = new InputStreamParserSource(is, "TestRules.txt",5);
		char c = parserSource.getNextChar();
		parserSource.commit();
		while (c != ')') {
			c = parserSource.getNextChar();
			parserSource.commit();
		}
		assertEquals(c,')');
		c = parserSource.getNextChar();
		parserSource.commit();
		assertEquals(c,'/');
		c = parserSource.getNextChar();
		parserSource.commit();
		assertEquals(c,'(');
		c = parserSource.getNextChar();
		parserSource.commit();
		assertEquals(c,'5');
	}
	@Test
	public void testGetNextChar2() {
		InputStream is = this.getClass().getResourceAsStream("Directed.txt");
		ParserSource parserSource = new InputStreamParserSource(is, "Directed.txt");
		char c = parserSource.getNextChar();
		parserSource.commit();
		while (c != '=') {
			c = parserSource.getNextChar();
			parserSource.commit();
		}
		while (c != 't') {
			c = parserSource.getNextChar();
			parserSource.commit();
		}
		c = parserSource.getNextChar();
		parserSource.commit();
		assertEquals(c,' ');
		c = parserSource.getNextChar();
		parserSource.commit();
		assertEquals(c,'/');
		c = parserSource.getNextChar();
		parserSource.commit();
		assertEquals(c,' ');
		c = parserSource.getNextChar();
		parserSource.commit();
		assertEquals(c,'(');
	}
	@Test
	public void testGetNextChar3() {
		InputStream is = this.getClass().getResourceAsStream("NotKnown.txt");
		ParserSource parserSource = new InputStreamParserSource(is, "NotKnown.txt");
		char c = parserSource.getNextChar();
		parserSource.commit();
		while (c != '=') {
			c = parserSource.getNextChar();
			parserSource.commit();
		}
		while (c != 't') {
			c = parserSource.getNextChar();
			parserSource.commit();
		}
		c = parserSource.getNextChar();
		parserSource.commit();
		assertEquals(c,' ');
		c = parserSource.getNextChar();
		parserSource.commit();
		assertEquals(c,'/');
		c = parserSource.getNextChar();
		parserSource.commit();
		assertEquals(c,' ');
		c = parserSource.getNextChar();
		parserSource.commit();
		assertEquals(c,'(');
	}
	@Test
	public void testGetNextChar4() {
		InputStream is = this.getClass().getResourceAsStream("EOF.txt");
		// Force the buffer to be very small to check it switches correctly.
		ParserSource parserSource = new InputStreamParserSource(is, "EOF.txt",80);
		char c = parserSource.getNextChar();
		while (c != '}') {
			parserSource.commit();
			c = parserSource.getNextChar();
		}
		assertEquals(c,'}');
		c = parserSource.getNextChar();
        while (" \t\r".indexOf(c) != -1)
        {
    		parserSource.commit();
        	c = parserSource.getNextChar();
        }
        boolean exceptionFound = false;
		try {
			c = parserSource.getNextChar();
		} catch (EndOfDataException e) {
			exceptionFound = true;
		}
		assertTrue(exceptionFound);
	}

}
