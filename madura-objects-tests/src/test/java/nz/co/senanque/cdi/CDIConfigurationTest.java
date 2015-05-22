package nz.co.senanque.cdi;

import static org.junit.Assert.assertEquals;

import javax.enterprise.inject.spi.Bean;

import nz.co.senanque.validationengine.ValidationEngine;
import nz.co.senanque.validationengine.ValidationEngineImpl;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CDIConfigurationTest {
	
	private final static Logger log = LoggerFactory.getLogger(CDIConfigurationTest.class);

	@Test
	public void testApplication() {
	    final ValidationEngine validationEngine = WeldContext.INSTANCE.getBean(ValidationEngine.class);
	    assertEquals("my-identifier",validationEngine.getIdentifier());
	    for (Bean<?> bean:WeldContext.INSTANCE.getContainer().getBeanManager().getBeans("*")) {
	    	log.debug("{} {}",bean.getName(),bean.getBeanClass());
	    }
    }

}
