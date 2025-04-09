package adc.dxp.rest.api.application.data.enumeration;

public enum ChangeTagType {

	WEB_CONTENT_BODY_IMAGE("img"), 
	WEB_CONTENT_LINE("hr");

	private String tag;

	ChangeTagType(String tag) {
		this.tag = tag;
	}

	public String getTag() {
		return tag;
	}

}