package adc.dxp.rest.api.application.data;

import java.util.Optional;

import com.liferay.journal.model.JournalArticle;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import adc.dxp.rest.api.application.data.enumeration.ChangeTagType;
import adc.dxp.rest.api.application.utils.Constants;
import adc.dxp.rest.api.application.utils.ContentType;
import adc.dxp.rest.api.application.utils.TransformUtils;


/**
 * @author 
 *
 */
public class Faqs extends BaseContent {
	
	/**
	 * logging instance
	 */
	private static Log _log = LogFactoryUtil.getLog(Faqs.class);
	
	private String textQuestion;
	private String textAnswer;
	private String categoryName;
	
	public Faqs() {
	}
	
	public Faqs(JournalArticle article, String languageId) {
		
		super(article, Optional.of(languageId == null ? Constants.DEFAULT_VALUE_LANGUAGE : languageId));
		
		if (getAttributes().get(Constants.STRUCTURE_FAQ_FIELD_QUESTION) != null) {
			
			textQuestion = getAttributes().get(Constants.STRUCTURE_FAQ_FIELD_QUESTION).getValue();
		}
		
		if (getAttributes().get(Constants.STRUCTURE_FAQ_FIELD_ANSWER) != null) {
			textAnswer = TransformUtils.updateTags(getAttributes().get(Constants.STRUCTURE_FAQ_FIELD_ANSWER).getValue(), ChangeTagType.WEB_CONTENT_BODY_IMAGE);
		}
		
		if (getAttributes().get(Constants.STRUCTURE_FAQ_FIELD_CATEGORY) != null) {
			categoryName = getAttributes().get(Constants.STRUCTURE_FAQ_FIELD_CATEGORY).getValue();
		}
		
		
	}
		
	

	//getters + setters
	

	

	public String getTextQuestion() {
		return textQuestion;
	}

	public void setTextQuestion(String textQuestion) {
		this.textQuestion = textQuestion;
	}
	

	public String getTextAnswer() {
		return textAnswer;
	}

	public void setTextAnswer(String textAnswer) {
		this.textAnswer = textAnswer;
	}

	public String getCategoryName() {
		return categoryName;
	}


	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}



	
	
	
	
}
