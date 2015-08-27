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

import java.lang.reflect.Method;
import java.util.List;

import nz.co.senanque.rules.annotations.Function;
import nz.co.senanque.rules.annotations.InternalFunction;

import org.jdom.Element;

/**
 * 
 * Describes a function. You can add extra functions by adding more of these to the list in the parser.
 * 
 * @author Roger Parkinson
 * @version $Revision: 1.4 $
 */
public class FunctionDescriptor
{
    private final String m_name;
    private final String m_className;
    private final String m_operator;
    private Class<?>[] m_parameterTypes;
    private final String m_fullFunctionName;
    private final int m_precedence;
    private final boolean m_assign;
    private final boolean m_native;
    private Class<?> m_returnType = String.class;
    private boolean m_hidden;
    
    public String toString() {
    	return m_fullFunctionName;
    }

    public FunctionDescriptor(Element e)
    {
        m_fullFunctionName = e.getAttributeValue("fullFunctionName");
        m_operator = e.getAttributeValue("operator");
        int i = m_fullFunctionName.lastIndexOf(".");
        m_name = m_fullFunctionName.substring(i+1);
        m_className = m_fullFunctionName.substring(0,i);
        String p = e.getAttributeValue("precedence");
        if (p != null)
        {
            m_precedence = Integer.valueOf(p);
        }
        else
        {
            m_precedence = 1;
        }
        p = e.getAttributeValue("isAssign");
        if (p != null)
        {
            m_assign = p.equals("true");
        }
        else
        {
            m_assign = false;
        }
        m_native = true;
        m_parameterTypes = new Class<?>[e.getChildren().size()];
        for (i=0;i<e.getChildren().size();i++)
        {
            Element child = (Element)e.getChildren().get(i);
            try
            {
                m_parameterTypes[i] = Class.forName(child.getAttributeValue("type"));
            }
            catch (ClassNotFoundException e1)
            {
                throw new RuleParserException(e1);
            }
        }
    }

    public FunctionDescriptor(String functionName, String fullFunctionName)
    {
        m_fullFunctionName = fullFunctionName;
        m_operator = null;
        m_precedence = 1;
        m_assign = false;
        m_native = false;
        int i = m_fullFunctionName.lastIndexOf(".");
        m_name = m_fullFunctionName.substring(i+1);
        m_className = m_fullFunctionName.substring(0,i);
    }

    public FunctionDescriptor(Method method, InternalFunction function)
    {
        m_fullFunctionName = method.getDeclaringClass().getName()+"."+method.getName();
        m_operator = ("x".equals(function.operator())?null:function.operator());
        m_precedence = function.precedence();
        m_assign = function.isAssign();
        m_hidden = function.isHidden();
        m_native = true;
        m_name = method.getName();
        m_className = method.getDeclaringClass().getName();
        if (m_assign)
        {
            Class<?>[] types = method.getParameterTypes();
            Class<?>[] newTypes = new Class<?>[types.length-1];
            for (int i=1;i<types.length;i++)
            {
                newTypes[i-1] = types[i];
            }
            m_parameterTypes = newTypes;
        }
        else
        {
            m_parameterTypes = method.getParameterTypes();
        }
        m_returnType  = method.getReturnType();
    }

    public FunctionDescriptor(Method method, Function function)
    {
        m_fullFunctionName = method.getDeclaringClass().getName()+"."+method.getName();
        m_operator = "x";
        m_precedence = 1;
        m_assign = false;
        m_hidden = false;
        m_native = false;
        m_name = method.getName();
        m_className = method.getDeclaringClass().getName();
        if (m_assign)
        {
            Class<?>[] types = method.getParameterTypes();
            Class<?>[] newTypes = new Class<?>[types.length-1];
            for (int i=1;i<types.length;i++)
            {
                newTypes[i-1] = types[i];
            }
            m_parameterTypes = newTypes;
        }
        else
        {
            m_parameterTypes = method.getParameterTypes();
        }
        m_returnType  = method.getReturnType();
    }

    public String getName()
    {
        return m_name;
    }

    public Class<?>[] getParameterTypes()
    {
        return m_parameterTypes;
    }

    public String getFullFunctionName()
    {
        return m_fullFunctionName;
    }

    public void setArgTypes(List<Class<?>> argTypes)
    {
        m_parameterTypes = argTypes.toArray(new Class<?>[argTypes.size()]);
    }

    public String getOperator()
    {
       return m_operator;
    }

    public int getPrecedence()
    {
        return m_precedence;
    }

    public boolean isAssign()
    {
        return m_assign;
    }

    public String getClassName()
    {
        return m_className;
    }

    public boolean isNative()
    {
        return m_native;
    }

    public Class<?> getReturnType()
    {
        return m_returnType;
    }

    public boolean isHidden()
    {
        return m_hidden;
    }

}
