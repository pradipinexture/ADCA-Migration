package adc.dxp.rest.api.application;

import adc.dxp.rest.api.application.resources.NewsResource;
import adc.dxp.rest.api.application.resources.PromotionsResource;
import adc.dxp.rest.api.application.resources.UserResource;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.Portal;
import org.osgi.service.component.annotations.Activate;
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
    private PromotionsResource _promotionResource;
    @Reference
    private ConfigurationProvider _configurationProvider;

    @Override
    public Set<Object> getSingletons() {
        Set<Object> singletons = new HashSet<>();
        singletons.add(_userResource);
        singletons.add(_newsResource);
        singletons.add(_promotionResource);
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

    public volatile AdcDxpRestApiConfiguration _dxpRESTConfiguration;

    @Activate
    protected void activate() {
        try {
            _dxpRESTConfiguration = _configurationProvider.getCompanyConfiguration(AdcDxpRestApiConfiguration.class, 0);
        } catch (ConfigurationException e) {}
    }
}