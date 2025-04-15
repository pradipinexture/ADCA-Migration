package adc.dxp.rest.api.application.utils;

import java.util.HashMap;
import java.util.Map;

public class Constants {
	
	//Announcement
	public static final String STRUCTURE_ANNOUNCEMENT_NAME_EN = "Announcement";
	public static final String VOCABULARY_ANNOUNCEMENT_NAME_EN = "Announcement";
	public static final String ANNOUNCEMENT_TYPE = "announcement";
	
	//Media
	public static final String STRUCTURE_MEDIA_NAME_EN = "Media";
	public static final String VOCABULARY_MEDIA_NAME_EN = "Media";
	public static final String MEDIA_TYPE = "media";
	
	public static final String STRUNCTURE_FIELD_NAME_VIDEO = "Video";
	public static final String STRUNCTURE_FIELD_NAME_GROUP_ID = "groupId";
	public static final String STRUNCTURE_FIELD_NAME_UUID = "uuid";
	
	public static final String NOTIFICATION_TYPE_WORKFLOW = "com_liferay_portal_workflow_task_web_portlet_MyWorkflowTaskPortlet";
	
	public static final String STRUNCTURE_FIELD_NAME_IMAGE = "PreviewImage";
	public static final String STRUNCTURE_FIELD_NAME_SHOW_IMAGE = "ShowInHomepage";
	public static final String STRUNCTURE_FIELD_NAME_CLASS_PK = "classPK";
	public static final String STRUNCTURE_FIELD_NAME_SUMMARY = "Summary";
	
	
	public static final String IMAGE_URL = "/c/document_library/get_file?uuid=%s&groupId=%d";
	public static final String IMAGE_URL_FOLDER = "/documents/%d/0/%s/%s?imagePreview=1";
	
	//Highlight
	public static final String STRUCTURE_HIGHLIGHT_NAME_EN = "Highlight";
	public static final String VOCABULARY_HIGHLIGHT_NAME_EN = "Highlight";
	public static final String HIGHLIGHT_TYPE = "highlight";
	
	//News
	public static final String STRUCTURE_NEWS_NAME_EN = "News";
	public static final String STRUCTURE_NEWS_FIELD_BODY = "Body";
	public static final String STRUCTURE_NEWS_FIELD_ACHIEVEMENT = "Achievement";
	public static final String STRUCTURE_NEWS_FIELD_LOCATION = "Location";
	public static final String VOCABULARY_NEWS_NAME_EN = "News";
	public static final String NEWS_TYPE = "news";

	//Newspaper
	public static final String STRUCTURE_NEWSPAPER_NAME_EN = "Newspaper";
	public static final String STRUCTURE_NEWSPAPER_FIELD_BODY = "Body";
	public static final String VOCABULARY_NEWSPAPER_NAME_EN = "Newspaper";
	public static final String NEWSPAPER_TYPE = "newspaper";

	public static final String STRUCTURE_NEWSPAPER_NEWSPAPER = "Newspaper";
	
	//TellaStory
	public static final String STRUCTURE_TELL_A_STORY_NAME_EN = "Tell a Story";
	public static final String VOCABULARY_TELL_A_STORY_NAME_EN = "Tell a Story";
	public static final String TELL_A_STORY_TYPE = "tell-a-story";
	
	//Promotions
	public static final String STRUCTURE_PROMOTIONS_EN = "Promotion";
	public static final String VOCABULARY_PROMOTIONS_EN = "Promotion";
	public static final String PROMOTIONS_TYPE="promotion";
	
	//QuickLinks
	public static final String STRUCTURE_QUICK_LINKS_EN = "Quick Link";	
	public static final String VOCABULARY_QUICK_LINKS_EN = "Quick Link";
	public static final String QUICK_LINKS_TYPE="quick-link";
	
	//Contacts
	public static final String STRUCTURE_CONTACTS_EN = "Contact";	
	public static final String VOCABULARY_CONTACTS_EN = "Contact";
	public static final String CONTACTS_TYPE="contact";
	
