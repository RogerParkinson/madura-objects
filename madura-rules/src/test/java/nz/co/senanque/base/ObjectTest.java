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
package nz.co.senanque.base;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import nz.co.senanque.rules.ConstraintViolationException;
import nz.co.senanque.validationengine.FieldMetadata;
import nz.co.senanque.validationengine.ObjectMetadata;
import nz.co.senanque.validationengine.ValidationEngine;
import nz.co.senanque.validationengine.ValidationException;
import nz.co.senanque.validationengine.ValidationSession;
import nz.co.senanque.validationengine.choicelists.ChoiceBase;

import org.apache.commons.logging.Log;
import org.jdom.input.SAXBuilder;
import org.jdom.transform.JDOMSource;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;



/**
 * Tests to verify that the XSD generated objects actually do serialise properly
 * 
 * @author Roger Parkinson
 * @version $Revision: 1.8 $
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"spring.xml"})
public class ObjectTest
{
    private static final Log log = org.apache.commons.logging.LogFactory.getLog(ObjectTest.class);
    @Autowired private transient ValidationEngine m_validationEngine;
    @Autowired private transient CustomerDAO m_customerDAO;
    @Autowired private transient Marshaller m_marshaller;
    @Autowired private transient Unmarshaller m_unmarshaller;
    

    @SuppressWarnings("unused")
	@Test
    public void test1() throws Exception
    {        
        ValidationSession validationSession = m_validationEngine.createSession();

        // create a customer
        Customer customer = m_customerDAO.createCustomer();
        validationSession.bind(customer);
        Invoice invoice = new Invoice();
        invoice.setDescription("test invoice");
        customer.getInvoices().add(invoice);
        boolean exceptionFound = false;
        try 
        {
            customer.setName("ttt");
        } 
        catch (ValidationException e) 
        {
            exceptionFound = true;
        }
        assertTrue(exceptionFound);
        
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
        final long id = m_customerDAO.save(customer);
        log.info(id);
        
        // fetch customer back
        customer = m_customerDAO.getCustomer(id);
        final int invoiceCount = customer.getInvoices().size();
        validationSession.bind(customer);
        invoice = new Invoice();
        invoice.setDescription("test invoice2");
        customer.getInvoices().add(invoice);
        m_customerDAO.save(customer);
        
        // fetch customer again
        customer = m_customerDAO.getCustomer(id);
        customer.toString();
        validationSession.bind(customer);
        final Invoice inv = customer.getInvoices().get(0);
        customer.getInvoices().remove(inv);
        
        //ObjectMetadata metadata = validationSession.getMetadata(customer);
        ObjectMetadata metadata = customer.getMetadata();
        org.junit.Assert.assertEquals("this is a description",metadata.getFieldMetadata(Customer.NAME).getDescription());
        List<ChoiceBase> choices = metadata.getFieldMetadata(Customer.BUSINESS).getChoiceList();
        assertEquals(1,choices.size());
        List<ChoiceBase> choices2 = metadata.getFieldMetadata(Customer.CUSTOMERTYPE).getChoiceList();
        assertEquals(2,choices2.size());
        choices2 = metadata.getFieldMetadata(Customer.CUSTOMERTYPE).getChoiceList();
        assertEquals(2,choices2.size());
        
        customer.setName("aab");
        choices2 = metadata.getFieldMetadata(Customer.CUSTOMERTYPE).getChoiceList();
        assertEquals(6,choices2.size());
        
        // Convert customer to XML
        QName qname = new QName("http://www.example.org/sandbox","Session");
        JAXBElement<Session> sessionJAXB =
                new JAXBElement<Session>(qname, Session.class, new Session() );
        sessionJAXB.getValue().getCustomers().add(customer); //??This fails to actually add
        StringWriter marshallWriter = new StringWriter();
        Result marshallResult = new StreamResult(marshallWriter);
        m_marshaller.marshal(sessionJAXB,marshallResult);
        marshallWriter.flush();       
        String result = marshallWriter.getBuffer().toString().trim();
        String xml = result.replaceAll("\\Qhttp://www.example.org/sandbox\\E", "http://www.example.org/sandbox");        
        log.info(xml);
        
        // Convert customer back to objects
        SAXBuilder builder = new SAXBuilder();
        org.jdom.Document resultDOM = builder.build(new StringReader(xml));
        @SuppressWarnings("unchecked")
		JAXBElement<Session> request  = (JAXBElement<Session>)m_unmarshaller.unmarshal(new JDOMSource(resultDOM));
        validationSession = m_validationEngine.createSession();
        validationSession.bind(request.getValue());
        assertEquals(3,validationSession.getProxyCount());
        List<Customer> customers = request.getValue().getCustomers();
        assertEquals(1,customers.size());
        customers.clear();
        assertEquals(1,validationSession.getProxyCount());
        request.toString();
        validationSession.close();
    }

    @Test
    public void test2() throws Exception
    {        
        ValidationSession validationSession = m_validationEngine.createSession();

        // create a customer
        Customer customer = m_customerDAO.createCustomer();
        customer.setName("aaaab");
        validationSession.bind(customer);
        assertEquals("a", customer.getCustomerType());
        assertTrue(customer.getBusiness().compareTo(IndustryType.AG)==0);
        customer.setName(null);
        assertNull(customer.getCustomerType());
        assertNull(customer.getBusiness());
        customer.setName("aaaaab");
        assertEquals("b", customer.getCustomerType());
        assertTrue(customer.getBusiness().compareTo(IndustryType.FISH)==0);

        validationSession.close();
    }
    @Test
    public void testCountConstraint() throws Exception
    {        
        ValidationSession validationSession = m_validationEngine.createSession();

        // create a customer
        Customer customer = m_customerDAO.createCustomer();
        customer.setName("aaaab");
        validationSession.bind(customer);
        int lastProxyCount = validationSession.getProxyCount();
        assertEquals(1,validationSession.getProxyCount());
        assertEquals("a", customer.getCustomerType());
        assertTrue(customer.getBusiness().compareTo(IndustryType.AG)==0);
        Invoice invoice = new Invoice();
        invoice.setDescription("test invoice0");
        customer.getInvoices().add(invoice);
        assertEquals(lastProxyCount+1,validationSession.getProxyCount());
        invoice = new Invoice();
        invoice.setDescription("test invoice1");
        customer.getInvoices().add(invoice);
        invoice = new Invoice();
        invoice.setDescription("test invoice1");
        customer.setOneInvoice(invoice);
        invoice = new Invoice();
        invoice.setDescription("test invoice2");
        lastProxyCount = validationSession.getProxyCount();
        // Invoice count at this point is 2
        boolean gotException=false;
        try
        {
            customer.getInvoices().add(invoice);
        }
        catch (ConstraintViolationException e)
        {
            gotException=true;
        }
        assertTrue(gotException);
        assertEquals(2, customer.getInvoices().size());
        assertEquals(lastProxyCount,validationSession.getProxyCount());
        customer.getInvoices().clear();
        assertEquals(2,validationSession.getProxyCount());
        validationSession.close();
    }
    @Test
    public void test3() throws Exception
    {        
        ValidationSession validationSession = m_validationEngine.createSession();

        // create a customer
        Customer customer = m_customerDAO.createCustomer();
        customer.setName("aaaab");
        validationSession.bind(customer);
        assertEquals("a", customer.getCustomerType());
        assertTrue(customer.getBusiness().compareTo(IndustryType.AG)==0);
        customer.getInvoices().add(new Invoice());
        assertEquals(1, customer.getInvoiceCount());
        validationSession.close();
    }
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
            customer.setCustomerType("a");
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

    
//org.hibernate.StaleObjectStateException: Row was updated or deleted by another transaction (or unsaved-value mapping was incorrect): [nz.co.senanque.madura.sandbox.Customer#0]
//at org.hibernate.persister.entity.AbstractEntityPersister.check(AbstractEntityPersister.java:1782)
//at org.hibernate.persister.entity.AbstractEntityPersister.update(AbstractEntityPersister.java:2425)
//at org.hibernate.persister.entity.AbstractEntityPersister.updateOrInsert(AbstractEntityPersister.java:2325)
//at org.hibernate.persister.entity.AbstractEntityPersister.update(AbstractEntityPersister.java:2625)
//at org.hibernate.action.EntityUpdateAction.execute(EntityUpdateAction.java:115)
//at org.hibernate.engine.ActionQueue.execute(ActionQueue.java:279)
//at org.hibernate.engine.ActionQueue.executeActions(ActionQueue.java:263)
//at org.hibernate.engine.ActionQueue.executeActions(ActionQueue.java:168)
//at org.hibernate.event.def.AbstractFlushingEventListener.performExecutions(AbstractFlushingEventListener.java:321)
//at org.hibernate.event.def.DefaultFlushEventListener.onFlush(DefaultFlushEventListener.java:50)
//at org.hibernate.impl.SessionImpl.flush(SessionImpl.java:1028)
//at nz.co.senanque.sandbox.CustomerDAOImpl.save(CustomerDAOImpl.java:65)
    
    @Ignore("See above, but only happens if run w other tests")
    @Test
    public void testTransaction() throws Exception
    {
        Customer customer = m_customerDAO.createCustomer();
        customer.setName("aaaaaab");
        @SuppressWarnings("unused")
		final long id = m_customerDAO.save(customer);
        m_customerDAO.transactionTester();
    }


}
