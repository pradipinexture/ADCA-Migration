package adc.dxp.rest.api.application.data;

import java.util.Optional;

import adc.dxp.rest.api.application.data.BaseContent;
import com.liferay.journal.model.JournalArticle;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import adc.dxp.rest.api.application.data.enumeration.ChangeTagType;
import adc.dxp.rest.api.application.utils.Constants;
import adc.dxp.rest.api.application.utils.ContentType;
import adc.dxp.rest.api.application.utils.TransformUtils;


/**
 * @author ana.cavadas
 *
 */
public class News extends BaseContent {
	
	/**
	 * logging instance
	 */
	private static Log _log = LogFactoryUtil.getLog(News.class);
	
	private String location;
	private String body;
	private boolean achievement;
	
	public News() {}
	
	public News(JournalArticle article, String languageId) {
		super(article, Optional.of(languageId == null ? Constants.DEFAULT_VALUE_LANGUAGE : languageId), ContentType.NEWS);

		if (getAttributes() != null) {
			
			if (getAttributes().get(Constants.STRUCTURE_NEWS_FIELD_BODY) != null) {
				body = TransformUtils.updateTags(getAttributes().get(Constants.STRUCTURE_NEWS_FIELD_BODY).getValue(), ChangeTagType.WEB_CONTENT_BODY_IMAGE);
			}
			
			if (getAttributes().get(Constants.STRUCTURE_NEWS_FIELD_LOCATION) != null) {
				location = getAttributes().get(Constants.STRUCTURE_NEWS_FIELD_LOCATION).getValue();
			}
			
			if (getAttributes().get(Constants.STRUCTURE_NEWS_FIELD_ACHIEVEMENT) != null) {
				String achievementParam = getAttributes().get(Constants.STRUCTURE_NEWS_FIELD_ACHIEVEMENT).getValue();
				achievement = achievementParam.compareTo("true") == 0 ? true: false;
			}
		}
	}
		
	

	//getters + setters
	
	public boolean isAchievement() {
		return achievement;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public void setAchievement(boolean achievement) {
		this.achievement = achievement;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	
	//equals and hashcode
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (achievement ? 1231 : 1237);
		result = prime * result + ((body == null) ? 0 : body.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		News other = (News) obj;
		if (achievement != other.achievement)
			return false;
		if (body == null) {
			if (other.body != null)
				return false;
		} else if (!body.equals(other.body))
			return false;
		return true;
	}
	
}