package adc.dxp.rest.api.application.utils;

public class LookupValue {
	
	private String label;
	private long value;
	
	public LookupValue() {}
	
	
	public LookupValue(String label, long value) {
		this.value = value;
		this.label = label;
	}
	
	
	//getters + setters
	
	public String getLabel() {
		return label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	public long getValue() {
		return value;
	}
	
	public void setValue(long value) {
		this.value = value;
	}
	
}
