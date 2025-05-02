package adc.dxp.rest.api.application.utils;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class DynamicElement implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@XmlAttribute(name = "name")
	private String name;
	
	@XmlAttribute(name = "type")
	private String type;
		
	@XmlAttribute(name = "index-type")
	private String indexType;
	
	@XmlAttribute(name = "instance-id")
	private String instanceId;
	
	@XmlElement(name = "dynamic-content")
	private DynamicContent dynamicContent;

	@XmlElement(name = "dynamic-element")
	private List<DynamicElement> dynamicElementList;

	public List<DynamicElement> getDynamicElementList() {
		return dynamicElementList;
	}
	public void setDynamicElementList(List<DynamicElement> dynamicElementList) {
		this.dynamicElementList = dynamicElementList;
	}

	@Override
	public String toString() {
		return "DynamicElement [name=" + name + ", type=" + type + ", indexType=" + indexType + ", instanceId="
				+ instanceId + ", dynamicContent=" + dynamicContent + "]";
	}
	
	
	// getters + setters 
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getIndexType() {
		return indexType;
	}

	public void setIndexType(String indexType) {
		this.indexType = indexType;
	}

	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	public DynamicContent getDynamicContent() {
		return dynamicContent;
	}

	public void setDynamicContent(DynamicContent dynamicContent) {
		this.dynamicContent = dynamicContent;
	}
	
	
	
}
