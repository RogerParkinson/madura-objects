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
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import nz.co.senanque.rules.annotations.InternalFunction;
import nz.co.senanque.validationengine.ConvertUtils;
import nz.co.senanque.validationengine.ObjectMetadata;
import nz.co.senanque.validationengine.ProxyField;
import nz.co.senanque.validationengine.ProxyObject;
import nz.co.senanque.validationengine.ValidationException;
import nz.co.senanque.validationengine.ValidationObject;
import nz.co.senanque.validationengine.ValidationSession;
import nz.co.senanque.validationengine.choicelists.ChoiceBase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * 
 * 
 * A session is the link between a Package (containing rules) and a DOM (containing data)
 * The public interface to this class is the main interface to the engine.

 * @author Roger Parkinson
 * @version $Revision: 1.12 $
 * 
 */
public final class RuleSessionImpl implements RuleSession, Serializable
{
	private static final long serialVersionUID = -1L;

	private final transient static Logger s_log = LoggerFactory.getLogger(RuleSessionImpl.class);

    private final RulesPlugin m_plugin;

    private ValidationSession m_session;
    private final Indenter m_indenter = new Indenter();

    private final Set<RuleContext> m_resettingRules = new HashSet<RuleContext>();
    private final Set<RuleContext> m_firedRules = new HashSet<RuleContext>();
    private final Map<ValidationObject,ValidationObject> m_boundObjects = new IdentityHashMap<ValidationObject,ValidationObject>();
    private final Set<Integer> m_unbinding = new TreeSet<>();
    private final Map<ProxyField,RuleProxyField> m_ruleProxyFields = new HashMap<ProxyField,RuleProxyField>();

    private final Set<RuleProxyField> m_agenda = new HashSet<RuleProxyField>();
    private final Set<RuleProxyField> m_reviewList = new HashSet<RuleProxyField>();

    private Set<ProxyField> m_assignedFields = new HashSet<ProxyField>();
    private Set<ProxyField> m_unknownFields = new HashSet<ProxyField>();
    private Map<String,RuleContext> m_ruleContextsMap = new HashMap<String,RuleContext>();

    private Set<ProxyField> m_readOnlyFields = new HashSet<ProxyField>();
    private Set<ProxyField> m_activateFields = new HashSet<ProxyField>();
    private Set<ProxyField> m_requiredFields = new HashSet<ProxyField>();
    private Set<Exclude> m_excludes  = new HashSet<Exclude>();

	private final transient MessageSourceAccessor m_messageSourceAccessor;

	private Set<RuleProxyField> m_evaluating = new HashSet<RuleProxyField>();

	private boolean m_open=true;

    protected RuleSessionImpl(final RulesPlugin plugin, final ValidationSession session)
	{
	    m_plugin = plugin;
	    m_session = session;
	    m_messageSourceAccessor = new MessageSourceAccessor(m_session.getValidationEngine().getMessageSource());
	}
    public RuleProxyField getRuleProxyField(final ProxyField proxyField)
    {
        RuleProxyField ruleProxyField = m_ruleProxyFields.get(proxyField); 
        if (ruleProxyField == null)
        {
            ruleProxyField = new RuleProxyField(proxyField,m_indenter,this);
            m_ruleProxyFields.put(proxyField,ruleProxyField);
        }
        return ruleProxyField;
    }
    protected RuleContext getRuleContext(final Rule rule, final ValidationObject validationObject, final ValidationObject ownerObject) throws FailsToMatchException
    {
        ValidationObject o = validationObject;
        ClassReference cr = this.m_plugin.getClassReferenceMap().get(rule.getClassName());       
        Class<?> ruleScope = cr.getClazz();
        Class<?> validationClass = validationObject.getClass();
        if (!ruleScope.isAssignableFrom(validationClass))
        {
            if (ownerObject == null)
            {
                throw new FailsToMatchException("Fatal internal error. "+o.getClass().getSimpleName()+" fails to match "+rule.getClassName());
            }
            o = ownerObject;
            if (!ruleScope.isAssignableFrom(ownerObject.getClass()))
            {
                throw new FailsToMatchException("Fatal internal error. "+o.getClass().getSimpleName()+" fails to match "+rule.getClassName());
            }
        }
        final String key = RuleContext.getKey(rule, o);
        RuleContext ret = m_ruleContextsMap.get(key);
        if (ret == null)
        {
            ret = new RuleContext(rule,this,m_indenter,o);
            m_ruleContextsMap.put(key,ret);
        }
        return ret;
    }
    /* (non-Javadoc)
     * @see nz.co.senanque.rules.RuleSession#getOperations()
     */
    
