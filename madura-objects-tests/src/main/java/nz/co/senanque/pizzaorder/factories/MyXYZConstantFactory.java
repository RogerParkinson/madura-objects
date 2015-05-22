package nz.co.senanque.pizzaorder.factories;

import nz.co.senanque.rules.factories.ConstantFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("xyz")
public class MyXYZConstantFactory implements ConstantFactory {

	private final Logger logger = LoggerFactory.getLogger(MyXYZConstantFactory.class);

	public MyXYZConstantFactory() {
		logger.debug("");
	}

	public String getValue(String constantName) {
		return "CC";
	}

}
