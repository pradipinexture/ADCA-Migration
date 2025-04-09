package adc.dxp.rest.api.application.data;

import java.util.Optional;

import com.liferay.journal.model.JournalArticle;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import adc.dxp.rest.api.application.utils.Constants;
import adc.dxp.rest.api.application.utils.ContentType;

/**
 * 
 * @author ricardo.gomes
 *
 */
public class Media extends BaseContent {

	private boolean showInHomePage;
	private long fileClassPk;
	private String videoSRC;

	/**
	 * logging instance
	 */
	private static Log _log = LogFactoryUtil.getLog(Media.class);

	public Media(JournalArticle article, String languageId) {

		super(article, Optional.of(languageId == null ? Constants.DEFAULT_VALUE_LANGUAGE : languageId), ContentType.MEDIA);

		if (getAttributes().containsKey(Constants.STRUNCTURE_FIELD_NAME_SHOW_IMAGE)) {

			String value = getAttributes().get(Constants.STRUNCTURE_FIELD_NAME_SHOW_IMAGE).getValue();

			if (value.isEmpty()) {
				showInHomePage = false;
			} else {
				showInHomePage = Boolean.valueOf(value);
			}

		}

		if (getAttributes().containsKey(Constants.STRUNCTURE_FIELD_NAME_IMAGE)) {

			JSONObject imagePayload;
			try {
				imagePayload = JSONFactoryUtil
						.createJSONObject(getAttributes().get(Constants.STRUNCTURE_FIELD_NAME_IMAGE).getValue());
				fileClassPk = imagePayload.getLong(Constants.STRUNCTURE_FIELD_NAME_CLASS_PK);
			} catch (JSONException e) {
				_log.error(e.getMessage());
			}

		}
		
		if (getAttributes().containsKey(Constants.STRUNCTURE_FIELD_NAME_VIDEO)) {

			JSONObject videoPayload;
			try {
				videoPayload = JSONFactoryUtil
						.createJSONObject(getAttributes().get(Constants.STRUNCTURE_FIELD_NAME_VIDEO).getValue());
				
				Long groupId = videoPayload.getLong(Constants.STRUNCTURE_FIELD_NAME_GROUP_ID);
				String uuid = videoPayload.getString(Constants.STRUNCTURE_FIELD_NAME_UUID);
				
				if (groupId != null && !uuid.contentEquals("")) {
					setVideoSRC(String.format(Constants.IMAGE_URL, uuid, Long.valueOf(groupId)));	
				}
				
			} catch (Exception e) {
				_log.error(e.getMessage());
			}
			
		}

	}

	public boolean isShowInHomePage() {
		return showInHomePage;
	}

	public void setShowInHomePage(boolean showInHomePage) {
		this.showInHomePage = showInHomePage;
	}

	public long getFileClassPk() {
		return fileClassPk;
	}

	public void setFileClassPk(long fileClassPk) {
		this.fileClassPk = fileClassPk;
	}

	public String getVideoSRC() {
		return videoSRC;
	}

	public void setVideoSRC(String videoSRC) {
		this.videoSRC = videoSRC;
	}
	
}
