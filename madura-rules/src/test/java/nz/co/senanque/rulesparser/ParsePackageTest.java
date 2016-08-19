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
package nz.co.senanque.rulesparser;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import nz.co.senanque.parser.InputStreamParserSource;
import nz.co.senanque.parser.ParserSource;
import nz.co.senanque.schemaparser.SchemaParserImpl;

import org.apache.tools.ant.BuildException;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.junit.Test;

/**
 * 
 * Tests the rules parser
 * 
 * @author Roger Parkinson
 * @version $Revision: 1.4 $
 */
public class ParsePackageTest
{
	
	String SOURCE_DIR = "src/test/resources/nz/co/senanque/rulesparser/";
	Document m_document = null;
	Class<?> m_externalFunctionsClass = null;
	
	private Document getSchema() {
		if (m_document == null) {
			SAXBuilder builder = new SAXBuilder();
			try {
				m_document = builder.build(new File(SOURCE_DIR + "schema.xsd"));
			} catch (Exception e) {
				throw new BuildException(e);
			}
		}
		return m_document;
	}
	
	private Class<?> getExternalFunctions() throws Exception
	{
		if (m_externalFunctionsClass == null) {
			m_externalFunctionsClass = Class.forName("nz.co.senanque.rulesparser.SampleExternalFunctions");
		}
		return m_externalFunctionsClass;
	}

    /**
     * Test method for
     * {@link nz.co.senanque.rulesparser.ParsePackage#parse(nz.co.senanque.parser.TextProvider)}
     * .
     */
    @Test
    public void testParse() throws Exception
    {
        SchemaParserImpl schemaParser = new SchemaParserImpl();
        schemaParser.parse(getSchema(), "nz.co.senanque.rulesparser");
        List<Class<?>> externalFunctions = new ArrayList<Class<?>>();
        externalFunctions.add(getExternalFunctions());
        String fileName = SOURCE_DIR+"TestRules.txt";
        ParserSource parserSource = new InputStreamParserSource(new FileInputStream(fileName), fileName, 3000);
        RulesTextProvider fileProvider = new RulesTextProvider(parserSource, schemaParser, externalFunctions);
        new FunctionDescriptorFactory().loadOperators(fileProvider);
        ParsePackage pp = new ParsePackage();
//        pp.setDebug(true);
        pp.parse(fileProvider);

    }
    @Test
    public void testLogicalNot() throws Exception
    {
        SchemaParserImpl schemaParser = new SchemaParserImpl();
        schemaParser.parse(getSchema(), "nz.co.senanque.rulesparser");
        List<Class<?>> externalFunctions = new ArrayList<Class<?>>();
        externalFunctions.add(getExternalFunctions());
        String fileName = SOURCE_DIR+"TestLogicalNot.txt";
        ParserSource parserSource = new InputStreamParserSource(new FileInputStream(fileName), fileName);
        RulesTextProvider fileProvider = new RulesTextProvider(parserSource, schemaParser, externalFunctions);
        new FunctionDescriptorFactory().loadOperators(fileProvider);
        ParsePackage pp = new ParsePackage();
        pp.parse(fileProvider);

    }

    @Test
    public void testListFunctions() throws Exception
    {
        SchemaParserImpl schemaParser = new SchemaParserImpl();
        schemaParser.parse(getSchema(), "nz.co.senanque.rulesparser");
        List<Class<?>> externalFunctions = new ArrayList<Class<?>>();
        externalFunctions.add(getExternalFunctions());
        String fileName = SOURCE_DIR+"TestListFunctions.txt";
        ParserSource parserSource = new InputStreamParserSource(new FileInputStream(fileName), fileName);
        RulesTextProvider fileProvider = new RulesTextProvider(parserSource, schemaParser, externalFunctions);
        new FunctionDescriptorFactory().loadOperators(fileProvider);
        ParsePackage pp = new ParsePackage();
        pp.parse(fileProvider);

    }

    @Test
    public void testNotKnownFunction() throws Exception
    {
        SchemaParserImpl schemaParser = new SchemaParserImpl();
        schemaParser.parse(getSchema(), "nz.co.senanque.rulesparser");
        List<Class<?>> externalFunctions = new ArrayList<Class<?>>();
        externalFunctions.add(getExternalFunctions());
        String fileName = SOURCE_DIR+"TestNotKnownFunction.txt";
        ParserSource parserSource = new InputStreamParserSource(new FileInputStream(fileName), fileName);
        RulesTextProvider fileProvider = new RulesTextProvider(parserSource, schemaParser, externalFunctions);
        new FunctionDescriptorFactory().loadOperators(fileProvider);
        ParsePackage pp = new ParsePackage();
        pp.parse(fileProvider);

    }

    @Test
    public void testPizzaOrder() throws Exception
    {
        SchemaParserImpl schemaParser = new SchemaParserImpl();
		SAXBuilder builder = new SAXBuilder();
		Document schemaDocument = builder.build(new File("src/test/resources/nz/co/senanque/pizzaorder/PizzaOrder.xsd"));
        schemaParser.parse(schemaDocument, "nz.co.senanque.pizzaorder");
        List<Class<?>> externalFunctions = new ArrayList<Class<?>>();
        String fileName = "src/test/resources/nz/co/senanque/pizzaorder/PizzaOrderRules.txt";
        ParserSource parserSource = new InputStreamParserSource(new FileInputStream(fileName), fileName, 2000);
        RulesTextProvider fileProvider = new RulesTextProvider(parserSource, schemaParser, externalFunctions);
        new FunctionDescriptorFactory().loadOperators(fileProvider);
        ParsePackage pp = new ParsePackage();
        pp.parse(fileProvider);

    }

    @Test
    public void testBufferOverflow() throws Exception
    {
        SchemaParserImpl schemaParser = new SchemaParserImpl();
		SAXBuilder builder = new SAXBuilder();
		Document schemaDocument = builder.build(new File("src/test/resources/nz/co/senanque/bufferoverflow/overflow.xsd"));
        schemaParser.parse(schemaDocument, "nz.co.senanque.pizzaorder");
        List<Class<?>> externalFunctions = new ArrayList<Class<?>>();
        String fileName = "src/test/resources/nz/co/senanque/bufferoverflow/overflow.rul";
        ParserSource parserSource = new InputStreamParserSource(new FileInputStream(fileName), fileName, 400);
        RulesTextProvider fileProvider = new RulesTextProvider(parserSource, schemaParser, externalFunctions);
        new FunctionDescriptorFactory().loadOperators(fileProvider);
        ParsePackage pp = new ParsePackage();
        pp.setDebug(true);
        pp.parse(fileProvider);

    }

}
