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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import nz.co.senanque.resourceloader.MessageResource;
import nz.co.senanque.rules.decisiontable.XMLDecisionTable;
import nz.co.senanque.rules.factories.ConstantFactory;
import nz.co.senanque.rules.factories.DecisionTableFactory;
import nz.co.senanque.validationengine.FieldMetadata;
import nz.co.senanque.validationengine.ObjectMetadata;
import nz.co.senanque.validationengine.Plugin;
import nz.co.senanque.validationengine.ProxyField;
import nz.co.senanque.validationengine.ProxyFieldImpl;
import nz.co.senanque.validationengine.ProxyObject;
import nz.co.senanque.validationengine.ValidationObject;
import nz.co.senanque.validationengine.ValidationSession;
import nz.co.senanque.validationengine.metadata.EngineMetadata;

import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

/**
 * 
 * The Madura Rules Engine is accessed using this plugin
 * 
 * @author Roger Parkinson
 * @version $Revision: 1.9 $
 */
@Service("maduraRules")
@MessageResource("RulesMessages")
public class RulesPlugin implements Plugin, MessageSourceAware, BeanFactoryAware, Serializable
{
	private static final long serialVersionUID = 1L;

	private final Logger logger = LoggerFactory.getLogger(RulesPlugin.class);

    private final Map<ValidationSession,RuleSessionImpl> m_ruleSessions = new Hashtable<ValidationSession,RuleSessionImpl>();
    private List<Rule> m_allRules = new ArrayList<Rule>();
    private List<Rule> m_allRulesWithNoListener = new ArrayList<Rule>();
    private final Map<String,ClassReference> m_classReferenceMap = new HashMap<String,ClassReference>();
    private MessageSource m_messageSource;
    private final Map<String,String> m_constants = new HashMap<String,String>();
    private DefaultListableBeanFactory m_beanFactory;

    // These are all injectable
    @Autowired(required=false)
    private Operations m_operations;
    @Value("${nz.co.senanque.rules.RulesPlugin.decisionTableDocument:}")
    private transient Resource m_decisionTableDocument;
    @Value("${nz.co.senanque.rules.RulesPlugin.constantsDocument:}")
    private transient Resource m_constantsDocument;
    @Value("${nz.co.senanque.rules.RulesPlugin.today:}")
    private transient String m_today;

    private Map<String,DecisionTableFactory> m_decisionTableFactories = new HashMap<String,DecisionTableFactory>();
    private Map<String,ConstantFactory> m_constantFactories = new HashMap<String,ConstantFactory>();

    
    private RuleSessionImpl getRuleSession(final ValidationSession session)
    {
        RuleSessionImpl ruleSession = m_ruleSessions.get(session);
        if (ruleSession == null)
        {
            ruleSession = new RuleSessionImpl(this,session);
            m_ruleSessions.put(session, ruleSession);
        }
        return ruleSession;
    }
    