	//Faqs
	public static final String VOCABULARY_FAQS_EN = "Faqs";
	public static final String FAQS_TYPE="faqs";
	public static final String STRUCTURE_FAQ_NAME_EN = "Faqs";
	public static final String STRUCTURE_FAQ_FIELD_QUESTION = "textQuestion";
	public static final String STRUCTURE_FAQ_FIELD_CATEGORY = "category";
	public static final String STRUCTURE_FAQ_FIELD_ANSWER = "textAnswer";
	
	//Director Message
	public static final String STRUCTURE_MANAGEMENT_MESSAGE_NAME_EN = "Management Message";
	public static final String VOCABULARY_MANAGEMENT_MESSAGE_NAME_EN = "Management Message";
	public static final String DIRECTOR_MESSAGE_TYPE = "management-message";
	
	//Knowledge Transfer	
	public static final String STRUCTURE_KNOWLEDGE_SHARING_NAME_EN = "Knowledge Sharing";
	public static final String VOCABULARY_KNOWLEDGE_SHARING_EN = "Knowledge Sharing";
	public static final String KNOWLEDGE_SHARING_TYPE="knowledge-sharing";
	
	public static final String STRUCTURE_KNOWLEDGE_FIELD_DOCUMENTS = "Documents";
	public static final String STRUCTURE_KNOWLEDGE_FIELD_POLICY = "Policy";
	
	public static final String EVENT_TYPE = "event";
	
	public static final String DOCUMENT_TYPE = "document";	
	
	public static final String HEADER_GROUP_ID = "groupId";
	public static final String HEADER_LANGUAGE_ID = "languageId";
	
	public static final String ACCEPT_LANGUAGE = "Accept-Language";
	public static final String ACCEPT_LANGUAGE_AR = "ar-AE";
	public static final String ACCEPT_LANGUAGE_EN = "en-US";
	
	//Contact
	public static final String STRUCTURE_CONTACT_NAME_EN = "Contact2";
	public static final String VOCABULARY_CONTACT_NAME_EN = "Contact";
	public static final String CONTACT_TYPE = "contact";
	
	
	public static final String CLASS_NAME = "com.liferay.journal.model.JournalArticle";
	
	//Default values
	public static final String DEFAULT_VALUE_LANGUAGE = "en";
	
	//Calendar Booking Status
	public static final int CALENDAR_BOOKING_STATUS_PENDING = 0;
	public static final int CALENDAR_BOOKING_STATUS_APPROVED = 1;
	public static final int CALENDAR_BOOKING_STATUS_DENIED = 2;
	public static final int CALENDAR_BOOKING_STATUS_DRAFT = 3;
	public static final int CALENDAR_BOOKING_STATUS_ACCEPT = 10;
	public static final int CALENDAR_BOOKING_STATUS_MAYBE = 11;
	public static final int CALENDAR_BOOKING_STATUS_DECLINE = 12;
	public static final String EXPRESSION_SEARCH_TITLE = "%%%s%%";
	public static final String EXPRESSION_SEARCH_DESCRIPTION = "%%%s%%";
	public static final String EXPRESSION_SEARCH_CONTENT = "%%<![CDATA[%%%s%%]]%%";
	public static final String EXPRESSION_SEARCH_LOCATION = "%%%s%%";
	public static final String EXPRESSION_SEARCH_TAG_DESCRIPTION = "%%<Description language-id=\"%%\">%%%s%%</Description>%%";
	public static final String EXPRESSION_SEARCH_TAG_TITLE = "%%<Title language-id=\"%%\">%%%s%%</Title>%%";
	public static final String EXPRESSION_SEARCH_TAG_LOCATION = "%%%s%%";
	
	//ENTITIES
	public static final String CALENDAR_ENTITY = "com.liferay.calendar.model.Calendar";
	
	
	//Style Classes
	public static final Map<String, String> STYLE_FOR = new HashMap<String, String>();
	public static final String STRUCTURE_DIRECTOR_MESSAGE_PROP_NAME = "structure-id-director-message";

	static {
		STYLE_FOR.put("WEB_CONTENT_BODY_IMAGE", "media_img_popup");
		STYLE_FOR.put("WEB_CONTENT_LINE", "line-for-web-content");
	}

	/**
	 * HTTP header for company ID
	 */
	public static final String HEADER_COMPANY_ID = "X-Liferay-Company-Id";

	/**
	 * API base path
	 */
	public static final String API_BASE_PATH = "/adc-dxp-services";
	
}
