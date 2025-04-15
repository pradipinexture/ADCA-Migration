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
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
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
import com.liferay.journal.util.comparator.ArticleDisplayDateComparator;
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

import adc.dxp.rest.api.application.AdcDxpRestApiConfiguration;
import adc.dxp.rest.api.application.data.Category;
import adc.dxp.rest.api.application.data.DirectorMessage;
import adc.dxp.rest.api.application.data.comparator.JournalArticleTitleComparator;
import adc.dxp.rest.api.application.utils.Constants;
import adc.dxp.rest.api.application.utils.JournalArticleUtil;
import adc.dxp.rest.api.application.utils.PageUtils;
import adc.dxp.rest.api.application.utils.StructureUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsResource;
import org.osgi.service.component.annotations.ServiceScope;

/**
 *
 * Endpoints  of the Management Memssage
 *
 */
@Component(
		property = {
				"osgi.jaxrs.application.select=(osgi.jaxrs.name=ADC.Services)",
				"osgi.jaxrs.resource=true"
		},
		scope = ServiceScope.PROTOTYPE,
		service = ManagementMessageResource.class
)
@Path("/management-messages")
public class ManagementMessageResource extends BasicResource {

	/**
	 * logging instance
	 */
	private static Log _log = LogFactoryUtil.getLog(NewsResource.class);

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
	@Operation(
			description = "Retrieves the list of the Director Message. Results can be paginated, filtered, searched, and sorted."
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
	@Path("")
	@Produces(MediaType.APPLICATION_JSON)
	public Page<DirectorMessage> search(
			@Parameter(hidden = true) @QueryParam("id") String idString,
			@Parameter(hidden = true) @QueryParam("search") String search,
			@Parameter(hidden = true) @QueryParam("categoryId") String categoryIdParam,
			@Parameter(hidden = true) @QueryParam("startDate") String startDateParam,
			@Parameter(hidden = true) @QueryParam("endDate") String endDateParam,
			@QueryParam("pageSize") Integer pageSize,
			@Context Filter filter,
			@Context Pagination pagination,
			@Context Sort[] sorts,
			@Context HttpServletRequest request) throws PortalException {

		System.out.println("Entering search API with idString: " + idString + ", search: " + search + ", categoryId: " + categoryIdParam);
		int paginationSize = pageSize == null ? _dxpRESTConfiguration.paginationSize() : pageSize;
		int paginationPage = pagination.getPage();

		long companyId = PortalUtil.getCompanyId(request);
		String groupIdString = request.getHeader("groupId");
		long groupId = Long.valueOf(groupIdString).longValue();
		String languageIdString = request.getHeader("languageId");

		long id = idString != null && !idString.isEmpty() && !idString.equalsIgnoreCase("null") ? Long.valueOf(idString) : -1;
		long categoryId = categoryIdParam != null && !categoryIdParam.isEmpty() ? Long.valueOf(categoryIdParam) : -1;

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

		_log.debug("startDate2s: "+ startDate);
		_log.debug("endDate: "+ endDate);

		DDMStructure structure = StructureUtil.getStructureByNameEn(Constants.STRUCTURE_MANAGEMENT_MESSAGE_NAME_EN);
		System.out.println("Structure ID: " + structure.getStructureId());

		List<JournalArticle> results = JournalArticleUtil.searchJournalArticles(companyId, groupId, search, structure.getStructureId(), startDate, endDate, orderByComparator);
		System.out.println("Search results size: " + (results != null ? results.size() : 0));

		List<DirectorMessage> lastResults = new ArrayList<>();

		for (JournalArticle article : results) {
			System.out.println("Processing article with resourcePrimKey: " + article.getResourcePrimKey());

			DirectorMessage tellaStory = new DirectorMessage(article, request.getHeader(Constants.HEADER_LANGUAGE_ID));

			_log.debug("idString: "+ idString);
			_log.debug("tellaStory.getResourcePrimaryKey(): "+ tellaStory.getResourcePrimaryKey());
			_log.debug("id: "+ id);

			AssetEntry assetUtil = AssetEntryLocalServiceUtil.getEntry("com.liferay.journal.model.JournalArticle", article.getResourcePrimKey());
			tellaStory.setEntryId(assetUtil.getEntryId());

			List<AssetCategory> categoryList = AssetCategoryLocalServiceUtil.getCategories(JournalArticle.class.getName(), assetUtil.getClassPK());
			System.out.println("Category list size for article: " + categoryList.size());

			Optional<AssetCategory> firstCategory = categoryList.stream().findFirst();

			if (firstCategory.isPresent()) {
				AssetCategory catego = AssetCategoryLocalServiceUtil.getCategory(firstCategory.get().getCategoryId());
				tellaStory.setCategory(new Category(catego.getTitle(languageIdString), catego.getCategoryId()));
				System.out.println("Set category: " + catego.getTitle(languageIdString) + ", ID: " + catego.getCategoryId());
			}

			_log.debug("DisplayDate: " + tellaStory.getDisplayDate());
			_log.debug("ExpirationDate: " + tellaStory.getExpirationDate());

			if ((categoryIdParam == null || categoryIdParam.equalsIgnoreCase("-1")
					|| Long.compare(categoryId, tellaStory.getCategory().getCategoryId()) == 0)
					&& (lastResults.indexOf(tellaStory) == -1)
					&& !(Long.compare(tellaStory.getResourcePrimaryKey(), id) == 0)) {
				lastResults.add(tellaStory);
			}
		}

		//order by category
		if (sorts != null && sorts[0].getFieldName().equalsIgnoreCase("category")) {
			if (!sorts[0].isReverse()) {
				lastResults.sort((entity1, entity2) -> entity1.getCategory().getName().compareTo(entity2.getCategory().getName()));
			} else {
				lastResults.sort((entity1, entity2) -> entity2.getCategory().getName().compareTo(entity1.getCategory().getName()));
			}
		}
		System.out.println("Final results size: " + lastResults.size());
		return PageUtils.createPage(lastResults, pagination, lastResults.size());
	}

	/**
	 *
	 * Returns Management message by id
	 *
	 * @param request hidden parameter
	 * @return
	 * @throws PortalException
	 */
	@GET
	@Path("/{entryId}")
	@Operation(description = "Get a management message by id")
	@Parameters(value = { @Parameter(in = ParameterIn.PATH, name = "entryId") })
	@Produces(MediaType.APPLICATION_JSON)
	public DirectorMessage getStoryById(
			@Parameter(hidden = true) @PathParam("entryId") long entryId,
			@Context HttpServletRequest request) throws PortalException {

		_log.debug("Call get a management message");

		String groupIdString = request.getHeader("groupId");
		String languageIdString = request.getHeader("languageId");

		long companyId = PortalUtil.getCompanyId(request);
		long groupId = Long.valueOf(groupIdString);

		long structureId = StructureUtil.getStructureByNameEn(Constants.STRUCTURE_MANAGEMENT_MESSAGE_NAME_EN)
				.getStructureId();

		JournalArticle article = JournalArticleLocalServiceUtil.getLatestArticle(entryId);
		DirectorMessage entity = new DirectorMessage(article, request.getHeader(Constants.HEADER_LANGUAGE_ID));

		AssetEntry assetUtil = AssetEntryLocalServiceUtil.getEntry("com.liferay.journal.model.JournalArticle", article.getResourcePrimKey());

		_log.debug("JournalArticle: " + article);
		_log.debug("article.getResourcePrimKey(): " + article.getResourcePrimKey());

		List<AssetCategory> categoryList = AssetCategoryLocalServiceUtil.getCategories(JournalArticle.class.getName(), assetUtil.getClassPK());

		Optional<AssetCategory> firstCategory = categoryList.stream().findFirst();

		if (firstCategory.isPresent()) {
			AssetCategory catego = AssetCategoryLocalServiceUtil.getCategory(firstCategory.get().getCategoryId());
			entity.setCategory(new Category(catego.getTitle(languageIdString), catego.getCategoryId()));
			_log.debug("categoryName: " + catego.getName());
		}

		if (article == null || article.getDDMStructureId() != (structureId)) {
			_log.debug("Not Found");
			throw new PortalException(javax.ws.rs.core.Response.Status.NOT_FOUND.toString());
		}

		return entity;
	}
}
