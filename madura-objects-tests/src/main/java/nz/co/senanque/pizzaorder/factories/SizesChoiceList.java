package nz.co.senanque.pizzaorder.factories;

import java.util.ArrayList;
import java.util.List;

import nz.co.senanque.validationengine.choicelists.ChoiceBase;
import nz.co.senanque.validationengine.choicelists.ChoiceListFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

@Component("Sizes")
public class SizesChoiceList implements ChoiceListFactory {

	private final Logger logger = LoggerFactory.getLogger(SizesChoiceList.class);
	public SizesChoiceList() {
		logger.debug("");
	}

	public List<ChoiceBase> getChoiceList(
			MessageSourceAccessor messageSourceAccessor) {
		List<ChoiceBase> ret = new ArrayList<ChoiceBase>();
		ret.add(new ChoiceBase("Small","Small",messageSourceAccessor));
		ret.add(new ChoiceBase("Medium","Medium",messageSourceAccessor));
		ret.add(new ChoiceBase("Large","Large",messageSourceAccessor));
		ret.add(new ChoiceBase("TrulyVast","Truly Vast",messageSourceAccessor));
		return ret;
	}

}
