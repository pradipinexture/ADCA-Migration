package adc.dxp.rest.api.application.utils;

import java.io.Serializable;

public class DynamicAttribute implements Serializable {

	private static final long serialVersionUID = 1L;
	
	
	private String attribute;
	private String value;
	
	public DynamicAttribute() {
		
	}
	
	public DynamicAttribute(String attribute, String value) {
		this.attribute = attribute;
		this.value = value;
	}
	
	
	
	//getters + setters 
	
	public String getAttribute() {
		return attribute;
	}
	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	
	
	
}