    public Operations getOperations()
    {
        return m_plugin.getOperations();
    }
    /* (non-Javadoc)
     * @see nz.co.senanque.rules.RuleSession#getMetadata(nz.co.senanque.validationengine.ValidationObject)
     */
    
    public ObjectMetadata getMetadata(ValidationObject object)
    {
        return m_session.getMetadata(object);
    }
    /* (non-Javadoc)
     * @see nz.co.senanque.rules.RuleSession#getSession()
     */
    
    public ValidationSession getSession()
    {
        return m_session;
    }
    /* (non-Javadoc)
     * @see nz.co.senanque.rules.RuleSession#setValue(nz.co.senanque.rules.RuleProxyField, java.lang.Object)
     */
    
    public void setValue(final RuleProxyField proxyField, final Object value)
    {
        final List<ProposedValue> newValues = new ArrayList<ProposedValue>();
        newValues.add(new ProposedValue(proxyField,value));
        setValues(newValues);
    }
	protected boolean alreadyFired(final RuleContext ruleContext)
	{
		if (m_firedRules.contains(ruleContext))
		{
		    return true;
		}
		m_firedRules.add(ruleContext);
		return false;
	}
	protected void forwardChain()
	{
	    s_log.debug("{}Forward Chain...",m_indenter);
	    m_indenter.increment();
	    final Set<RuleContext> rules = new HashSet<RuleContext>();
		m_firedRules.clear();
		while (m_agenda.size() > 0)
		{
			rules.clear();
			for (RuleProxyField proxy: m_agenda)
			{
			    s_log.debug("{}agenda item: {}",m_indenter,proxy.toString());
			    List<RuleContext> ruleContexts = proxy.getInputRules();
				rules.addAll(ruleContexts);
			}
			if (s_log.isDebugEnabled())
			{
			    s_log.debug("{}rules to fire:{}",m_indenter,rules.size());
			    m_indenter.increment();
			    for (RuleContext rule: rules)
			    {
					s_log.debug("{}{}",m_indenter,rule);
				}
				m_indenter.decrement();
			}
			clearAgenda();
            for (RuleContext ruleContext: rules)
			{
				ruleContext.fire();
			}
		}
		m_indenter.decrement();
		s_log.debug("{}Completed Forward Chain",m_indenter);
	}
	/* (non-Javadoc)
     * @see nz.co.senanque.rules.RuleSession#clearDerived()
     */
	
    public List<RuleProxyField> clearDerived()
	{
        List<RuleProxyField> ret = new ArrayList<RuleProxyField>();
        List<ProxyObject> proxyObjects = m_session.getProxyObjects();
        for (ProxyObject proxyObject: proxyObjects)
        {
            for (Map.Entry<String,ProxyField> entry: proxyObject.getFieldMap().entrySet())
            {
                ProxyField proxyField = entry.getValue();
                if (proxyField.isDerived())
                {
                    RuleProxyField ruleProxyField = m_ruleProxyFields.get(proxyField);
                    ret.add(ruleProxyField);
                    ruleProxyField.reset();
                }
            }
        }
        return ret;
	}
    protected void addToAgenda(RuleProxyField ruleProxyField)
    {
        if (m_agenda.contains(ruleProxyField))
        {
            return;
        }
        s_log.debug("{}adding to agenda {}",m_indenter,ruleProxyField);
        m_agenda.add(ruleProxyField);
    }
    protected void clearAgenda()
    {
        s_log.debug("{}clearing agenda",m_indenter);
        m_agenda.clear();
    }

