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

import nz.co.senanque.validationengine.ProxyField;

/**
 * 
 * Holds entries in the choice list that have been excluded
 * 
 * @author Roger Parkinson
 * @version $Revision: 1.1 $
 */
public class Exclude implements Serializable
{

	private static final long serialVersionUID = 1L;
	private final transient ProxyField m_proxyField;
    private final transient String m_key;
    private final transient RuleContext m_ruleContext;

    public Exclude(final ProxyField proxyField, final String key, final RuleContext ruleContext)
    {
        m_proxyField = proxyField;
        m_key = key;
        m_ruleContext = ruleContext;
    }

    public ProxyField getProxyField()
    {
        return m_proxyField;
    }

    public String getKey()
    {
        return m_key;
    }

    public RuleContext getRuleContext()
    {
        return m_ruleContext;
    }

}
