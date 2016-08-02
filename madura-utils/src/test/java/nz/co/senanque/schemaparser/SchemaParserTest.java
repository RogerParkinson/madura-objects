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
package nz.co.senanque.schemaparser;

import static org.junit.Assert.assertEquals;

import java.util.Set;

import javax.annotation.Resource;

import nz.co.senanque.schemaparser.restrictions.Restrictions;

import org.dom4j.DocumentHelper;
import org.dom4j.QName;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * @author Roger Parkinson
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class SchemaParserTest {

	private static final Logger log = LoggerFactory
			.getLogger(SchemaParserTest.class);
	@Autowired
	ApplicationContext m_applicationContext;
	@Resource(name="schema")
	org.springframework.core.io.Resource m_schema;

	/**
	 * Test method for
	 * {@link nz.co.senanque.processparser.ParsePackage#parse(nz.co.senanque.parser.TextProvider)}
	 * .
	 */
	@Test
	public void testParse() throws Exception {
		
		log.debug("starting");

		SAXBuilder builder = new SAXBuilder();
		Document doc = null;
		doc = builder.build(m_schema.getInputStream());
		SchemaParser schemaParser = new SchemaParser();
		schemaParser.parse(doc, "nz.co.senanque.processparser");
	}
	@Test
	public void testParse2() throws Exception {

		SAXBuilder builder = new SAXBuilder();
		Document doc = null;
		doc = builder.build(m_schema.getInputStream());
		SchemaParser schemaParser = new SchemaParser();
		schemaParser.parse(doc);
		Set<String> operands = schemaParser.findOperandsInScope("Order", "");
		assertEquals(8,operands.size());
	}
	@Test
	public void testParse3() throws Exception {

		SAXBuilder builder = new SAXBuilder();
		Document doc = null;
		doc = builder.build(m_schema.getInputStream());
		SchemaParser schemaParser = new SchemaParser();
		schemaParser.parse(doc);
		QName root = DocumentHelper.createQName("root",DocumentHelper.createNamespace("root", "http://www.madurasoftware.com/root"));
		SchemaVisitorDom4j visitor = new SchemaVisitorDom4j(root,"PizzaOrder.xsd","http://www.madurasoftware.com/root.xsd");
		schemaParser.traverse(visitor);
		
        OutputFormat format = OutputFormat.createPrettyPrint();
        XMLWriter writer = new XMLWriter( System.out, format );
        writer.write( visitor.getDocument() );
	}
	@Test
	public void testParse4() throws Exception {

		SAXBuilder builder = new SAXBuilder();
		Document doc = null;
		doc = builder.build(m_schema.getInputStream());
		SchemaParser schemaParser = new SchemaParser();
		schemaParser.parse(doc);
		Namespace ns = Namespace.getNamespace("root", "http://www.madurasoftware.com/root");
		Element root = new Element("root",ns);
		SchemaVisitorJdom visitor = new SchemaVisitorJdom(root,"PizzaOrder.xsd","http://www.madurasoftware.com/root.xsd");
		schemaParser.traverse(visitor);
		
		XMLOutputter fmt = new XMLOutputter(Format.getPrettyFormat());
		fmt.output(visitor.getDocument(), System.out);
		
		FieldDescriptor fd = schemaParser.findOperandInScope("Order", "age");
		Restrictions restrictions = fd.getRestrictions();
		assertEquals(2,restrictions.getRestrictions().size());
	}
}
