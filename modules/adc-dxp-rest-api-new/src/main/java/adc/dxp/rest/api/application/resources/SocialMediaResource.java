package adc.dxp.rest.api.application.resources;

import java.net.URL;
import java.nio.charset.Charset;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsUtil;

import adc.dxp.rest.api.application.AdcDxpRestApiApplication;
import adc.dxp.rest.api.application.data.Posts;
import adc.dxp.rest.api.application.utils.Converter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;

/**
 * 
 * Endpoints of social media
 * 
 * @author luis.correia
 *
 */
@Path("/social-medias")
public class SocialMediaResource extends BasicResource {

	/**
	 * logging instance
	 */
	private static Log _log = LogFactoryUtil.getLog(SocialMediaResource.class);

	/**
	 * app instance
	 */
	AdcDxpRestApiApplication _app;

	/**
	 * 
	 * Returns all social media posts
	 * 
	 * @param isRead  with flag read
	 * @param request hidden parameter
	 * @return
	 * @throws PortalException
	 */
	@GET
	@Path("/posts")
	@Operation(description = "Get all social media posts.")
	@Parameters(value = { @Parameter(in = ParameterIn.QUERY, name = "socialMediaName"),
						@Parameter(in = ParameterIn.QUERY, name = "maxResults"),
						@Parameter(in = ParameterIn.QUERY, name = "locale")})
	@Produces(MediaType.APPLICATION_JSON)
	public Posts getAllPosts(@Context HttpServletRequest request,
			@Parameter(hidden = true) @QueryParam("socialMediaName") String socialMediaName,
			@Parameter(hidden = true) @QueryParam("maxResults") String maxResults,
			@Parameter(hidden = true) @QueryParam("locale") String locale)
			throws PortalException {
								
		try {
			String serviceURL = GetterUtil.get(PropsUtil.get("social-media-service-url"),
					"http://vsbsefmw12214.aitec.pt:7009/api-internal/social-media/v1/posts");
			
			if(socialMediaName != null || maxResults != null || locale != null) {
				String queryParams = "?";
				if (socialMediaName != null) {
					queryParams = queryParams + "SocialMediaName=" + socialMediaName + "&";
				}
				if (maxResults != null) {
					queryParams = queryParams + "MaxResults=" + maxResults;
				}
				if (locale != null) {
					queryParams = queryParams + "Locale=" + locale;
				}
				serviceURL = serviceURL + queryParams;
			}

			InputStream is = new URL(serviceURL).openStream();
			try {
				BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
				
				return Converter.fromJsonString(readAll(rd));
			} finally {
				is.close();
			}
		} catch (IOException e) {
			_log.error(e);
			return null;
		}

	}
	
	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}
}
