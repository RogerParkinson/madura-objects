package nz.co.senanque.cdi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import nz.co.senanque.pizzaorder.generated.Customer;
import nz.co.senanque.pizzaorder.generated.Pizza;
import nz.co.senanque.rules.RulesPlugin;
import nz.co.senanque.validationengine.ValidationEngine;
import nz.co.senanque.validationengine.ValidationException;
import nz.co.senanque.validationengine.ValidationSession;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CDIConfigurationTest {
	
	private final static Logger log = LoggerFactory.getLogger(CDIConfigurationTest.class);

	@Test
	public void testPizza() {
	    final ValidationEngine validationEngine = WeldContext.INSTANCE.getBean(ValidationEngine.class);
	    assertEquals("my-identifier",validationEngine.getIdentifier());
        ValidationSession validationSession = validationEngine.createSession();
        Pizza pizza = new Pizza();
        validationSession.bind(pizza);
        boolean exceptionFound = false;
        try 
        {
            pizza.setTestDouble(50D);
        } 
        catch (ValidationException e) 
        {
            exceptionFound = true;
        }
        assertTrue(exceptionFound);
        pizza.setTestDouble(500D);
        pizza.setTopping("Turkish");
        exceptionFound = false;
        try 
        {
            pizza.setSize("Large");
        } 
        catch (ValidationException e) 
        {
            exceptionFound = true;
        }
        assertTrue(exceptionFound);
        pizza.setSize("TrulyVast");
        RulesPlugin rulesPlugin = validationEngine.getPlugin(RulesPlugin.class);
        validationSession.close();
    }
	@Test
	public void testCustomer() {
	    final ValidationEngine validationEngine = WeldContext.INSTANCE.getBean(ValidationEngine.class);
        assertEquals(validationEngine.getIdentifier(),"my-identifier");
        ValidationSession validationSession = validationEngine.createSession();
        Customer customer = new Customer();
        validationSession.bind(customer);
        customer.setName("fred");
        assertEquals("yes, that's okay",customer.getPassword());
        assertEquals(12D,customer.getWeight(),0);
        validationSession.close();
    }

}
