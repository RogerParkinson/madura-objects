package nz.co.senanque.schemaparser.restrictions;

import org.jdom.Element;

public class Enumerate {

	Long value;
	
	public Enumerate(Element restriction) {
		value = Long.parseLong(restriction.getAttributeValue("value"));
	}
}
