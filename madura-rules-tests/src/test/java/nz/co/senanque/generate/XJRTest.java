package nz.co.senanque.generate;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import nz.co.senanque.parser.InputStreamParserSource;
import nz.co.senanque.parser.ParserSource;
import nz.co.senanque.rulesparser.FunctionDescriptorFactory;
import nz.co.senanque.rulesparser.ParsePackage;
import nz.co.senanque.rulesparser.RulesTextProvider;
import nz.co.senanque.schemaparser.SchemaParserImpl;

import org.apache.tools.ant.BuildException;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.junit.Test;

public class XJRTest {

	String SOURCE_DIR = "src/main/resources/";
	Document m_document = null;
	Class<?> m_externalFunctionsClass = null;
	
	private Document getSchema() {
		if (m_document == null) {
			SAXBuilder builder = new SAXBuilder();
			try {
				m_document = builder.build(new File(SOURCE_DIR + "Customer.xsd"));
			} catch (Exception e) {
				throw new BuildException(e);
			}
		}
		return m_document;
	}
	
	private Class<?> getExternalFunctions() throws Exception
	{
		if (m_externalFunctionsClass == null) {
			m_externalFunctionsClass = Class.forName("nz.co.senanque.pizzaorder.externals.MyExternalFunctions");
		}
		return m_externalFunctionsClass;
	}
    @Test
    public void testListFunctions() throws Exception
    {
        SchemaParserImpl schemaParser = new SchemaParserImpl();
        schemaParser.parse(getSchema(), "nz.co.senanque.base");
        List<Class<?>> externalFunctions = new ArrayList<Class<?>>();
        externalFunctions.add(getExternalFunctions());
        String fileName = SOURCE_DIR+"ListFunctions.rul";
        ParserSource parserSource = new InputStreamParserSource(new FileInputStream(fileName), fileName);
        RulesTextProvider fileProvider = new RulesTextProvider(parserSource, schemaParser, externalFunctions);
        new FunctionDescriptorFactory().loadOperators(fileProvider);
        ParsePackage pp = new ParsePackage();
        pp.parse(fileProvider);

    }

}
