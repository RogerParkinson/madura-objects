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

import com.sun.codemodel.JInvocation;


/**
 * Encapsulates an Operator (+,-,* etc)
 * 
 * @author Roger Parkinson
 * @version $Revision: 1.4 $
 */
public final class Operator extends ExpressionElement
{
	private FunctionDescriptor m_od;
    private JInvocation m_expr;
	/**
	 * Constructor for Operator.
	 */
	public Operator(FunctionDescriptor od)
	{
		super(od.getFullFunctionName());
		m_od = od;
	}
	/**
	 * Returns the precedent.
	 * @return int
	 */
	public int getPrecedent()
	{
		return m_od.getPrecedence();
	}

	public String toString()
	{
	    if (m_od.isNative())
	    {
	        if (m_od.getOperator() != null)
	        {
	            return SPACES+"operator "+m_od.getOperator();
	        }
	    }
		return SPACES+"operator "+getTargetClassName()+" "+getMethodName()+" "+getArgumentCount()+super.toString()+"\n";
	}
	public boolean isAssign()
	{
		return m_od.isAssign();
	}
    public int getArgumentCount()
    {
        return m_od.getParameterTypes().length;
    }
    public Class<?>[] getArguments()
    {
        return m_od.getParameterTypes();
    }
    public String getMethodName()
    {
        return m_od.getName();
    }
    public String getTargetClassName()
    {
        return m_od.getClassName();
    }
    public void setExpr(JInvocation expr)
    {
        m_expr = expr;
    }
    public JInvocation getExpr()
    {
        return m_expr;
    }
    public boolean isNative()
    {
        return m_od.isNative();
    }
    public Class<?> getReturnType()
    {
        return m_od.getReturnType();
    }
    public String getSymbol()
    {
        return m_od.getOperator();
    }
    public void setFunctionDescriptor(FunctionDescriptor function)
    {
        m_od = function;
        
    }
    public Class<?> getTypeAsClass()
    {
        return getReturnType();
    }

}
