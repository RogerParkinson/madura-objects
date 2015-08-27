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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import nz.co.senanque.parser.AbstractTextProvider;
import nz.co.senanque.parser.ParserException;
import nz.co.senanque.parser.ParserSource;
import nz.co.senanque.parser.TOCInterface;
import nz.co.senanque.schemaparser.EnumeratedConstant;
import nz.co.senanque.schemaparser.FieldDescriptor;
import nz.co.senanque.schemaparser.SchemaParser;

/**
 * 
 * 
 * @author Roger Parkinson
 * @version $Revision: 1.4 $
 */
public class RulesTextProvider extends AbstractTextProvider implements FunctionDescriptorHolder, TOCInterface
{
    private Map<String,FunctionDescriptor> m_functionMap = new HashMap<String,FunctionDescriptor>();
    private Map<String,FunctionDescriptor> m_operatorMap = new HashMap<String,FunctionDescriptor>();
    private final SchemaParser m_schemaParser;
    private String m_currentScope;
    private List<AbstractRule> m_rules = new ArrayList<AbstractRule>();
    private int m_ruleCount=0;
    private final StringBuilder m_accumulate = new StringBuilder();
    private FieldDescriptor m_lastResolvedField;
    private final List<Class<?>> m_externalFunctionClasses;
    private EnumeratedConstant m_lastResolvedConstant;
    private TOCInterface m_toc;
    
    public RulesTextProvider(ParserSource parserSource, SchemaParser schemaParser,List<Class<?>> externalFunctionClasses) throws ParserException
    {
        super(parserSource);
        m_schemaParser = schemaParser;
        m_externalFunctionClasses = externalFunctionClasses;
    }
    public Set<Entry<String, FunctionDescriptor>> getOperators()
    {
        return m_operatorMap.entrySet();
    }
    public Set<Entry<String, FunctionDescriptor>> getFunctions()
    {
        return m_functionMap.entrySet();
    }
    public SchemaParser getSchemaParser()
    {
        return m_schemaParser;
    }
    public boolean findOperandInScope(String operand)
    {
        m_lastResolvedField = m_schemaParser.findOperandInScope(m_currentScope, operand);
        return (m_lastResolvedField != null);
    }
    public FunctionDescriptor getFunction(String functionName)
    {
        return m_functionMap.get(functionName);
    }
    public String getCurrentScope()
    {
        return m_currentScope;
    }
    public void setCurrentScope(String currentScope)
    {
        m_currentScope = currentScope;
    }
    public void addFunction(FunctionDescriptor functionDescriptor)
    {
        m_functionMap.put(functionDescriptor.getName(),functionDescriptor);
        if (functionDescriptor.getName().equals("logicalNot"))
        {
            m_functionMap.put("!",functionDescriptor);
        }
        
    }
    public void addOperator(String operator, FunctionDescriptor fd)
    {
        m_operatorMap.put(operator, fd);
    }
    public AbstractRule addRule(AbstractRule rule)
    {
        m_rules .add(rule);
        return rule;
    }
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        for (AbstractRule rule:m_rules)
        {
            sb.append(rule.toString());
        }
        return sb.toString();
    }
    public int incrementRuleCount()
    {
        return ++m_ruleCount;
    }
    public List<AbstractRule> getRules()
    {
        return m_rules;
    }
    public void setLastToken(String lastToken)
    {
        super.setLastToken(lastToken);
        m_accumulate.append(lastToken);
    }
//    public void addToLastToken(char c)
//    {
//        super.addToLastToken(c);
//    }
    public String getAccumulate()
    {
        String ret = m_accumulate.toString();
        m_accumulate.setLength(0);
        return ret;
    }
    public void accumulate(String string)
    {
        m_accumulate.append(string);
    }
    public FieldDescriptor getLastResolvedField()
    {
        return m_lastResolvedField;
    }
    public List<Class<?>> getExternalFunctionClasses()
    {
        return m_externalFunctionClasses;
    }
    public boolean findConstantInScope(String constant)
    {
        m_lastResolvedConstant = m_schemaParser.findConstantInScope(m_currentScope, constant);
        return (m_lastResolvedConstant != null);
    }
    public EnumeratedConstant getLastConstant()
    {
        return m_lastResolvedConstant;
    }
	@Override
	public Object addTOCElement(Object parent, String name, long start,
			long end, int type) {
		if (m_toc != null) {
			return m_toc.addTOCElement(parent,name,start,end,type);
		}
		return null;
	}
	@Override
	public void addErrorElement(String name, int line) {
		if (m_toc != null) {
			m_toc.addErrorElement(name,line);
		}
	}
	public TOCInterface getToc() {
		return m_toc;
	}
	public void setToc(TOCInterface toc) {
		m_toc = toc;
	}

}