	/**
	 * Method alreadyResettingRule.
	 * Sometimes a rule attempts to reset twice and gets into a loop
	 * This checks for that situation. Rules only need to be reset once.
	 * @param rc
	 * @return boolean
	 */
	protected boolean alreadyResettingRule(final RuleContext rc)
	{
		if (m_resettingRules.contains(rc))
		{
		    return true;
		}
		m_resettingRules.add(rc);
		return false;
	}
	public boolean alreadyEvaluating(RuleProxyField ruleProxyField) {
		if (m_evaluating.contains(ruleProxyField))
		{
		    return true;
		}
		m_evaluating.add(ruleProxyField);
		return false;
	}
	public void clearEvaluating()
	{
		m_evaluating.clear();
	}

	/* (non-Javadoc)
     * @see nz.co.senanque.rules.RuleSession#setValues(java.util.List)
     */
	
    public void setValues(List<ProposedValue> values)
		throws InferenceException
	{
		m_resettingRules.clear();
        m_assignedFields.clear();
        m_readOnlyFields.clear();
        m_activateFields.clear();
        m_requiredFields.clear();
		m_excludes.clear();
		@SuppressWarnings("unused")
		int count=0;
		m_indenter.clear();
		s_log.debug("{}Setting values...",m_indenter);
		m_indenter.increment();
		for (ProposedValue pv: values)
		{
			RuleProxyField rpf = null;
			count++;
			try
			{
			    rpf = pv.getRuleProxyField();
		        addToAgenda(rpf);
				rpf.reset();
				rpf.setValue(pv.getValue());
			}
			catch (InferenceException e)
			{
                clearAgenda();
				throw e;
			}
	        finally
	        {
	        	removeUnknownField(rpf.getProxyField());
	            m_resettingRules.clear();
	            clearReviewList();
	        }
		}
		try
		{
			forwardChain();
		}
        catch (InferenceException e)
        {
            s_log.error(e.getMessage(),e);
            throw e;
        }
        catch (ConstraintViolationException e)
        {
            s_log.error(e.getMessage(),e);
            throw e;
        }
        catch (ValidationException e)
        {
            s_log.error(e.getMessage(),e);
            throw e;
        }
		finally
		{
            clearAgenda();
			m_resettingRules.clear();
            clearReviewList();
		}
		m_indenter.clear();
		// This effectively commits the changes to the exposed data
		boolean enabled = m_session.isEnabled();
		m_session.setEnabled(false);
        for (ProxyField proxyField: m_assignedFields)
        {
            proxyField.updateValue();
        }
        for (ProxyField proxyField: m_unknownFields)
        {
            proxyField.getFieldMetadata().setUnknown(true);
        }
        m_unknownFields.clear();
        for (ProxyField proxyField: m_activateFields)
        {
            proxyField.setInActive(false);
        }
        for (ProxyField proxyField: m_readOnlyFields)
        {
            proxyField.setReadOnly(true);
        }
        for (ProxyField proxyField: m_requiredFields)
        {
            proxyField.setRequired(true);
        }
        for (Exclude exclude: m_excludes)
        {
            ProxyField proxyField = exclude.getProxyField();
            proxyField.exclude(exclude.getKey());
            exclude.getRuleContext().addExclude(exclude);

        }
		m_session.setEnabled(enabled);
		s_log.debug("Finished Setting values");
	}

	/**
	 * Method addToReviewList.
	 * @param proxyField
	 */
	protected void addToReviewList(RuleProxyField proxyField)
	{
		m_reviewList.add(proxyField);
	}

	protected void clearReviewList()
	{
		for (RuleProxyField proxyField: m_reviewList)
		{
			proxyField.setNeedsReset(false);
		}
		m_reviewList.clear();
	}

	/**
	 * Method killRuleContext.
	 * @param rc
	 */
	protected void killRuleContext(final RuleContext rc)
	{
		m_ruleContextsMap.remove(rc.getKey());
	}
    /**
     * @param proxyField
     * @return true if required
     */
    public static boolean isRequired(RuleProxyField proxyField)
    {
		return proxyField.isRequired();
    }
    protected void log(Object[] messages)
    {
        s_log.debug("{}{}",m_indenter,messages);
    }
    /* (non-Javadoc)
     * @see nz.co.senanque.rules.RuleSession#getIndenter()
     */
    
