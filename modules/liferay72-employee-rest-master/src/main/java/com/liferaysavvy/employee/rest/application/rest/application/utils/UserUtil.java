package com.liferaysavvy.employee.rest.application.rest.application.utils;

import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferaysavvy.employee.rest.application.AdcDxpRestApiApplication;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;


public class UserUtil {
	
	public static User getCurrentUser(@Context HttpServletRequest request, AdcDxpRestApiApplication _app) {

		/*
		 * NOTE: the JEE standard request.getRemoteUser() returns a string
		 * representation the numeric ID of the logged-in user. If no user is logged in
		 * yet this ID is currently (GA2) the ID of the Liferay 'default' user. But we
		 * also check for null 'just in case'
		 */
		UserLocalService userLocalService = _app.getUserLocalService();
		String userid = request.getRemoteUser();

		User user = null;
		
		try {
			if (userid != null && userLocalService != null) {
				user = userLocalService.getUser(new Long(userid));
							}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return user;
		
	}
}
