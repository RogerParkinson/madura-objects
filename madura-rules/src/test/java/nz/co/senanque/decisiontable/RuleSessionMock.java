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
package nz.co.senanque.decisiontable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nz.co.senanque.rules.FieldReference;
import nz.co.senanque.rules.Indenter;
import nz.co.senanque.rules.InferenceException;
import nz.co.senanque.rules.Operations;
import nz.co.senanque.rules.ProposedValue;
import nz.co.senanque.rules.RuleContext;
import nz.co.senanque.rules.RuleProxyField;
import nz.co.senanque.rules.RuleSession;
import nz.co.senanque.validationengine.ObjectMetadata;
import nz.co.senanque.validationengine.ProxyField;
import nz.co.senanque.validationengine.ValidationObject;
import nz.co.senanque.validationengine.ValidationSession;
import nz.co.senanque.validationengine.choicelists.ChoiceBase;

import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;

/**
 * 
 * Short description
 * 
 * @author Roger Parkinson
 * @version $Revision:$
 */
public class RuleSessionMock implements RuleSession
{
    private static Logger logger = LoggerFactory.getLogger(RuleSessionMock.class);


    Map<String,List<ChoiceBase>> m_map = new HashMap<String,List<ChoiceBase>>();
    
    public RuleSessionMock(Element decisionTableElement, MessageSource messageSource)
    {
        List<Element> choiceListElements = decisionTableElement.getChildren("ChoiceList");
        for (Element choiceListElement: choiceListElements)
        {
            String fieldName = choiceListElement.getAttributeValue("name");
            List<ChoiceBase> choices = new ArrayList<ChoiceBase>();
            for (Element choiceElement: (List<Element>)choiceListElement.getChildren())
            {
                ChoiceBase choiceBase =  new ChoiceBase(choiceElement.getAttributeValue("name"),choiceElement.getText(),messageSource);
                choices.add(choiceBase);
            }
            m_map.put(fieldName, choices);
        }
    }
    protected List<ChoiceBase> getChoices(String fieldName)
    {
        return m_map.get(fieldName);
    }

    /* (non-Javadoc)
     * @see nz.co.senanque.rules.RuleSession#bind(nz.co.senanque.validationengine.ValidationObject, nz.co.senanque.validationengine.ProxyField, nz.co.senanque.rules.FieldReference, nz.co.senanque.validationengine.ValidationObject)
     */
    
    public void bind(ValidationObject validationObject, ProxyField proxyField,
            FieldReference fieldReference, ValidationObject owner)
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see nz.co.senanque.rules.RuleSession#getRuleProxyField(nz.co.senanque.validationengine.ProxyField)
     */
    
    public RuleProxyField getRuleProxyField(ProxyField proxyField)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see nz.co.senanque.rules.RuleSession#getOperations()
     */
    
    public Operations getOperations()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see nz.co.senanque.rules.RuleSession#getMetadata(nz.co.senanque.validationengine.ValidationObject)
     */
    
    public ObjectMetadata getMetadata(ValidationObject object)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see nz.co.senanque.rules.RuleSession#getSession()
     */
    
    public ValidationSession getSession()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see nz.co.senanque.rules.RuleSession#setValue(nz.co.senanque.rules.RuleProxyField, java.lang.Object)
     */
    
    public void setValue(RuleProxyField proxyField, Object value)
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see nz.co.senanque.rules.RuleSession#evaluateReadOnly()
     */
    
    public void evaluateReadOnly()
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see nz.co.senanque.rules.RuleSession#clearDerived()
     */
    
    public List<RuleProxyField> clearDerived()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see nz.co.senanque.rules.RuleSession#setValues(java.util.List)
     */
    
    public void setValues(List<ProposedValue> values) throws InferenceException
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see nz.co.senanque.rules.RuleSession#getIndenter()
     */
    
    public Indenter getIndenter()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see nz.co.senanque.rules.RuleSession#removeRuleProxyField(nz.co.senanque.validationengine.ProxyField)
     */
    
    public void removeRuleProxyField(ProxyField proxyField)
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see nz.co.senanque.rules.RuleSession#assign(nz.co.senanque.rules.RuleContext, java.lang.Object, nz.co.senanque.validationengine.ProxyField)
     */
    
    public void assign(RuleContext ruleContext, Object value, ProxyField target)
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see nz.co.senanque.rules.RuleSession#assign(nz.co.senanque.rules.RuleContext, java.lang.Object, java.util.List)
     */
    
    public void assign(RuleContext ruleContext, Object value,
            List<ProxyField> list)
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see nz.co.senanque.rules.RuleSession#assign(nz.co.senanque.validationengine.ProxyField, java.lang.Object, nz.co.senanque.rules.RuleContext, boolean)
     */
    
    public void assign(ProxyField target, Object value,
            RuleContext ruleContext, boolean dummy)
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see nz.co.senanque.rules.RuleSession#addAssignedField(nz.co.senanque.validationengine.ProxyField)
     */
    
