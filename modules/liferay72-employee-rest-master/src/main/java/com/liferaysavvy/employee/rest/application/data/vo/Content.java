package com.liferaysavvy.employee.rest.application.data.vo;

import com.liferaysavvy.employee.rest.application.rest.application.utils.ContentType;

public class Content {

	private ContentType type;

	public Content () {
	}
	
	public Content (ContentType type) {
		this.type = type;
	}

	// getters + setters
	
	public ContentType getType() {
		return type;
	}

	public void setType(ContentType type) {
		this.type = type;
	}
	
	
	
}
