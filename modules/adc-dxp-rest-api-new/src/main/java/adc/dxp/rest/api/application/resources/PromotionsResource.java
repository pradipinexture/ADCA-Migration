package adc.dxp.rest.api.application.resources;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import adc.dxp.rest.api.application.data.DirectorMessage;
import adc.dxp.rest.api.application.data.News;
import adc.dxp.rest.api.application.utils.JournalArticleUtil;
import adc.dxp.rest.api.application.utils.PageUtils;
import adc.dxp.rest.api.application.utils.StructureUtil;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.util.PortalUtil;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.util.comparator.ArticleDisplayDateComparator;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import adc.dxp.rest.api.application.AdcDxpRestApiConfiguration;
import adc.dxp.rest.api.application.data.Category;
import adc.dxp.rest.api.application.data.Promotion;
import adc.dxp.rest.api.application.data.comparator.JournalArticleTitleComparator;
import adc.dxp.rest.api.application.utils.Constants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Endpoints of the Promotions
 *
 * @author luis.correia
 */
@Component(
		property = {
				"osgi.jaxrs.application.select=(osgi.jaxrs.name=ADC.Services)",
				"osgi.jaxrs.resource=true"
		},
		scope = ServiceScope.PROTOTYPE,
		service = PromotionsResource.class
)
@Path("/promotions")
@Tag(name = "Promotions")
public class PromotionsResource {

	/**
	 * logging instance
	 */
	private static final Log _log = LogFactoryUtil.getLog(PromotionsResource.class);

	@Reference
	private Portal _portal;

	@Reference
	private JournalArticleLocalService _journalArticleLocalService;

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	@Reference
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private StructureResource _structureResource;

	private volatile AdcDxpRestApiConfiguration _dxpRESTConfiguration;

	@Activate
	protected void activate() {
		try {
			_dxpRESTConfiguration = _configurationProvider.getCompanyConfiguration(AdcDxpRestApiConfiguration.class, 0);
		} catch (ConfigurationException e) {
			_log.error("Error loading configuration", e);
		}
	}

	@GET
	@Operation(
			description = "Retrieves the list of the Promotions. Results can be paginated, filtered, searched, and sorted."
	)
	@Parameters(
			value = {
					@Parameter(in = ParameterIn.QUERY, name = "search"),
					@Parameter(in = ParameterIn.QUERY, name = "filter"),
					@Parameter(in = ParameterIn.QUERY, name = "page"),
					@Parameter(in = ParameterIn.QUERY, name = "pageSize"),
					@Parameter(in = ParameterIn.QUERY, name = "sort")
			}
	)
	@Path("/search")
	@Produces(MediaType.APPLICATION_JSON)
	public Page<Promotion> search(
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

		// Pagination setup
		int paginationSize = pageSize == null ? _dxpRESTConfiguration.paginationSize() : pageSize;
		int paginationPage = pagination.getPage();

		// Get basic parameters
		long companyId = PortalUtil.getCompanyId(request);
		String groupIdString = request.getHeader("groupId");
		String languageIdString = request.getHeader("languageId");


		// Category filter
		long categoryId = categoryIdParam != null && !categoryIdParam.isEmpty() && !categoryIdParam.equalsIgnoreCase("null") ?
				Long.valueOf(categoryIdParam) : -1;

		String structureId = _structureResource.getStructure(groupId, Constants.STRUCTURE_PROMOTIONS_EN);

		// Date parsing
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
			_log.error("Error parsing date", e);
		}

		OrderByComparator<JournalArticle> orderByComparator = null;

		if (sorts != null && sorts[0].getFieldName().equalsIgnoreCase("displayDate")) {
			orderByComparator = new ArticleDisplayDateComparator(!sorts[0].isReverse());
		} else if (sorts != null && sorts[0].getFieldName().equalsIgnoreCase("title")) {
			orderByComparator = new JournalArticleTitleComparator(!sorts[0].isReverse());
		}


		// Get all articles for the group
		List<JournalArticle> allArticles = _journalArticleLocalService.getArticles(groupId);
//
//		// Filter by approved status and structure ID
//		List<JournalArticle> filteredArticles = new ArrayList<>();
//		for (JournalArticle article : allArticles) {
//			if (article.getStatus() == WorkflowConstants.STATUS_APPROVED &&
//					structureId.equals(article.getDDMStructureKey())) {
//				filteredArticles.add(article);
//			}
//		}
//
//		// Filter by search term if provided
//		if (search != null && !search.isEmpty()) {
//			String searchLowerCase = search.toLowerCase();
//			List<JournalArticle> searchFilteredArticles = new ArrayList<>();
//			for (JournalArticle article : filteredArticles) {
//				String title = article.getTitle(languageIdString).toLowerCase();
//				String description = article.getDescription(languageIdString).toLowerCase();
//				if (title.contains(searchLowerCase) || description.contains(searchLowerCase)) {
//					searchFilteredArticles.add(article);
//				}
//			}
//			filteredArticles = searchFilteredArticles;
//		}
//
//		// Sort results if needed
//		if (sorts != null && sorts.length > 0) {
//			Sort sort = sorts[0];
//			OrderByComparator<JournalArticle> orderByComparator = null;
//
//			if (sort.getFieldName().equalsIgnoreCase("displayDate")) {
//				orderByComparator = new ArticleDisplayDateComparator(!sort.isReverse());
//				Collections.sort(filteredArticles, orderByComparator);
//			} else if (sort.getFieldName().equalsIgnoreCase("title")) {
//				orderByComparator = new JournalArticleTitleComparator(!sort.isReverse());
//				Collections.sort(filteredArticles, orderByComparator);
//			}
//		}
//
//		// Process results

