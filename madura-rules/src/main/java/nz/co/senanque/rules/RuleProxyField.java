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

import nz.co.senanque.validationengine.ListeningArray;
import nz.co.senanque.validationengine.ObjectMetadata;
import nz.co.senanque.validationengine.ProxyField;
import nz.co.senanque.validationengine.ValidationUtils;
import nz.co.senanque.validationengine.metadata.PropertyMetadata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * This describes the settings on a particular field.
 * 
 * @author Roger Parkinson
 * @version $Revision: 1.3 $
 */
public class RuleProxyField implements Serializable
{
	private static final long serialVersionUID = 1L;

	protected enum Required {
        TRUE, TEMP, FALSE
    }

    private final transient ProxyField m_proxyField;
    private transient List<RuleContext> m_inputRules = null;
    private transient List<RuleContext> m_outputRules = null;
    private transient Rule m_sourceRule = null;
    private static final Logger log = LoggerFactory.getLogger(RuleProxyField.class);
    private final transient Indenter m_indenter;
    private final transient RuleSessionImpl m_ruleSession;
    private transient boolean m_needsReset;

    protected RuleProxyField(final ProxyField proxyField, final Indenter indenter, final RuleSessionImpl ruleSession)
    {
        m_proxyField = proxyField;
        m_indenter = indenter;
        m_ruleSession = ruleSession;
    }

    protected String getPath()
    {
        return m_proxyField.getPath();
    }

    protected PropertyMetadata getPropertyMetadata()
    {
        return m_proxyField.getPropertyMetadata();
    }

    protected boolean isReadOnly()
    {
        return m_proxyField.isReadOnly();
    }

    protected void setReadOnly(final boolean readOnly)
    {
        m_proxyField.setReadOnly(readOnly);
    }

    protected void setInActive(final boolean readOnly)
    {
        m_proxyField.setInActive(readOnly);
    }

    protected boolean isInActive()
    {
        return m_proxyField.isInActive();
    }

    public List<RuleContext> getInputRules()
    {
        if (m_inputRules == null)
        {
            m_inputRules = new ArrayList<RuleContext>();
        }
        return m_inputRules;
    }

    private List<RuleContext> getOutputRules()
    {
        if (m_outputRules == null)
        {
            m_outputRules = new ArrayList<RuleContext>();
        }
        return m_outputRules;
    }
    protected void addOutputRule(final RuleContext ruleContext)
    {
        final List<RuleContext> rules = getOutputRules();
        if (!rules.contains(ruleContext))
        {
            rules.add(ruleContext);     
        }
    }
    protected void addInputRule(final RuleContext ruleContext)
    {
        final List<RuleContext> rules = getInputRules();
        if (!rules.contains(ruleContext))
        {
            rules.add(ruleContext);     
        }
    }
    protected void removeInputRule(final RuleContext ruleContext)
    {
        final List<RuleContext> rules = getInputRules();
        if (!rules.contains(ruleContext))
        {
            rules.remove(ruleContext);     
        }
    }
    public void reset()
    {
        log.debug("{}resetting {}",m_indenter,this);
        m_sourceRule = null;
        setNeedsReset(true);
        // if this field is annotated unknown then remember it for later.
        if (m_proxyField.getPropertyMetadata().isUnknown())
        {
        	m_ruleSession.addUnknownField(m_proxyField);
        }
        Object currentValue=null;
		try {
			currentValue = getValue();
		} catch (UnKnownFieldValueException e1) {
			// ignore
		}
        m_ruleSession.clearEvaluating();
        m_ruleSession.addAssignedField(m_proxyField);
        if (currentValue != null && !(currentValue instanceof ListeningArray<?>))
        {
            m_proxyField.reset();
        }
        m_ruleSession.addToReviewList(this);
        for (RuleContext rc: getInputRules())
        {
            if (m_ruleSession.alreadyResettingRule(rc))
            {
                continue;
            }
            try
            {
                rc.reset();
            }
            catch (InferenceException e)
            {
                log.error("{}attempting to reset rule: {}",new Object[]{m_indenter,rc},e);
            }
        }
    }

