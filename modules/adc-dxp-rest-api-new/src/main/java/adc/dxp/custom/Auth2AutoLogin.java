package adc.dxp.custom;


import com.liferay.oauth2.provider.service.OAuth2AuthorizationLocalServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auto.login.AutoLogin;
import com.liferay.portal.kernel.security.auto.login.AutoLoginException;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.osgi.service.component.annotations.Component;

@Component(immediate = true)
public class Auth2AutoLogin implements AutoLogin {
    private static final Log LOGGER = LogFactoryUtil.getLog(Auth2AutoLogin.class);

    public String[] handleException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Exception e) throws AutoLoginException {
        return null;
    }

    public String[] login(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws AutoLoginException {
        try {
            String authorization = GetterUtil.get(httpServletRequest.getHeader("Authorization"), "");
            if (!authorization.isEmpty()) {
                String bearerToken = authorization.toLowerCase().replace("bearer ", "");
                User user = getUserFromBearerToken(bearerToken);
                return new String[] { String.valueOf(user.getUserId()), user.getPassword(),
                        String.valueOf(user.isPasswordEncrypted()) };
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public User getUserFromBearerToken(String bearerToken) {
        try {
            return UserLocalServiceUtil.getUser(
                    OAuth2AuthorizationLocalServiceUtil.getOAuth2AuthorizationByAccessTokenContent(bearerToken).getUserId());
        } catch (PortalException e) {
            LOGGER.debug(e.getMessage());
            return null;
        }
    }
}