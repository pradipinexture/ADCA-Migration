package adc.dxp.rest.api.application.data;

import java.util.Locale;
import java.util.Optional;

import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalServiceUtil;

import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import adc.dxp.rest.api.application.utils.Constants;
import adc.dxp.rest.api.application.utils.ContentType;
import adc.dxp.rest.api.application.utils.StructureType;

public class Highlight extends BaseContent implements Comparable<QuickLinks> {

	private static Log _log = LogFactoryUtil.getLog(Highlight.class);
	
	private String link;
	private boolean isURL;
	private BaseContent relatedContent;
	private String relatedContentUrl;
	
	public Highlight() {
	}
	
	public Highlight(JournalArticle article, String languageId) {
		super(article, Optional.of(languageId == null ? Constants.DEFAULT_VALUE_LANGUAGE : languageId), ContentType.HIGHLIGHT);
		
		isURL = true;
		
		if (getAttributes().get("Url") != null) {
			link = getAttributes().get("Url").getValue();
			isURL = true;
		}
		if (getAttributes().get("WebContent") != null) {
			String webcontentString = getAttributes().get("WebContent").getValue();
			
			webcontentString = webcontentString.replace("\"{", "{");
			webcontentString = webcontentString.replace("}\"", "}");
			
			JSONObject jsonObject = null;
			try {
				jsonObject = JSONFactoryUtil.createJSONObject(webcontentString);
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (jsonObject.get("classPK") != null) {				

				long classPK = Long.valueOf(jsonObject.get("classPK").toString());
				
				JournalArticle webContent = JournalArticleLocalServiceUtil.fetchLatestArticle(classPK, WorkflowConstants.STATUS_APPROVED);
				
				String structureName = webContent.getDDMStructure().getName(Locale.ENGLISH);
				StructureType type = StructureType.getEnumByValue(structureName);
				
				_log.debug("type: " +type);
				
				switch(type) {
					case NEWS:
						relatedContent = new News(webContent, languageId);
						link = "/group/portal/news/detail?id=" + webContent.getResourcePrimKey();
						break;
					case NEWSPAPER:
						relatedContent = new Newspaper(webContent, languageId);
						link = "/group/portal/newspaper/detail?id=" + webContent.getResourcePrimKey();
						break;
					case TELL_A_STORY:
						relatedContent = new TellaStory(webContent, languageId);
						link = "/group/portal/tell-a-story/detail?id=" + webContent.getResourcePrimKey();
						break;
					case MEDIA:					
						relatedContent = new Media(webContent, languageId);	
						link = "/group/portal/media/detail?id=" + webContent.getResourcePrimKey();
						break;
					case PROMOTION:
						relatedContent = new Promotion(webContent, languageId);
						link = "/group/portal/promotion/detail?id=" + webContent.getResourcePrimKey();
						break;
					case QUICK_LINK:
						relatedContent = new QuickLinks(webContent, languageId);
						link = relatedContent.getUrl();
						break;
					case CONTACT:
						relatedContent = new Contacts(webContent, languageId);
						//relatedContentUrl = "/group/portal/media/detail?id=" + webContent.getArticleId();
						break;
					case ANNOUNCEMENT:
						relatedContent = new Announcements(webContent, languageId);
						link = "/group/portal/announcements/detail?id=" + webContent.getResourcePrimKey();
						break;
					default:
						break;
					
				}
				
			}
			
			
		}
		
	}
 
    @Override
    public int compareTo(QuickLinks quickLink) {
        return quickLink.getTitle().compareTo(this.getTitle());
    }

	public String getLink() {
		return this.link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public BaseContent getRelatedContent() {
		return relatedContent;
	}

	public void setRelatedContent(BaseContent relatedContent) {
		this.relatedContent = relatedContent;
	}

	public boolean isURL() {
		return isURL;
	}

	public void setURL(boolean isURL) {
		this.isURL = isURL;
	}
	
}