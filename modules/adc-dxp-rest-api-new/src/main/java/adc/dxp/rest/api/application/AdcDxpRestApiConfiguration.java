package adc.dxp.rest.api.application;

import aQute.bnd.annotation.metatype.Meta;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

import java.util.List;


@ExtendedObjectClassDefinition(category = "api")
@Meta.OCD(
        id = "adc.dxp.rest.api.application.AdcDxpRestApiConfiguration",
        localization = "content/Language",
        name = "adc-dxp-rest-api-configuration-name"
)
public interface AdcDxpRestApiConfiguration {
    @Meta.AD(
            description = "outlook-auth-connector-client-id-description",
            name = "client-id", required = false,
            deflt = "bd3792e8-12bd-4ba0-a8b4-e6b8b6878b2e"
    )
    String clientId();

    @Meta.AD(description= "PaginationSize", deflt="6", required=false)
    public Integer paginationSize();

    @Meta.AD(description= "calendars", deflt="", required=false)
    public List<Long> calendars();

}