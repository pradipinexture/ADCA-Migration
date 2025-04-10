package adc.dxp.rest.api.application.resources;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetCategoryLocalServiceUtil;
import com.liferay.asset.kernel.service.AssetEntryLocalServiceUtil;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.journal.util.comparator.ArticleDisplayDateComparator;

import adc.dxp.rest.api.application.AdcDxpRestApiConfiguration;
import adc.dxp.rest.api.application.data.Category;
import adc.dxp.rest.api.application.data.QuickLinks;
import adc.dxp.rest.api.application.data.Ratings;
import adc.dxp.rest.api.application.data.comparator.JournalArticleTitleComparator;
import adc.dxp.rest.api.application.utils.Constants;
import adc.dxp.rest.api.application.utils.JournalArticleUtil;
import adc.dxp.rest.api.application.utils.PageUtils;
import adc.dxp.rest.api.application.utils.StructureUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import org.osgi.service.component.annotations.*;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsResource;

@Component(
		property = {
				"osgi.jaxrs.application.select=(osgi.jaxrs.name=ADC.Services)",
				"osgi.jaxrs.resource=true"
		},
		scope = ServiceScope.PROTOTYPE,
		service = QuickLinksResource.class
)
@Path("/quick-links")
public class QuickLinksResource extends BasicResource {

	private static Log _log = LogFactoryUtil.getLog(QuickLinksResource.class);

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
	@Path("/favorites")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(description = "Retrieves the list of the Quick Links ordered by current user rating, or default order.")
	public List<QuickLinks> getFavorites(
			@Context Filter filter,
			@Context Sort[] sorts,
			@Context HttpServletRequest request) throws PortalException {

		String groupIdString = request.getHeader("groupId");
		long groupId = Long.parseLong(groupIdString);
		long companyId = PortalUtil.getCompanyId(request);

		long structureId = StructureUtil.getStructureByNameEn(Constants.STRUCTURE_QUICK_LINKS_EN).getStructureId();
		List<JournalArticle> results = JournalArticleUtil.searchJournalArticles(companyId, groupId, null, structureId, null, null, null);

		List<QuickLinks> lastResultsUpVoted = new ArrayList<>();
		boolean defaultOrder = true;

		for (JournalArticle article : results) {
			QuickLinks quickLink = new QuickLinks(article, request.getHeader(Constants.HEADER_LANGUAGE_ID));
			Ratings ratings = RatingsResource.getRatingsById(String.valueOf(article.getResourcePrimKey()), request);
			quickLink.setRatings(ratings);

			if (ratings.isThumbsUp() && !lastResultsUpVoted.contains(quickLink)) {
				defaultOrder = false;
				lastResultsUpVoted.add(quickLink);
			}
		}

		lastResultsUpVoted.sort(Comparator.comparing(QuickLinks::getTitle));
		return lastResultsUpVoted.size() <= 5 ? lastResultsUpVoted : lastResultsUpVoted.subList(0, 5);
	}

	@GET
	@Path("/search")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(description = "Retrieves the list of all Quick Links.")
	@Parameters({
			@Parameter(in = ParameterIn.QUERY, name = "search"),
			@Parameter(in = ParameterIn.QUERY, name = "filter"),
			@Parameter(in = ParameterIn.QUERY, name = "page"),
			@Parameter(in = ParameterIn.QUERY, name = "pageSize"),
			@Parameter(in = ParameterIn.QUERY, name = "sort")
	})
	public Page<QuickLinks> search(
			@Parameter(hidden = true) @QueryParam("search") String search,
			@Parameter(hidden = true) @QueryParam("categoryId") String categoryIdParam,
			@Parameter(hidden = true) @QueryParam("startDate") String startDateParam,
			@Parameter(hidden = true) @QueryParam("endDate") String endDateParam,
			@QueryParam("pageSize") Integer pageSize,
			@Context Filter filter,
			@Context Pagination pagination,
			@Context Sort[] sorts,
			@Context HttpServletRequest request,
			@HeaderParam(Constants.HEADER_GROUP_ID) long groupId) throws PortalException {

		int paginationSize = pageSize == null ? _dxpRESTConfiguration.paginationSize() : pageSize;
		int paginationPage = pagination.getPage();

		String languageIdString = request.getHeader("languageId");
		long companyId = PortalUtil.getCompanyId(request);
		long categoryId = (categoryIdParam != null && !categoryIdParam.isEmpty() && !categoryIdParam.equalsIgnoreCase("null")) ? Long.parseLong(categoryIdParam) : -1;

		long structureId = StructureUtil.getStructureByNameEn(Constants.STRUCTURE_QUICK_LINKS_EN).getStructureId();

		Date startDate = null;
		Date endDate = null;

		try {
			if (startDateParam != null && !startDateParam.isEmpty()) {
				startDate = new SimpleDateFormat("dd-MM-yyyy").parse(startDateParam);
			}
			if (endDateParam != null && !endDateParam.isEmpty()) {
				endDate = new SimpleDateFormat("dd-MM-yyyy").parse(endDateParam);
			}
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
		List<QuickLinks> lastResults = new ArrayList<>();

		for (JournalArticle article : results) {
			QuickLinks quickLink = new QuickLinks(article, request.getHeader(Constants.HEADER_LANGUAGE_ID));
			AssetEntry assetUtil = AssetEntryLocalServiceUtil.getEntry("com.liferay.journal.model.JournalArticle", article.getResourcePrimKey());
			quickLink.setEntryId(assetUtil.getEntryId());
			Ratings ratings = RatingsResource.getRatingsById(String.valueOf(article.getResourcePrimKey()), request);
			quickLink.setRatings(ratings);

			List<AssetCategory> categoryList = AssetCategoryLocalServiceUtil.getCategories(JournalArticle.class.getName(), assetUtil.getClassPK());
			Optional<AssetCategory> firstCategory = categoryList.stream().findFirst();
			firstCategory.ifPresent(cat -> quickLink.setCategory(new Category(cat.getTitle(languageIdString), cat.getCategoryId())));

			if ((categoryIdParam == null || categoryIdParam.equalsIgnoreCase("-1") || Long.compare(categoryId, quickLink.getCategory().getCategoryId()) == 0)
					&& !lastResults.contains(quickLink)) {
				lastResults.add(quickLink);
			}
		}

		return PageUtils.createPage(lastResults, pagination, lastResults.size());
	}
}