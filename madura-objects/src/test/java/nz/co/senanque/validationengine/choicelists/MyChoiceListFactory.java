package nz.co.senanque.validationengine.choicelists;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.support.MessageSourceAccessor;

public class MyChoiceListFactory implements ChoiceListFactory {

	public MyChoiceListFactory() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<ChoiceBase> getChoiceList(
			MessageSourceAccessor messageSourceAccessor) {
		List<ChoiceBase> ret = new ArrayList<>();
		ret.add(new ChoiceBase("1","1",messageSourceAccessor));
		ret.add(new ChoiceBase("2","2",messageSourceAccessor));
		ret.add(new ChoiceBase("3","3",messageSourceAccessor));
		ret.add(new ChoiceBase("4","4",messageSourceAccessor));
		ret.add(new ChoiceBase("5","5",messageSourceAccessor));
		ret.add(new ChoiceBase("6","6",messageSourceAccessor));
		ret.add(new ChoiceBase("7","7",messageSourceAccessor));
		return ret;
	}

}
