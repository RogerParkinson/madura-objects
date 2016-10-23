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
package nz.co.senanque.listfunctions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import nz.co.senanque.base.Customer;
import nz.co.senanque.base.CustomerDAO;
import nz.co.senanque.base.Invoice;
import nz.co.senanque.base.Session;
import nz.co.senanque.rules.ConstraintViolationException;
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
 * Tests to verify that the XSD generated objects actually do serialise properly
 * 
 * @author Roger Parkinson
 * @version $Revision: 1.3 $
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"spring.xml"})
public class HeapMonitorTest
{
    private static final Logger logger = LoggerFactory.getLogger(HeapMonitorTest.class);
    @Autowired private transient ValidationEngine m_validationEngine;
    @Autowired private transient CustomerDAO m_customerDAO;

    @Test
    public void test1() throws Exception
    {        
        ValidationSession validationSession = m_validationEngine.createSession();

        Customer customer = m_customerDAO.createCustomer();
        validationSession.bind(customer);
        logger.debug(validationSession.getStats());
        customer.setMinInvoiceCount(0);

        Invoice invoice = new Invoice();
        invoice.setAmount(130);
        customer.getInvoices().add(invoice);
        logger.debug(validationSession.getStats());
        long j = customer.getInvoiceCount();
        assertEquals(1,j);

        invoice = new Invoice();
        invoice.setAmount(130);
        customer.getInvoices().add(invoice);
        logger.debug(validationSession.getStats());
        j = customer.getInvoiceCount();
        assertEquals(2,j);

        invoice = new Invoice();
        invoice.setAmount(130);
        customer.getInvoices().add(invoice);
        logger.debug(validationSession.getStats());
        j = customer.getInvoiceCount();
        assertEquals(3,j);

        invoice = new Invoice();
        invoice.setAmount(200);
        invoice.setTestBoolean(true);
        customer.getInvoices().add(invoice);
        logger.debug(validationSession.getStats());
        j = customer.getInvoiceCount();
        assertEquals(4,j);
        
        assertEquals(new Double(590.0),new Double(customer.getAmount()));
        assertEquals(true,customer.isAnytrue());
        assertEquals(false,customer.isAlltrue());
        assertEquals(1,customer.getCount());
        
        for (Invoice inv : customer.getInvoices())
        {
             inv.setTestBoolean(true);
        }
        assertEquals(true,customer.isAnytrue());
        assertEquals(4,customer.getCount());
        assertEquals(true,customer.isAlltrue());

        for (Invoice inv : customer.getInvoices())
        {
            inv.setTestBoolean(false);
        }
        assertEquals(false,customer.isAnytrue());
        assertEquals(false,customer.isAlltrue());
        assertEquals(0,customer.getCount());
        assertEquals(4,customer.getInvoiceCount());
        
        Invoice inv = customer.getInvoices().get(0);
        customer.getInvoices().remove(inv);
        j = customer.getInvoiceCount();
        logger.debug(validationSession.getStats());
        assertEquals(3,j);

        customer.getInvoices().clear();
        j = customer.getInvoiceCount();
        logger.debug(validationSession.getStats());
        assertEquals(0,j);
        
        validationSession.close();

    }
    /**
     * tests the array .clear() method
     * @throws Exception
     */
    @SuppressWarnings("unused")
	@Test
    public void test1a() throws Exception
    {        
        ValidationSession validationSession = m_validationEngine.createSession();

        Customer customer = m_customerDAO.createCustomer();
        validationSession.bind(customer);
        logger.debug(validationSession.getStats());

        Invoice invoice = new Invoice();
        invoice.setAmount(130);
        customer.getInvoices().add(invoice);
        logger.debug(validationSession.getStats());
        long j = customer.getInvoiceCount();
        assertEquals(1,j);

        invoice = new Invoice();
        invoice.setAmount(130);
        customer.getInvoices().add(invoice);
        logger.debug(validationSession.getStats());
        j = customer.getInvoiceCount();
        assertEquals(2,j);

        invoice = new Invoice();
        invoice.setAmount(130);
        customer.getInvoices().add(invoice);
        logger.debug(validationSession.getStats());
        j = customer.getInvoiceCount();
        assertEquals(3,j);

        invoice = new Invoice();
        invoice.setAmount(200);
        invoice.setTestBoolean(true);
        customer.getInvoices().add(invoice);
        logger.debug(validationSession.getStats());
        j = customer.getInvoiceCount();
        assertEquals(4,j);
        
        assertEquals(new Double(590.0),new Double(customer.getAmount()));
        assertEquals(true,customer.isAnytrue());
        assertEquals(false,customer.isAlltrue());
        assertEquals(1,customer.getCount());
        
        for (Invoice inv : customer.getInvoices())
        {
             inv.setTestBoolean(true);
        }
        assertEquals(true,customer.isAnytrue());
        assertEquals(4,customer.getCount());
        assertEquals(true,customer.isAlltrue());

        for (Invoice inv : customer.getInvoices())
        {
            inv.setTestBoolean(false);
        }
        assertEquals(false,customer.isAnytrue());
        assertEquals(false,customer.isAlltrue());
        assertEquals(0,customer.getCount());
        assertEquals(4,customer.getInvoiceCount());
        
        Invoice inv = customer.getInvoices().get(0);
        customer.getInvoices().clear();
        j = customer.getInvoiceCount();
        logger.debug(validationSession.getStats());
        assertEquals(0,j);

        validationSession.close();

    }
    /**
     * Tests for constraint violation when removing an array element
     * @throws Exception
     */
    @Test
    public void test1b() throws Exception
    {        
        ValidationSession validationSession = m_validationEngine.createSession();

        Customer customer = m_customerDAO.createCustomer();
        validationSession.bind(customer);
        logger.debug(validationSession.getStats());

        Invoice invoice = new Invoice();
        invoice.setAmount(130);
        customer.getInvoices().add(invoice);
        logger.debug(validationSession.getStats());
        long j = customer.getInvoiceCount();
        assertEquals(1,j);

        invoice = new Invoice();
        invoice.setAmount(130);
        customer.getInvoices().add(invoice);
        logger.debug(validationSession.getStats());
        j = customer.getInvoiceCount();
        assertEquals(2,j);

        invoice = new Invoice();
        invoice.setAmount(130);
        customer.getInvoices().add(invoice);
        logger.debug(validationSession.getStats());
        j = customer.getInvoiceCount();
        assertEquals(3,j);

        invoice = new Invoice();
        invoice.setAmount(200);
        invoice.setTestBoolean(true);
        customer.getInvoices().add(invoice);
        logger.debug(validationSession.getStats());
        j = customer.getInvoiceCount();
        assertEquals(4,j);
        
        customer.setMinInvoiceCount(4);
        boolean exceptionFired = false;
        try {
			customer.getInvoices().remove(invoice);
		} catch (ConstraintViolationException e) {
			logger.warn(e.getMessage());
			exceptionFired = true;
		}
        assertTrue(exceptionFired);
        logger.debug(validationSession.getStats());
        j = customer.getInvoiceCount();
        assertEquals(4,j);

        validationSession.close();

    }

