package nz.co.senanque.schemaparser.restrictions;

public class CandidateValue {
	
	private final String value;
	private final boolean result;

	public CandidateValue(String value, boolean result) {
		this.value = value;
		this.result = result;
	}

	public String getValue() {
		return value;
	}

	public boolean isResult() {
		return result;
	}

}
