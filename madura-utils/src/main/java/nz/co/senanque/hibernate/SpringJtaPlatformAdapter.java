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
package nz.co.senanque.hibernate;

import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import org.hibernate.engine.transaction.jta.platform.internal.AbstractJtaPlatform;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.util.Assert;

/**
 * Needed to get Hibernate and Atomikos to work together
 * see  <a href="http://stackoverflow.com/questions/20681245/how-to-use-atomikos-transaction-essentials-with-hibernate-4-3">how-to-use-atomikos-transaction-essentials-with-hibernate-4-3</a>
 * @author Roger Parkinson
 *
 */
@SuppressWarnings("serial")
public class SpringJtaPlatformAdapter extends AbstractJtaPlatform {

    private static TransactionManager sTransactionManager;
    private static UserTransaction sUserTransaction;


    protected TransactionManager locateTransactionManager() {
        Assert.notNull(sTransactionManager, "TransactionManager has not been setted");
        return sTransactionManager;
    }


    protected UserTransaction locateUserTransaction() {
        Assert.notNull(sUserTransaction, "UserTransaction has not been setted");
        return sUserTransaction;
    }


    public void setJtaTransactionManager(JtaTransactionManager jtaTransactionManager) {
        sTransactionManager = jtaTransactionManager.getTransactionManager();
        sUserTransaction = jtaTransactionManager.getUserTransaction();
    }


    public void setTransactionManager(TransactionManager transactionManager) {
        sTransactionManager = transactionManager;
    }


    public void setUserTransaction(UserTransaction userTransaction) {
        sUserTransaction = userTransaction;
    }
}