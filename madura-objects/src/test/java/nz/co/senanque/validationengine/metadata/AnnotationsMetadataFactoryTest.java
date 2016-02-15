package nz.co.senanque.validationengine.metadata;

import java.lang.reflect.Method;

import javax.persistence.Column;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnnotationsMetadataFactoryTest {
	
	private static Logger m_logger = LoggerFactory.getLogger(AnnotationsMetadataFactoryTest.class);

	@Test
	public void test() throws Exception {
		Class<?> clazz = Class.forName("nz.co.senanque.madura.sandbox.Customer");
		for (Method method: clazz.getMethods()) {
			if (method.getParameterTypes().length == 0 && method.getName().startsWith("get")) {
				Column column = method.getAnnotation(Column.class);
				if (column != null) {
					int fractional = column.scale();
					int precision = column.precision();
					int length = column.length();
					m_logger.debug("{} fractional: {} ,precision: {},length: {}",new Object[]{method.getName(),fractional,precision,length});
				}
			}
		}
	}

}
