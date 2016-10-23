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
import nz.co.senanque.base.Customer;
import nz.co.senanque.base.CustomerDAO;
import nz.co.senanque.base.Invoice;
import nz.co.senanque.validationengine.ValidationEngine;
import nz.co.senanque.validationengine.ValidationSession;

import org.apache.commons.logging.Log;
import org.junit.Test;
import org.junit.runner.RunWith;
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
public class ListFunctionTest
{
    @SuppressWarnings("unused")
	private static final Log log = org.apache.commons.logging.LogFactory.getLog(ListFunctionTest.class);
    @Autowired private transient ValidationEngine m_validationEngine;
    @Autowired private transient CustomerDAO m_customerDAO;

    @Test
    public void test1() throws Exception
    {        
        ValidationSession validationSession = m_validationEngine.createSession();

        Customer customer = m_customerDAO.createCustomer();
        validationSession.bind(customer);

        Invoice invoice = new Invoice();
        invoice.setAmount(130);
        customer.getInvoices().add(invoice);

        invoice = new Invoice();
        invoice.setAmount(130);
        customer.getInvoices().add(invoice);

        invoice = new Invoice();
        invoice.setAmount(130);
        customer.getInvoices().add(invoice);

        invoice = new Invoice();
        invoice.setAmount(200);
        invoice.setTestBoolean(true);
        customer.getInvoices().add(invoice);
        
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
        assertEquals(590L,new Double(customer.getAmount()).longValue());
        
        Invoice inv = customer.getInvoices().get(0);
        customer.getInvoices().remove(inv);
        long j = customer.getInvoiceCount();
        assertEquals(3,j);
        // Proves the amount was re-evaluated on removal of one of the invoices
        assertEquals(590L-130L,new Double(customer.getAmount()).longValue());

        customer.getInvoices().clear();
        j = customer.getInvoiceCount();
        assertEquals(0,j);
    }
    @Test
    public void test2() throws Exception
    {        
        ValidationSession validationSession = m_validationEngine.createSession();

        Customer customer = m_customerDAO.createCustomer();
        validationSession.bind(customer);

        Invoice invoice1 = new Invoice();
        invoice1.setAmount(130);
        customer.getInvoices().add(invoice1);

        assertEquals(1L,customer.getInvoiceCount());
        assertEquals(130L,Double.valueOf(customer.getAmount()).longValue());

        Invoice invoice2 = new Invoice();
        invoice2.setAmount(140);
        customer.getInvoices().add(invoice2);

        assertEquals(2L,customer.getInvoiceCount());
        assertEquals(270L,Double.valueOf(customer.getAmount()).longValue());
        
        customer.getInvoices().remove(invoice2);
        assertEquals(1L,customer.getInvoiceCount());
        assertEquals(130L,Double.valueOf(customer.getAmount()).longValue());
        
        Invoice invoice3 = new Invoice();
        invoice3.setAmount(150);
        customer.getInvoices().add(invoice3);
        
        assertEquals(2L,customer.getInvoiceCount());
        assertEquals(280L,Double.valueOf(customer.getAmount()).longValue());
        invoice3.setAmount(200);
        assertEquals(330L,Double.valueOf(customer.getAmount()).longValue());

    }
    @Test
    public void test3() throws Exception
    {        
        ValidationSession validationSession = m_validationEngine.createSession();

        Customer customer = m_customerDAO.createCustomer();
        validationSession.bind(customer);

        Invoice invoice1 = new Invoice();
        invoice1.setTestBoolean(true);
        invoice1.setHjid(1L);
        invoice1.setDescription("one");
        customer.getInvoices().add(invoice1);

        assertEquals(1L,customer.getInvoiceCount());
        assertEquals(true,customer.isAlltrue());

        Invoice invoice2 = new Invoice();
        invoice2.setTestBoolean(true);
        invoice2.setHjid(2L);
        invoice2.setDescription("two");
        customer.getInvoices().add(invoice2);

        assertEquals(2L,customer.getInvoiceCount());
        assertEquals(true,customer.isAlltrue());
        
        customer.getInvoices().remove(invoice2);
        assertEquals(1L,customer.getInvoiceCount());
        assertEquals(true,customer.isAlltrue()); 
        
        Invoice invoice3 = new Invoice();
        invoice3.setTestBoolean(true);
        customer.getInvoices().add(invoice3);
        
        assertEquals(2L,customer.getInvoiceCount());
        assertEquals(true,customer.isAlltrue());
        
        invoice3.setTestBoolean(false);
        assertEquals(false,customer.isAlltrue());
    }


}
