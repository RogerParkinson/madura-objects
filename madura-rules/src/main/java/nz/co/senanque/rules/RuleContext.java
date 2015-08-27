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
import java.util.HashSet;
import java.util.Set;

import nz.co.senanque.validationengine.ValidationObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * This describes an instance of a rule, ie where a rule applies to a specific object.
 * 
 * @author Roger Parkinson
 * @version $Revision: 1.5 $
 */
public class RuleContext implements Serializable
{
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(RuleContext.class);
    private final transient Rule m_rule;
    private final transient RuleSession m_ruleSession;
    private final transient Indenter m_indenter;
    private final transient ValidationObject m_validationObject;
    public ValidationObject getValidationObject() {
		return m_validationObject;
	}
	private transient Set<Exclude> m_excludes = null;
    private Set<RuleProxyField> m_activates = null;
    private Set<RuleProxyField> m_requireds = null;
    private Set<RuleProxyField> m_assigns = null;
    private Set<RuleProxyField> m_readOnlys;
    protected static String getKey(final Rule rule, final ValidationObject validationObject)
    {
        return rule.getRuleName()+":"+System.identityHashCode(validationObject);
    }
    protected String getKey()
    {
        return getKey(m_rule,m_validationObject);
    }

    public RuleContext(final Rule rule, final RuleSession session, final Indenter indenter,
            final ValidationObject validationObject)
    {
        m_rule = rule;
        m_ruleSession = session;
        m_indenter = indenter;
        m_validationObject = validationObject;
    }

    public void fire()
    {
        log.debug("{} firing {}", m_indenter, this);
        m_indenter.increment();
        boolean b = false;
        try
        {
            m_rule.evaluate(m_ruleSession, m_validationObject,this);
        }
        catch (ConstraintViolationException e)
        {
            if (!m_ruleSession.isUnbinding(m_validationObject)) {
            	b = false;
                m_indenter.decrement();
                log.debug("{} completed firing {} result: {} {}", new Object[]{ m_indenter, this, b, e.getMessage() });
                e.setRule(m_rule);
                throw e;
            }
        }
        catch (InferenceException e)
        {
            b = false;
            m_indenter.decrement();
            log.debug("{} completed firing {} result: {} {}", new Object[]{ m_indenter, this, b, e.getMessage() });
            ConstraintViolationException e1 = new ConstraintViolationException(m_rule.getMessage(m_ruleSession,m_validationObject),e);
            e1.setRule(m_rule);
            throw e1;
        }
        m_indenter.decrement();
        log.debug("{} completed firing {}", new Object[]{ m_indenter, this });
    }

    public void reset()
    {
        log.debug("{}resetting {}", m_indenter, this);
        m_indenter.increment();
        clearExcludes();
        clearActivates();
        clearAssigns();
        clearReadOnlys();
        clearRequireds();
        m_indenter.decrement();
        log.debug("{}completed resetting {}", m_indenter, this);
    }

    private void clearActivates()
    {
        if (m_activates == null)
        {
            return;
        }
        for (RuleProxyField ruleProxyField : m_activates)
        {
            ruleProxyField.setInActive(true);
        }
        m_activates.clear();
    }

    private void clearReadOnlys()
    {
        if (m_readOnlys == null)
        {
            return;
        }
        for (RuleProxyField ruleProxyField : m_readOnlys)
        {
            ruleProxyField.setReadOnly(false);
        }
        m_readOnlys.clear();
    }

    private void clearRequireds()
    {
        if (m_requireds == null)
        {
            return;
        }
        for (RuleProxyField ruleProxyField : m_requireds)
        {
            ruleProxyField.setRequired(false);
        }
        m_requireds.clear();
    }

    private void clearExcludes()
    {
        if (m_excludes == null)
        {
            return;
        }
        for (Exclude exclude : m_excludes)
        {
            exclude.getProxyField().clearExclude(exclude.getKey());
        }
        m_excludes.clear();
    }

    private void clearAssigns()
    {
        if (m_assigns == null)
        {
            log.debug("{}No assigns",m_indenter);
            return;
        }
        for (RuleProxyField ruleProxyField : m_assigns)
        {
            ruleProxyField.reset();
        }
        m_assigns.clear();
    }

    public Rule getRule()
    {
        return m_rule;
    }
    public String toString()
    {
        return "Rule: "+m_rule.toString();
    }
    public void addAssign(RuleProxyField proxyField)
    {
        if (m_assigns == null)
        {
            m_assigns = new HashSet<RuleProxyField>();
        }
        m_assigns.add(proxyField);        
    }
    public void addReadOnly(RuleProxyField proxyField)
    {
        if (m_readOnlys == null)
        {
            m_readOnlys = new HashSet<RuleProxyField>();
        }
        m_readOnlys.add(proxyField);        
    }
    public void addActivate(RuleProxyField proxyField)
    {
        if (m_activates == null)
        {
            m_activates = new HashSet<RuleProxyField>();
        }
        m_activates.add(proxyField);        
   }
    public void addRequired(RuleProxyField proxyField)
    {
        if (m_requireds == null)
        {
            m_requireds = new HashSet<RuleProxyField>();
        }
        m_requireds.add(proxyField);        
    }
    public void addExclude(Exclude exclude)
    {
        if (m_excludes == null)
        {
            m_excludes = new HashSet<Exclude>();
        }
        m_excludes.add(exclude);        
    }
	public Set<RuleProxyField> getAllRuleProxyFields() {
		Set<RuleProxyField> ret = new HashSet<RuleProxyField>();
		if (m_activates != null)
		{
			ret.addAll(m_activates);
		}
		if (m_requireds != null)
		{
			ret.addAll(m_requireds);
		}
		if (m_assigns != null)
		{
			ret.addAll(m_assigns);
		}
		if (m_readOnlys != null)
		{
			ret.addAll(m_readOnlys);
		}
		return ret;
	}

}
