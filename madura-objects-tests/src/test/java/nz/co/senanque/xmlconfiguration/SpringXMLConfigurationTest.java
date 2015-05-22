package nz.co.senanque.xmlconfiguration;

import static org.junit.Assert.assertEquals;
import nz.co.senanque.validationengine.ValidationEngine;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringXMLConfigurationTest {

	@Test
	public void testApplication() {
		ApplicationContext context = new ClassPathXmlApplicationContext(
				new String[] { "/nz/co/senanque/xmlconfiguration/Spring-context.xml" });

		ValidationEngine validationEngine = (ValidationEngine) context.getBean("validationEngine");
        assertEquals(validationEngine.getIdentifier(),"my-identifier");
    }

}
