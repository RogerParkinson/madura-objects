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
import java.util.Collection;
import java.util.List;
import java.util.Stack;

import nz.co.senanque.parser.ParserException;
import nz.co.senanque.rules.RuleProxyField;
import nz.co.senanque.schemaparser.EnumeratedConstant;
import nz.co.senanque.schemaparser.FieldDescriptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The parser builds the expression using this class
 * It is not to be confused with the executable expression
 * 
 * @author Roger Parkinson
 * @version $Revision: 1.8 $
 */
public final class Expression extends ExpressionElement
{
    public static final String CONSTANT = "constant"; 
    public static final String PARAMETER = "parameter"; 
    public static final String LIST = "list"; 
    public static final String OUTPUT = "output"; 
    public static final String EXPRESSION = "expression"; 
    public static final String SOFT = "soft";
    public static final String NODE = "node";
    private static final Logger logger = LoggerFactory
            .getLogger(Expression.class);
    

	private final List<ExpressionElement> m_elements = new ArrayList<ExpressionElement>();
	private final Stack<ExpressionElement> m_operators = new Stack<ExpressionElement>();

	public Expression()
	{
	    super("Expession");
		logger.debug("create expression {}",System.identityHashCode(this));

	}
	public void expression(Expression e)
	{
		logger.debug("adding expression {}",System.identityHashCode(e));
		m_elements.add(e);
	}
	public void operator(FunctionDescriptor od)
	{
		logger.debug("adding operator {}",od);
		int precedence = od.getPrecedence();
		flush(precedence);
		m_operators.push(ExpressionElement.parsedElementFactory(od));
	}
	public void function(FunctionDescriptor od)
	{
		logger.debug("adding function {}",od);
		m_elements.add(ExpressionElement.parsedElementFactory(od));
	}
	public void constant(String value)
	{
		logger.debug("adding constant {}",value);
		m_elements.add(new Constant(value));
	}
    public void constant(Number n)
    {
		logger.debug("adding constant {}",n);
        m_elements.add(new Constant(n));
    }
    public void constant(Boolean n)
    {
		logger.debug("adding constant {}",n);
        m_elements.add(new Constant(n));
    }
    public void constant(EnumeratedConstant enumeratedConstant)
    {
		logger.debug("adding constant {}",enumeratedConstant);
        m_elements.add(new Constant(enumeratedConstant));
    }
    public void externalConstant(String value)
    {
		logger.debug("adding constant {}",value);
        m_elements.add(new ExternalConstant(value));        
    }
	public void operand(String value, FieldDescriptor fieldDescriptor)
	{
		logger.debug("adding operand {}",fieldDescriptor);
		m_elements.add(new Parameter(value,fieldDescriptor,fieldDescriptor.isList()));
	}
	public void list(String value, FieldDescriptor fieldDescriptor)
	{
		logger.debug("adding list {}",fieldDescriptor);
		m_elements.add(new Parameter(value,fieldDescriptor,true));
	}

