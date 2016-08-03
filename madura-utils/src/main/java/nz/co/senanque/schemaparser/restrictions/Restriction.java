package nz.co.senanque.schemaparser.restrictions;

import java.util.List;

public abstract class Restriction {
	

	public abstract List<CandidateValue> getCandidateValues();
	public abstract String getValue();
	
	public String toString() {
		return this.getClass().getSimpleName()+"="+getValue();
	}
	
}
