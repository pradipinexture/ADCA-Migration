package com.liferaysavvy.employee.rest.application.rest.application.utils;


public enum ContentType {

	//WEB CONTENT
	NEWS(Constants.NEWS_TYPE, Constants.STRUCTURE_NEWS_NAME_EN),
	NEWSPAPER(Constants.NEWSPAPER_TYPE, Constants.STRUCTURE_NEWSPAPER_NAME_EN),
	HIGHLIGHT(Constants.HIGHLIGHT_TYPE, Constants.STRUCTURE_HIGHLIGHT_NAME_EN),
	TELL_A_STORY(Constants.TELL_A_STORY_TYPE, Constants.STRUCTURE_TELL_A_STORY_NAME_EN),
	ANNOUNCEMENT(Constants.ANNOUNCEMENT_TYPE, Constants.STRUCTURE_ANNOUNCEMENT_NAME_EN),
	MEDIA(Constants.MEDIA_TYPE, Constants.STRUCTURE_MEDIA_NAME_EN),
	PROMOTION(Constants.PROMOTIONS_TYPE, Constants.STRUCTURE_PROMOTIONS_EN),
	QUICK_LINK(Constants.QUICK_LINKS_TYPE, Constants.STRUCTURE_QUICK_LINKS_EN),
	CONTACT(Constants.CONTACTS_TYPE, Constants.STRUCTURE_CONTACTS_EN),
	KNOWLEDGE_SHARING(Constants.KNOWLEDGE_SHARING_TYPE, Constants.STRUCTURE_KNOWLEDGE_SHARING_NAME_EN),

	EVENT(Constants.EVENT_TYPE),
	DOCUMENT(Constants.DOCUMENT_TYPE),
	;


	private String value;
	private String structure;

	private ContentType(String value) {
		this.value = value;
	}

	private ContentType(String value, String structure) {
		this.value = value;
		this.structure = structure;
	}

	public static com.liferaysavvy.employee.rest.application.rest.application.utils.ContentType getEnumByValue(String type) {
		for (com.liferaysavvy.employee.rest.application.rest.application.utils.ContentType e : com.liferaysavvy.employee.rest.application.rest.application.utils.ContentType.values()) {
			if (e.getValue().equalsIgnoreCase(type)) {
				return e;
			}
		}
		return null;
	}

	// getters + setters

	public void setValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public String getStructure() {
		return structure;
	}

	public void setStructure(String structure) {
		this.structure = structure;
	}




}
