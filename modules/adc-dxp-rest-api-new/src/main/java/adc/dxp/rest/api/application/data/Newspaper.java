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

import adc.dxp.rest.api.application.utils.Constants;
import adc.dxp.rest.api.application.utils.ContentType;
import adc.dxp.rest.api.application.utils.DynamicAttribute;


/**
 * @author joao.loureiro
 *
 */
public class Newspaper extends BaseContent {
	
	/**
	 * logging instance
	 */
	private static Log _log = LogFactoryUtil.getLog(Newspaper.class);

	private List<Document> documentList;
	
	public Newspaper() {}
	
	public Newspaper(JournalArticle article, String languageId) {
		super(article, Optional.of(languageId == null ? Constants.DEFAULT_VALUE_LANGUAGE : languageId), ContentType.NEWSPAPER, true);
		
		if (getListAttributes() != null && getListAttributes().get(Constants.STRUCTURE_NEWSPAPER_NEWSPAPER) != null) {	
			documentList = new ArrayList<>();
			
			List<DynamicAttribute> list = getListAttributes().get(Constants.STRUCTURE_NEWSPAPER_NEWSPAPER);
			
			for (DynamicAttribute da: list) {
				
				String webcontentString = da.getValue();
				
				webcontentString = webcontentString.replace("\"{", "{");
				webcontentString = webcontentString.replace("}\"", "}");
				
				JSONObject jsonObject = null;
				try {
					jsonObject = JSONFactoryUtil.createJSONObject(webcontentString);
					
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

	public List<Document> getDocumentList() {
		return documentList;
	}

	public void setDocumentList(List<Document> documentList) {
		this.documentList = documentList;
	}
	
	//equals and hashcode
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result;
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
		Newspaper other = (Newspaper) obj;
		return true;
	}
	
}