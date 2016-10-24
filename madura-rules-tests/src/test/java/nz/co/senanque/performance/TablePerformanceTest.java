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
package nz.co.senanque.performance;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import nz.co.senanque.base.Customer;
import nz.co.senanque.base.CustomerDAO;
import nz.co.senanque.base.IndustryType;
import nz.co.senanque.base.SpringConfiguration;
import nz.co.senanque.validationengine.FieldMetadata;
import nz.co.senanque.validationengine.ObjectMetadata;
import nz.co.senanque.validationengine.ValidationEngine;
import nz.co.senanque.validationengine.ValidationSession;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;



/**
 * check the performance of decision tables of various sizes
 * 
 * @author Roger Parkinson
 * @version $Revision: 1.1 $
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration( classes = {SpringConfiguration.class, PerformanceConfiguration.class})
public class TablePerformanceTest
{
    private static final Logger log = LoggerFactory.getLogger(TablePerformanceTest.class);
    @Autowired private transient ValidationEngine m_validationEngine;
    @Autowired private transient CustomerDAO m_customerDAO;
    private static int MAX_LOOP = 10;
    public static int MAX_LOOP2 = 1000;

    @SuppressWarnings("unused")
	@Test
    public void simpleTest() throws Exception
    {        
        ValidationSession validationSession = m_validationEngine.createSession();
        log.info("starting simpleTest (loop is {})",MAX_LOOP);
        long startTime = System.currentTimeMillis();
        
        for (int loop=0;loop < MAX_LOOP;loop++)
        {
            log.info("simpleTest:{}",loop);
            Customer customer = simpleTest(validationSession);
        }
        log.info("Table performance: {} operations/sec",(MAX_LOOP*1000)/(System.currentTimeMillis()-startTime));
        validationSession.close();
    }
    
    private Customer simpleTest(ValidationSession validationSession)
    {
        Customer customer = m_customerDAO.createCustomer();
        validationSession.bind(customer);
        final ObjectMetadata customerMetadata = validationSession.getMetadata(customer);
        final FieldMetadata customerTypeMetadata = customerMetadata.getFieldMetadata(Customer.CUSTOMERTYPE);
        final FieldMetadata businessMetadata = customerMetadata.getFieldMetadata(Customer.BUSINESS);
        int customerTypeSize = customerTypeMetadata.getChoiceList().size();
        int businessSize = businessMetadata.getChoiceList().size();
        
        assertTrue(businessSize==3);
        
        assertFalse(customerTypeMetadata.isActive());
        assertFalse(customerTypeMetadata.isReadOnly());
        assertFalse(customerTypeMetadata.isRequired());
        
        customer.setBusiness(IndustryType.AG);
        int newCustomerTypeSize = customerTypeMetadata.getChoiceList().size();
        
        assertFalse(newCustomerTypeSize==customerTypeSize);
        return customer;
    }

}
