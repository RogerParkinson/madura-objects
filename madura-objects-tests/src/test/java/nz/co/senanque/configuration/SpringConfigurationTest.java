package nz.co.senanque.configuration;

import static org.junit.Assert.*;
import nz.co.senanque.validationengine.ValidationEngine;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class SpringConfigurationTest {

	@Test
	public void testApplication() {
        ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfiguration.class);
        ValidationEngine validationEngine = (ValidationEngine) context.getBean("validationEngine");
        assertEquals(validationEngine.getIdentifier(),"my-identifier");
    }

}
