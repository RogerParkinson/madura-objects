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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import nz.co.senanque.schemaparser.FieldDescriptor;

/**
 * Encapsulates a Rule abstract (real rules, formulae and constraints descend from this).
 * 
 * @author Roger Parkinson
 * @version $Revision: 1.4 $
 */
public abstract class AbstractRule implements Comparable<AbstractRule>
{
	private List<Expression> m_conditions = new ArrayList<Expression>();
	private List<Expression> m_actions = new ArrayList<Expression>();
    private String m_name;
    private String m_className;
    private String m_message;
    private String m_comment;
    private List<Parameter> m_messageArguments = new ArrayList<Parameter>();

    public AbstractRule()
	{
	}
	public abstract String toString();
	
	/**
	 * @see java.lang.Comparable#compareTo(Object)
	 * 
	 * We need this because we sort by the number of conditions on the rule
	 * The comparison is done in reverse because we want the high conditions at the top
	 */
	public final int compareTo(AbstractRule o)
	{
		Integer us = new Integer(this.getSpecificity());
		Integer them = new Integer(o.getSpecificity());
		return them.compareTo(us);
	}
	public final int getSpecificity()
	{
		return m_conditions.size();
	}
	protected final void addAction(Expression e)
	{
		m_actions.add(e);
	}
	protected final void addCondition(Expression e)
	{
		m_conditions.add(e);
	}
	public final String getName()
	{
		return m_name;
	}
	private final static String getList(String title, List<Expression> list)
	{
		StringBuilder ret = new StringBuilder();
		for (Expression expression:list)
		{
            ret.append(title);
            ret.append(expression.toString());
            ret.append("    end\n");
		}
		return ret.toString();
	}
	public final String getActions()
	{
		return getList("    action\n",m_actions);
	}
	public final String getConditions()
	{
		return getList("    condition\n",m_conditions);
	}
    public String getClassName()
    {
        return m_className;
    }
    public void setClassName(String className)
    {
        m_className = className;
    }
    public String getMessage()
    {
        return m_message;
    }
    public void setMessage(String message)
    {
        m_message = message;
    }
    public void setName(String name)
    {
        m_name = name;
    }
    public Set<Parameter> getAllFieldsReferredTo()
    {
        Set<Parameter> ret = new HashSet<Parameter>();
        for (Expression expression: m_conditions)
        {
            ret.addAll(expression.getAllFieldsReferredTo());
        }
        for (Expression expression: m_actions)
        {
            ret.addAll(expression.getAllFieldsReferredTo());
        }
        ret.addAll(m_messageArguments);
        return ret;
    }
    public List<Expression> getConditionList()
    {
        return m_conditions;
    }
    public List<Expression> getActionList()
    {
        return m_actions;
    }
    public Set<FieldDescriptor> getAllFieldsRead()
    {
        Set<FieldDescriptor> ret = new HashSet<FieldDescriptor>();
        Set<Parameter> parameters = new HashSet<Parameter>();
        for (Expression expression: m_conditions)
        {
        	parameters.addAll(expression.getAllFieldsRead());
        }
        for (Expression expression: m_actions)
        {
        	parameters.addAll(expression.getAllFieldsRead());
        }
        for (Parameter parameter: parameters)
        {
        	FieldDescriptor fieldDescriptor = parameter.getFieldDescriptor();
        	if (!fieldDescriptor.getClazz().equals(m_className))
        	{
        		String parameterName = parameter.getValue();
        		String[] parameterNames = StringUtils.split(parameterName,".");
        		if (parameterNames != null)
        		{
        			parameterName = parameterNames[0];
        		}
        		FieldDescriptor fd = new FieldDescriptor(m_className,parameterName,m_className,parameter.isList());
        		ret.add(fd);
        	}
        	ret.add(fieldDescriptor);
        }
        return ret;
    }
    public void setComment(String comment)
    {
        m_comment = comment;
    }
    public String getComment()
    {
        return m_comment;
    }
    public void addMessageArgument(String operand, FieldDescriptor fieldDescriptor)
    {
        if (fieldDescriptor.isList())
        {
            throw new RuleParserException("List parameters in messages are not allowed");
        }
        Parameter parameter = new Parameter(operand,fieldDescriptor,false);
        m_messageArguments.add(parameter);
        
    }
    public List<Parameter> getMessageArguments()
    {
        return m_messageArguments;
    }
	public Set<FieldDescriptor> getAllFieldsOutput() {
		Set<FieldDescriptor> ret = new HashSet<FieldDescriptor>();
		for (Expression action: m_actions)
		{
			if (action.isAssign())
			{
				Parameter parameter = action.getAllFieldsReferredTo().iterator().next();
				if (parameter.isAssign())
				{
					ret.add(parameter.getFieldDescriptor());
				}
			}
		}
		return ret;
	}
	
	public String getDescription() {
		StringBuilder ret = new StringBuilder();
		if (m_message != null) {
			ret.append(m_message);
			if (m_name != null) {
				ret.append(" (");
				ret.append(m_name);
				ret.append(")");
			}
		} else if (m_name != null) {
			ret.append(m_name);
		} else {
			ret.append("");
		}
		return ret.toString();
	}

}
