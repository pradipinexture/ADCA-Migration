// Migrated version of TellAStoryResource.java will now be placed here following 7.4 standards.

package adc.dxp.rest.api.application.resources;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.liferay.asset.kernel.model.*;
import com.liferay.asset.kernel.service.*;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.*;
import com.liferay.journal.util.comparator.ArticleDisplayDateComparator;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import adc.dxp.rest.api.application.AdcDxpRestApiConfiguration;
import adc.dxp.rest.api.application.data.Category;
import adc.dxp.rest.api.application.data.TellaStory;
import adc.dxp.rest.api.application.data.comparator.JournalArticleTitleComparator;
import adc.dxp.rest.api.application.data.vo.UserVO;
import adc.dxp.rest.api.application.utils.*;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import org.osgi.service.component.annotations.*;

@Component(
		property = {
				"osgi.jaxrs.application.select=(osgi.jaxrs.name=ADC.Services)",
				"osgi.jaxrs.resource=true"
		},
		scope = ServiceScope.PROTOTYPE,
		service = TellAStoryResource.class
)
@Path("/tellastory")
public class TellAStoryResource extends BasicResource {

	private static Log _log = LogFactoryUtil.getLog(TellAStoryResource.class);

	@Reference
	private ConfigurationProvider _configurationProvider;

	public volatile AdcDxpRestApiConfiguration _dxpRESTConfiguration;

	@Activate
	protected void activate() {
		try {
			_dxpRESTConfiguration = _configurationProvider.getCompanyConfiguration(AdcDxpRestApiConfiguration.class, 0);
		} catch (ConfigurationException e) {}
	}