	public void unbind(ValidationSession session, ProxyField owner, ValidationObject arg2) {
    	logger.debug("unbind(ValidationSession session, ProxyField owner, ValidationObject arg2) {} {}",
    			(owner==null)?"null":owner.getPath(), (arg2==null)?"null":arg2.getClass().getName());
        RuleSessionImpl ruleSession = m_ruleSessions.get(session);
        if (ruleSession == null || !ruleSession.isOpen()) {
        	return;
        }
        final List<ProposedValue> newValues = new ArrayList<ProposedValue>();
        if (owner != null)
        {
            RuleProxyField ruleProxyField = ruleSession.getRuleProxyField(owner);
            newValues.add(new ProposedValue(ruleProxyField,new DummyValue()));
        }
        ruleSession.unbind(arg2);
        ruleSession.setValues(newValues);
	}
    public void bind(final ValidationSession session,
            final Map<ValidationObject, ProxyObject> bound, final ProxyField pProxyField, ValidationObject owner)
    {
        logger.debug("Binding...");
        final RuleSessionImpl ruleSession = getRuleSession(session);
        final List<ProposedValue> newValues = new ArrayList<ProposedValue>();

        for (Map.Entry<ValidationObject, ProxyObject> entry: bound.entrySet())
        {
            final ValidationObject validationObject = entry.getKey();
            final ProxyObject proxyObject = entry.getValue();
            if (ruleSession.bind(validationObject))
            {
                // If the object is not already there then add the fields
                for (Map.Entry<String, ProxyField> fieldEntry : proxyObject.getFieldMap().entrySet())
                {
                    final ProxyField proxyField = fieldEntry.getValue();
                    ClassReference classReference = m_classReferenceMap.get(validationObject.getClass().getSimpleName());
                    if (classReference == null)
                    {
                        continue;
                    }
                    FieldReference fieldReference = classReference.getFieldReference(proxyField.getFieldName());
                    if (fieldReference == null)
                    {
                        continue;
                    }
                    ruleSession.bind(validationObject,proxyField,fieldReference,owner);
                }
                for (Rule rule: m_allRulesWithNoListener)
                {
                    if (rule.listeners() == null && rule.getScope().isAssignableFrom(validationObject.getClass()))
                    {
                        RuleContext ruleContext;
						try {
							ruleContext = ruleSession.getRuleContext(rule,validationObject,(owner==null)?validationObject:owner);
						} catch (FailsToMatchException e) {
							continue;
						}
                        ruleContext.fire();
                    }
                }
                for (Map.Entry<String, ProxyField> fieldEntry : proxyObject.getFieldMap().entrySet())
                {
                    final ProxyField proxyField = fieldEntry.getValue();
                    if (proxyField != null && !proxyField.isUnknown())
                    {
                        Object value = proxyField.getValue();
                        RuleProxyField ruleProxyField = ruleSession.getRuleProxyField(proxyField);
                        newValues.add(new ProposedValue(ruleProxyField,value));
                    }
                }
            }
        }
        if (pProxyField != null && !pProxyField.isUnknown())
        {
            RuleProxyField ruleProxyField = ruleSession.getRuleProxyField(pProxyField);
            newValues.add(new ProposedValue(ruleProxyField,new DummyValue()));
        }
        ruleSession.setValues(newValues);
        ruleSession.clearEvaluating();
        logger.debug("Finished Bind");
    }
    /* (non-Javadoc)
     * @see nz.co.senanque.validationengine.Plugin#clean(nz.co.senanque.validationengine.ProxyField)
     */
    public void clean(final ValidationSession session,final ProxyObject proxyObject)
    {
        // Nothing to do here, we never have dirty data
    }

    /* (non-Javadoc)
     * @see nz.co.senanque.validationengine.Plugin#set(nz.co.senanque.validationengine.ValidationSession, nz.co.senanque.validationengine.ProxyField, java.lang.Object)
     */
    public void set(ValidationSession session, ProxyField proxyField,
            Object value)
    {
        RuleSessionImpl ruleSession = getRuleSession(session);
        RuleProxyField ruleProxyField = ruleSession.getRuleProxyField(proxyField);
        if (ruleProxyField != null)
        {
            ruleSession.setValue(ruleProxyField, value);
        }
    }

    public void close(ValidationSession validationSession)
    {
        RuleSessionImpl ruleSession = m_ruleSessions.get(validationSession);
        if ((ruleSession != null)) {
	        ruleSession.setOpen(false);
	        m_ruleSessions.remove(validationSession);
        }
    }

