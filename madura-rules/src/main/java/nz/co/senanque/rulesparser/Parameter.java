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

import nz.co.senanque.schemaparser.FieldDescriptor;
import nz.co.senanque.validationengine.ListeningArray;

/**
 * Encapsulates a parameter
 * 
 * @author Roger Parkinson
 * @version $Revision: 1.3 $
 */
public final class Parameter extends ExpressionElement
{
    private boolean m_assigned;
    private final boolean m_list;
    private final FieldDescriptor m_fieldDescriptor;
    
	public Parameter(String value, FieldDescriptor fieldDescriptor, boolean b)
    {
	    super(value);
	    m_list = (b)?true:(value.indexOf('.')>-1);
	    m_fieldDescriptor = fieldDescriptor;
    }
    public String toString()
	{
		return SPACES+"parameter "+getValue()+" "+getType()+(isList()?"list":"")+super.toString()+"\n";
	}
    public void setAssigned(boolean b)
    {
        m_assigned = b;        
    }
    public boolean isAssign()
    {
        return m_assigned;
    }
    public FieldDescriptor getFieldDescriptor()
    {
        return m_fieldDescriptor;
    }
    public boolean isList()
    {
        return m_list;
    }
    public String getType()
    {
        return m_fieldDescriptor.getType();
    }
    public Class<?> getTypeAsClass()
    {
        if (isList())
        {
            return ListeningArray.class;
        }
        try
        {
            return Class.forName(m_fieldDescriptor.getType());
        }
        catch (ClassNotFoundException e)
        {
            throw new RuleParserException(e);
        }
    }

}