    public Indenter getIndenter()
    {
        return m_indenter;
    }
    /* (non-Javadoc)
     * @see nz.co.senanque.rules.RuleSession#removeRuleProxyField(nz.co.senanque.validationengine.ProxyField)
     */
    
    public void removeRuleProxyField(ProxyField proxyField)
    {
        // TODO: if this is a complex object it may have attached fields to remove as well
        m_ruleProxyFields.remove(proxyField);        
    }
    /* (non-Javadoc)
     * @see nz.co.senanque.rules.RuleSession#assign(nz.co.senanque.rules.RuleContext, java.lang.Object, nz.co.senanque.validationengine.ProxyField)
     */
    
    @InternalFunction(operator="=", precedence=1,isAssign=true)
    public void assign(RuleContext ruleContext, Object value, RuleProxyField target)
    {
        s_log.debug("{}assign: {} {}",new Object[]{m_indenter,target,value});
        assign(target,value,ruleContext,true);
    }
    /* (non-Javadoc)
     * @see nz.co.senanque.rules.RuleSession#assign(nz.co.senanque.rules.RuleContext, java.lang.Object, java.util.List)
     */
    
    public void assign(RuleContext ruleContext, Object value, List<ProxyField> list)
    {
        for (ProxyField proxyField: list)
        {
            assign(getRuleProxyField(proxyField),value,ruleContext,true);
        }
    }
    /* (non-Javadoc)
     * @see nz.co.senanque.rules.RuleSession#assign(nz.co.senanque.validationengine.ProxyField, java.lang.Object, nz.co.senanque.rules.RuleContext, boolean)
     */
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public void assign(RuleProxyField target, Object value, RuleContext ruleContext, boolean dummy)
    {
    	ProxyField target1 = target.getProxyField();
        Class<?> clazz = target1.getPropertyMetadata().getSetMethod().getParameterTypes()[0];
        if (value instanceof List<?>)
        {
			List<ProxyField> sourceList = (List<ProxyField>)value;
            RuleProxyField rpf = target;
            for (ProxyField pf: sourceList)
            {
                Object listValue = pf.getValue();
                if (rpf.isDifferent(listValue))
                {
                    if (isExcluded(target1,String.valueOf(listValue)))
                    {
                        String message = m_messageSourceAccessor.getMessage("nz.co.senanque.rules.excluded.value", new Object[]{ listValue, target1.getFieldName()});
                        throw new InferenceException(message);
                    }
                    if (listValue != null && !clazz.isAssignableFrom(listValue.getClass()))
                    {
                        listValue = ConvertUtils.convertToComparable((Class<Comparable>)clazz, listValue,m_messageSourceAccessor);
                    }
                    addAssignedField(target1);
                    target.assign(listValue);
                    addToAgenda(rpf);
                }
                ruleContext.addAssign(rpf);
            }
            return;
        }
        if (value != null && !clazz.isAssignableFrom(value.getClass()))
        {
            value = ConvertUtils.convertToComparable((Class<Comparable>)clazz, value,m_messageSourceAccessor);
        }
        RuleProxyField rpf = getRuleProxyField(target1);
        if (rpf.isDifferent(value))
        {
            if (isExcluded(target1,String.valueOf(value)))
            {
                String message = m_messageSourceAccessor.getMessage("nz.co.senanque.rules.excluded.value", new Object[]{ value, target1.getFieldName()});
                throw new InferenceException(message);
            }
            addAssignedField(target1);
            removeUnknownField(target1);
            target.assign(value);
            addToAgenda(rpf);
        }
        ruleContext.addAssign(rpf);
    }
    /* (non-Javadoc)
     * @see nz.co.senanque.rules.RuleSession#addAssignedField(nz.co.senanque.validationengine.ProxyField)
     */
    
    public void addAssignedField(ProxyField proxyField)
    {
        m_assignedFields.add(proxyField);
    }
    public void addUnknownField(ProxyField proxyField)
    {
    	if (!m_unknownFields.contains(proxyField))
    	{
    		m_unknownFields.add(proxyField);
    	}
    }
    public void removeUnknownField(ProxyField proxyField)
    {
        m_unknownFields.remove(proxyField);
    }
    /* (non-Javadoc)
     * @see nz.co.senanque.rules.RuleSession#bind(nz.co.senanque.validationengine.ValidationObject, nz.co.senanque.validationengine.ProxyField, nz.co.senanque.rules.FieldReference, nz.co.senanque.validationengine.ValidationObject)
     */
    
