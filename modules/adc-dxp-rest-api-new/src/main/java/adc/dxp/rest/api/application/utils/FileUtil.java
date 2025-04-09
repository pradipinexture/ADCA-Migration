package adc.dxp.rest.api.application.utils;

import com.liferay.document.library.kernel.model.DLFileEntry;

/**
 * 
 * @author ricardo.gomes
 *
 */
public class FileUtil {

	/**
	 * 
	 * @param file
	 * @return URL for content
	 */
	public static String getImageURL(DLFileEntry file) {
		
		return String.format(Constants.IMAGE_URL, file.getUuid(), file.getGroupId());
		
	}

}
