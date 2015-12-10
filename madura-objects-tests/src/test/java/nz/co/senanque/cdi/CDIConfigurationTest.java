package nz.co.senanque.cdi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Locale;

import nz.co.senanque.pizzaorder.generated.Customer;
import nz.co.senanque.pizzaorder.generated.Pizza;
import nz.co.senanque.rules.RulesPlugin;
import nz.co.senanque.validationengine.ValidationEngine;
import nz.co.senanque.validationengine.ValidationException;
import nz.co.senanque.validationengine.ValidationSession;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.MessageSourceAccessor;

public class CDIConfigurationTest {
	
	@SuppressWarnings("unused")
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
        @SuppressWarnings("unused")
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
	@Test
	public void testResourceBundles() {
	    final ValidationEngine validationEngine = WeldContext.INSTANCE.getBean(ValidationEngine.class);
        assertEquals(validationEngine.getIdentifier(),"my-identifier");
		MessageSourceAccessor messageSourceAccessor = new MessageSourceAccessor(validationEngine.getMessageSource());
		Locale locale = new Locale("en");
		String m = messageSourceAccessor.getMessage("nz.co.senanque.validationengine.class.not.recognised",locale);
		assertEquals("Bind failed. Class not recognised: {0}",m);
		m = messageSourceAccessor.getMessage("nz.co.senanque.rules.divide.by.zero",locale);
		assertEquals("Divide by zero error",m);
		m = messageSourceAccessor.getMessage("nz.co.senanque.pizzaorder.generated.R22",locale);
		assertEquals("try out external functions",m);
		m = messageSourceAccessor.getMessage("shopping.cart.status",locale);
		assertEquals("{0} items in cart",m);
	}
}