    public void init(EngineMetadata engineMetadata)
    {
        gatherRules();
        Document choices = engineMetadata.getChoicesDocument();
        Document decisionTableDocument = choices;
        Document constantsDocument = choices;
		SAXBuilder saxBuilder = new SAXBuilder();
		try {
			if (m_decisionTableDocument != null && !StringUtils.isEmpty(m_decisionTableDocument.getFilename())) {
				decisionTableDocument = saxBuilder.build(m_decisionTableDocument.getInputStream());
			}
			if (m_constantsDocument != null && !StringUtils.isEmpty(m_constantsDocument.getFilename())) {
				constantsDocument = saxBuilder.build(m_constantsDocument.getInputStream());
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
        buildConstants(constantsDocument);
        m_classReferenceMap.putAll(createClassReferences(engineMetadata.getAllClasses()));
        buildDecisionTables(decisionTableDocument);
        for (Rule rule: m_allRules)
        {
            logger.debug("Rule: {}",rule.getRuleName());
            if (rule.listeners() == null)
            {
                m_allRulesWithNoListener.add(rule);
            }
            else
            {
                for (FieldReference ref: rule.listeners())
                {
                    // locate the field and attach the rule
                    for (ClassReference classReference: getClassReferences(ref.getClassName()))
                    {
                        FieldReference fieldReference = classReference.getFieldReference(ref.getFieldName());
                        fieldReference.addListener(rule);
                    }
                }
            }
            if (rule.outputs() != null)
            {
                for (FieldReference ref: rule.outputs())
                {
                    // locate the field and attach the rule
                    for (ClassReference classReference: getClassReferences(ref.getClassName()))
                    {
                        FieldReference fieldReference = classReference.getFieldReference(ref.getFieldName());
                        fieldReference.addOutput(rule);
                    }
                }            	
            }
        }
    }
    private void gatherRules()
    {
        for (Rule rule:m_beanFactory.getBeansOfType(Rule.class).values())
        {
        	m_allRules.add(rule);
        }
        Collections.sort(m_allRules, new Comparator<Rule>(){

			public int compare(Rule o1, Rule o2) {
				return o1.getRuleName().compareTo(o2.getRuleName());
			}});
    }
    
    private Map<String,ClassReference> createClassReferences(List<Class<?>> allClasses)
    {
        Map<String,ClassReference> ret = new HashMap<String,ClassReference>();
        for (Class<?> clazz: allClasses)
        {
            String className = clazz.getSimpleName();
            ret.put(className, new ClassReference(clazz));
        }
        for (Map.Entry<String, ClassReference> entry: ret.entrySet())
        {
            entry.getValue().figureChildren(ret);
        }
        return ret;
    }

    /**
     * Figure out which classes extend the named class and return the current class and the extenders
     * @param className
     * @param engineMetadata 
     * @return list of class references
     */
    private List<ClassReference> getClassReferences(String className)
    {
        List<ClassReference> ret = new ArrayList<ClassReference>();
        ClassReference cr = m_classReferenceMap.get(className);
        ret.addAll(cr.getChildren());
        return ret;
    }
    private void buildDecisionTables(Document decisionTableDocument)
    {
        if (decisionTableDocument == null)
        {
            return;
        }
        List<Element> decisionTableElements=null;
		try {
			decisionTableElements = decisionTableDocument.getRootElement().getChildren("DecisionTable");
		} catch (Exception e) {
			logger.warn("No decision tables found");
		}
        for (Element decisionTableElement: decisionTableElements)
        {
            Rule decisionTable = new XMLDecisionTable(decisionTableElement,getDecisionTableFactories(),this);
            getAllRules().add(decisionTable);
        }
    }
    private void buildConstants(Document constantsDocument)
    {
        if (constantsDocument == null)
        {
            return;
        }
        List<Element> constantElements=null;
		try {
			constantElements = constantsDocument.getRootElement().getChild("Constants").getChildren("Constant");
		} catch (NullPointerException e) {
			logger.warn("No constants found");
			return;
		}
        for (Element constantElement: constantElements)
        {
            final String constantName = constantElement.getAttributeValue("name");
            ConstantFactory constantFactory = m_constantFactories.get(constantName);
            if (constantFactory != null)
            {
                getConstants().put(constantName, constantFactory.getValue(constantName));
            }
            else
            {
                getConstants().put(constantElement.getAttributeValue("name"),constantElement.getTextTrim());
            }
        }
    }

    public List<Rule> getAllRules()
    {
        return m_allRules;
    }

    public void setAllRules(List<Rule> allRules)
    {
        m_allRules = allRules;
    }

    public void setMessageSource(MessageSource arg0)
    {
        m_messageSource = arg0;
    }

    public Map<String, String> getConstants()
    {
        return m_constants;
    }
    public Operations getOperations()
    {
        return m_operations;
    }

    public Map<ValidationSession, RuleSessionImpl> getRuleSessions()
    {
        return m_ruleSessions;
    }

    public void setOperations(Operations operations)
    {
        m_operations = operations;
    }

    public Map<String, ClassReference> getClassReferenceMap()
    {
        return m_classReferenceMap;
    }

    public Map<String, ConstantFactory> getConstantFactories()
    {
        return m_constantFactories;
    }

    public void setBeanFactory(BeanFactory beanFactory) throws BeansException
    {
        m_beanFactory = (DefaultListableBeanFactory)beanFactory;        
    }

    public Map<String, DecisionTableFactory> getDecisionTableFactories()
    {
        return m_decisionTableFactories;
    }

	public String getStats(ValidationSession session) {
        RuleSession ruleSession = getRuleSession(session);
        return ruleSession.getStats(ruleSession);
	}

    @PostConstruct
    public void init() throws Exception     {
		m_operations = new OperationsImpl((StringUtils.isEmpty(m_today)||m_today.startsWith("$"))?null:java.sql.Date.valueOf(m_today),m_messageSource);
    	Map<String, DecisionTableFactory> decisionTableMap = m_beanFactory.getBeansOfType(DecisionTableFactory.class);
    	m_decisionTableFactories.putAll(decisionTableMap);
    	Map<String, ConstantFactory> constantMap = m_beanFactory.getBeansOfType(ConstantFactory.class);
    	m_constantFactories.putAll(constantMap);
	}

	/**
	 * Find an empty field, ie one that is unknown and not 'not known'
	 * If we don't find one then return null.
	 * @param fieldMetadata
	 * @return qualifying field
	 */
	public FieldMetadata getEmptyField(FieldMetadata fieldMetadata) {
		ValidationSession session = fieldMetadata.getValidationSession();
        RuleSessionImpl ruleSession = getRuleSession(session);
        ProxyField proxyField = session.getProxyField(fieldMetadata);
        RuleProxyField ruleProxyField = ruleSession.getRuleProxyField(proxyField);
        while (true)
        {
        	RuleProxyField rpf = ruleProxyField.backChain();
        	if (rpf == null)
        	{
        		// there are no unfilled fields
        		return null;
        	}
        	else
        	{
        		// We got an unfilled field return it
        		return rpf.getProxyField().getFieldMetadata();
        	}
        }
	}
	/**
	 * Clear all the fields that were originally flagged as unknown on this object
	 * we should assume the dynamic flag has been removed and we need to reset it.
	 * Also set the current value of each to null, ie we lose any data from the unknown fields for this object.
	 * Because you only do this one object at a time there may be problems if the unknowns use
	 * rules that cross multiple objects.
	 * @param object
	 */
	public void clearUnknowns(ValidationObject object)
	{
		ObjectMetadata objectMetadata = object.getMetadata();
		ValidationSession session = object.getMetadata().getProxyObject().getSession();
		session.setEnabled(false);
		Map<String,ProxyField> fieldMap = objectMetadata.getProxyObject().getFieldMap();
		for (Map.Entry<String,ProxyField> entry: fieldMap.entrySet())
		{
			FieldMetadata fieldMetadata = entry.getValue().getFieldMetadata();
			ProxyFieldImpl proxyField = (ProxyFieldImpl)session.getProxyField(fieldMetadata);
			if (proxyField.getPropertyMetadata().isUnknown()) // this tests the static unknown flag
			{
				// force the value to null and then set the dynamic unknown flag
				proxyField.reset();
				proxyField.setValue(null);
				proxyField.updateValue();
				proxyField.setUnknown(true);
				logger.debug("Cleared {}",proxyField.toString());
			}
		}
		session.setEnabled(true);
	}

	public void setNotKnown(FieldMetadata fieldMetadata) {
		ValidationSession session = fieldMetadata.getValidationSession();
		ProxyFieldImpl proxyField = (ProxyFieldImpl)session.getProxyField(fieldMetadata);;
		set(session, proxyField, FieldMetadata.NOT_KNOWN);
		proxyField.setNotKnown(true);
	}

	public Resource getDecisionTableDocument() {
		return m_decisionTableDocument;
	}

	public void setDecisionTableDocument(Resource decisionTableResource) {
		m_decisionTableDocument = decisionTableResource;
	}

	public Resource getConstantsDocument() {
		return m_constantsDocument;
	}

	public void setConstantsDocument(Resource constantsResource) {
		m_constantsDocument = constantsResource;
	}

	public void setDecisionTableFactories(
			Map<String, DecisionTableFactory> decisionTableFactories) {
		m_decisionTableFactories = decisionTableFactories;
	}

	public void setConstantFactories(Map<String, ConstantFactory> constantFactories) {
		m_constantFactories = constantFactories;
	}

	public String getToday() {
		return m_today;
	}

	public void setToday(String today) {
		m_today = today;
	}
}