	@GET
	@Path("/search")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(description = "Retrieves the list of the Tell a Story. Results can be paginated, filtered, searched, and sorted.")
	@Parameters({
			@Parameter(in = ParameterIn.QUERY, name = "search"),
			@Parameter(in = ParameterIn.QUERY, name = "filter"),
			@Parameter(in = ParameterIn.QUERY, name = "page"),
			@Parameter(in = ParameterIn.QUERY, name = "pageSize"),
			@Parameter(in = ParameterIn.QUERY, name = "sort")
	})
	public Page<TellaStory> search(
			@QueryParam("search") String search,
			@QueryParam("categoryId") String categoryIdParam,
			@QueryParam("startDate") String startDateParam,
			@QueryParam("endDate") String endDateParam,
			@QueryParam("pageSize") Integer pageSize,
			@Context Filter filter,
			@Context Pagination pagination,
			@Context Sort[] sorts,
			@Context HttpServletRequest request) throws PortalException {

		int paginationSize = pageSize == null ? _dxpRESTConfiguration.paginationSize() : pageSize;
		int paginationPage = pagination.getPage();

		long companyId = PortalUtil.getCompanyId(request);
		long groupId = Long.parseLong(request.getHeader("groupId"));
		String languageId = request.getHeader("languageId");
		long categoryId = categoryIdParam != null && !categoryIdParam.isEmpty() ? Long.parseLong(categoryIdParam) : -1;

		long structureId = StructureUtil.getStructureByNameEn(Constants.STRUCTURE_TELL_A_STORY_NAME_EN).getStructureId();

		Date startDate = null, endDate = null;
		try {
			if (startDateParam != null) startDate = new SimpleDateFormat("dd-MM-yyyy").parse(startDateParam);
			if (endDateParam != null) endDate = new SimpleDateFormat("dd-MM-yyyy").parse(endDateParam);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		OrderByComparator<JournalArticle> orderByComparator = null;
		if (sorts != null && sorts[0].getFieldName().equalsIgnoreCase("displayDate")) {
			orderByComparator = new ArticleDisplayDateComparator(!sorts[0].isReverse());
		} else if (sorts != null && sorts[0].getFieldName().equalsIgnoreCase("title")) {
			orderByComparator = new JournalArticleTitleComparator(!sorts[0].isReverse());
		}

		List<JournalArticle> results = JournalArticleUtil.searchJournalArticles(companyId, groupId, search, structureId, startDate, endDate, orderByComparator);
		List<TellaStory> lastResults = new ArrayList<>();

		for (JournalArticle article : results) {
			TellaStory tellaStory = new TellaStory(article, request.getHeader(Constants.HEADER_LANGUAGE_ID));
			User user = UserLocalServiceUtil.fetchUserByScreenName(companyId, tellaStory.getAuthor());
			if (user == null) user = UserLocalServiceUtil.fetchUserByEmailAddress(companyId, tellaStory.getAuthorEmail());

			if (user != null) {
				UserVO userVo = new UserVO(user);
				userVo.complementValues();
				userVo.setContacts(null);
				tellaStory.setUser(userVo);
			} else {
				_log.warn("User is null");
			}

			AssetEntry assetUtil = AssetEntryLocalServiceUtil.getEntry("com.liferay.journal.model.JournalArticle", article.getResourcePrimKey());
			tellaStory.setEntryId(assetUtil.getEntryId());

			List<AssetCategory> categoryList = AssetCategoryLocalServiceUtil.getCategories(JournalArticle.class.getName(), assetUtil.getClassPK());
			categoryList.stream().findFirst().ifPresent(c -> tellaStory.setCategory(new Category(c.getTitle(languageId), c.getCategoryId())));

			if ((categoryIdParam == null || categoryIdParam.equalsIgnoreCase("-1") || categoryId == tellaStory.getCategory().getCategoryId()) && !lastResults.contains(tellaStory)) {
				lastResults.add(tellaStory);
			}
		}

		if (sorts != null && sorts[0].getFieldName().equalsIgnoreCase("category")) {
			if (!sorts[0].isReverse()) {
				lastResults.sort(Comparator.comparing(t -> t.getCategory().getName()));
			} else {
				lastResults.sort((t1, t2) -> t2.getCategory().getName().compareTo(t1.getCategory().getName()));
			}
		}

		return PageUtils.createPage(lastResults, pagination, lastResults.size());
	}

	@GET
	@Path("/detail")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(description = "Get story by id")
	@Parameters(@Parameter(in = ParameterIn.QUERY, name = "id"))
	public TellaStory getStoryById(@QueryParam("id") String idString, @Context HttpServletRequest request) throws PortalException {

		long groupId = Long.parseLong(request.getHeader("groupId"));
		long id = Long.parseLong(idString);
		String languageId = request.getHeader("languageId");
		long companyId = PortalUtil.getCompanyId(request);

		String structureId = StructureUtil.getStructureByNameEn(Constants.STRUCTURE_TELL_A_STORY_NAME_EN).getStructureKey();
		JournalArticle article = JournalArticleLocalServiceUtil.getLatestArticle(id);
		TellaStory storyDetail = new TellaStory(article, request.getHeader(Constants.HEADER_LANGUAGE_ID));

		AssetEntry assetUtil = AssetEntryLocalServiceUtil.getEntry("com.liferay.journal.model.JournalArticle", article.getResourcePrimKey());
		User user = UserLocalServiceUtil.fetchUserByScreenName(companyId, storyDetail.getAuthor());
		if (user == null) user = UserLocalServiceUtil.fetchUserByEmailAddress(companyId, storyDetail.getAuthorEmail());

		if (user != null) {
			UserVO userVo = new UserVO(user);
			userVo.complementValues();
			userVo.setContacts(null);
			storyDetail.setUser(userVo);
		} else {
			_log.warn("User is null");
		}

		List<AssetCategory> categoryList = AssetCategoryLocalServiceUtil.getCategories(JournalArticle.class.getName(), assetUtil.getClassPK());
		categoryList.stream().findFirst().ifPresent(c -> storyDetail.setCategory(new Category(c.getTitle(languageId), c.getCategoryId())));

		if (article == null || !String.valueOf(article.getDDMStructureKey()).equals(structureId)) {
			_log.debug("Not Found");
			throw new PortalException(javax.ws.rs.core.Response.Status.NOT_FOUND.toString());
		}

		return storyDetail;
	}
}