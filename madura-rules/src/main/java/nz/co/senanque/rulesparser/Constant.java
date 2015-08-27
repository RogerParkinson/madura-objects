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

import nz.co.senanque.schemaparser.EnumeratedConstant;

public final class Constant extends ExpressionElement
{
    private Object m_object;
    public Constant(String value)
    {
        super(value);
        m_object = value;
    }
    public Constant(Number value)
    {
        super(String.valueOf(value));
        m_object = value;
    }
    public Constant(Boolean value)
    {
        super(String.valueOf(value));
        m_object = value;
    }
	public Constant(EnumeratedConstant value)
    {
        super(String.valueOf(value));
        m_object = value;
    }
    public String toString()
	{
		return SPACES+"constant "+getValue()+" "+((m_object==null)?null:m_object.getClass().getName())+super.toString()+"\n";
	}
	public Object getConstantValue()
	{
	    return m_object;
	}
	public Class<?> getTypeAsClass()
	{
	    return getConstantValue().getClass();
	}
}
