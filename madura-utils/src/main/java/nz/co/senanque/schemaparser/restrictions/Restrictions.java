package nz.co.senanque.schemaparser.restrictions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nz.co.senanque.schemaparser.FieldDescriptor;

public class Restrictions {
	
	List<Restriction> restrictions = new ArrayList<>();
	List<Enumerate> enumerations = new ArrayList<>();
	String base;
	FieldDescriptor fieldDescriptor;
	
	public Restrictions(String base) {
		this.base = base;
	}

	public void addRestriction(Restriction r) {
		restrictions.add(r);
	}

	public void addEnumerate(Enumerate enumerate) {
		enumerations.add(enumerate);
	}

	public List<Restriction> getRestrictions() {
		return Collections.unmodifiableList(restrictions);
	}

	public List<Enumerate> getEnumerations() {
		return Collections.unmodifiableList(enumerations);
	}

	public boolean isActive() {
		return !(restrictions.isEmpty() && enumerations.isEmpty());
	}

	public void setOwner(FieldDescriptor fieldDescriptor) {
		this.fieldDescriptor = fieldDescriptor;
	}

}