    public void setValue(final Object newValue)
    {
        if (m_sourceRule != null)
        {
        	String message = m_ruleSession.getMessage("nz.co.senanque.rules.cannot.force.value", new Object[]{ this.toString(),m_sourceRule.getRuleName() });
            throw new InferenceException(message);
        }
        if (!(newValue instanceof DummyValue))
        {
            m_proxyField.setValue(newValue);
        }
    }
    protected void setValue(final Object value, final Rule rule)
    {
        log.debug("{}Setting field: {} rule: {}",new Object[]{m_indenter,this,rule});
        m_sourceRule = rule;
        if (!(value instanceof DummyValue))
        {
            m_proxyField.setValue(value);
        }
    }

	/**
	 * Find the first unfilled field needed to supply this field and return it.
	 * @return proxyfield describing the unfilled field
	 */
	public RuleProxyField backChain() {
		ObjectMetadata objectMetadata = m_proxyField.getObjectMetadata();
		// loop through the rules that output this field
        for (RuleContext rc: getOutputRules())
        {
        	// loop through the fields needed by this rule to fire
            for (FieldReference fr :rc.getRule().listeners())
            {
            	RuleProxyField rpf = m_ruleSession.getRuleProxyField(objectMetadata.getProxyField(fr.getFieldName()));
        		// if this rpf has no value yet then look for an rpf that can deliver it.
        		// if none is found then return this rpf, else return the one we found.
            	if (rpf.askFor())
            	{
            		RuleProxyField rpf1 = rpf.backChain();
            		if (rpf1 == null)
            		{
            			return rpf;
            		}
            		else
            		{
            			return rpf1;
            		}
            	}
            }
        }
		return null;
	}
	private boolean askFor()
	{
		ProxyField proxy = getProxyField();
		if (proxy.isNotKnown())
		{
			// this means we asked for it and they said they did not know
			// so we don't ask again
	    	log.debug("{} NotKnown {} UnKnown {} FALSE",new Object[]{proxy.getFieldName(),proxy.isNotKnown(),proxy.isUnknown()});
			return false;
		}
		if (proxy.isUnknown())
		{
	    	log.debug("{} NotKnown {} UnKnown {} TRUE",new Object[]{proxy.getFieldName(),proxy.isNotKnown(),proxy.isUnknown()});
			return true;
		}
    	log.debug("{} NotKnown {} UnKnown {} FALSE",new Object[]{proxy.getFieldName(),proxy.isNotKnown(),proxy.isUnknown()});
		return false;
	}
    public Object getValue()
    {
    	if (m_proxyField == null)
    	{
    		log.debug("no proxyfield found");
    		return null;
    	}
    	if (m_proxyField.isUnknown() || m_proxyField.isNotKnown())
    	{
    		throw new UnKnownFieldValueException(this);
    	}
        Object ret = m_proxyField.getValue();
        return ret;
    }
    public void assign(final Object a)
    {
    	if (m_proxyField != null)
    	{
    		m_proxyField.assign(a);
    	}
    }

	/**
     * Get the current value without running the backchainer
     * @return the internal value
     */
    public Object getInternalValue()
    {
       return m_proxyField.getValue();
    }

    public String toString()
    {
        return getPath();
    }

    public boolean isRequired()
    {
        return m_proxyField.isRequired();
    }

    public boolean isDerived()
    {
        return m_proxyField.isDerived();
    }

    public void setDerived(final boolean derived)
    {
        m_proxyField.setDerived(derived);
    }

    public boolean needsReset()
    {
        return m_needsReset;
    }

    public void setNeedsReset(final boolean needsReset)
    {
        m_needsReset = needsReset;
    }

    public boolean isDifferent(final Object value)
    {
        return !ValidationUtils.equals(getInternalValue(),value);
    }

    public void setRequired(final boolean required)
    {
        m_proxyField.setRequired(required);        
    }

	public ProxyField getProxyField() {
		return m_proxyField;
	}


}
