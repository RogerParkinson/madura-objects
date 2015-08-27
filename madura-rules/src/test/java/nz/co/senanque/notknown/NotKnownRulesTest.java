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
package nz.co.senanque.notknown;

import static org.junit.Assert.assertEquals;
import nz.co.senanque.rules.RulesPlugin;
import nz.co.senanque.notknown.Customer;
import nz.co.senanque.validationengine.FieldMetadata;
import nz.co.senanque.validationengine.ValidationEngine;
import nz.co.senanque.validationengine.ValidationSession;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"spring.xml"})
public class NotKnownRulesTest
{
    private static final Logger log = LoggerFactory.getLogger(NotKnownRulesTest.class);
    @Autowired private transient ValidationEngine m_validationEngine;
    @Autowired private transient RulesPlugin m_rulesPlugin;

    /**
     * Runs a scenario which does directed questioning. The rules include one that uses
     * the isNotKnown function and verifies it works.
     * @throws Exception
     */
    @Test
    public void testNotKnownRulesTest() throws Exception
    {        
        ValidationSession validationSession = m_validationEngine.createSession();
        Customer customer = new Customer();
		validationSession.bind(customer);
        m_rulesPlugin.clearUnknowns(customer);
        FieldMetadata fieldMetadata = null;
        log.debug("starting loop");
        while ((fieldMetadata = m_rulesPlugin.getEmptyField(customer.getMetadata().getFieldMetadata("bmi"))) != null)
        {
        	log.debug("found field {}",fieldMetadata.getName());

        	if (fieldMetadata.getName().equals("weightKilos"))
        	{
        		m_rulesPlugin.setNotKnown(fieldMetadata);
        		continue;
        	}
        	if (fieldMetadata.getName().equals("heightMetric"))
        	{
        		fieldMetadata.setValue(new Double(1.92D));
        		continue;
        	}
        	if (fieldMetadata.getName().equals("weightPounds"))
        	{
        		fieldMetadata.setValue(new Double(198D));
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
        assertEquals("not known rule fired",customer.getAddress());
        validationSession.close();
    }
}
