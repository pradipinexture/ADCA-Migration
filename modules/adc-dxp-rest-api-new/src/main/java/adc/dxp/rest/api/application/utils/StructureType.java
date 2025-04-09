package adc.dxp.rest.api.application.utils;

public enum StructureType {
	
	NEWS(Constants.STRUCTURE_NEWS_NAME_EN),
    NEWSPAPER(Constants.STRUCTURE_NEWSPAPER_NAME_EN),
	HIGHLIGHT(Constants.STRUCTURE_HIGHLIGHT_NAME_EN),
	TELL_A_STORY(Constants.STRUCTURE_TELL_A_STORY_NAME_EN),
	ANNOUNCEMENT(Constants.STRUCTURE_ANNOUNCEMENT_NAME_EN),
	MEDIA(Constants.STRUCTURE_MEDIA_NAME_EN),
	PROMOTION(Constants.STRUCTURE_PROMOTIONS_EN),
	QUICK_LINK(Constants.STRUCTURE_QUICK_LINKS_EN),
	CONTACT(Constants.STRUCTURE_CONTACTS_EN),
    KNOWLEDGE_SHARING(Constants.STRUCTURE_KNOWLEDGE_SHARING_NAME_EN),
	;
	
	
	private String value;
	
	private StructureType(String value) {
		this.value = value;
	}
	
	
	public static StructureType getEnumByValue(String type) {
        for (StructureType e : StructureType.values()) {
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
}
