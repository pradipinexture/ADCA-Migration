package adc.dxp.rest.api.application.utils;

import java.io.Serializable;

public class ImageJSON implements Serializable {
	
	private static final long serialVersionUID = -6018977472253400237L;
	
	private String classPK;
	private String groupId;
	private String name;
	private String alt;
	private String title;
	private String type;
	private String uuid;
	private String fileEntryId;
	private String resourcePrimKey;
	
	
	// getters + setters
	
	public String getClassPK() {
		return classPK;
	}
	
	public void setClassPK(String classPK) {
		this.classPK = classPK;
	}
	
	public String getGroupId() {
		return groupId;
	}
	
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getAlt() {
		return alt;
	}
	
	public void setAlt(String alt) {
		this.alt = alt;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getUuid() {
		return uuid;
	}
	
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	public String getFileEntryId() {
		return fileEntryId;
	}
	
	public void setFileEntryId(String fileEntryId) {
		this.fileEntryId = fileEntryId;
	}
	
	public String getResourcePrimKey() {
		return resourcePrimKey;
	}
	
	public void setResourcePrimKey(String resourcePrimKey) {
		this.resourcePrimKey = resourcePrimKey;
	}
	
}