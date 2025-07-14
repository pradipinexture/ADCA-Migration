package com.ADCA.logout.hook;
import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.events.LifecycleAction;
import com.liferay.portal.kernel.events.LifecycleEvent;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.osgi.service.component.annotations.Component;

@Component(
        immediate = true,
        property = {
                "key=logout.events.post",
                "service.ranking:Integer=1000"
        },
        service = LifecycleAction.class
)
public class CustomLogoutRedirect implements LifecycleAction {
    private final String logoutUrl = "/c/portal/login";

    @Override
    public void processLifecycleEvent(LifecycleEvent event) throws ActionException {
        try {
            HttpServletRequest request = event.getRequest();
            HttpServletResponse response = event.getResponse();

            String baseURL = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
            String redirectURL = baseURL + logoutUrl;

            if (!response.isCommitted()) {
                response.reset();
                response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
                response.setHeader("Location", redirectURL);
                response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
                response.setHeader("Pragma", "no-cache");
                response.setHeader("Expires", "0");
                response.flushBuffer();
            }
        } catch (Exception e) {
            throw new ActionException("Logout redirect failed", e);
        }
    }
}