package adc.dxp.rest.api.application.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.service.DLFileEntryLocalServiceUtil;
import com.liferay.journal.model.JournalArticle;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import adc.dxp.rest.api.application.data.enumeration.ChangeTagType;
import adc.dxp.rest.api.application.utils.Constants;
import adc.dxp.rest.api.application.utils.ContentType;
import adc.dxp.rest.api.application.utils.DynamicAttribute;
import adc.dxp.rest.api.application.utils.TransformUtils;


/**
 * @author ana.cavadas
 *
 */
public class Knowledge extends BaseContent {
	
	/**
	 * logging instance
	 */
	private static Log _log = LogFactoryUtil.getLog(Knowledge.class);
	
	
	private String body;
	private String policy;
	private List<Document> documentList;
	
	public Knowledge() {}
	
	public Knowledge(JournalArticle article, String languageId) {
		super(article, Optional.of(languageId == null ? Constants.DEFAULT_VALUE_LANGUAGE : languageId), ContentType.KNOWLEDGE_SHARING, true);
		
		if (getAttributes() != null) {
			
			if (getAttributes().get(Constants.STRUCTURE_NEWS_FIELD_BODY) != null) {
				body = TransformUtils.updateTags(getAttributes().get(Constants.STRUCTURE_NEWS_FIELD_BODY).getValue(), ChangeTagType.WEB_CONTENT_BODY_IMAGE);
			}
			
			if (getAttributes().get(Constants.STRUCTURE_KNOWLEDGE_FIELD_POLICY) != null) {
				policy = getAttributes().get(Constants.STRUCTURE_KNOWLEDGE_FIELD_POLICY).getValue();
			}
			
		}
		
		
			if (getListAttributes() != null && getListAttributes().get(Constants.STRUCTURE_KNOWLEDGE_FIELD_DOCUMENTS) != null) {	
				documentList = new ArrayList<>();
				
				List<DynamicAttribute> list = getListAttributes().get(Constants.STRUCTURE_KNOWLEDGE_FIELD_DOCUMENTS);
				
				for (DynamicAttribute da: list) {
					
					String webcontentString = da.getValue();
				
				
					_log.info("Knowledge: " + webcontentString);
					
					webcontentString = webcontentString.replace("\"{", "{");
					webcontentString = webcontentString.replace("}\"", "}");
					
					JSONObject jsonObject = null;
					try {
						jsonObject = JSONFactoryUtil.createJSONObject(webcontentString);
						
						_log.info("Knowledge jsonObject: " + jsonObject);
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					if (jsonObject.get("classPK") != null) {				
	
						long classPK = Long.valueOf(jsonObject.get("classPK").toString());
						
						if (jsonObject.get("groupId") != null) {	
							
							String groupIdString = jsonObject.get("groupId").toString();
							
							try {
								DLFileEntry file = DLFileEntryLocalServiceUtil.getFileEntry(classPK);
								
								Document doc = new Document(file, groupIdString);
								
								_log.info("Documents: " + doc.getMimeType());
								_log.info("Documents: " + doc.toString());
								
								documentList.add(doc);
							
							} catch (PortalException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						
							
						}
						
					}
				}
			
		}
	}
		
	

	//getters + setters
	
	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getPolicy() {
		return policy;
	}

	public void setPolicy(String policy) {
		this.policy = policy;
	}

	public List<Document> getDocumentList() {
		return documentList;
	}

	public void setDocumentList(List<Document> documentList) {
		this.documentList = documentList;
	}
	
	

	
	
	//equals and hashcode
	
	/*@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (achievement ? 1231 : 1237);
		result = prime * result + ((body == null) ? 0 : body.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Knowledge other = (Knowledge) obj;
		if (achievement != other.achievement)
			return false;
		if (body == null) {
			if (other.body != null)
				return false;
		} else if (!body.equals(other.body))
			return false;
		return true;
	}*/
	
	
	
	
	
	
}