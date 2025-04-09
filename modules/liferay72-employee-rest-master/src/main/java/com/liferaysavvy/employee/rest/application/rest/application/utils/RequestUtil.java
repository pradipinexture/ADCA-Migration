package com.liferaysavvy.employee.rest.application.rest.application.utils;

import javax.servlet.http.HttpServletRequest;

import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

/**
 * 
 * @author ricardo.gomes
 *
 */
public class RequestUtil {

	/**
	 * 
	 * @param request
	 * @return themeDisplay with lots of information, including groupId and companyId
	 */
	public static ThemeDisplay getTheme(HttpServletRequest request) {
		return (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
	}
	
	/**
	 * 
	 * @param request
	 * @return LayoutSet with lots of information, including groupId and companyId
	 */
	public static LayoutSet getLayoutSet(HttpServletRequest request) {
		return (LayoutSet) request.getAttribute(WebKeys.VIRTUAL_HOST_LAYOUT_SET);
	}

}
