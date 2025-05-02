package adc.dxp.rest.api.application.resources;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetCategoryLocalServiceUtil;
import com.liferay.asset.kernel.service.AssetEntryLocalServiceUtil;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.journal.service.JournalArticleServiceUtil;
import com.liferay.journal.util.comparator.ArticleDisplayDateComparator;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import adc.dxp.rest.api.application.AdcDxpRestApiConfiguration;
import adc.dxp.rest.api.application.data.Category;
import adc.dxp.rest.api.application.data.Newspaper;
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
		service = NewspaperResource.class
)
@Path("/newspaper")
public class NewspaperResource extends BasicResource {

	private static Log _log = LogFactoryUtil.getLog(NewspaperResource.class);

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
	@Operation(description = "Retrieves the list of the Newspapers. Results can be paginated, filtered, searched, and sorted.")
	@Parameters({
			@Parameter(in = ParameterIn.QUERY, name = "search"),
			@Parameter(in = ParameterIn.QUERY, name = "filter"),
			@Parameter(in = ParameterIn.QUERY, name = "page"),
			@Parameter(in = ParameterIn.QUERY, name = "pageSize"),
			@Parameter(in = ParameterIn.QUERY, name = "sort")
	})
	public Page<Newspaper> search(
			@Parameter(hidden = true) @QueryParam("search") String search,
			@Parameter(hidden = true) @QueryParam("categoryId") String categoryIdParam,
			@Parameter(hidden = true) @QueryParam("startDate") String startDateParam,
			@Parameter(hidden = true) @QueryParam("endDate") String endDateParam,
			@QueryParam("pageSize") Integer pageSize,
			@Context Filter filter,
			@Context Pagination pagination,
			@Context Sort[] sorts,
			@Context HttpServletRequest request) throws PortalException {

		_log.debug("startDateParam: " + startDateParam);
		_log.debug("endDateParam: " + endDateParam);

		int paginationSize = pageSize == null ? _dxpRESTConfiguration.paginationSize() : pageSize;
		int paginationPage = pagination.getPage();

		long companyId = PortalUtil.getCompanyId(request);
		String groupIdString = request.getHeader("groupId");
		String languageIdString = request.getHeader("languageId");
		long groupId = Long.parseLong(groupIdString);

		long categoryId = (categoryIdParam != null && !categoryIdParam.isEmpty() && !categoryIdParam.equalsIgnoreCase("null")) ? Long.parseLong(categoryIdParam) : -1;

		DDMStructure structure = StructureUtil.getStructureByNameEn(Constants.STRUCTURE_NEWSPAPER_NAME_EN);

		Date startDate = null;
		Date endDate = null;
		try {
			if (startDateParam != null && !startDateParam.isEmpty()) {
				startDate = new SimpleDateFormat("yyyy-MM-dd").parse(startDateParam);
			}
			if (endDateParam != null && !endDateParam.isEmpty()) {
				endDate = new SimpleDateFormat("yyyy-MM-dd").parse(endDateParam);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

		OrderByComparator<JournalArticle> orderByComparator = null;
		if (sorts != null && sorts[0].getFieldName().equalsIgnoreCase("displayDate")) {
			orderByComparator = new ArticleDisplayDateComparator(!sorts[0].isReverse());
		} else if (sorts != null && sorts[0].getFieldName().equalsIgnoreCase("title")) {
			orderByComparator = OrderByComparatorFactoryUtil.create("JournalArticleLocalization", sorts[0].getFieldName(), false);
		}

		_log.debug("startDate2: " + startDate);
		_log.debug("endDate: " + endDate);

		List<JournalArticle> results = JournalArticleUtil.searchJournalArticles(companyId, groupId, search, structure.getStructureKey(), startDate, endDate, orderByComparator);

		List<Newspaper> lastResults = new ArrayList<>();
		for (JournalArticle article : results) {
			_log.debug("JournalArticle: " + article);
			if (JournalArticleLocalServiceUtil.isLatestVersion(groupId, article.getArticleId(), article.getVersion(), WorkflowConstants.STATUS_APPROVED)) {
				Newspaper newspaper = new Newspaper(article, request.getHeader(Constants.HEADER_LANGUAGE_ID));
				AssetEntry assetUtil = AssetEntryLocalServiceUtil.getEntry("com.liferay.journal.model.JournalArticle", article.getResourcePrimKey());
				newspaper.setEntryId(assetUtil.getEntryId());

				List<AssetCategory> categoryList = AssetCategoryLocalServiceUtil.getCategories(JournalArticle.class.getName(), assetUtil.getClassPK());
				Optional<AssetCategory> firstCategory = categoryList.stream().findFirst();
				firstCategory.ifPresent(assetCategory -> newspaper.setCategory(new Category(assetCategory.getTitle(languageIdString), assetCategory.getCategoryId())));

				_log.debug("DisplayDate: " + newspaper.getDisplayDate());
				_log.debug("ExpirationDate: " + newspaper.getExpirationDate());

				if ((categoryIdParam == null || categoryIdParam.equalsIgnoreCase("-1") || Long.compare(categoryId, newspaper.getCategory().getCategoryId()) == 0)
						&& !lastResults.contains(newspaper)) {
					lastResults.add(newspaper);
				}
			}
		}

		if (sorts != null && sorts[0] != null && !sorts[0].isReverse()) {
			Collections.reverse(lastResults);
		}

		return PageUtils.createPage(lastResults, pagination, lastResults.size());
	}

	@GET
	@Path("/detail")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(description = "Get newspaper by id")
	@Parameters(@Parameter(in = ParameterIn.QUERY, name = "id"))
	public Newspaper getNewspaperById(
			@Parameter(hidden = true) @QueryParam("id") String idString,
			@Context HttpServletRequest request) throws PortalException {

		String groupIdString = request.getHeader("groupId");
		String languageIdString = request.getHeader("languageId");

		long groupId = Long.parseLong(groupIdString);
		long id = Long.parseLong(idString);

		String structureId = StructureUtil.getStructureByNameEn(Constants.STRUCTURE_NEWSPAPER_NAME_EN).getStructureKey();

		JournalArticle article = JournalArticleServiceUtil.getLatestArticle(id);
		Newspaper newspaperDetail = new Newspaper(article, request.getHeader(Constants.HEADER_LANGUAGE_ID));

		AssetEntry assetUtil = AssetEntryLocalServiceUtil.getEntry("com.liferay.journal.model.JournalArticle", article.getResourcePrimKey());
		List<AssetCategory> categoryList = AssetCategoryLocalServiceUtil.getCategories(JournalArticle.class.getName(), assetUtil.getClassPK());

		Optional<AssetCategory> firstCategory = categoryList.stream().findFirst();
		firstCategory.ifPresent(assetCategory -> newspaperDetail.setCategory(new Category(assetCategory.getTitle(languageIdString), assetCategory.getCategoryId())));

		if (article == null || !String.valueOf(article.getDDMStructureKey()).equals(structureId)) {
			_log.debug("Not Found");
			throw new PortalException(javax.ws.rs.core.Response.Status.NOT_FOUND.toString());
		}

		return newspaperDetail;
	}
}
