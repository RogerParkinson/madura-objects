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
package nz.co.senanque.oneinvoice;

import static org.junit.Assert.assertEquals;
import nz.co.senanque.base.BusinessCustomer;
import nz.co.senanque.base.Customer;
import nz.co.senanque.base.CustomerDAO;
import nz.co.senanque.base.IndustryType;
import nz.co.senanque.base.Invoice;
import nz.co.senanque.base.SpringConfiguration;
import nz.co.senanque.validationengine.ValidationEngine;
import nz.co.senanque.validationengine.ValidationSession;

import org.apache.commons.logging.Log;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;



/**
 * Tests to verify that the XSD generated objects actually do serialise properly
 * 
 * @author Roger Parkinson
 * @version $Revision: 1.3 $
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration( classes = {SpringConfiguration.class, OneInvoiceConfiguration.class})
public class OneInvoiceTest
{
    @SuppressWarnings("unused")
	private static final Log log = org.apache.commons.logging.LogFactory.getLog(OneInvoiceTest.class);
    @Autowired private transient ValidationEngine m_validationEngine;
    @Autowired private transient CustomerDAO m_customerDAO;
    @Autowired private transient MessageSource m_messageSource;

    @Test
    public void test1() throws Exception
    {
        ValidationSession validationSession = m_validationEngine.createSession();

        Customer customer = m_customerDAO.createCustomer();
        validationSession.bind(customer);
        Invoice invoice = new Invoice();
        invoice.setDescription("test invoice");
        customer.setOneInvoice(invoice);
        customer.setAmount(130);
        
        Double d = invoice.getAmount();
        
        // proves r1 fired okay
        assertEquals(130L,d.longValue());

        d = customer.getAmountOne();

        // proves r2 fired okay
        assertEquals(130L,d.longValue());

        invoice = new Invoice();
        invoice.setAmount(45);
        customer.getInvoices().add(invoice);
        d = customer.getAmountTwo();
        
        // proves r3 fired okay
        assertEquals(45L,d.longValue()); // Fails
        d = customer.getAmountThree();
        // proves r4 fired okay.
        assertEquals(45L,d.longValue());
        validationSession.close();
    }

    @Test
    public void test1a() throws Exception
    {
        ValidationSession validationSession = m_validationEngine.createSession();

        Customer customer = m_customerDAO.createCustomer();
        validationSession.bind(customer);

        // proves r2 fired okay
        Invoice invoice = new Invoice();
        invoice.setDescription("test invoice");
        invoice.setAmount(100);
        customer.setOneInvoice(invoice);
        Double d = customer.getAmountOne();
        assertEquals(100L,d.longValue());

        customer.setOneInvoice(null);
        d = customer.getAmountOne();
        assertEquals(0L,d.longValue());

        validationSession.close();
    }

    @Test
    public void test2() throws Exception
    {        
        ValidationSession validationSession = m_validationEngine.createSession();

        Customer customer = new BusinessCustomer();
        validationSession.bind(customer);
        IndustryType industryType = customer.getBusinessx();
        assertEquals(IndustryType.FISH,industryType);
        industryType = customer.getBusinessy();
        assertEquals(null,industryType);
        validationSession.close();
    }


}
