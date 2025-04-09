package adc.dxp.rest.api.application.data;

import java.util.Optional;

import org.apache.commons.text.StringEscapeUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.liferay.journal.model.JournalArticle;

import adc.dxp.rest.api.application.data.enumeration.ChangeTagType;
import adc.dxp.rest.api.application.data.vo.UserVO;
import adc.dxp.rest.api.application.utils.Constants;
import adc.dxp.rest.api.application.utils.ContentType;
import adc.dxp.rest.api.application.utils.ImageJSON;
import adc.dxp.rest.api.application.utils.TransformUtils;

/**
 * 
 * @author ana.cavadas
 *
 */
public class TellaStory extends BaseContent {

	private String previewImage;
	private String text;
	private String contentTellaStory;
	private String story;
	private String author;
	private String AuthorEmail;
	private UserVO user;
	
	public TellaStory(JournalArticle article, String languageId) {
		super(article, Optional.of(languageId == null ? Constants.DEFAULT_VALUE_LANGUAGE : languageId), ContentType.TELL_A_STORY);
		
		if (getAttributes().get("PreviewImage") != null && !(getAttributes().get("PreviewImage").getValue().isEmpty())) {
			String imageJson = getAttributes().get("PreviewImage").getValue();
			
			String imageJsonOut = StringEscapeUtils.unescapeJava(imageJson);
			
			ObjectMapper mapper = new ObjectMapper();
			try {
				ImageJSON obj = mapper.readValue(imageJsonOut, ImageJSON.class);
				String groupId = obj.getGroupId();
				String uuid = obj.getUuid();
				previewImage = "/c/document_library/get_file?uuid=" + uuid + "&groupId=" + groupId;
				
			} catch (JsonMappingException e) {
				// TODO luis.correia
				e.printStackTrace();
			} catch (JsonProcessingException e) {
				// TODO luis.correia
				e.printStackTrace();
			}
		}
		
		if (getAttributes().get("story") != null) {
			story = TransformUtils.updateTags(getAttributes().get("story").getValue(), ChangeTagType.WEB_CONTENT_BODY_IMAGE);
		}
		
		if (getAttributes().get("author") != null) {
			author = getAttributes().get("author").getValue();
		}
		
		if (getAttributes().get("AuthorEmail") != null) {
			AuthorEmail = getAttributes().get("AuthorEmail").getValue();
		}
		
	}

	// getters + setters
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getContentTellaStory() {
		return contentTellaStory;
	}

	public void setContentTellaStory(String contentTellaStory) {
		this.contentTellaStory = contentTellaStory;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getAuthorEmail() {
		return AuthorEmail;
	}

	public void setAuthorEmail(String AuthorEmail) {
		this.AuthorEmail = AuthorEmail;
	}

	public UserVO getUser() {
		return user;
	}

	public void setUser(UserVO user) {
		this.user = user;
	}

	public String getStory() {
		return story;
	}

	public void setStory(String story) {
		this.story = story;
	}

	public String getPreviewImage() {
		return previewImage;
	}

	public void setPreviewImage(String previewImage) {
		this.previewImage = previewImage;
	}
}
