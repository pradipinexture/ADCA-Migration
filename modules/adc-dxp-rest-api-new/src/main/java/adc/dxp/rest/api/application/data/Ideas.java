package adc.dxp.rest.api.application.data;

import com.fasterxml.jackson.annotation.*;

import java.io.Serializable;
import java.util.List;

public class Ideas implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
    private List<Idea> ideas;

    @JsonProperty("Ideas")
    public List<Idea> getIdeas() { return ideas; }
    @JsonProperty("Ideas")
    public void setIdeas(List<Idea> value) { this.ideas = value; }
}
