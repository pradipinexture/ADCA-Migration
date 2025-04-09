package adc.dxp.rest.api.application.data;

import java.util.Optional;

import com.liferay.journal.model.JournalArticle;

import adc.dxp.rest.api.application.data.enumeration.ChangeTagType;
import adc.dxp.rest.api.application.utils.Constants;
import adc.dxp.rest.api.application.utils.ContentType;
import adc.dxp.rest.api.application.utils.TransformUtils;

/**
 * 
 * @author ana.cavadas
 *
 */
public class GenericContent extends BaseContent {
	
	private String body;
	
	public GenericContent(JournalArticle article, String languageId, ContentType type) {
		
		super(article, Optional.of(languageId == null ? Constants.DEFAULT_VALUE_LANGUAGE : languageId), type);
		
		if (getAttributes().containsKey("Body")) {
			
			this.setBody(TransformUtils.updateTags(getAttributes().get("Body").getValue(), ChangeTagType.WEB_CONTENT_BODY_IMAGE));
			
		}
		
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

}
