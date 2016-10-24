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


import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.ResourceTransactionManager;

/**
 * 
 * Short description
 * 
 * @author Roger Parkinson
 * @version $Revision: 1.1 $
 */
@Component
public class CustomerDAOImpl implements CustomerDAO 
{
	@PersistenceContext
	private EntityManager entityManager;

	@Autowired private transient ObjectFactory m_objectFactory;
	@Autowired private transient SubTransaction m_subTransaction;
//	@Autowired private transient ResourceTransactionManager m_txManager;

    /* (non-Javadoc)
     * @see nz.co.senanque.sandbox.CustomerDAO#createCustomer()
     */
    public Customer createCustomer()
    {
        return m_objectFactory.createCustomer();

    }
    /* (non-Javadoc)
     * @see nz.co.senanque.sandbox.CustomerDAO#save(nz.co.senanque.madura.sandbox.Customer, org.hibernate.Session)
     */
    @Transactional
    public long save(final Customer customer)
    {
    	entityManager.merge(customer);
    	entityManager.flush();
        return customer.getId();
    }
    /* (non-Javadoc)
     * @see nz.co.senanque.sandbox.CustomerDAO#getCustomer(long, org.hibernate.Session)
     */
	@Transactional(readOnly=true)
    public Customer getCustomer(long id)
    {
		Customer customer = entityManager.find(Customer.class, id);
        customer.getInvoices().size();
        return customer;
    }
   
//    public SubTransaction getSubTransaction()
//    {
//        return m_subTransaction;
//    }
//    public void setSubTransaction(final SubTransaction subTransaction)
//    {
//        m_subTransaction = subTransaction;
//    }
//    public ResourceTransactionManager getTxManager()
//    {
//        return m_txManager;
//    }
//    public void setTxManager(final ResourceTransactionManager txManager)
//    {
//        m_txManager = txManager;
//    }

}
