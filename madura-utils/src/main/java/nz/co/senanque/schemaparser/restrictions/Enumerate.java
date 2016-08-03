package nz.co.senanque.schemaparser.restrictions;

import java.util.List;

import org.jdom.Element;

public class Enumerate extends Restriction {

	String value;
	
	public Enumerate(Element restriction) {
		value = restriction.getAttributeValue("value");
	}

	public String getValue() {
		return value;
	}

	@Override
	public List<CandidateValue> getCandidateValues() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