    public void bind(final ValidationObject validationObject, final ProxyField proxyField, final FieldReference fieldReference, final ValidationObject owner)
    {
        final RuleProxyField ruleProxyField = getRuleProxyField(proxyField);
        for (Rule rule: fieldReference.getListeners())
        {
            RuleContext ruleContext;
			try {
				ruleContext = getRuleContext(rule,validationObject,(owner==null)?validationObject:owner);
			} catch (FailsToMatchException e) {
				continue;
			}
            ruleProxyField.addInputRule(ruleContext);
        }
        for (Rule rule: fieldReference.getOutputs())
        {
            RuleContext ruleContext;
			try {
				ruleContext = getRuleContext(rule,validationObject,(owner==null)?validationObject:owner);
			} catch (FailsToMatchException e) {
				continue;
			}
            ruleProxyField.addOutputRule(ruleContext);
        }

    }
    /* (non-Javadoc)
     * @see nz.co.senanque.rules.RuleSession#bind(nz.co.senanque.validationengine.ValidationObject)
     */
    
    public boolean bind(ValidationObject validationObject)
    {
        if (m_boundObjects.containsKey(validationObject))
        {
            return false;
        }
        m_boundObjects.put(validationObject,validationObject);
        return true;
    }
    /* (non-Javadoc)
     * @see nz.co.senanque.rules.RuleSession#bind(nz.co.senanque.validationengine.ValidationObject, nz.co.senanque.validationengine.ProxyField, nz.co.senanque.rules.FieldReference, nz.co.senanque.validationengine.ValidationObject)
     */
    public void unbind(final ValidationObject validationObject, final ProxyField proxyField, final FieldReference fieldReference, final ValidationObject owner)
    {
        final RuleProxyField ruleProxyField = getRuleProxyField(proxyField);
        for (Rule rule: fieldReference.getListeners())
        {
            RuleContext ruleContext;
			try {
				ruleContext = getRuleContext(rule,validationObject,(owner==null)?validationObject:owner);
			} catch (FailsToMatchException e) {
				continue;
			}
            ruleProxyField.removeInputRule(ruleContext);
        }
        removeRuleProxyField(proxyField);
    }
    /* (non-Javadoc)
     * @see nz.co.senanque.rules.RuleSession#bind(nz.co.senanque.validationengine.ValidationObject)
     */
    public void unbind(ValidationObject validationObject)
    {
        Integer id = System.identityHashCode(validationObject);
    	m_unbinding.add(id);
        List<ProposedValue> proposedValues = new ArrayList<ProposedValue>();
    	ProxyObject proxyObject = getSession().getProxyObject(validationObject);
    	for (ProxyField proxyField: proxyObject.getFieldMap().values())
    	{
    		if (!proxyField.isDerived())
    		{
    			// Figure out if this property contains a list or a known object type
    			// we want to ignore those and only reset primitive objects (long, String etc)
    			Class<?> clazz = proxyField.getPropertyMetadata().getGetMethod().getReturnType();
       			if (clazz.isAssignableFrom(List.class))
    			{
    				continue;
    			}
       			if (getSession().getValidationEngine().getClassMetadata(clazz) != null)
    			{
    				continue;
    			}
    			RuleProxyField ruleProxyField = getRuleProxyField(proxyField);
    			if (ruleProxyField.isDifferent(proxyField.getInitialValue()))
    			{
    				ProposedValue pv = new ProposedValue(ruleProxyField,proxyField.getInitialValue());
    				proposedValues.add(pv);
    			}
    		}    		
    	}
        setValues(proposedValues);
        m_boundObjects.remove(id);
    	m_unbinding.remove(id);
        List<String> kill = new ArrayList<String>();
        for (Map.Entry<String, RuleContext> entry: m_ruleContextsMap.entrySet())
        {
        	if (entry.getValue().getValidationObject() == validationObject)
        	{
        		kill.add(entry.getKey());
        	}
        }

        for (String key: kill)
        {
        	RuleContext rc = m_ruleContextsMap.remove(key);
        	rc.reset();
        }
    }
    public boolean isUnbinding(ValidationObject validationObject) {
    	return (m_unbinding.contains(System.identityHashCode(validationObject)));
    }
    /* (non-Javadoc)
     * @see nz.co.senanque.rules.RuleSession#activate(nz.co.senanque.rules.RuleContext, nz.co.senanque.validationengine.ProxyField)
     */
    
