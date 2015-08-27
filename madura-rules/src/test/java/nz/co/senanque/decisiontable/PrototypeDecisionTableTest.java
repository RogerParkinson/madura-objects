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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nz.co.senanque.rules.Rule;
import nz.co.senanque.rules.RuleSession;
import nz.co.senanque.decisiontable.Pizza;
import nz.co.senanque.rules.factories.DecisionTableFactory;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 
 * Test to drive the prototype decision table with everything mocked
 * 
 * @author Roger Parkinson
 * @version $Revision:$
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class PrototypeDecisionTableTest
{
    private static Logger logger = LoggerFactory.getLogger(PrototypeDecisionTableTest.class);

    @Autowired private Resource m_decisionTableResource;
    @Autowired private MessageSource m_messageSource;
    private Map<String,DecisionTableFactory> m_decisionTableFactoryMap = new HashMap<String,DecisionTableFactory>();


    /**
     * Test method for {@link nz.co.senanque.decisiontable.PrototypeDecisionTable#evaluate(nz.co.senanque.rules.RuleSession, nz.co.senanque.validationengine.ValidationObject)}.
     */
    @Test
    public void testEvaluate() throws Exception
    {
		SAXBuilder saxBuilder = new SAXBuilder();
		Document decisionTableDocument = saxBuilder.build(m_decisionTableResource.getInputStream());
    	
    	Element root = decisionTableDocument.getRootElement();
        List<Element> decisionTableElements = root.getChildren("DecisionTable");
        Element decisionTableElement = decisionTableElements.get(0);
        Rule decisionTable = new PrototypeDecisionTable(decisionTableElement,getDecisionTableFactoryMap());
        RuleSession ruleSession = new RuleSessionMock(root, new MessageSourceAccessor(m_messageSource));
        Pizza pizza = new Pizza(); 

        logger.debug("setting size {} topping {}",pizza.getSize(),pizza.getTopping());
        decisionTable.evaluate(ruleSession, pizza, null);
        pizza.setSize("Medium");
        logger.debug("setting size {} topping {}",pizza.getSize(),pizza.getTopping());
        decisionTable.evaluate(ruleSession, pizza, null);
        pizza.setTopping("Spanish");
        logger.debug("setting size {} topping {}",pizza.getSize(),pizza.getTopping());
        decisionTable.evaluate(ruleSession, pizza, null);
        pizza.setTopping(null);
        logger.debug("setting size {} topping {}",pizza.getSize(),pizza.getTopping());
        decisionTable.evaluate(ruleSession, pizza, null);
        pizza.setSize(null);
        logger.debug("setting size {} topping {}",pizza.getSize(),pizza.getTopping());
        decisionTable.evaluate(ruleSession, pizza, null);
        
    }
    public Map<String, DecisionTableFactory> getDecisionTableFactoryMap()
    {
        return m_decisionTableFactoryMap;
    }
    public MessageSource getMessageSource()
    {
        return m_messageSource;
    }
    public void setMessageSource(MessageSource messageSource)
    {
        m_messageSource = messageSource;
    }


}
