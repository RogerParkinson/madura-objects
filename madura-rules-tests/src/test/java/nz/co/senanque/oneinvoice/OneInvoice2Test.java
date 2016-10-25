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
import nz.co.senanque.base.SpringConfiguration;
import nz.co.senanque.validationengine.ValidationEngine;
import nz.co.senanque.validationengine.ValidationSession;

import org.apache.commons.logging.Log;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;



/**
 * Not sure we still need this one.
 * 
 * @author Roger Parkinson
 * @version $Revision: 1.3 $
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SpringConfiguration.class, OneInvoiceConfiguration.class})
public class OneInvoice2Test
{
    @SuppressWarnings("unused")
	private static final Log log = org.apache.commons.logging.LogFactory.getLog(OneInvoice2Test.class);
    @Autowired private transient ValidationEngine m_validationEngine;
    @Autowired private transient CustomerDAO m_customerDAO;


    @Test
    public void test1() throws Exception
    {        
        ValidationSession validationSession = m_validationEngine.createSession();

        Customer customer = new BusinessCustomer();
        validationSession.bind(customer);
        IndustryType industryType = customer.getBusinessy();
        assertEquals(null,industryType);
    }


}
