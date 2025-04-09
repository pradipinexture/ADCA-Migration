package adc.dxp.rest.api.application.utils;

public enum VocabularyType {

	NEWS(Constants.VOCABULARY_NEWS_NAME_EN, Constants.NEWS_TYPE),
	NEWSPAPER(Constants.VOCABULARY_NEWSPAPER_NAME_EN, Constants.NEWSPAPER_TYPE),
	TELL_A_STORY(Constants.VOCABULARY_TELL_A_STORY_NAME_EN, Constants.TELL_A_STORY_TYPE),
	ANNOUNCEMENT(Constants.VOCABULARY_ANNOUNCEMENT_NAME_EN, Constants.ANNOUNCEMENT_TYPE),
	MEDIA(Constants.VOCABULARY_MEDIA_NAME_EN, Constants.MEDIA_TYPE),
	PROMOTION(Constants.VOCABULARY_PROMOTIONS_EN, Constants.PROMOTIONS_TYPE),
	QUICK_LINK(Constants.VOCABULARY_QUICK_LINKS_EN, Constants.QUICK_LINKS_TYPE),
	CONTACT(Constants.VOCABULARY_CONTACTS_EN, Constants.CONTACTS_TYPE),
	FAQS(Constants.VOCABULARY_FAQS_EN, Constants.FAQS_TYPE),
	KNOWLEDGE_SHARING(Constants.VOCABULARY_KNOWLEDGE_SHARING_EN, Constants.KNOWLEDGE_SHARING_TYPE),
	;
	
	
	private String value;
	private String type;
	
	private VocabularyType(String value, String type) {
		this.value = value;
		this.type = type;
	}
	
	
	public static VocabularyType getEnumByValue(String type) {
        for (VocabularyType e : VocabularyType.values()) {
            if (e.getValue().equalsIgnoreCase(type)) {
                return e;
            }
        }
        return null;
    }
	

	public static VocabularyType getEnumByType(String type) {
        for (VocabularyType e : VocabularyType.values()) {
            if (e.getType().equalsIgnoreCase(type)) {
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


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}

    
	
}
