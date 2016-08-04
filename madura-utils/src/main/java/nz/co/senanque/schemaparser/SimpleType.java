package nz.co.senanque.schemaparser;

import java.util.List;

import nz.co.senanque.schemaparser.restrictions.Restriction;

public class SimpleType {
	
	private final String name;
	private final String type;
	private final List<Restriction> restrictions;

	public SimpleType(String name, String type,
			List<Restriction> restrictions) {
		this.name = name;
		this.type = type;
		this.restrictions = restrictions;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public List<Restriction> getRestrictions() {
		return restrictions;
	}

}
