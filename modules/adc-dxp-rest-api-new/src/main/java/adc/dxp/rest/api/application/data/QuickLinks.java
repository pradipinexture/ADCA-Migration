package adc.dxp.rest.api.application.data;

import java.util.List;
import java.util.Optional;

import javax.xml.bind.annotation.XmlRootElement;
import com.liferay.journal.model.JournalArticle;

import adc.dxp.rest.api.application.utils.Constants;
import adc.dxp.rest.api.application.utils.ContentType;

/**
 * @author luis.correia
 *
 */
@XmlRootElement(name="QuickLinks")
public class QuickLinks  extends BaseContent implements Comparable<QuickLinks> {

	private String link;
	private String fontName;
	private int relevance;
	private Ratings ratings;
	
	public QuickLinks() {
	}
	
	public QuickLinks(JournalArticle article, String languageId) {
		super(article, Optional.of(languageId == null ? Constants.DEFAULT_VALUE_LANGUAGE : languageId), ContentType.QUICK_LINK);
		
		if (getAttributes().get("quickLinksLink") != null) {
			link = getAttributes().get("quickLinksLink").getValue();
		}
		
		if (getAttributes().get("FontName") != null) {
			fontName = getAttributes().get("FontName").getValue();
		}
		
	}
 
    @Override
    public int compareTo(QuickLinks quickLink) {
        return quickLink.getTitle().compareTo(this.getTitle());
    }

    //getters + setters
    
	public String getLink() {
		return this.link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public int getRelevance() {
		return this.relevance;
	}

	public void setRelevance(int relevance) {
		this.relevance = relevance;
	}

	public Ratings getRatings() {
		return this.ratings;
	}

	public void setRatings(Ratings ratings) {
		this.ratings = ratings;
	}

	public String getFontName() {
		return fontName;
	}

	public void setFontName(String fontName) {
		this.fontName = fontName;
	}
	
}