		//	List<Promotion> lastResults = new ArrayList<>();

		// Get all articles for the group

		// Filter by approved status and structure ID
		List<JournalArticle> filteredArticles = new ArrayList<>();
		for (JournalArticle article : allArticles) {
			if (article.getStatus() == WorkflowConstants.STATUS_APPROVED &&
					structureId.equals(article.getDDMStructureKey())) {
				filteredArticles.add(article);
			}
		}

		// Filter by search term if provided
		if (search != null && !search.isEmpty()) {
			String searchLowerCase = search.toLowerCase();
			List<JournalArticle> searchFilteredArticles = new ArrayList<>();
			for (JournalArticle article : filteredArticles) {
				String title = article.getTitle(languageIdString).toLowerCase();
				String description = article.getDescription(languageIdString).toLowerCase();
				if (title.contains(searchLowerCase) || description.contains(searchLowerCase)) {
					searchFilteredArticles.add(article);
				}
			}
			filteredArticles = searchFilteredArticles;
		}

		DDMStructure structure = StructureUtil.getStructureByNameEn(Constants.STRUCTURE_PROMOTIONS_EN);

		List<JournalArticle> results = JournalArticleUtil.searchJournalArticles(companyId, groupId, search, structure.getStructureKey(), startDate, endDate, orderByComparator);
		List<Promotion> lastResults = new ArrayList<>();

		for (JournalArticle article : results) {
			try {
				Promotion promotion = new Promotion(article, request.getHeader(Constants.HEADER_LANGUAGE_ID));
				if ((startDate != null && (startDate.after(promotion.getEndDate()) || promotion.getStartDate().after(startDate)))
						|| (endDate != null && (endDate.before(promotion.getStartDate()) || promotion.getEndDate().before(endDate)))) {
					continue;
				}

				// Get asset entry
				AssetEntry assetUtil = _assetEntryLocalService.getEntry(
						"com.liferay.journal.model.JournalArticle", article.getResourcePrimKey());
				promotion.setEntryId(assetUtil.getEntryId());

				// Get categories
				List<AssetCategory> categoryList = _assetCategoryLocalService.getCategories(
						"com.liferay.journal.model.JournalArticle", article.getResourcePrimKey());

				Optional<AssetCategory> firstCategory = categoryList.stream().findFirst();

				if (firstCategory.isPresent()) {
					AssetCategory catego = _assetCategoryLocalService.getCategory(firstCategory.get().getCategoryId());
					promotion.setCategory(new Category(catego.getTitle(languageIdString), catego.getCategoryId()));
				}

				// Apply category filter
				if ((categoryIdParam == null || categoryIdParam.equalsIgnoreCase("-1")
						|| Long.compare(categoryId, promotion.getCategory().getCategoryId()) == 0)
						&& (lastResults.indexOf(promotion) == -1)) {
					lastResults.add(promotion);
				}
			} catch (Exception e) {
				_log.error("Error processing article: " + article.getArticleId(), e);
			}
		}
		// Handle pagination
		int start = (paginationPage - 1) * paginationSize;
		int end = Math.min(start + paginationSize, lastResults.size());

		List<Promotion> pageResults = start < lastResults.size() ?
				lastResults.subList(start, end) : Collections.emptyList();

		return Page.of(pageResults, pagination, lastResults.size());
	}

	@GET
	@Path("/detail")
	@Operation(description = "Get promotion by id")
	@Parameters(
			value = {
					@Parameter(in = ParameterIn.QUERY, name = "id")
			}
	)
	@Produces(MediaType.APPLICATION_JSON)
	public Promotion getPromotionById(
			@Parameter(hidden = true) @QueryParam("id") String idString,
			@Context HttpServletRequest request) throws PortalException {

		_log.debug("Call get promotion");
		String groupIdString = request.getHeader("groupId");
		String languageIdString = request.getHeader("languageId");

		long groupId = Long.valueOf(groupIdString);
		long id = Long.valueOf(idString);

		String structureId = _structureResource.getStructure(groupId, Constants.STRUCTURE_PROMOTIONS_EN);
		JournalArticle article = _journalArticleLocalService.getLatestArticle(id);

		if (article == null || !article.getDDMStructureKey().equals(structureId)) {
			_log.debug("Not Found");
			throw new PortalException(javax.ws.rs.core.Response.Status.NOT_FOUND.toString());
		}
		Promotion promotionDetail = new Promotion(article, request.getHeader(Constants.HEADER_LANGUAGE_ID));
		AssetEntry assetUtil = _assetEntryLocalService.getEntry(
				"com.liferay.journal.model.JournalArticle", article.getResourcePrimKey());
		_log.debug("JournalArticle: " + article);
		_log.debug("article.getResourcePrimKey(): " + article.getResourcePrimKey());

		List<AssetCategory> categoryList = _assetCategoryLocalService.getCategories(
				"com.liferay.journal.model.JournalArticle", article.getResourcePrimKey());

		Optional<AssetCategory> firstCategory = categoryList.stream().findFirst();
		if (firstCategory.isPresent()) {
			AssetCategory catego = _assetCategoryLocalService.getCategory(firstCategory.get().getCategoryId());
			promotionDetail.setCategory(new Category(catego.getTitle(languageIdString), catego.getCategoryId()));
			_log.debug("categoryName: " + catego.getName());
		}

		return promotionDetail;
	}
}