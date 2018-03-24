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
package nz.co.senanque.functions;

import static org.junit.Assert.assertEquals;

import java.text.SimpleDateFormat;
import java.util.Date;

import nz.co.senanque.base.Customer;
import nz.co.senanque.base.CustomerDAO;
import nz.co.senanque.base.Invoice;
import nz.co.senanque.base.SpringConfiguration;
import nz.co.senanque.validationengine.ValidationEngine;
import nz.co.senanque.validationengine.ValidationSession;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;



/**
 * Tests to verify certain rule functions re working correctly, specifically addDays and daysSince.
 * 
 * @author Roger Parkinson
 * @version $Revision: 1.2 $
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration( classes = {SpringConfiguration.class,FunctionConfiguration.class})
public class FunctionTest
{
    @Autowired private transient ValidationEngine m_validationEngine;
    @Autowired private transient CustomerDAO m_customerDAO;

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

        long days = invoice.getDays();

        // proves r2 fired okay
        assertEquals(3834L,days);
        
        Date revisedDate = invoice.getRevisedDate();
        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy:HH:mm:SS");
        String ss = DATE_FORMAT.format(revisedDate);
        assertEquals("24-04-2000:00:00:00",ss);
        
    }

}
