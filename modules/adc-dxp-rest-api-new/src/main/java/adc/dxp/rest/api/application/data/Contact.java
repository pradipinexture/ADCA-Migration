package adc.dxp.rest.api.application.data;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.liferay.journal.model.JournalArticle;

import adc.dxp.rest.api.application.data.enumeration.ChangeTagType;
import adc.dxp.rest.api.application.utils.Constants;
import adc.dxp.rest.api.application.utils.ContentType;
import adc.dxp.rest.api.application.utils.TransformUtils;

public class Contact extends BaseContent {
	
//	private String body;
	
	private List<String> sections = new ArrayList<String>();
	private List<Map<String,List<String>>> numbers = new ArrayList<>();
	//private List<Map<String,String>> numbers = new ArrayList<>();
	
	public Contact(JournalArticle article, String languageId) {
		
		super(article, Optional.of(languageId == null ? Constants.DEFAULT_VALUE_LANGUAGE : languageId), ContentType.ANNOUNCEMENT);
		
		this.setSections(TransformUtils.getLocalizableField(article, languageId, "Sections"));
		this.setNumbers(TransformUtils.getContactNumbers(article, languageId));
	}

	public List<String> getSections() {
		return sections;
	}

	void setSections(List<String> sections) {
		this.sections = sections;
	}

	/*public List<Map<String,String>> getNumbers() {
		return numbers;
	}

	private void setNumbers(List<Map<String,String>> numbers) {
		this.numbers = numbers;
	}*/
	
	public List<Map<String,List<String>>> getNumbers() {
		return numbers;
	}

	private void setNumbers(List<Map<String,List<String>>> numbers) {
		this.numbers = numbers;
	}

//	public String getBody() {
//		return body;
//	}
//
//	public void setBody(String body) {
//		this.body = body;
//	}

}
