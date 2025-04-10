//package adc.dxp.rest.api.application.resources;
//
//import java.util.List;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.ws.rs.GET;
//import javax.ws.rs.Path;
//import javax.ws.rs.Produces;
//import javax.ws.rs.core.Context;
//import javax.ws.rs.core.MediaType;
//
//import com.liferay.portal.kernel.dao.orm.Criterion;
//import com.liferay.portal.kernel.dao.orm.DynamicQuery;
//import com.liferay.portal.kernel.dao.orm.QueryUtil;
//import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
//import com.liferay.portal.kernel.exception.PortalException;
//import com.liferay.portal.kernel.log.Log;
//import com.liferay.portal.kernel.log.LogFactoryUtil;
//import com.liferay.portal.kernel.model.PortletPreferences;
//import com.liferay.portal.kernel.service.PortletPreferencesLocalServiceUtil;
//
//import adc.dxp.rest.api.application.AdcDxpRestApiApplication;
//import adc.dxp.rest.api.application.data.Question;
//import io.swagger.v3.oas.annotations.Operation;
//
///**
// *
// * Endpoints  of the Related
// *
// * @author ana.cavadas
// *
// */
//@Path("/portlet")
//public class PortletResource {
//
//
//	/**
//	 * app instance
//	 */
//	private AdcDxpRestApiApplication _app;
//
//	/**
//	 * logging instance
//	 */
//	private static Log _log = LogFactoryUtil.getLog(PortletResource.class);
//
//
//	/**
//	 *
//	 * Constructor, will be used in AdcDxpRestApiApplication.getSingletons()
//	 *
//	 * @param _app
//	 */
//	public PortletResource(AdcDxpRestApiApplication _app) {
//		this._app = _app;
//	}
//
//	@GET
//	@Operation(
//		description = "Retrieves the question ID of the active poll."
//	)
//	@Path("/poll")
//	@Produces(MediaType.APPLICATION_JSON)
//	public Question poll(@Context HttpServletRequest request) throws PortalException {
//
//		String pollPortletID = _app.get_dxpRESTConfiguration().pollsPortletID();
//
//		DynamicQuery dynamic = PortletPreferencesLocalServiceUtil.dynamicQuery();
//		Criterion descriptionCriterion = RestrictionsFactoryUtil.like("portletId",  pollPortletID);
//		dynamic.add(descriptionCriterion);
//
//		List<PortletPreferences> prefs = PortletPreferencesLocalServiceUtil.dynamicQuery(dynamic, QueryUtil.ALL_POS,QueryUtil.ALL_POS);
//
//		String pattern = "<portlet-preferences><preference><name>questionId</name><value>(.+?)</value>";
//
//	    Pattern r = Pattern.compile(pattern);
//	    Matcher m = r.matcher(prefs.get(0).getPreferences());
//	    if (m.find( )) {
//    	  _log.debug("Found value: " + m.group(1));
//
//    	  return new Question(m.group(1));
//	    }
//
//	    return null;
//	}
//
//}
