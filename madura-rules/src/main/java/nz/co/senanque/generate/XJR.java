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
package nz.co.senanque.generate;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import nz.co.senanque.parser.InputStreamParserSource;
import nz.co.senanque.parser.ParserException;
import nz.co.senanque.parser.ParserSource;
import nz.co.senanque.rulesparser.AbstractRule;
import nz.co.senanque.rulesparser.FunctionDescriptorFactory;
import nz.co.senanque.rulesparser.ParsePackage;
import nz.co.senanque.rulesparser.RulesTextProvider;
import nz.co.senanque.schemaparser.SchemaParser;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * This is an ant task that can invoke the rules parser. 
 * 
 * @author Roger Parkinson
 * @version $Revision: 1.9 $
 */
public class XJR extends Task
{
    private static final Logger log = LoggerFactory.getLogger(XJR.class);
    String m_rules;
    String m_packageName;
    String m_schema;
    String m_destdir;
    private int m_bufferSize=3000;
    public int getBufferSize() {
		return m_bufferSize;
	}

	public void setBufferSize(int bufferSize) {
		this.m_bufferSize = bufferSize;
	}

	List<ClassReference> m_externalFunctionClasses = new ArrayList<ClassReference>();
    private String m_xsdpackageName;
    
    public ClassReference createClassReference()
    {
        ClassReference cr = new ClassReference();
        m_externalFunctionClasses.add(cr);
        return cr;
    }

    public void execute() {
        // parse the schema
        SAXBuilder builder = new SAXBuilder();
//        builder.setValidation(true);
//        builder.setIgnoringElementContentWhitespace(true);
        Document doc=null;
        try
        {
            doc = builder.build(new File(m_schema));
        }
        catch (Exception e)
        {
           throw new BuildException(e);
        }
        SchemaParser schemaParser = new SchemaParser();
        schemaParser.parse(doc,(m_xsdpackageName==null)?m_packageName:m_xsdpackageName);
        List<Class<?>> externalFunctions = new ArrayList<Class<?>>();
        if (m_externalFunctionClasses != null)
        {
            for (ClassReference cr: m_externalFunctionClasses)
            {
                try
                {
                    externalFunctions.add(Class.forName(cr.getName()));
                }
                catch (ClassNotFoundException e)
                {
                    throw new BuildException(e);
                }
            }
        }
        
		ParserSource parserSource;
		try {
			parserSource = new InputStreamParserSource(new FileInputStream(m_rules),m_rules,m_bufferSize);
		} catch (FileNotFoundException e3) {
			throw new BuildException(e3);
		}

        // parse the rules
        RulesTextProvider textProvider = new RulesTextProvider(parserSource, schemaParser,externalFunctions);
        new FunctionDescriptorFactory().loadOperators(textProvider);
        ParsePackage parsePackage = new ParsePackage();
        try
        {
            parsePackage.parse(textProvider);
        }
        catch (ParserException e2)
        {
            throw new BuildException(e2);
        }
        if (log.isDebugEnabled())
        {
            log.debug(textProvider.toString());
        }

        // generate the output
        String packageDirectory = m_destdir+"/";
        File targetDir = new File(packageDirectory+(m_packageName.replace('.','/'))+"/");
        targetDir.mkdirs();
        targetDir = new File(packageDirectory);
        Generator generator = new Generator();
        PrintStream messages=null;
        try
        {
            messages = new PrintStream(new FileOutputStream(packageDirectory+(m_packageName.replace('.','/'))+"/messages.properties"));
        }
        catch (FileNotFoundException e1)
        {
            throw new BuildException(e1);
        }
        for (AbstractRule rule: textProvider.getRules())
        {
            messages.println(m_packageName+"."+rule.getName()+"="+rule.getMessage());
            try
            {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                generator.generate(m_packageName, rule, m_xsdpackageName, targetDir, new PrintStream(baos));
                baos.flush();
                baos.close();
                log.debug(baos.toString());
            }
            catch (Exception e)
            {
                throw new BuildException(e);
            }
        }
        try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			generator.generate(m_packageName, targetDir, new PrintStream(baos));
			baos.flush();
			baos.close();
			log.debug(baos.toString());
		} catch (Exception e) {
			throw new BuildException(e);
		}

        messages.close();
    }

    public String getRules()
    {
        return m_rules;
    }

    public void setRules(String rules)
    {
        m_rules = rules;
    }

    public String getSchema()
    {
        return m_schema;
    }

    public void setSchema(String schema)
    {
        m_schema = schema;
    }

    public String getDestdir()
    {
        return m_destdir;
    }

    public void setDestdir(String destdir)
    {
        m_destdir = destdir;
    }

    public String getPackageName()
    {
        return m_packageName;
    }

    public void setPackageName(String packageName)
    {
        m_packageName = packageName;
    }

    public void setXSDPackageName(String packageName)
    {
        m_xsdpackageName = packageName;
        
    }

}