	public void tidyExpression(RulesTextProvider rtp)
	{
		flush(-1);
		List<ExpressionElement> elements = new ArrayList<ExpressionElement>();
		for (ExpressionElement o:m_elements)
		{
			if (o instanceof Expression)
			{
				elements.addAll(((Expression)o).m_elements);
				continue;
			}
			if (o instanceof Operator)
			{
				elements.add(o);
				continue;
			}
			if (o instanceof ExpressionElement)
			{
				elements.add(o);
				continue;
			}
			throw new ParserException("found unexpected: "+o.getClass().getName(),rtp);
		}
        @SuppressWarnings("unused")
		int count = 0;
		for (ExpressionElement pe: elements)
		{
			count++;
			if (pe instanceof Operator)
			{
				if (((Operator)pe).isAssign())
				{
				    ExpressionElement element = elements.get(0);
				    if (element instanceof Parameter)
				    {
				        ((Parameter)element).setAssigned(true);
				    }
				    else
				    {
                        throw new ParserException("Attempt to assign to a non-parameter: "+pe.toString().trim(),rtp);
				    }
//					if (count != m_elements.size())
//						throw new ParserException("Assignment must not be in the middle",lineNumber);
//					ExpressionElement output = (ExpressionElement)m_elements.get(0);
//					if (output == null || !(output instanceof Parameter))
//						throw new ParserException("Assignment must be to a parameter",lineNumber);
//					((Parameter)output).setOutput((Operator)pe);
				}
			}
		}
		m_elements.clear();
		m_elements.addAll(elements);
	}
	public void validateExpression(RulesTextProvider rtp)
	{
//	    int lineNumber = rtp.getLineCount();
        try
        {
            for (int i=0;i<m_elements.size();i++)
            {
                ExpressionElement expressionElement = m_elements.get(i);
                if (expressionElement.isUsed())
                {
                    continue;
                }
                if (expressionElement instanceof Operator)
                {
                    Operator operator = (Operator)expressionElement;
                    int count = 0;
                    for (int j=i-1;count<operator.getArgumentCount();j--)
                    {
                        ExpressionElement expressionElement2=null;
                        try
                        {
                            expressionElement2 = m_elements.get(j);
                        }
                        catch (ArrayIndexOutOfBoundsException e2)
                        {
                            throw new ParserException("Not enough arguments found for: "+operator.toString().trim(),rtp);
                        }
                        if (expressionElement2.isUsed())
                        {
                            continue;
                        }
                        count++;
                        expressionElement2.setUsed(true);
                        if (expressionElement2 instanceof Parameter)
                        {
                            Parameter p = (Parameter)expressionElement2;
                            if (p.isAssign() && operator.isAssign() && count == operator.getArgumentCount())
                            {
                                continue;
                            }
                        }
                        Class<?> expressionClass = expressionElement2.getTypeAsClass();
                        Class<?> argClass = operator.getArguments()[count-1];
                        if (!argClass.isAssignableFrom(expressionClass))
                        {
                            logger.debug("{} {}",argClass.getName(),expressionClass.getName());
                            if ("add".equals(operator.getMethodName()) && count == 1)
                            {
                                operator.setFunctionDescriptor(rtp.getFunction("concat"));
                                expressionElement2.setUsed(false);
                                count = 0;
                                j=i;
                                continue;
                            }
                            if ("toString".equals(operator.getMethodName()) && count == 1)
                            {
                                operator.setFunctionDescriptor(rtp.getFunction("toStringD"));
                                expressionElement2.setUsed(false);
                                count = 0;
                                j=i;
                                continue;
                            }
                            // This handles the case where the function needs a RuleProxyField argument
                            // It has to be matched with a parameter, not a sub-expression.
                            if (expressionElement2 instanceof Parameter && argClass == RuleProxyField.class)
                            {
                                continue;
                            }
                            throw new ParserException("Invalid argument ("+expressionElement2.toString().replace('\n', ' ').trim()+") found for: "+operator.toString().trim(),rtp);                            
                        }
                    }
                }
            }
        }
        catch (RuntimeException e)
        {
            throw e;
        }
        finally
        {
            for (int i=0;i<m_elements.size();i++)
            {
                ExpressionElement expressionElement = m_elements.get(i);
                expressionElement.setUsed(false);
            }
        }
	    
	}
	private void flush(int precedence)
	{
		while (!m_operators.isEmpty())
		{
			Operator o = (Operator)m_operators.peek();
			if (o.getPrecedent() <= precedence)
				break;
			m_elements.add(o);
			m_operators.pop();
		}
	}
	public String toString()
	{
		return toString(m_elements);
	}
	private static String toString(List<ExpressionElement> list)
	{
		StringBuilder ret = new StringBuilder();
		for (ExpressionElement o: list)
		{
		    if (o instanceof Expression)
				ret.append(o.toString());
			else 
			{
				ExpressionElement pe = (ExpressionElement)o;
				ret.append(pe.toString());
			}
		}
		return ret.toString();
	}
	@SuppressWarnings("unused")
	private void dump(String comment)
	{
		System.out.println(comment);
		System.out.println(toString(m_elements));
	}
    public List<ExpressionElement> getExpressionElements()
    {
        return m_elements;
    }
    public Collection<Parameter> getAllFieldsReferredTo()
    {
        List<Parameter> ret = new ArrayList<Parameter>();
        for (ExpressionElement expressionElement: m_elements)
        {
            if (expressionElement instanceof Parameter)
            {
                Parameter p = (Parameter)expressionElement;
                ret.add(p);
            }
        }
        return ret;
    }
    public Collection<Parameter> getAllFieldsRead()
    {
        List<Parameter> ret = new ArrayList<Parameter>();
        for (ExpressionElement expressionElement: m_elements)
        {
            if (expressionElement instanceof Parameter)
            {
                if (!expressionElement.isAssign())
                {
                    Parameter p = (Parameter)expressionElement;
                    ret.add(p);
                }
            }
        }
        return ret;
    }
    @Override
    public Class<?> getTypeAsClass()
    {
        throw new RuntimeException("We should never get here");
    }
    public boolean isAssign()
    {
        List<ExpressionElement> elements = getExpressionElements();
        if (elements.size()==0) return false;
        ExpressionElement e = elements.get(elements.size()-1);
        if ("nz.co.senanque.rules.RuleSession.assign".equals(e.getValue()))
        {
            return true;
        }
        return false;
    }
}
