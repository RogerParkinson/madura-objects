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
package nz.co.senanque.directed;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nz.co.senanque.base.Customer;
import nz.co.senanque.base.SpringConfiguration;
import nz.co.senanque.rules.RulesPlugin;
import nz.co.senanque.validationengine.FieldMetadata;
import nz.co.senanque.validationengine.ObjectMetadata;
import nz.co.senanque.validationengine.ProxyField;
import nz.co.senanque.validationengine.ValidationEngine;
import nz.co.senanque.validationengine.ValidationObject;
import nz.co.senanque.validationengine.ValidationSession;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration( classes = {SpringConfiguration.class,DirectedConfiguration.class})
public class DirectedRulesTest
{
    private static final Logger log = LoggerFactory.getLogger(DirectedRulesTest.class);
    @Autowired private transient ValidationEngine m_validationEngine;
    @Autowired private transient RulesPlugin m_rulesPlugin;

    /**
     * Runs a scenario which tries directed questioning, then resets the values
     * and runs it again, verifying that the same questions are asked.
     * @throws Exception
     */
    @Test
    public void testDirectedQuestion1() throws Exception
    {        
        ValidationSession validationSession = m_validationEngine.createSession();
        Customer customer = new Customer();
		validationSession.bind(customer);
		List<FieldMetadata> log1 = tryScenario(customer);
        m_rulesPlugin.clearUnknowns(customer);
        for (FieldMetadata fieldMetadata: log1)
        {
        	dumpObject(customer);
        	FieldMetadata fm = m_rulesPlugin.getEmptyField(customer.getMetadata().getFieldMetadata("bmi"));
        	String currentName = (fm==null)?"null":fm.getName();
        	log.debug("{} {}",fieldMetadata.getName(),currentName);
        	// this proves we ask the same questions the second time around.
         	assertEquals(fieldMetadata.getName(),currentName);
        	if (fieldMetadata.getName().equals("heightInches"))
        	{
        		fieldMetadata.setValue(new Double(4));
        		dumpObject(customer);
        		continue;
        	}
        	if (fieldMetadata.getName().equals("heightMetric"))
        	{
        		m_rulesPlugin.setNotKnown(fieldMetadata);
        		continue;
        	}
        	if (fieldMetadata.getName().equals("heightFeet"))
        	{
        		fieldMetadata.setValue(new Double(5));
        		continue;
        	}
        	if (fieldMetadata.getName().equals("weightKilos"))
        	{
        		dumpObject(customer);
        		log.debug("Just before set value");
        		fieldMetadata.setValue(new Double(90D));
        		log.debug("Just after set value");
        		dumpObject(customer);
        		log.debug("complete: field=weightKilos");
        		continue;
        	}

        }
        long bmi = new Double(customer.getBmi()).longValue();
        assertEquals(34L,bmi);
        validationSession.close();
    }
    @Test
    public void testDirectedQuestion2() throws Exception
    {        
        ValidationSession validationSession = m_validationEngine.createSession();
        Customer customer = new Customer();
		validationSession.bind(customer);
        m_rulesPlugin.clearUnknowns(customer);
        FieldMetadata fieldMetadata = null;
        while ((fieldMetadata = m_rulesPlugin.getEmptyField(customer.getMetadata().getFieldMetadata("bmi"))) != null)
        {
        	log.debug("found field {}",fieldMetadata.getName());

        	if (fieldMetadata.getName().equals("weightKilos"))
        	{
        		fieldMetadata.setValue(new Double(90D));
        		continue;
        	}
        	if (fieldMetadata.getName().equals("heightMetric"))
        	{
        		fieldMetadata.setValue(new Double(1.92D));
        		continue;
        	}
        	if (fieldMetadata.getName().equals("weightPounds"))
        	{
        		m_rulesPlugin.setNotKnown(fieldMetadata);
        		continue;
        	}        	
        	if (fieldMetadata.getName().equals("heightFeet"))
        	{
        		m_rulesPlugin.setNotKnown(fieldMetadata);
        		continue;
        	}
        	if (fieldMetadata.getName().equals("heightInches"))
        	{
        		m_rulesPlugin.setNotKnown(fieldMetadata);
        		continue;
        	}
        }
        long bmi = new Double(customer.getBmi()).longValue();
        assertEquals(24L,bmi);
        validationSession.close();
    }
	private void dumpObject(ValidationObject object)
	{
		ObjectMetadata objectMetadata = object.getMetadata();
		ValidationSession session = object.getMetadata().getProxyObject().getSession();
		Map<String,ProxyField> fieldMap = objectMetadata.getProxyObject().getFieldMap();
		log.debug("Dumping object {}",object.getClass().getName());
		for (Map.Entry<String,ProxyField> entry: fieldMap.entrySet())
		{
			FieldMetadata fieldMetadata = entry.getValue().getFieldMetadata();
			ProxyField proxyField = session.getProxyField(fieldMetadata);
			if (proxyField.getPropertyMetadata().isUnknown())
			{
				log.debug("field {} value {} {} unknown:{} derived:{} notknown:{}", new Object[]{proxyField.getFieldName(), proxyField.fetchValue(), proxyField.getValue(), proxyField.isUnknown(), proxyField.isNotKnown(), proxyField.isDerived()});
			}
		}
	}
	private List<FieldMetadata> tryScenario(Customer customer)
	{
        List<FieldMetadata> metadataLog = new ArrayList<FieldMetadata>();
        FieldMetadata fieldMetadata = null;
        while ((fieldMetadata = m_rulesPlugin.getEmptyField(customer.getMetadata().getFieldMetadata("bmi"))) != null)
        {
        	log.debug("found field {}",fieldMetadata.getName());
        	metadataLog.add(fieldMetadata);

        	if (fieldMetadata.getName().equals("weightKilos"))
        	{
        		fieldMetadata.setValue(new Double(90D));
        		continue;
        	}
        	if (fieldMetadata.getName().equals("heightMetric"))
        	{
        		m_rulesPlugin.setNotKnown(fieldMetadata);
        		continue;
        	}
        	if (fieldMetadata.getName().equals("weightPounds"))
        	{
        		m_rulesPlugin.setNotKnown(fieldMetadata);
        		continue;
        	}        	
        	if (fieldMetadata.getName().equals("heightFeet"))
        	{
        		fieldMetadata.setValue(new Double(6));
        		continue;
        	}
        	if (fieldMetadata.getName().equals("heightInches"))
        	{
        		fieldMetadata.setValue(new Double(4));
        		continue;
        	}
        }
        long bmi = new Double(customer.getBmi()).longValue();
        assertEquals(24L,bmi);
        return metadataLog;
	}
}
