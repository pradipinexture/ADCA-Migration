package adc.dxp.rest.api.application.utils;

import adc.dxp.rest.api.application.AdcDxpRestApiApplication;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.http.HttpServletRequest;

public class UserUtil {
    private static final Log _log = LogFactoryUtil.getLog(UserUtil.class);

    /**
     * Gets the current user from the request
     *
     * @param request the HTTP servlet request
     * @return the current user
     * @throws PortalException if unable to determine the current user
     */
    public static User getCurrentUser(HttpServletRequest request, AdcDxpRestApiApplication app) {
        if (request == null) {// || app == null) {
            return null;
        }

//        UserLocalService userLocalService = app.getUserLocalService();
//        if (userLocalService == null) {
//            return null;
//        }

        String userId = request.getRemoteUser();
        if (userId == null || userId.isEmpty()) {
            return null;
        }

        try {
            return UserLocalServiceUtil.getUser(Long.valueOf(userId));
        } catch (Exception e) {
            // Use proper logging instead of printStackTrace
            Log _log = LogFactoryUtil.getLog(UserUtil.class);
            _log.error("Error retrieving current user", e);
            return null;
        }
    }
}
