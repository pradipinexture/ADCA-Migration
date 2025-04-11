package adc.dxp.rest.api.application.resources;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import adc.dxp.rest.api.application.AdcDxpRestApiConfiguration;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.module.configuration.ConfigurationProvider;
import com.liferay.portal.model.impl.UserModelImpl;
import com.liferay.ratings.kernel.model.RatingsEntry;
import com.liferay.ratings.kernel.model.RatingsStats;
import com.liferay.ratings.kernel.service.RatingsEntryLocalServiceUtil;
import com.liferay.ratings.kernel.service.RatingsStatsLocalServiceUtil;

import adc.dxp.rest.api.application.AdcDxpRestApiApplication;
import adc.dxp.rest.api.application.data.Ratings;
import adc.dxp.rest.api.application.utils.UserUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Reference;

@Path("/ratings")
public class RatingsResource {

	private static AdcDxpRestApiApplication _app;

	public RatingsResource(AdcDxpRestApiApplication _app) {
		RatingsResource._app = _app;
	}
	/**
	 * logging instance
	 */
	private static Log _log = LogFactoryUtil.getLog(RatingsResource.class);

	@Reference
	private ConfigurationProvider _configurationProvider;

	public volatile AdcDxpRestApiConfiguration _dxpRESTConfiguration;

	@Activate
	protected void activate() {
		try {
			_dxpRESTConfiguration = _configurationProvider.getCompanyConfiguration(AdcDxpRestApiConfiguration.class, 0);
		} catch (ConfigurationException e) {}
	}

	/**
	 * 
	 * Returns news by id
	 * 
	 * @param request hidden parameter
	 * @return
	 * @throws PortalException
	 */
	@GET
	@Path("/detail")
	@Operation(description = "Get news by id")
	@Parameters(
			value = {
				@Parameter(in = ParameterIn.QUERY, name = "id")
			}
		)
	@Produces(MediaType.APPLICATION_JSON)
	public Ratings getContentRatingsById(
			@Parameter(hidden = true) @QueryParam("id") String idString,
			@Context HttpServletRequest request
			) throws PortalException {
			
		return RatingsResource.getRatingsById(idString, request);
	}
	
	public static Ratings getRatingsById(String idString, HttpServletRequest request) throws PortalException {
		
		double positive = 0.0;
		double negative = 0.0;
		double average = 0.0;
		
		long id = Long.valueOf(idString).longValue();
		
		Ratings rating = new Ratings();
		
		try {
			RatingsStats stats = RatingsStatsLocalServiceUtil.getStats("com.liferay.journal.model.JournalArticle", id);
			
			positive = stats.getTotalScore();
			negative = stats.getTotalEntries() - positive;
			
			average = stats.getAverageScore();
		
			rating.setNegativeRatings(negative);
			rating.setPositiveRatings(positive);
			
			_log.debug("positive: " + positive);
			_log.debug("negative: " + negative);
			_log.debug("average: " + average);
			
		} catch(PortalException e) {
			_log.debug("Not Found rating for this content");
		}
		
		// Get User
		UserModelImpl currentUser = (UserModelImpl) UserUtil.getCurrentUser(request, _app);
		_log.debug("User: "+ currentUser.getUserId());
		
		try {
			RatingsEntry ratingEntry = RatingsEntryLocalServiceUtil.getEntry(currentUser.getUserId(), "com.liferay.journal.model.JournalArticle", id);
			
			_log.debug("rating: " + ratingEntry.getScore());
			
			if (ratingEntry.getScore() == 1.0) {
				rating.setThumbsUp(true);				
			} else {
				rating.setThumbsDown(true);
			}
			
			rating.setVoted(true);
			
		} catch(PortalException e) {
			_log.debug("Not Found rating for this user");
		}
		
		return rating;
	}
}
