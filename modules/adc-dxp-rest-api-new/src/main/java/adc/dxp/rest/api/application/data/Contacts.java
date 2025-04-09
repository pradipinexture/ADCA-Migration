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
@XmlRootElement(name="Contacts")
public class Contacts  extends BaseContent implements Comparable<Contacts> {

	private String link;
	private int relevance;
	private Ratings ratings;
	
	public Contacts() {
	}
	
	public Contacts(JournalArticle article, String languageId) {
		super(article, Optional.of(languageId == null ? Constants.DEFAULT_VALUE_LANGUAGE : languageId), ContentType.QUICK_LINK);
		
		if (getAttributes().get("ContactsLink") != null) {
			link = getAttributes().get("ContactsLink").getValue();
		}
		
	}
 
    @Override
    public int compareTo(Contacts contact) {
        return contact.getTitle().compareTo(this.getTitle());
    }

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
}