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
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * The field reference records what rules get fired if this field changes.
 * 
 * @author Roger Parkinson
 * @version $Revision: 1.3 $
 */
public class FieldReference implements Serializable
{

	private static final long serialVersionUID = 1L;
	private final transient String m_className;
    private final transient String m_ruleClassName;
    private final transient String m_fieldName;
    private final transient List<Rule> m_listeners = new ArrayList<Rule>();
    private final transient List<Rule> m_outputs = new ArrayList<Rule>();

    public FieldReference(final String className, final String ruleClassName, final String fieldName)
    {
        m_className = className;
        m_ruleClassName = ruleClassName;
        m_fieldName = fieldName;
    }

    public FieldReference(final String className, final String fieldName)
    {
        m_className = className;
        m_ruleClassName = className;
        m_fieldName = fieldName;
    }

    public String getClassName()
    {
        return m_className;
    }

    public String getFieldName()
    {
        return m_fieldName;
    }

    public void addListener(final Rule rule)
    {
        m_listeners.add(rule);
    }

    public List<Rule> getListeners()
    {
        return m_listeners;
    }
    public List<Rule> getOutputs()
    {
        return m_outputs;
    }

    public String getRuleClassName()
    {
        return m_ruleClassName;
    }

	public void addOutput(Rule rule) {
		m_outputs.add(rule);
	}

}
