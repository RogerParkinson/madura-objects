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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * Encapsulates an element which most of the other abstractions
 * implement. Also contains the Element factory.
 * 
 * The element factory is not particularly efficient (string compares)
 * But it isn't run very often, this is the parser code not the runtime code.
 * 
 * @author Roger Parkinson
 * @version $Revision: 1.4 $
 */
public abstract class ExpressionElement
{
    private static final Logger log = LoggerFactory.getLogger(ExpressionElement.class);
    protected static final String SPACES = "        ";
	private final String m_value;
	private boolean m_used;
	/**
	 * Constructor for ParsedElement1.
	 */
	public ExpressionElement(String value)
	{
		m_value = value;
	}
	public boolean isAssign()
	{
		return false;
	}
	
	/**
	 * Returns the value.
	 * @return String
	 */
	public final String getValue()
	{
		return m_value;
	}
	
    public final static ExpressionElement parsedElementFactory(FunctionDescriptor od)
    {
        return new Operator(od);
    }
    public boolean isUsed()
    {
        return m_used;
    }
    public void setUsed(boolean used)
    {
        m_used = used;
    }
    public abstract Class<?> getTypeAsClass();
    
    public String toString()
    {
        if (!log.isDebugEnabled())
        {
            return "";
        }
        return (isUsed()?" used":"");
    }

}
