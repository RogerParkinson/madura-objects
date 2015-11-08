package nz.co.senanque.pizzaorder.factories;

import java.util.ArrayList;
import java.util.List;

import nz.co.senanque.validationengine.choicelists.ChoiceBase;
import nz.co.senanque.validationengine.choicelists.ChoiceListFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component("Sizes")
public class SizesChoiceList implements ChoiceListFactory {

	private final Logger logger = LoggerFactory.getLogger(SizesChoiceList.class);
	public SizesChoiceList() {
		logger.debug("");
	}

	public List<ChoiceBase> getChoiceList(
			MessageSource messageSource) {
		List<ChoiceBase> ret = new ArrayList<ChoiceBase>();
		ret.add(new ChoiceBase("Small","Small",messageSource));
		ret.add(new ChoiceBase("Medium","Medium",messageSource));
		ret.add(new ChoiceBase("Large","Large",messageSource));
		ret.add(new ChoiceBase("TrulyVast","Truly Vast",messageSource));
		return ret;
	}

}
