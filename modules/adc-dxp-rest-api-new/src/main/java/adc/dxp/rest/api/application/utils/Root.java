package adc.dxp.rest.api.application.utils;

import java.io.Serializable;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "root")
@XmlAccessorType(XmlAccessType.FIELD)
public class Root implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@XmlElement(name = "dynamic-element")
	private List<DynamicElement> dynamicElementList;
	
	@XmlAttribute(name = "available-locales")
	private String availableLocales;
	
	@XmlAttribute(name = "default-locale")
	private String defaultLocale;
	
	
	
	//getters + setters
		
	public List<DynamicElement> getDynamicElementList() {
		return dynamicElementList;
	}
	public void setDynamicElementList(List<DynamicElement> dynamicElementList) {
		this.dynamicElementList = dynamicElementList;
	}
	public String getAvailableLocales() {
		return availableLocales;
	}
	public void setAvailableLocales(String availableLocales) {
		this.availableLocales = availableLocales;
	}
	public String getDefaultLocale() {
		return defaultLocale;
	}
	public void setDefaultLocale(String defaultLocale) {
		this.defaultLocale = defaultLocale;
	}
	
	@Override
	public String toString() {
		return "Root [dynamicElementList=" + dynamicElementList + ", availableLocales=" + availableLocales
				+ ", defaultLocale=" + defaultLocale + "]";
	}
	
		
}