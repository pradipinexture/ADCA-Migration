package adc.dxp.rest.api.application.data;

import com.fasterxml.jackson.annotation.*;

import java.util.Date;

public class SocialMedia {
    private String title;
    private String socialMediaName;
    private String type;
	private String content;
	private String thumbnail;
    private Enterprise enterprise;
    private Date contentDate;
	private String id;
	private String url;

    @JsonProperty("Title")
    public String getTitle() { return title; }
    @JsonProperty("Title")
    public void setTitle(String value) { this.title = value; }

    @JsonProperty("SocialMediaName")
    public String getSocialMediaName() { return socialMediaName; }
    @JsonProperty("SocialMediaName")
    public void setSocialMediaName(String value) { this.socialMediaName = value; }

    @JsonProperty("Type")
    public String getType() { return type; }
    @JsonProperty("Type")
    public void setType(String value) { this.type = value; }
	
	@JsonProperty("Content")
    public String getContent() { return content; }
    @JsonProperty("Content")
    public void setContent(String value) { this.content = value; }
	
	@JsonProperty("Thumbnail")
    public String getThumbnail() { return thumbnail; }
    @JsonProperty("Thumbnail")
    public void setThumbnail(String value) { this.thumbnail = value; }

    @JsonProperty("ContentDate")
    public Date getContentDate() { return contentDate; }
    @JsonProperty("ContentDate")
    public void setContentDate(Date value) { this.contentDate = value; }

    @JsonProperty("Enterprise")
    public Enterprise getEnterprise() { return enterprise; }
    @JsonProperty("Enterprise")
    public void setEnterprise(Enterprise value) { this.enterprise = value; }
	
	@JsonProperty("Id")
    public String getId() { return id; }
    @JsonProperty("Id")
    public void setId(String value) { this.id = value; }
	
	@JsonProperty("URL")
    public String getURL() { return url; }
    @JsonProperty("URL")
    public void setURL(String value) { this.url = value; }
}