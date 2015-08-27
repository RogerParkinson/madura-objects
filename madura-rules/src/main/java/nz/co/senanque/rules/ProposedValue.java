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
package nz.co.senanque.rules;

import java.io.Serializable;


/**
 * This encapsulates a proposed value that is passed to the engine.
 * The value may be rejected if it is incompatible with the rules and existing data.
 * 
 * @author Roger Parkinson
 * @version $Revision: 1.1 $
 */
public class ProposedValue implements Serializable
{

	private static final long serialVersionUID = 1L;
	private final RuleProxyField m_proxyField;
    private final Object m_value;
    private final Object m_oldValue;

    public ProposedValue(final RuleProxyField proxyField, final Object value)
    {
        m_proxyField = proxyField;
        m_value = value;
        m_oldValue = proxyField.getInternalValue();
    }

    public RuleProxyField getRuleProxyField()
    {
        return m_proxyField;
    }

    public Object getValue()
    {
        return m_value;
    }

    public void restore()
    {
        m_proxyField.reset();
        m_proxyField.setValue(m_oldValue);
    }
    public String toString()
    {
    	return m_proxyField.toString()+" "+m_value;
    }
}