    @InternalFunction(precedence=1,isAssign=true)
    public void activate(RuleContext ruleContext,RuleProxyField ruleProxyField)
    {
        setActivate(ruleProxyField,ruleContext);
    }
    /* (non-Javadoc)
     * @see nz.co.senanque.rules.RuleSession#setActivate(nz.co.senanque.validationengine.ProxyField, nz.co.senanque.rules.RuleContext)
     */
    
    public void setActivate(RuleProxyField ruleProxyField,
            RuleContext ruleContext)
    {
        if (ruleProxyField.isInActive()) // only do it if the field changes
        {
            m_activateFields.add(ruleProxyField.getProxyField());
            ruleContext.addActivate(ruleProxyField);
        }
    }
    /* (non-Javadoc)
     * @see nz.co.senanque.rules.RuleSession#readonly(nz.co.senanque.rules.RuleContext, nz.co.senanque.validationengine.ProxyField)
     */
    @InternalFunction(precedence=1,isAssign=true)
    public void readonly(RuleContext ruleContext,RuleProxyField ruleProxyField)
    {
        setReadOnly(ruleProxyField,ruleContext);
    }
    /* (non-Javadoc)
     * @see nz.co.senanque.rules.RuleSession#setReadOnly(nz.co.senanque.validationengine.ProxyField, nz.co.senanque.rules.RuleContext)
     */
    public void setReadOnly(RuleProxyField ruleProxyField,
            RuleContext ruleContext)
    {
        if (!ruleProxyField.isReadOnly()) // only do it if the field changes
        {
            m_readOnlyFields.add(ruleProxyField.getProxyField());
            ruleContext.addReadOnly(ruleProxyField);
        }
    }
    /* (non-Javadoc)
     * @see nz.co.senanque.rules.RuleSession#require(nz.co.senanque.rules.RuleContext, nz.co.senanque.validationengine.ProxyField)
     */
    @InternalFunction(precedence=1,isAssign=true)
    public void require(RuleContext ruleContext,RuleProxyField ruleProxyField)
    {
        setRequired(ruleProxyField,ruleContext);
    }
    @InternalFunction(precedence=1,isAssign=false)
    public boolean isNotKnown(RuleProxyField ruleProxyField)
    {
        return ruleProxyField.getProxyField().isNotKnown();
    }
    /* (non-Javadoc)
     * @see nz.co.senanque.rules.RuleSession#setRequired(nz.co.senanque.validationengine.ProxyField, nz.co.senanque.rules.RuleContext)
     */
    public void setRequired(RuleProxyField ruleProxyField,
            RuleContext ruleContext)
    {
        {
            if (!ruleProxyField.isRequired()) // only do it if the field changes
            {
                m_requiredFields.add(ruleProxyField.getProxyField());
                ruleContext.addRequired(ruleProxyField);
            }
        }
    }
    /* (non-Javadoc)
     * @see nz.co.senanque.rules.RuleSession#exclude(nz.co.senanque.rules.RuleContext, java.lang.String, nz.co.senanque.validationengine.ProxyField)
     */
    @InternalFunction(precedence=1,isAssign=true)
    public void exclude(RuleContext ruleContext, String key, RuleProxyField proxyField)
    {
        exclude(proxyField,key,ruleContext);
    }
    /* (non-Javadoc)
     * @see nz.co.senanque.rules.RuleSession#exclude(nz.co.senanque.validationengine.ProxyField, java.lang.String, nz.co.senanque.rules.RuleContext)
     */
    public void exclude(RuleProxyField ruleProxyField, String key,
            RuleContext ruleContext)
    {
        if (key.equals(ruleProxyField.getValue())) // this does not match when it should
        {
            String message = m_messageSourceAccessor.getMessage("nz.co.senanque.rules.invalid.exclude",
                    new Object[]
                    { ruleProxyField.getPropertyMetadata().getLabelName(), key });
            throw new InferenceException(message);
        }
        s_log.debug("Excluding {} for {}", key, ruleProxyField.getProxyField().getFieldName());
        if (!isExcluded(ruleProxyField.getProxyField(),key)) // only do this if not already excluded
        {
            Exclude exclude = new Exclude(ruleProxyField.getProxyField(), key,ruleContext);
            m_excludes.add(exclude);
            s_log.debug("{}excluding: {} {}",new Object[]{m_indenter,ruleProxyField,key});
        }        
    }
    /* (non-Javadoc)
     * @see nz.co.senanque.rules.RuleSession#autoAssign(nz.co.senanque.validationengine.ProxyField, nz.co.senanque.rules.RuleContext)
     */
    public void autoAssign(ProxyField proxyField, RuleContext ruleContext)
    {
        // Count the excludes left and if only 1 then assign this value.
        // TODO: work out what should happen if a later rule excludes this value
        ChoiceBase lastChoice = findLastChoice(proxyField);
        if (lastChoice != null)
        {
            Object value = proxyField.getPropertyMetadata().convertFromString(lastChoice.getKey().toString());
            s_log.debug("{}auto-assign: {} {}",new Object[]{m_indenter,proxyField,value});
            assign(getRuleProxyField(proxyField),value,ruleContext,true);
        }        
    }
    private boolean isExcluded(ProxyField proxyField, String key)
    {
        if (proxyField.isExcluded(key))
        {
            return true;
        }
        for (Exclude exclude: m_excludes)
        {
            if (exclude.getProxyField().equals(proxyField) && exclude.getKey().equals(key))
            {
                return true;
            }
        }
        return false;
    }
    private ChoiceBase findLastChoice(ProxyField proxyField)
    {
        int count = proxyField.getChoiceList().size();
        ChoiceBase ret = null;
        for (ChoiceBase choiceBase : proxyField.getChoiceList())
        {
            if (isExcluded(proxyField, choiceBase.getKey().toString()))
            {
                count--;
            }
            else
            {
                ret = choiceBase;
            }
        }
        if (count == 1)
        {
            return ret;
        }
        else
        {
            return null;
        }
        
    }
    /* (non-Javadoc)
     * @see nz.co.senanque.rules.RuleSession#getConstant(java.lang.String)
     */
    public String getConstant(String key)
    {
        return m_plugin.getConstants().get(key);
    }
    /* (non-Javadoc)
     * @see nz.co.senanque.rules.RuleSession#getMessage(java.lang.String, java.lang.Object[])
     */
    public String getMessage(String message, Object[] args)
    {
        for (int i=0;i<args.length;i++)
        {
            if (args[i] instanceof List<?>)
            {
                @SuppressWarnings("unchecked")
				List<ProxyField> list = (List<ProxyField>)args[i];
                StringBuilder sb = new StringBuilder();
                for (ProxyField pf: list)
                {
                    sb.append(String.valueOf(pf.getValue()));
                    sb.append(",");
                }
                if (sb.length()>0)
                {
                    args[i] = sb.substring(0, sb.length()-1);
                }
                else
                {
                    args[i] = "";
                }
            }
        }
        return m_messageSourceAccessor.getMessage(message, args);
    }
    /* (non-Javadoc)
     * @see nz.co.senanque.rules.RuleSession#getLastProxyField()
     */
    public ProxyField getLastProxyField()
    {
        if (m_assignedFields.size() == 1)
        {
            for (ProxyField proxyField: m_assignedFields)
            {
                return proxyField;
            }
        }
        return null;
    }
	public String getStats(RuleSession ruleSession) {
		return MessageFormat.format("\nRule session has {0} objects bound ruleproxyfields: {1} m_ruleContextsMap: {2}", m_boundObjects.size(), m_ruleProxyFields.size(), m_ruleContextsMap.size());
	}

	public MessageSourceAccessor getMessageSourceAccessor() {
		return m_messageSourceAccessor;
	}
	public void setOpen(boolean b) {
		m_open = b;
	}
	public boolean isOpen() {
		return m_open;
	}

}
