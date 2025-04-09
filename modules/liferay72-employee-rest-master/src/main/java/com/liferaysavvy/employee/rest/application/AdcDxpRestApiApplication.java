package com.liferaysavvy.employee.rest.application;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.jsonwebservice.JSONWebService;
import com.liferay.portal.kernel.service.UserLocalService;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Application;

import com.liferaysavvy.employee.rest.application.rest.application.resources.EventResource;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author ana.cavadas
 */
@Component(
		property = {
				"osgi.jaxrs.application.base=/adc-dxp-services",
				"osgi.jaxrs.extension.select=(osgi.jaxrs.name=Liferay.Vulcan)",
				"osgi.jaxrs.name=Liferay.Services",
				"jaxrs.application=true",
				"auth.verifier.guest.allowed=true",
				"auth.verifier.BasicAuthHeaderAuthVerifier.basic_auth=true"
			},
		service = Application.class,
		immediate=true, 
		configurationPolicy = ConfigurationPolicy.OPTIONAL,
		configurationPid = "adc.dxp.rest.api.application.AdcDxpRestApiConfiguration"
	)
@JSONWebService
@ApplicationPath("")
public class AdcDxpRestApiApplication extends Application {
	

	private UserLocalService _userLocalService;
	public Set<Object> getSingletons() {
		Set<Object> singletons = new HashSet<Object>();
		singletons.add(new JacksonJsonProvider());
		singletons.add(this);
		singletons.add(new EventResource(this));
		return singletons;
	}

	@GET
	@Produces("text/plain")
	public String working() {
		return "It's works!";
	}
	

	@GET
	@Path("/morning")
	@Produces("text/plain")
	public String hello() {
		return "Good morning!";
	}

	@GET
	@Path("/morning/{name}")
	@Produces("text/plain")
	public String morning(
		@PathParam("name") String name,
		@QueryParam("drink") String drink) {

		String greeting = "Good Morning " + name;

		if (drink != null) {
			greeting += ". Would you like some " + drink + "?";
		}

		return greeting;
	}
	
	
	/**
	 * 
	 * This method will be called when the package is activated / updated
	 * 
	 * @param properties
	 */
//	@Activate
//	@Modified
//	public void activate(Map<String, Object> properties) {
//
//		System.out.println("The Ad Customs REST app has been activated/updated at " + new Date().toString());
//
//		_dxpRESTConfiguration = ConfigurableUtil.createConfigurable(AdcDxpRestApiConfiguration.class, properties);
//
//		if (_dxpRESTConfiguration != null) {
//			System.out.println("Announcements per page = "+_dxpRESTConfiguration.announcementsPerPage());
//		} else {
//			System.out.println("The Ad Customs REST app is not yet initialized");
//		}
//	}

	
	// getters + setters
	
	/**
	 * 
	 * Commom user
	 * 
	 * @param userLocalService
	 */
	@Reference
	public void setUserLocalService(UserLocalService userLocalService) {
		this._userLocalService = userLocalService;
	}
	
	/**
	 * 
	 * Commom user
	 * 
	 * @return
	 */
	public UserLocalService getUserLocalService() {
		return this._userLocalService;
	}
	

	
	
}