    /**
     * Tests for constraint violation when removing an array element with clear()
     * @throws Exception
     */
    @Test
    public void test1c() throws Exception
    {        
        ValidationSession validationSession = m_validationEngine.createSession();

        Customer customer = m_customerDAO.createCustomer();
        validationSession.bind(customer);
        logger.debug(validationSession.getStats());

        Invoice invoice = new Invoice();
        invoice.setAmount(130);
        customer.getInvoices().add(invoice);
        logger.debug(validationSession.getStats());
        long j = customer.getInvoiceCount();
        assertEquals(1,j);

        invoice = new Invoice();
        invoice.setAmount(130);
        customer.getInvoices().add(invoice);
        logger.debug(validationSession.getStats());
        j = customer.getInvoiceCount();
        assertEquals(2,j);

        invoice = new Invoice();
        invoice.setAmount(130);
        customer.getInvoices().add(invoice);
        logger.debug(validationSession.getStats());
        j = customer.getInvoiceCount();
        assertEquals(3,j);

        invoice = new Invoice();
        invoice.setAmount(200);
        invoice.setTestBoolean(true);
        customer.getInvoices().add(invoice);
        logger.debug(validationSession.getStats());
        j = customer.getInvoiceCount();
        assertEquals(4,j);
        
        customer.setMinInvoiceCount(4);
        boolean exceptionFired = false;
        try {
			customer.getInvoices().clear();
		} catch (ConstraintViolationException e) {
			logger.warn(e.getMessage());
			exceptionFired = true;
		}
        assertTrue(exceptionFired);
        logger.debug(validationSession.getStats());
        j = customer.getInvoiceCount();
        assertEquals(4,j);

        validationSession.close();

    }

    @Test
    public void test2() throws Exception
    {        
        ValidationSession validationSession = m_validationEngine.createSession();
        
        Session session = new Session();
        validationSession.bind(session);
        logger.debug(validationSession.getStats());

        Customer customer1 = m_customerDAO.createCustomer();
        customer1.setId(1);
        session.getCustomers().add(customer1);
        logger.debug(validationSession.getStats());

        Customer customer2 = m_customerDAO.createCustomer();
        customer2.setId(2);
        session.getCustomers().add(customer2);
        logger.debug(validationSession.getStats());
        
        session.getCustomers().remove(customer2);
//        logger.debug(validationSession.getStats());
        logger.debug(validationSession.getStats());
        session.getCustomers().clear();
        logger.debug(validationSession.getStats());
        validationSession.close();
        logger.debug(validationSession.getStats());

    }

}
