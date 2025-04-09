package adc.dxp.rest.api.application.utils;

import javax.servlet.http.HttpServletRequest;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

/**
 * 
 * @author ricardo.gomes
 *
 */
public class RequestUtil {

	private static final Log _log = LogFactoryUtil.getLog(RequestUtil.class);

	/**
	 * Gets the ThemeDisplay from the request
	 *
	 * @param request the HTTP servlet request
	 * @return the ThemeDisplay object
	 */
	public static ThemeDisplay getTheme(HttpServletRequest request) {
		ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);

		if (themeDisplay == null) {
			_log.warn("ThemeDisplay not found in request");
			// Create a minimal ThemeDisplay to avoid NullPointerExceptions
			themeDisplay = new ThemeDisplay();
			themeDisplay.setPathImage("/image");
		}

		return themeDisplay;
	}

}
