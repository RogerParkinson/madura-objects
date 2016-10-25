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
package nz.co.senanque.pizzaorder;

import static org.junit.Assert.assertEquals;

import java.util.List;

import nz.co.senanque.base.Order;
import nz.co.senanque.base.Pizza;
import nz.co.senanque.base.SpringConfiguration;
import nz.co.senanque.validationengine.ObjectMetadata;
import nz.co.senanque.validationengine.ValidationEngine;
import nz.co.senanque.validationengine.ValidationSession;
import nz.co.senanque.validationengine.choicelists.ChoiceBase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration( classes = {SpringConfiguration.class,PizzaOrderConfiguration.class})
public class PizzaOrderTest
{
    private static final Logger log = LoggerFactory.getLogger(PizzaOrderTest.class);
    @Autowired private transient ValidationEngine m_validationEngine;
    @Autowired private transient MessageSource m_messageSource;

    @Test
    public void test0() throws Exception
    {  
    	String s = m_messageSource.getMessage("shopping.cart.status.empty", new Object[]{},null);
    	String s1 = m_messageSource.getMessage("Large", new Object[]{},null);
    	log.debug("{} {}",s,s1);
    }
    @Test
    public void test1() throws Exception
    {  
        ValidationSession validationSession = m_validationEngine.createSession();
        Order order = new Order();
        validationSession.bind(order);
        Pizza pizza = new Pizza();
        order.getOrderItems().add(pizza);
        pizza.setSize("Small");
        //pizza.setSize(null);
        pizza.setSize("Medium");
        assertEquals("Medium",pizza.getSize());
        validationSession.close();
    }
    @Test
    public void test2() throws Exception
    {        
        ValidationSession validationSession = m_validationEngine.createSession();
        Order order = new Order();
        validationSession.bind(order);
        Pizza pizza = new Pizza();
        order.getOrderItems().add(pizza);
        pizza.setSize("Medium");
        assertEquals("Medium",pizza.getSize());
        order.getOrderItems().remove(pizza);
        pizza = new Pizza();
        pizza.setAmount(0);
        order.getOrderItems().add(pizza);
        pizza.setSize("Medium"); // this fails.
        validationSession.close();
    }
    @Test
    public void test3() throws Exception
    {        
        ValidationSession validationSession = m_validationEngine.createSession();
        Order order = new Order();
        validationSession.bind(order);
        assertEquals(new Double(0),new Double(order.getAmount()));
        Pizza pizza = new Pizza();
        order.getOrderItems().add(pizza);
        assertEquals(new Double(0),new Double(order.getAmount()));
        log.debug("setting size to Medium");
        pizza.setSize("Medium");
        assertEquals("Medium",pizza.getSize());
        log.debug("verified setting size to Medium");
        assertEquals(new Double(15),new Double(order.getAmount()));
        pizza.setSize("Small");
        assertEquals("Small",pizza.getSize());
        assertEquals(new Double(10),new Double(order.getAmount()));
        validationSession.close();
    }
    @Test
    public void testClearingOptions() throws Exception
    {        
        @SuppressWarnings("unused")
		int size = 0;
        ValidationSession validationSession = m_validationEngine.createSession();
        Order order = new Order();
        validationSession.bind(order);
        Pizza pizza = new Pizza();
        order.getOrderItems().add(pizza);
        ObjectMetadata metadata = pizza.getMetadata();
        countChoices(metadata,Pizza.TOPPING,5);
        countChoices(metadata,Pizza.SIZE,3);
        
        pizza.setSize("Medium");
        countChoices(metadata,Pizza.TOPPING,2);
        countChoices(metadata,Pizza.SIZE,3);

        pizza.setTopping("Spanish");
        countChoices(metadata,Pizza.TOPPING,2);
        countChoices(metadata,Pizza.SIZE,1);

        pizza.setTopping(null);
        countChoices(metadata,Pizza.TOPPING,2);
        countChoices(metadata,Pizza.SIZE,3);

        pizza.setSize(null);
        countChoices(metadata,Pizza.TOPPING,5); // we do see this in the UI
        countChoices(metadata,Pizza.SIZE,3); // ...but we don't see this, although the log suggests this is different. The excludes are still there in some (mysterious) cases.
        validationSession.close();

    }
    private void countChoices(ObjectMetadata metadata, String type, int expected)
    {
        List<ChoiceBase> choices = metadata.getFieldMetadata(type).getChoiceList();
        assertEquals(expected,choices.size());
    }
    

}
