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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;

/**
 * 
 * Used to parse the schema file so that we can validate the rules
 * 
 * @author Roger Parkinson
 * @version $Revision: 1.6 $
 */
public class SchemaParser
{
    private Map<String,ObjectDescriptor> m_classes = new HashMap<String,ObjectDescriptor>();
    private Map<String,List<String>> m_constants = new HashMap<String,List<String>>();
    private String m_xsdpackageName;
    
    private static Map<String, String> s_types;
    
    static 
    {
        s_types = new HashMap<String, String>();
        s_types.put("string", "java.lang.String");
        s_types.put("boolean", "java.lang.Boolean");
        s_types.put("integer", "java.lang.Integer");
        s_types.put("long", "java.lang.Long");
        s_types.put("float", "java.lang.Float");
        s_types.put("double", "java.lang.Double");
        s_types.put("date", "java.util.Date");
        s_types.put("list", "nz.co.senanque.validationengine.ListeningArray");
    }

    public void parse(Document schemaDocument, String xsdpackageName)
    {
        m_xsdpackageName = xsdpackageName;
        findComplexTypes(schemaDocument.getRootElement());
        findSimpleTypes(schemaDocument.getRootElement());
    }
    public void parse(Document schemaDocument)
    {
        m_xsdpackageName = findPackageName(schemaDocument.getRootElement());;
        findComplexTypes(schemaDocument.getRootElement());
        findSimpleTypes(schemaDocument.getRootElement());
    }
    private String findPackageName(Element parent) {
    	Namespace xsdNameSpace = null;
    	Namespace jaxbNameSpace = null;
    	for (Namespace namespace: (List<Namespace>)parent.getAdditionalNamespaces()) {
    		if (namespace.getURI().equals("http://www.w3.org/2001/XMLSchema")) {
    			xsdNameSpace = namespace;
    		}
    		if (namespace.getURI().equals("http://java.sun.com/xml/ns/jaxb")) {
    			jaxbNameSpace = namespace;
    		}
    	}
        try {
			Element annotation = parent.getChild("annotation",xsdNameSpace);
			Element appinfo = annotation.getChild("appinfo",xsdNameSpace);
			Element schemaBindings = appinfo.getChild("schemaBindings",jaxbNameSpace);
			Element packageElement = schemaBindings.getChild("package",jaxbNameSpace);
			return packageElement.getAttributeValue("name");
		} catch (Exception e) {
			throw new SchemaParserException("Failed to find package name",e);
		}
    }
    private void findComplexTypes(Element parent)
    {
        List<Element> children = parent.getChildren();
        for (Element element: children)
        {
            if (element.getName().equals("complexType"))
            {
                String name = element.getAttributeValue("name");
                ObjectDescriptor fields = new ObjectDescriptor(name);
                m_classes.put(name,fields);
                findElements(element,fields,name);
            }
            else
            {
                findComplexTypes(element);
            }
        }
    }
    private void findSimpleTypes(Element parent)
    {
        List<Element> children = parent.getChildren();
        for (Element element: children)
        {
            if (element.getName().equals("simpleType"))
            {
                String name = element.getAttributeValue("name");
                List<String> fields = new ArrayList<String>();
                m_constants.put(name,fields);
                for (Element e: (List<Element>)element.getChildren())
                {
                    if (e.getName().equals("restriction"))
                    {
                        for (Element e1: (List<Element>)e.getChildren())
                        {
                            if (e1.getName().equals("enumeration"))
                            {
                                String value = e1.getAttributeValue("value").toUpperCase();
                                fields.add(value);
                            }
                        }
                    }
                }
            }
        }
    }
    private void findElements(Element parent, ObjectDescriptor fields, String clazz)
    {
        List<Element> children = parent.getChildren();
        for (Element element: children)
        {
            if (element.getName().equals("element"))
            {
                String name = element.getAttributeValue("name");
                String type = element.getAttributeValue("type");
                if (type == null)
                {
                    for (Element e: (List<Element>)element.getChildren())
                    {
                        if (e.getName().equals("simpleType"))
                        {
                            for (Element e1: (List<Element>)e.getChildren())
                            {
                                if (e1.getName().equals("restriction"))
                                {
                                    type = e1.getAttributeValue("base");
                                    break;
                                }
                                break;
                            }
                            
                        }
                    }
//                    Element simpleType = element.getChild("simpleType");
//                    if (simpleType != null)
//                    {
//                        Element restriction = simpleType.getChild("restriction");
//                        if (restriction != null)
//                        {
//                            type = restriction.getAttributeValue("base");
//                        }
//                    }
                    type = (type==null)?"string":type;
                }
                int i = type.indexOf(':');
                if (i > -1)
                {
                    type = type.substring(i+1);
                }
                boolean list = false;
                String maxOccurs = element.getAttributeValue("maxOccurs");
                if (maxOccurs != null)
                {
                    if (maxOccurs.equals("unbounded") || Integer.parseInt(maxOccurs)>1)
                    {
                        list = true;
                    }
                }
                String translatedType = s_types.get(type);
                if (translatedType == null)
                {
                    translatedType = type; 
                }
                fields.put(name,new FieldDescriptor(clazz,name,translatedType,list));
            }
            else
            {
                if (element.getName().equals("extension"))
                {
                    String base = element.getAttributeValue("base");
                    if (base != null)
                    {
                        int i = base.indexOf(':');
                        base = base.substring(i+1);
                    }
                    fields.setParent(base);
                }
                findElements(element,fields,clazz);
            }
        }
    }
    public FieldDescriptor findOperandInScope(String scope, String operand)
    {
        ObjectDescriptor currentScope = m_classes.get(scope);
        StringTokenizer st = new StringTokenizer(operand,".");
        FieldDescriptor ret = null;
        while(st.hasMoreTokens())
        {
            if (currentScope == null)
            {
                return null;
            }
            String token = st.nextToken();
            ret = currentScope.get(token);
            if (ret == null)
            {
                if (currentScope.getParent() != null)
                {
                    return findOperandInScope(currentScope.getParent(),operand);
                }
                return null;
            }
            currentScope = m_classes.get(ret.getType());
        }
        return ret;
    }
    public Set<String> findOperandsInScope(String scope, String operand)
    {
        ObjectDescriptor currentScope = m_classes.get(scope);
        StringTokenizer st = new StringTokenizer(operand,".");
        FieldDescriptor fd = null;
        ObjectDescriptor lastScope = currentScope;
        while(st.hasMoreTokens())
        {
            if (currentScope == null)
            {
                break;
            }
            lastScope = currentScope;
            String token = st.nextToken();
            fd = currentScope.get(token);
            if (fd == null)
            {
            	ObjectDescriptor parent = getParent(lastScope);
            	while (parent != null) {
            		fd = parent.get(token);
            		if (fd != null) {
            			break;
            		}
            		parent = getParent(lastScope);
            	}
            }
            currentScope = m_classes.get(fd.getType());
        }
        return Collections.unmodifiableSet(lastScope.keySet());
    }
    private ObjectDescriptor getParent(ObjectDescriptor scope) {
    	String parentName = scope.getParent();
    	if (parentName != null) {
    		return m_classes.get(parentName);
    	}
    	return null;
    }
    private FieldDescriptor[] findOperandsInScope(String scope, StringTokenizer st)
    {
        ObjectDescriptor currentScope = m_classes.get(scope);
        FieldDescriptor[] ret = null;
        ObjectDescriptor lastScope;
        while(st.hasMoreTokens())
        {
            if (currentScope == null)
            {
                break;
            }
            lastScope = currentScope;
            String token = st.nextToken();
            FieldDescriptor fd = currentScope.get(token);
            if (fd == null)
            {
                if (currentScope.getParent() != null)
                {
                    return findOperandsInScope(currentScope.getParent(),st);
                }
                return null;
            }
            currentScope = m_classes.get(fd.getType());
        }
        return ret;
    }
    public EnumeratedConstant findConstantInScope(String currentScope, String constant)
    {
        int i = constant.indexOf('.');
        if (i == -1)
        {
            return null;
        }
        String className = constant.substring(0,i);
        String fieldName = constant.substring(i+1);
        List<String> constants = m_constants.get(className);
        if (constants == null)
        {
            return null;
        }
        if (constants.contains(fieldName))
        {
            return new EnumeratedConstant(m_xsdpackageName,className,fieldName);
        }
        return null;
    }
}
