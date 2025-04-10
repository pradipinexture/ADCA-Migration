package adc.dxp.rest.api.application.resources;

import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
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

import com.liferay.portal.kernel.dao.orm.Criterion;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsUtil;

import adc.dxp.rest.api.application.AdcDxpRestApiApplication;
import adc.dxp.rest.api.application.data.Idea;
import adc.dxp.rest.api.application.data.Ideas;
import adc.dxp.rest.api.application.data.vo.UserData;
import adc.dxp.rest.api.application.data.vo.UserVO;
import adc.dxp.rest.api.application.utils.Converter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;

/**
 * 
 * Endpoints of Ideas
 * 
 * @author ana.cavadas
 *
 */
@Path("/ideas")
public class IdeasResource extends BasicResource {

	/**
	 * logging instance
	 */
	private static Log _log = LogFactoryUtil.getLog(IdeasResource.class);

	/**
	 * 
	 * Returns all ideas
	 * 
	 * @param request hidden parameter
	 * @return
	 * @throws PortalException
	 */
	@GET
	@Path("/all")
	@Operation(description = "Get all ideas")
	@Parameters(value = { @Parameter(in = ParameterIn.QUERY, name = "maxResults")})
	@Produces(MediaType.APPLICATION_JSON)
	public Ideas getAllIdeas(@Context HttpServletRequest request,
			@Parameter(hidden = true) @QueryParam("maxResults") String maxResults)
			throws PortalException {
		
		String languageIdString = request.getHeader("languageId");
		
		try {
			String serviceURL = GetterUtil.get(PropsUtil.get("ideas-service-url"),
					"http://vsbsefmw12214.aitec.pt:7009/api-internal/innovation/v1/ideas?numberOfIdeas=1");
			
			InputStream is = new URL(serviceURL).openStream();
			try {
				BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
				
				Ideas ideas = Converter.fromIdeasJsonString(readAll(rd));
				
				for (Idea idea: ideas.getIdeas()) {
					if (idea.getEmail() != null && !idea.getEmail().isEmpty()) {
						
						DynamicQuery usersDynamicQuery = UserLocalServiceUtil.dynamicQuery();
						Criterion emailaddressCriterion = RestrictionsFactoryUtil.ilike("emailAddress", idea.getEmail());
						
						usersDynamicQuery.add(emailaddressCriterion);
						
						List<User> users = UserLocalServiceUtil.dynamicQuery(usersDynamicQuery, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
						if (users != null && users.stream().findFirst().isPresent()) {
							UserData user = new UserData(users.stream().findFirst().get());
							idea.setUser(user);						
						}
					}
					
				}
				
				return ideas;
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
