package nz.co.senanque.validationengine.choicelists;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.MessageSource;

public class MyChoiceListFactory implements ChoiceListFactory {

	public MyChoiceListFactory() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<ChoiceBase> getChoiceList(
			MessageSource messageSource) {
		List<ChoiceBase> ret = new ArrayList<>();
		ret.add(new ChoiceBase("1","1",messageSource));
		ret.add(new ChoiceBase("2","2",messageSource));
		ret.add(new ChoiceBase("3","3",messageSource));
		ret.add(new ChoiceBase("4","4",messageSource));
		ret.add(new ChoiceBase("5","5",messageSource));
		ret.add(new ChoiceBase("6","6",messageSource));
		ret.add(new ChoiceBase("7","7",messageSource));
		return ret;
	}

}
