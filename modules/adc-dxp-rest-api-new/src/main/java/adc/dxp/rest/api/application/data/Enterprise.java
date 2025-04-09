package adc.dxp.rest.api.application.data;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Enterprise {
    private String name;
    private String url;
    private String logo;
    
    @JsonProperty("Name")
    public String getName() { return name; }
    @JsonProperty("Name")
    public void setName(String value) { this.name = value; }
    
    @JsonProperty("URL")
    public String getUrl() { return url; }
    @JsonProperty("URL")
    public void setUrl(String value) { this.url = value; }
    
    @JsonProperty("Logo")
    public String getLogo() { return logo; }
    @JsonProperty("Logo")
    public void setLogo(String value) { this.logo = value; }
}
