package adc.dxp.rest.api.application.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.text.StringEscapeUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.liferay.journal.model.JournalArticle;

import adc.dxp.rest.api.application.data.enumeration.ChangeTagType;
import adc.dxp.rest.api.application.utils.Constants;
import adc.dxp.rest.api.application.utils.ContentType;
import adc.dxp.rest.api.application.utils.ImageJSON;
import adc.dxp.rest.api.application.utils.TransformUtils;

/**
 * @author luis.correia
 *
 */
@XmlRootElement(name="Promotion")
public class Promotion extends BaseContent {

	private String previewImage;
	private String companyDescription;
	private String description;
	private Date startDate;
	private Date endDate;
	private String link;
	private String location;
	private String locationLink;
	private String body;
	
	public Promotion() {
	}
	
	public Promotion(JournalArticle article, String languageId) {
		super(article, Optional.of(languageId == null ? Constants.DEFAULT_VALUE_LANGUAGE : languageId), ContentType.PROMOTION);

		if (getAttributes().get(Constants.STRUCTURE_NEWS_FIELD_BODY) != null) {
			body = TransformUtils.updateTags(getAttributes().get(Constants.STRUCTURE_NEWS_FIELD_BODY).getValue(), ChangeTagType.WEB_CONTENT_BODY_IMAGE);
		}

		if (getAttributes().get("CompanyDescription") != null) {
			companyDescription = TransformUtils.updateTags(getAttributes().get("CompanyDescription").getValue(), ChangeTagType.WEB_CONTENT_BODY_IMAGE);
		}
		
		if (getAttributes().get("Description") != null) {
			description = getAttributes().get("Description").getValue();
		}
		
		if (getAttributes().get("StartDate") != null) {
			try {
				startDate = new SimpleDateFormat("yyyy-MM-dd").parse(getAttributes().get("StartDate").getValue());
			} catch (ParseException e) {
				// TODO luis.correia
				e.printStackTrace();
			}
		}
		
		if (getAttributes().get("EndDate") != null) {
			try {
				endDate = new SimpleDateFormat("yyyy-MM-dd").parse(getAttributes().get("EndDate").getValue());
			} catch (ParseException e) {
				// TODO luis.correia
				e.printStackTrace();
			}
		}
		
		if (getAttributes().get("Link") != null) {
			link = getAttributes().get("Link").getValue();
		}
		if (getAttributes().get("Location") != null) {
			location = getAttributes().get("Location").getValue();
		}
		
		if (getAttributes().get("LocationLink") != null) {
			locationLink = getAttributes().get("LocationLink").getValue();
		}
		
//		if (getAttributes().get("PreviewImage") != null) {
//			String imageJson = getAttributes().get("PreviewImage").getValue();
//
//			String imageJsonOut = StringEscapeUtils.unescapeJava(imageJson);
//
//			ObjectMapper mapper = new ObjectMapper();
//			try {
//				ImageJSON obj = mapper.readValue(imageJsonOut, ImageJSON.class);
//				String groupId = obj.getGroupId();
//				String uuid = obj.getUuid();
//				previewImage = "/c/document_library/get_file?uuid=" + uuid + "&groupId=" + groupId;
//
//			} catch (JsonMappingException e) {
//				// TODO luis.correia
//				e.printStackTrace();
//			} catch (JsonProcessingException e) {
//				// TODO luis.correia
//				e.printStackTrace();
//			}
//		}
	
	}	

	//getters + setters
	
	public String getImage() {
		return previewImage;
	}

	public void setImage(String image) {
		this.previewImage = image;
	}

	public String getCompanyDescription() {
		return companyDescription;
	}

	public void setCompanyDescription(String companyDescription) {
		this.companyDescription = companyDescription;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getLocationLink() {
		return locationLink;
	}

	public void setLocationLink(String locationLink) {
		this.locationLink = locationLink;
	}
}