package nz.co.senanque.configuration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import nz.co.senanque.pizzaorder.generated.Customer;
import nz.co.senanque.pizzaorder.generated.Pizza;
import nz.co.senanque.validationengine.ValidationEngine;
import nz.co.senanque.validationengine.ValidationException;
import nz.co.senanque.validationengine.ValidationSession;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class SpringConfigurationTest {
	
	private static ValidationEngine validationEngine;

	@BeforeClass
	public static void setup() {
        ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfiguration.class);
        validationEngine = (ValidationEngine) context.getBean("validationEngine");		
	}
	@AfterClass
	public static void takeDown() {
		//
	}
	@Test
	public void testPizza() {
        assertEquals(validationEngine.getIdentifier(),"my-identifier");
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
        validationSession.close();
    }

	@Test
	public void testCustomer() {
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
