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
package nz.co.senanque.madura_rules_maven_plugin;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import net.peachjean.slf4j.mojo.AbstractLoggingMojo;
import nz.co.senanque.generate.ClassReference;
import nz.co.senanque.generate.Generator;
import nz.co.senanque.parser.InputStreamParserSource;
import nz.co.senanque.parser.ParserException;
import nz.co.senanque.parser.ParserSource;
import nz.co.senanque.rulesparser.AbstractRule;
import nz.co.senanque.rulesparser.FunctionDescriptorFactory;
import nz.co.senanque.rulesparser.ParsePackage;
import nz.co.senanque.rulesparser.RulesTextProvider;
import nz.co.senanque.schemaparser.SchemaParser;

import org.apache.maven.plugin.MojoExecutionException;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;

/**
 * Goal which generates java rules from the Madura Rules file
 * 
 * @goal xjr
 * 
 * @phase generate-sources
 */
public class XJRMojo
    extends AbstractLoggingMojo
{
	/**
	 * List of external classes
	 * 
	 * @parameter
	 * @readonly
	 * 
	 */
	private String[] externalFunctionClasses;
	
	/**
	 * Location of the resourceDirectory.
	 * 
	 * @parameter expression="${basedir}/src/main/resources"
	 * @readonly
	 * 
	 */
	private File resourceDirectory;

	/**
	 * Location of the targetDirectory.
	 * 
	 * @parameter expression="${basedir}/generated-sources/xjc"
	 */
    private File destdir;

    /**
	 * Name of the rulesFile.
	 * 
	 * @parameter
	 * @required
	 */
    private String rules;
	/**
	 * Name of the schemaFile.
	 * 
	 * @parameter
	 * @required
	 */
    private String schema;
	/**
	 * Name of the package.
	 * 
	 * @parameter
	 * @required
	 */
    private String packageName;

    /**
	 * buffer size for parser
	 * 
	 * @parameter
	 */
    private int bufferSize=1000;
    
	public int getBufferSize() {
		return bufferSize;
	}

	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	/**
	 * Name of the xsdpackage.
	 * Defaults to the packageName
	 * 
	 * @parameter
	 */
    private String xsdpackageName;
    
    private String getPath(String name)
    {
    	if (resourceDirectory != null) {
    		return resourceDirectory.getAbsolutePath()+File.separatorChar+name;
    	}
    	Object base = getPluginContext().get("project.base");
    	if (base != null && base instanceof String) {
    		return base.toString()+File.separatorChar+name;
    	}
    	return name;
    }

    public void executeWithLogging()
        throws MojoExecutionException
    {
        // parse the schema
        SAXBuilder builder = new SAXBuilder();
        schema = getPath(schema);
        rules = getPath(rules);
        Document doc=null;
        try
        {
            doc = builder.build(schema);
        }
        catch (Exception e)
        {
           throw new MojoExecutionException(e.getMessage());
        }
        SchemaParser schemaParser = new SchemaParser();
        schemaParser.parse(doc,(xsdpackageName==null)?packageName:xsdpackageName);
        List<Class<?>> externalFunctions = new ArrayList<Class<?>>();
		if (externalFunctionClasses != null) {
			for (String clazz : externalFunctionClasses) {
				try {
					externalFunctions.add(Class.forName(clazz));
				} catch (ClassNotFoundException e) {
					throw new MojoExecutionException(e.getMessage());
				}
			}
		}

        // parse the rules
        ParserSource parserSource;
		try {
			parserSource = new InputStreamParserSource(new FileInputStream(rules), rules,bufferSize);
		} catch (FileNotFoundException e3) {
			throw new MojoExecutionException(e3.getMessage());
		}
        RulesTextProvider textProvider = new RulesTextProvider(parserSource, schemaParser, externalFunctions);
        new FunctionDescriptorFactory().loadOperators(textProvider);
        ParsePackage parsePackage = new ParsePackage();
        try
        {
            parsePackage.parse(textProvider);
        }
        catch (ParserException e2)
        {
            throw new MojoExecutionException(e2.getMessage());
        }

        // generate the output
        String packageDirectory = destdir.getAbsolutePath()+"/";
        File targetDir = new File(packageDirectory+(packageName.replace('.','/'))+"/");
        targetDir.mkdirs();
        targetDir = new File(packageDirectory);
        Generator generator = new Generator();
        PrintStream messages=null;
        try
        {
            messages = new PrintStream(new FileOutputStream(packageDirectory+(packageName.replace('.','/'))+"/messages.properties"));
        }
        catch (FileNotFoundException e1)
        {
            throw new MojoExecutionException(e1.getMessage());
        }
        for (AbstractRule rule: textProvider.getRules())
        {
            messages.println(packageName+"."+rule.getName()+"="+rule.getMessage());
            try
            {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                generator.generate(packageName, rule, xsdpackageName, targetDir, new PrintStream(baos));
                baos.flush();
                baos.close();
//                log.debug(baos.toString());
            }
            catch (Exception e)
            {
                throw new MojoExecutionException(e.getMessage());
            }
        }
        messages.close();
        try
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            generator.generate(packageName, targetDir, new PrintStream(baos));
            baos.flush();
            baos.close();
//            log.debug(baos.toString());
        }
        catch (Exception e)
        {
            throw new MojoExecutionException(e.getMessage());
        }
    }

	public File getResourceDirectory() {
		return resourceDirectory;
	}

	public void setResourceDirectory(File resourceDirectory) {
		this.resourceDirectory = resourceDirectory;
	}

	public File getDestdir() {
		return destdir;
	}

	public void setDestdir(File destdir) {
		this.destdir = destdir;
	}

	public String getRules() {
		return rules;
	}

	public void setRules(String rules) {
		this.rules = rules;
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getXsdpackageName() {
		return xsdpackageName;
	}

	public void setXsdpackageName(String xsdpackageName) {
		this.xsdpackageName = xsdpackageName;
	}
}
