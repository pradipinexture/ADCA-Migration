package adc.dxp.rest.api.application.data;

import com.fasterxml.jackson.annotation.*;

import java.io.Serializable;
import java.util.List;

public class Posts implements Serializable {
	private static final long serialVersionUID = 1L;
	private List<SocialMedia> posts;
	private Object cbmHeader;

    @JsonProperty("Posts")
    public List<SocialMedia> getPosts() { return posts; }
    @JsonProperty("Posts")
    public void setPosts(List<SocialMedia> value) { this.posts = value; }

    @JsonProperty("CBMHeader")
    public Object getCBMHeader() { return cbmHeader; }
    @JsonProperty("CBMHeader")
    public void setCBMHeader(Object value) { this.cbmHeader = value; }
}
