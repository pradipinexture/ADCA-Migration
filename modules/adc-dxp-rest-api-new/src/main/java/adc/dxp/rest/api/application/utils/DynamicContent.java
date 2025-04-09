package adc.dxp.rest.api.application.utils;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;


@XmlAccessorType(XmlAccessType.NONE)
public class DynamicContent implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@XmlAttribute(name = "language-id")
	private String _languageId;
	
	@XmlValue
	private String __cdata;
	 
	 
	// getters + setters
	 
	public String get__cdata() {
		return __cdata;
	}
	
	public void set__cdata(String __cdata) {
		this.__cdata = __cdata;
	}
	
	public String get_languageId() {
		return _languageId;
	}
	
	public void set_languageId(String _languageId) {
		this._languageId = _languageId;
	}
	
	@Override
	public String toString() {
		return "DynamicContent [_languageId=" + _languageId + ", __cdata=" + __cdata + "]";
	}
	
	
	

}
