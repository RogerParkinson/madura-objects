package nz.co.senanque.schemaparser.restrictions;

import java.util.List;

import nz.co.senanque.schemaparser.FieldDescriptor;

public abstract class Restriction {
	

	private FieldDescriptor fieldDescriptor;
	public abstract List<CandidateValue> getCandidateValues();
	public abstract String getValue();
	
	public String toString() {
		return this.getClass().getSimpleName()+"="+getValue();
	}
	public void setFieldDescriptor(FieldDescriptor fieldDescriptor) {
		this.fieldDescriptor = fieldDescriptor;
	}
	public FieldDescriptor getFieldDescriptor() {
		return fieldDescriptor;
	}
	
}
