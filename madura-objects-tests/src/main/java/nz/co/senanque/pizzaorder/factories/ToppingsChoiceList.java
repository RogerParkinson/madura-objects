package nz.co.senanque.pizzaorder.factories;

import java.util.ArrayList;
import java.util.List;

import nz.co.senanque.validationengine.choicelists.ChoiceBase;
import nz.co.senanque.validationengine.choicelists.ChoiceListFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component("Toppings")
public class ToppingsChoiceList implements ChoiceListFactory {

	private final Logger logger = LoggerFactory.getLogger(ToppingsChoiceList.class);
	public ToppingsChoiceList() {
		logger.debug("");
	}

	public List<ChoiceBase> getChoiceList(
			MessageSource messageSource) {
		List<ChoiceBase> ret = new ArrayList<ChoiceBase>();
		ret.add(new ChoiceBase("Seafood","Seafood",messageSource));
		ret.add(new ChoiceBase("Italian","Italian",messageSource));
		ret.add(new ChoiceBase("Spanish","Spanish",messageSource));
		ret.add(new ChoiceBase("Hawaiian","Hawaiian",messageSource));
		ret.add(new ChoiceBase("Greek","Greek",messageSource));
		ret.add(new ChoiceBase("Turkish","TrulyVast",messageSource));
		return ret;
	}

}
