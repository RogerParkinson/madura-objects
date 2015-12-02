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

public class ParserTest {

	@Test
	public void testExactStringTextProvider() {
		InputStream is = this.getClass().getResourceAsStream("OneExpression.txt");
		ParserSource parserSource = new InputStreamParserSource(is, "OneExpression.txt");
		TextProvider textProvider = new TestTextProvider(parserSource);
		Parser parser = new Parser();
		parser.setDebug(true);
		parser.exactOrError("invoiceCount", textProvider);
		parser.exactOrError("=", textProvider);
		parser.exactOrError("count(invoices", textProvider);
		parser.exactOrError(")", textProvider);
		assertFalse(parser.exact("=", textProvider));
		assertTrue(parser.exact("/", textProvider));
		assertFalse(parser.exact(";", textProvider));
		assertFalse(parser.exact(",", textProvider));
		assertFalse(parser.exact("!", textProvider));
		assertTrue(parser.exact("(", textProvider));
	}

}
