package adc.dxp.rest.api.application;

import adc.dxp.rest.api.application.resources.*;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.Portal;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.jaxrs.whiteboard.JaxrsWhiteboardConstants;
import javax.ws.rs.core.Application;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Component(
        property = {
                "osgi.jaxrs.application.base=/adc-dxp-services",
                "osgi.jaxrs.extension.select=(osgi.jaxrs.name=Liferay.Vulcan)",
                "osgi.jaxrs.name=ADC.Services",
                "jaxrs.application=true",
                "auth.verifier.guest.allowed=true",
                "auth.verifier.BasicAuthHeaderAuthVerifier.basic_auth=true"
        },
        service = Application.class,
        immediate = true,
        configurationPolicy = ConfigurationPolicy.OPTIONAL,
        configurationPid = "adc.dxp.rest.api.application.AdcDxpRestApiConfiguration"
)
public class AdcDxpRestApiApplication extends Application {

    @Reference
    private Portal _portal;
    @Reference
    private UserResource _userResource;

    @Reference
    private NewsResource _newsResource;
    @Reference
    private PromotionsResource _promotionsResource;
    @Reference
    private MediaResource _mediaResource;
    @Reference
    private ContactResource _contactResource;
    @Reference
    private EventResource _eventResource;
    @Reference
    private KnowledgeResource _knowledgeResource;
    @Reference
    private FaqsResource _faqsResource;
    @Reference
    private ManagementMessageResource _managementMessageResource;
    @Reference
    private QuickLinksResource _quickLinksResource;
    @Override
    public Set<Object> getSingletons() {
        Set<Object> singletons = new HashSet<>();
        singletons.add(_userResource);
        singletons.add(_newsResource);
        singletons.add(_promotionsResource);
        singletons.add(_mediaResource);
        singletons.add(_contactResource);
        singletons.add(_mediaResource);
        singletons.add(_eventResource);
        singletons.add(_eventResource);
        singletons.add(_knowledgeResource);
        singletons.add(_managementMessageResource);
        singletons.add(_quickLinksResource);
        return singletons;
    }

    private UserLocalService _userLocalService;
    @Reference
    public void setUserLocalService(UserLocalService userLocalService) {
        this._userLocalService = userLocalService;
    }

    public UserLocalService getUserLocalService() {
        return this._userLocalService;
    }
}