    public void addAssignedField(ProxyField proxyField)
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see nz.co.senanque.rules.RuleSession#bind(nz.co.senanque.validationengine.ValidationObject)
     */
    
    public boolean bind(ValidationObject validationObject)
    {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see nz.co.senanque.rules.RuleSession#activate(nz.co.senanque.rules.RuleContext, nz.co.senanque.validationengine.ProxyField)
     */
    
    public void activate(RuleContext ruleContext, ProxyField proxyField)
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see nz.co.senanque.rules.RuleSession#setActivate(nz.co.senanque.validationengine.ProxyField, nz.co.senanque.rules.RuleContext)
     */
    
    public void setActivate(ProxyField proxyField, RuleContext ruleContext)
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see nz.co.senanque.rules.RuleSession#readonly(nz.co.senanque.rules.RuleContext, nz.co.senanque.validationengine.ProxyField)
     */
    
    public void readonly(RuleContext ruleContext, ProxyField proxyField)
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see nz.co.senanque.rules.RuleSession#setReadOnly(nz.co.senanque.validationengine.ProxyField, nz.co.senanque.rules.RuleContext)
     */
    
    public void setReadOnly(ProxyField proxyField, RuleContext ruleContext)
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see nz.co.senanque.rules.RuleSession#require(nz.co.senanque.rules.RuleContext, nz.co.senanque.validationengine.ProxyField)
     */
    
    public void require(RuleContext ruleContext, ProxyField proxyField)
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see nz.co.senanque.rules.RuleSession#setRequired(nz.co.senanque.validationengine.ProxyField, nz.co.senanque.rules.RuleContext)
     */
    
    public void setRequired(ProxyField proxyField, RuleContext ruleContext)
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see nz.co.senanque.rules.RuleSession#exclude(nz.co.senanque.rules.RuleContext, java.lang.String, nz.co.senanque.validationengine.ProxyField)
     */
    
    public void exclude(RuleContext ruleContext, String key,
            ProxyField proxyField)
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see nz.co.senanque.rules.RuleSession#exclude(nz.co.senanque.validationengine.ProxyField, java.lang.String, nz.co.senanque.rules.RuleContext)
     */
    
    public void exclude(ProxyField proxyField, String key,
            RuleContext ruleContext)
    {
        logger.debug("Excluding {} for {}", key, proxyField.getFieldName());
        proxyField.exclude(key);
    }

    /* (non-Javadoc)
     * @see nz.co.senanque.rules.RuleSession#autoAssign(nz.co.senanque.validationengine.ProxyField, nz.co.senanque.rules.RuleContext)
     */
    
    public void autoAssign(ProxyField proxyField, RuleContext ruleContext)
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see nz.co.senanque.rules.RuleSession#getConstant(java.lang.String)
     */
    
    public String getConstant(String key)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see nz.co.senanque.rules.RuleSession#getMessage(java.lang.String, java.lang.Object[])
     */
    
    public String getMessage(String message, Object[] args)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see nz.co.senanque.rules.RuleSession#getLastProxyField()
     */
    
    public ProxyField getLastProxyField()
    {
        // TODO Auto-generated method stub
        return null;
    }
	
	public String getStats(RuleSession ruleSession) {
		// TODO Auto-generated method stub
		return null;
	}
	public void assign(RuleContext ruleContext, Object value,
			RuleProxyField target) {
		// TODO Auto-generated method stub
		
	}
	public void assign(RuleProxyField target, Object value,
			RuleContext ruleContext, boolean dummy) {
		// TODO Auto-generated method stub
		
	}
	public void activate(RuleContext ruleContext, RuleProxyField ruleProxyField) {
		// TODO Auto-generated method stub
		
	}
	public void setActivate(RuleProxyField proxyField, RuleContext ruleContext) {
		// TODO Auto-generated method stub
		
	}
	public void readonly(RuleContext ruleContext, RuleProxyField ruleProxyField) {
		// TODO Auto-generated method stub
		
	}
	public void setReadOnly(RuleProxyField proxyField, RuleContext ruleContext) {
		// TODO Auto-generated method stub
		
	}
	public void require(RuleContext ruleContext, RuleProxyField ruleProxyField) {
		// TODO Auto-generated method stub
		
	}
	public void setRequired(RuleProxyField proxyField, RuleContext ruleContext) {
		// TODO Auto-generated method stub
		
	}
	public void exclude(RuleContext ruleContext, String key,
			RuleProxyField proxyField) {
		// TODO Auto-generated method stub
		
	}
	public void exclude(RuleProxyField proxyField, String key,
			RuleContext ruleContext) {
		// TODO Auto-generated method stub
		
	}
	public boolean isNotKnown(RuleContext ruleContext,
			RuleProxyField ruleProxyField) {
		// TODO Auto-generated method stub
		return false;
	}
	public boolean isNotKnown(RuleProxyField ruleProxyField) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean isUnbinding(ValidationObject validationObject) {
		// TODO Auto-generated method stub
		return false;
	}

}
