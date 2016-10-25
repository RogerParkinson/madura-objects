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

import java.util.List;

import nz.co.senanque.base.Customer;
import nz.co.senanque.base.CustomerDAO;
import nz.co.senanque.base.IndustryType;
import nz.co.senanque.base.Invoice;
import nz.co.senanque.base.SpringConfiguration;
import nz.co.senanque.validationengine.FieldMetadata;
import nz.co.senanque.validationengine.ObjectMetadata;
import nz.co.senanque.validationengine.ValidationEngine;
import nz.co.senanque.validationengine.ValidationSession;
import nz.co.senanque.validationengine.choicelists.ChoiceBase;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;



/**
 * Tests to exercise various engine operations
 * 
 * @author Roger Parkinson
 * @version $Revision: 1.2 $
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration( classes = {SpringConfiguration.class, PerformanceConfiguration.class})
public class PerformanceTest
{
    private static final Logger log = LoggerFactory.getLogger(PerformanceTest.class);
    @Autowired private transient ValidationEngine m_validationEngine;
    @Autowired private transient CustomerDAO m_customerDAO;
    private static int MAX_LOOP = 10;
    private static int MAX_LOOP2 = 10;
    private static int MAX_LOOP3 = 10;

    @Test
    public void simpleTest() throws Exception
    {        
        ValidationSession validationSession = m_validationEngine.createSession();
        log.info("starting simpleTest (loop is {})",MAX_LOOP);
        long startTime = System.currentTimeMillis();
        
        for (int loop=0;loop < MAX_LOOP;loop++)
        {
            simpleTest(validationSession);
        }
        log.info("simpleTest: {} operations/sec",(MAX_LOOP*1000)/(System.currentTimeMillis()-startTime));
        validationSession.close();
    }
    
    @SuppressWarnings("unused")
	@Test @Ignore
    public void listTest() throws Exception
    {        
        ValidationSession validationSession = m_validationEngine.createSession();
        log.info("starting listTest (loop is {})",MAX_LOOP*MAX_LOOP2);
        long startTime = System.currentTimeMillis();
        
        for (int loop=0;loop < MAX_LOOP;loop++)
        {
            Customer customer = simpleTest(validationSession);
            for (int loop2=0;loop2 < MAX_LOOP2;loop2++)
            {
                listTest(validationSession,customer,loop2);
            }
        }
//        log.info("listTest: {} operations/sec",(System.currentTimeMillis()-startTime)/(MAX_LOOP*MAX_LOOP2*1000));
        validationSession.close();
    }
    
    @Test
    public void bigTest() throws Exception
    {        
        log.info("starting bigTest (loop is {})",MAX_LOOP*MAX_LOOP2*MAX_LOOP3);
        long startTime = System.currentTimeMillis();
        
        for (int loop=0;loop < MAX_LOOP3;loop++)
        {
            listTest();
        }
        log.info("bigTest: {} operations/sec",(MAX_LOOP3*1000)/(System.currentTimeMillis()-startTime));
    }
    
    private void listTest(ValidationSession validationSession,Customer customer,int j)
    {
        Invoice invoice = new Invoice();
        invoice.setDescription("test invoice "+j);
        customer.getInvoices().add(invoice);
        invoice.setAmount(100);
    }
    
    @SuppressWarnings("unused")
	private Customer simpleTest(ValidationSession validationSession)
    {
        Customer customer = m_customerDAO.createCustomer();
        validationSession.bind(customer);
        Invoice invoice = new Invoice();
        invoice.setDescription("test invoice");
        customer.getInvoices().add(invoice);
        boolean exceptionFound = false;
        final ObjectMetadata customerMetadata = validationSession.getMetadata(customer);
        final FieldMetadata customerTypeMetadata = customerMetadata.getFieldMetadata(Customer.CUSTOMERTYPE);
        
        assertFalse(customerTypeMetadata.isActive());
        assertFalse(customerTypeMetadata.isReadOnly());
        assertFalse(customerTypeMetadata.isRequired());
        
        customer.setName("aaaab");
        assertTrue(customerTypeMetadata.isActive());
        assertTrue(customerTypeMetadata.isReadOnly());
        assertTrue(customerTypeMetadata.isRequired());
        exceptionFound = false;
        try
        {
            customer.setCustomerType("B");
        }
        catch (Exception e)
        {
            exceptionFound = true;
        }
        assertTrue(exceptionFound);
        exceptionFound = false;
        try
        {
            customer.setCustomerType("XXX");
        }
        catch (Exception e)
        {
            exceptionFound = true;
        }
        assertTrue(exceptionFound);
        customer.setBusiness(IndustryType.AG);
        customer.setAmount(new Double(500.99));
        invoice = new Invoice();
        invoice.setDescription("test invoice2");
        customer.getInvoices().add(invoice);
        final Invoice inv = customer.getInvoices().get(0);
        customer.getInvoices().remove(inv);
        
        //ObjectMetadata metadata = validationSession.getMetadata(customer);
        ObjectMetadata metadata = customer.getMetadata();
        org.junit.Assert.assertEquals("this is a description",metadata.getFieldMetadata(Customer.NAME).getDescription());
        List<ChoiceBase> choices = metadata.getFieldMetadata(Customer.BUSINESS).getChoiceList();
//        assertEquals(1,choices.size());
        List<ChoiceBase> choices2 = metadata.getFieldMetadata(Customer.CUSTOMERTYPE).getChoiceList();
//        assertEquals(1,choices2.size());
        choices2 = metadata.getFieldMetadata(Customer.CUSTOMERTYPE).getChoiceList();
//        assertEquals(1,choices2.size());
        
        customer.setName("aab");
        choices2 = metadata.getFieldMetadata(Customer.CUSTOMERTYPE).getChoiceList();
//        assertEquals(6,choices2.size());
        return customer;
    }

//    @Test
//    public void test2() throws Exception
//    {        
//        ValidationSession validationSession = m_validationEngine.createSession();
//
//        // create a customer
//        Customer customer = m_customerDAO.createCustomer();
//        customer.setName("aaaab");
//        validationSession.bind(customer);
//        assertEquals("A", customer.getCustomerType());
//        assertTrue(customer.getBusiness().compareTo(IndustryType.AG)==0);
//        customer.setName(null);
//        assertNull(customer.getCustomerType());
//        assertNull(customer.getBusiness());
//        customer.setName("aaaaab");
//        assertEquals("B", customer.getCustomerType());
//        assertTrue(customer.getBusiness().compareTo(IndustryType.FISH)==0);
//
//        validationSession.close();
//    }
//    @Test
//    public void testCountConstraint() throws Exception
//    {        
//        ValidationSession validationSession = m_validationEngine.createSession();
//
//        // create a customer
//        Customer customer = m_customerDAO.createCustomer();
//        customer.setName("aaaab");
//        validationSession.bind(customer);
//        int lastProxyCount = validationSession.getProxyCount();
//        assertEquals(1,validationSession.getProxyCount());
//        assertEquals("A", customer.getCustomerType());
//        assertTrue(customer.getBusiness().compareTo(IndustryType.AG)==0);
//        Invoice invoice = new Invoice();
//        invoice.setDescription("test invoice0");
//        customer.getInvoices().add(invoice);
//        assertEquals(lastProxyCount+1,validationSession.getProxyCount());
//        invoice = new Invoice();
//        invoice.setDescription("test invoice1");
//        customer.getInvoices().add(invoice);
//        invoice = new Invoice();
//        invoice.setDescription("test invoice1");
//        customer.setOneInvoice(invoice);
//        invoice = new Invoice();
//        invoice.setDescription("test invoice2");
//        lastProxyCount = validationSession.getProxyCount();
//        // Invoice count at this point is 2
//        boolean gotException=false;
//        try
//        {
//            customer.getInvoices().add(invoice);
//        }
//        catch (ConstraintViolationException e)
//        {
//            gotException=true;
//        }
//        assertTrue(gotException);
//        assertEquals(2, customer.getInvoices().size());
//        assertEquals(lastProxyCount,validationSession.getProxyCount());
//        customer.getInvoices().clear();
//        assertEquals(2,validationSession.getProxyCount());
//        validationSession.close();
//    }
//    @Test
//    public void test3() throws Exception
//    {        
//        ValidationSession validationSession = m_validationEngine.createSession();
//
//        // create a customer
//        Customer customer = m_customerDAO.createCustomer();
//        customer.setName("aaaab");
//        validationSession.bind(customer);
//        assertEquals("A", customer.getCustomerType());
//        assertTrue(customer.getBusiness().compareTo(IndustryType.AG)==0);
//        customer.getInvoices().add(new Invoice());
//        assertEquals(1, customer.getInvoiceCount());
//        validationSession.close();
//    }
//    @Test
//    public void testTableConstraint() throws Exception
//    {        
//        ValidationSession validationSession = m_validationEngine.createSession();
//
//        // create a customer
//        Customer customer = m_customerDAO.createCustomer();
//        customer.setBusiness(IndustryType.FINANCE);
//        validationSession.bind(customer);
//        List<ChoiceBase> choices = customer.getMetadata().getFieldMetadata(Customer.CUSTOMERTYPE).getChoiceList();
//        assertEquals(4,choices.size());
//        boolean exception = false;
//        try
//        {
//            customer.setCustomerType("A");
//        }
//        catch (ValidationException e)
//        {
//            //e.printStackTrace();
//            exception = true;
//        }
//        assertTrue(exception);
//        validationSession.close();
//    }
//    @Test
//    public void testTableConstraint2() throws Exception
//    {        
//        ValidationSession validationSession = m_validationEngine.createSession();
//
//        // create a customer
//        Customer customer = m_customerDAO.createCustomer();
//        customer.setCustomerType("F");
//        validationSession.bind(customer);
//        List<ChoiceBase> choices = customer.getMetadata().getFieldMetadata(Customer.BUSINESS).getChoiceList();
//        assertEquals(1,choices.size());
//        validationSession.close();
//    }
//    @Test
//    public void inheritedRules() throws Exception
//    {        
//        ValidationSession validationSession = m_validationEngine.createSession();
//
//        // create a customer
//        BusinessCustomer customer = new BusinessCustomer();
//        customer.setCustomerType("F");
//        validationSession.bind(customer);
//        List<ChoiceBase> choices = customer.getMetadata().getFieldMetadata(Customer.BUSINESS).getChoiceList();
//        assertEquals(1,choices.size());
//        validationSession.close();
//    }
//    @Ignore("Not sure why this is disabled")
//    @Test
//    public void testTransaction() throws Exception
//    {
//        Customer customer = m_customerDAO.createCustomer();
//        customer.setName("aaaaaab");
//        final long id = m_customerDAO.save(customer);
//        m_customerDAO.transactionTester();
//        
//    }


}
