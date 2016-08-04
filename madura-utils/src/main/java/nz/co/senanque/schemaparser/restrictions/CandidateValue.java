package nz.co.senanque.schemaparser.restrictions;

public class CandidateValue {
	
	private final String value;
	private final boolean result;
	private final Restriction restriction;

	public CandidateValue(String value, boolean result, Restriction restriction) {
		this.value = value;
		this.result = result;
		this.restriction = restriction;
	}

	public String getValue() {
		return value;
	}

	public boolean isResult() {
		return result;
	}

	public Restriction getRestriction() {
		return restriction;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(restriction.getClass().getSimpleName());
		sb.append(':');
		sb.append(result);
		sb.append(':');
		sb.append(value);
		return sb.toString();
	}

}
