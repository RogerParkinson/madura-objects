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
package nz.co.senanque.tableconstraint;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import nz.co.senanque.base.BusinessCustomer;
import nz.co.senanque.base.Customer;
import nz.co.senanque.base.CustomerDAO;
import nz.co.senanque.base.IndustryType;
import nz.co.senanque.base.SpringConfiguration;
import nz.co.senanque.validationengine.ObjectMetadata;
import nz.co.senanque.validationengine.ValidationEngine;
import nz.co.senanque.validationengine.ValidationException;
import nz.co.senanque.validationengine.ValidationSession;
import nz.co.senanque.validationengine.choicelists.ChoiceBase;

import org.apache.commons.logging.Log;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;



/**
 * Verifies the behaviour of a simple decision table. There are no rul rules in this.
 * 
 * @author Roger Parkinson
 * @version $Revision: 1.1 $
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration( classes = {SpringConfiguration.class, TableConstraintConfiguration.class})
public class TableConstraintTest
{
    @SuppressWarnings("unused")
	private static final Log log = org.apache.commons.logging.LogFactory.getLog(TableConstraintTest.class);
    @Autowired private transient ValidationEngine m_validationEngine;
    @Autowired private transient CustomerDAO m_customerDAO;
    @Autowired private transient Marshaller m_marshaller;
    @Autowired private transient Unmarshaller m_unmarshaller;
    
    @Test
    public void testTableConstraint() throws Exception
    {        
        ValidationSession validationSession = m_validationEngine.createSession();

        // create a customer
        Customer customer = m_customerDAO.createCustomer();
        customer.setBusiness(IndustryType.FINANCE);
        validationSession.bind(customer);
        List<ChoiceBase> choices = customer.getMetadata().getFieldMetadata(Customer.CUSTOMERTYPE).getChoiceList();
        assertEquals(4,choices.size());
        boolean exception = false;
        try
        {
            customer.setCustomerType("A");
        }
        catch (ValidationException e)
        {
            //e.printStackTrace();
            exception = true;
        }
        assertTrue(exception);
        validationSession.close();
    }
    @Test
    public void testTableConstraint2() throws Exception
    {        
        ValidationSession validationSession = m_validationEngine.createSession();

        // create a customer
        Customer customer = m_customerDAO.createCustomer();
        customer.setCustomerType("f");
        validationSession.bind(customer);
        List<ChoiceBase> choices = customer.getMetadata().getFieldMetadata(Customer.BUSINESS).getChoiceList();
        assertEquals(2,choices.size());
        validationSession.close();
    }
    @Test
    public void testTableConstraint3() throws Exception
    {        
        ValidationSession validationSession = m_validationEngine.createSession();

        // create a customer
        Customer customer = m_customerDAO.createCustomer();
        validationSession.bind(customer);
        customer.setCustomerType("f");
        List<ChoiceBase> choices = customer.getMetadata().getFieldMetadata(Customer.BUSINESS).getChoiceList();
        assertEquals(2,choices.size());
        validationSession.close();
    }
    @Test
    public void inheritedRules() throws Exception
    {        
        ValidationSession validationSession = m_validationEngine.createSession();

        // create a customer
        BusinessCustomer customer = new BusinessCustomer();
        customer.setCustomerType("f");
        validationSession.bind(customer);
        List<ChoiceBase> choices = customer.getMetadata().getFieldMetadata(Customer.BUSINESS).getChoiceList();
        assertEquals(2,choices.size());
        validationSession.close();
    }
    @Test
    @Ignore
    public void testTableConstraintReset() throws Exception
    {        
        ValidationSession validationSession = m_validationEngine.createSession();

        // create a customer
        Customer customer = m_customerDAO.createCustomer();
        validationSession.bind(customer);
        ObjectMetadata metadata = customer.getMetadata();
        countChoices(metadata,Customer.BUSINESS,3);
        countChoices(metadata,Customer.CUSTOMERTYPE,6);
        
        customer.setCustomerType("f");
        countChoices(metadata,Customer.BUSINESS,2);
        countChoices(metadata,Customer.CUSTOMERTYPE,6);
        
        customer.setBusiness(IndustryType.FINANCE);
        countChoices(metadata,Customer.BUSINESS,2);
        countChoices(metadata,Customer.CUSTOMERTYPE,1);
        // two fields are now set
        
        customer.setBusiness(null);
        countChoices(metadata,Customer.BUSINESS,2);
        countChoices(metadata,Customer.CUSTOMERTYPE,1);

        customer.setCustomerType(null);
        countChoices(metadata,Customer.BUSINESS,3);
        countChoices(metadata,Customer.CUSTOMERTYPE,6);
        validationSession.close();
    }
    private void countChoices(ObjectMetadata metadata, String type, int expected)
    {
        List<ChoiceBase> choices = metadata.getFieldMetadata(type).getChoiceList();
        assertEquals(expected,choices.size());
    }
    @Test
    public void testTableConstraintReset2() throws Exception
    {        
        ValidationSession validationSession = m_validationEngine.createSession();

        // create a customer
        Customer customer = m_customerDAO.createCustomer();
        validationSession.bind(customer);
        List<ChoiceBase> choices = customer.getMetadata().getFieldMetadata(Customer.BUSINESS).getChoiceList();
        assertEquals(3,choices.size());
        customer.setCustomerType("f");
        customer.setBusiness(IndustryType.FINANCE);
        // two fields are now set
        
        customer.setCustomerType(null);
        choices = customer.getMetadata().getFieldMetadata(Customer.BUSINESS).getChoiceList();
        assertEquals(3,choices.size());
        validationSession.close();
    }
}
