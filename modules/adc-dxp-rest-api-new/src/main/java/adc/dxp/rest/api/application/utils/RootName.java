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
public class RootName implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@XmlElement(name = "Name")
	private String name;
	
	@XmlAttribute(name = "available-locales")
	private String availableLocales;
	
	@XmlAttribute(name = "default-locale")
	private String defaultLocale;
	
	
	
	//getters + setters
	
	public String getAvailableLocales() {
		return availableLocales;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
		return "RootName [name=" + name + ", availableLocales=" + availableLocales + ", defaultLocale=" + defaultLocale
				+ "]";
	}
	
	
	
		
}