package nz.co.senanque.schemaparser.restrictions;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jdom.Element;

public class Enumerate extends Restriction {

	private List<CandidateValue> candidateValues = new ArrayList<>();
	private String value;
	
	public Enumerate(Element restriction) {
		value = restriction.getAttributeValue("value");
	}

	public String getValue() {
		return value;
	}

	@Override
	public List<CandidateValue> getCandidateValues() {
		if (candidateValues.isEmpty()) {
			candidateValues.add(new CandidateValue(value,true,this));
		}
		return candidateValues;
	}
	
}
