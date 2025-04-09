package adc.dxp.rest.api.application.data;

import com.fasterxml.jackson.annotation.*;

import adc.dxp.rest.api.application.data.vo.UserData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.util.Date;

public class Idea {
    private String title;
    private String description;
    private String username;
    private OffsetDateTime date;
    private String url;
    private long likesCount;
    private long disikesCount;
    private long commentsCount;
    private String newIndicator;
	private String email;
	private UserData user;
	
	
    @JsonProperty("Title")
    public String getTitle() { return title; }
    @JsonProperty("Title")
    public void setTitle(String value) { this.title = value; }

    @JsonProperty("Description")
    public String getDescription() { return description; }
    @JsonProperty("Description")
    public void setDescription(String value) { this.description = value; }

    @JsonProperty("Username")
    public String getUsername() { return username; }
    @JsonProperty("Username")
    public void setUsername(String value) { this.username = value; }

    @JsonProperty("Date")
    public OffsetDateTime getDate() { return date; }
    @JsonProperty("Date")
    public void setDate(OffsetDateTime value) { this.date = value; }

    @JsonProperty("URL")
    public String getURL() { return url; }
    @JsonProperty("URL")
    public void setURL(String value) { this.url = value; }

    @JsonProperty("LikesCount")
    public long getLikesCount() { return likesCount; }
    @JsonProperty("LikesCount")
    public void setLikesCount(long value) { this.likesCount = value; }

    @JsonProperty("DisikesCount")
    public long getDisikesCount() { return disikesCount; }
    @JsonProperty("DisikesCount")
    public void setDisikesCount(long value) { this.disikesCount = value; }

    @JsonProperty("CommentsCount")
    public long getCommentsCount() { return commentsCount; }
    @JsonProperty("CommentsCount")
    public void setCommentsCount(long value) { this.commentsCount = value; }
    
    @JsonProperty("NewIndicator")
    public String getNewIndicator() { return newIndicator; }
    @JsonProperty("NewIndicator")
    public void setNewIndicator(String value) { this.newIndicator = value; }

    @JsonProperty("Email")
    public String getEmail() { return email; }
    @JsonProperty("Email")
    public void setEmail(String value) { this.email = value; }
    
    public UserData getUser() { return user; }
    
    public void setUser(UserData value) { this.user = value; }
    
    public String getParserDate() {
    	try {
			SimpleDateFormat sourceDateFormat = new SimpleDateFormat("yyyy-MM-DD'T'HH:mm:ss");
			Date originalDate = sourceDateFormat.parse(this.date.toString());
			
			SimpleDateFormat targetDateFormat = new SimpleDateFormat("d MMM. yyyy");
			return targetDateFormat.format(originalDate);
		} catch (ParseException e) {
			return this.date.toString();
		}
    }
    
    
}