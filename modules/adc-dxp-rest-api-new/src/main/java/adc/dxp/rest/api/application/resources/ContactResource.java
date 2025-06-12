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

import adc.dxp.rest.api.application.AdcDxpRestApiConfiguration;
import adc.dxp.rest.api.application.data.DirectorMessage;
import adc.dxp.rest.api.application.utils.JournalArticleUtil;
import adc.dxp.rest.api.application.utils.PageUtils;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetCategoryLocalServiceUtil;
import com.liferay.asset.kernel.service.AssetEntryLocalServiceUtil;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.journal.util.comparator.ArticleDisplayDateComparator;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import adc.dxp.rest.api.application.data.Category;
import adc.dxp.rest.api.application.data.Contact;
import adc.dxp.rest.api.application.data.comparator.JournalArticleTitleComparator;
import adc.dxp.rest.api.application.utils.Constants;

import adc.dxp.rest.api.application.utils.StructureUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

@Component(
		property = {
				"osgi.jaxrs.application.select=(osgi.jaxrs.name=ADC.Services)",
				"osgi.jaxrs.resource=true"
		},
		scope = ServiceScope.PROTOTYPE,
		service = ContactResource.class
)
@Path("/contacts")
public class ContactResource {

	/**
	 * app instance
	 */
	@Reference
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private Portal _portal;

	private volatile AdcDxpRestApiConfiguration _dxpRESTConfiguration;

	@Activate
	protected void activate() {
		try {
			_dxpRESTConfiguration = _configurationProvider.getCompanyConfiguration(AdcDxpRestApiConfiguration.class, 0);
		} catch (ConfigurationException e) {
			_log.error("Error loading configuration", e);
		}
	}

	/**
	 * logging instance
	 */
	private static Log _log = LogFactoryUtil.getLog(ContactResource.class);

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
	public Page<Contact> search(
			@Parameter(hidden = true) @QueryParam("search") String search,
			@Parameter(hidden = true) @QueryParam("categoryId") String categoryIdParam,
			@Parameter(hidden = true) @QueryParam("excludeCategoryId") String excludeCategoryIdParam,
			@Parameter(hidden = true) @QueryParam("startDate") String startDateParam,
			@Parameter(hidden = true) @QueryParam("endDate") String endDateParam,
			@QueryParam("pageSize") Integer pageSize,
			@Context Filter filter,
			@Context Pagination pagination,
			@Context Sort[] sorts,
			@Context HttpServletRequest request) throws PortalException {

		int paginationSize = pageSize == null ? _dxpRESTConfiguration.paginationSize() : pageSize;
		int paginationPage = pagination.getPage();

		long companyId = PortalUtil.getCompanyId(request);

		String groupIdString = request.getHeader("groupId");
		long groupId = Long.valueOf(groupIdString).longValue();
		String languageIdString = request.getHeader("languageId");

		long categoryId = categoryIdParam != null && !categoryIdParam.isEmpty() ? Long.valueOf(categoryIdParam).longValue() : -1;
		String structureId = StructureUtil.getStructureByNameEn(Constants.STRUCTURE_CONTACT_NAME_EN).getStructureKey();

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
		}

		else if (sorts != null && sorts[0].getFieldName().equalsIgnoreCase("title")) {
			orderByComparator = new JournalArticleTitleComparator(!sorts[0].isReverse());
		}
		DDMStructure structure = StructureUtil.getStructureByNameEn(Constants.STRUCTURE_CONTACT_NAME_EN);

		List<JournalArticle> results = JournalArticleUtil.searchJournalArticles(companyId, groupId, search, structure.getStructureKey(), startDate, endDate, orderByComparator);

		List<Contact> lastResults = new ArrayList<>();
		for (JournalArticle article: results) {

			Contact contact = new Contact(article, request.getHeader(Constants.HEADER_LANGUAGE_ID));

			AssetEntry assetUtil = AssetEntryLocalServiceUtil.getEntry("com.liferay.journal.model.JournalArticle", article.getResourcePrimKey());
			contact.setEntryId(assetUtil.getEntryId());

			List<AssetCategory> categoryList = _assetCategoryLocalService.getCategories(JournalArticle.class.getName(), assetUtil.getClassPK());

			Optional<AssetCategory> firstCategory = categoryList.stream().findFirst();

			if (firstCategory.isPresent()) {
				AssetCategory catego = AssetCategoryLocalServiceUtil.getCategory(firstCategory.get().getCategoryId());
				contact.setCategory(new Category(catego.getTitle(languageIdString), catego.getCategoryId()));
			}

			DynamicQuery query = DynamicQueryFactoryUtil.forClass(AssetCategory.class, AssetCategory.class.getClassLoader()).add(PropertyFactoryUtil.forName("name").eq(excludeCategoryIdParam));
			long excludeCatID = -1;

			List<AssetCategory> categories = AssetCategoryLocalServiceUtil.dynamicQuery(query, 0, 1); // we only want to first one
			if (categories.size() > 0) {
				AssetCategory cat = categories.get(0);
				excludeCatID = cat.getPrimaryKey();

			}

			if ((categoryIdParam == null || categoryIdParam.equalsIgnoreCase("-1")
					|| Long.compare(categoryId, contact.getCategory().getCategoryId()) == 0)
					&& (lastResults.indexOf(contact) == -1)) {
				if (!(Long.compare(excludeCatID, contact.getCategory().getCategoryId()) == 0)) {
					lastResults.add(contact);
				}

			}
		}

		//order by category
		if (sorts != null && sorts[0].getFieldName().equalsIgnoreCase("category")) {

			if (!sorts[0].isReverse())
			{
				//asc
				lastResults.sort((entity1, entity2) -> entity1.getCategory().getName().compareTo(entity2.getCategory().getName()));
			}
			else
			{
				//desc
				lastResults.sort((entity1, entity2) -> entity2.getCategory().getName().compareTo(entity1.getCategory().getName()));
			}
		}

		int start = (paginationPage - 1) * paginationSize;
		int end = Math.min(start + paginationSize, lastResults.size());
		List<Contact> pageResults = start < lastResults.size() ?
				lastResults.subList(start, end) : Collections.emptyList();

		return Page.of(pageResults, pagination, lastResults.size());
	}

	/**
	 *
	 * Returns Contact  by id
	 *
	 * @param request hidden parameter
	 * @return
	 * @throws PortalException
	 */
	@GET
	@Path("/{entryId}")
	@Operation(description = "Get a contact  by id")
	@Parameters(value = { @Parameter(in = ParameterIn.PATH, name = "entryId") })
	@Produces(MediaType.APPLICATION_JSON)
	public Contact getStoryById(
			@Parameter(hidden = true) @PathParam("entryId") long entryId,
			@Context HttpServletRequest request
	) throws PortalException {

		_log.debug("Call get a contact");

		String groupIdString = request.getHeader("groupId");
		String languageIdString = request.getHeader("languageId");

		long companyId = PortalUtil.getCompanyId(request);

		long groupId = Long.valueOf(groupIdString).longValue();

		String structureId = StructureUtil.getStructureByNameEn(Constants.STRUCTURE_CONTACT_NAME_EN)
				.getStructureKey();

		JournalArticle article = JournalArticleLocalServiceUtil.getLatestArticle(entryId);
		Contact entity = new Contact(article, request.getHeader(Constants.HEADER_LANGUAGE_ID));

		AssetEntry assetUtil = AssetEntryLocalServiceUtil.getEntry("com.liferay.journal.model.JournalArticle", article.getResourcePrimKey());

		List<AssetCategory> categoryList = _assetCategoryLocalService.getCategories(JournalArticle.class.getName(), assetUtil.getClassPK());

		Optional<AssetCategory> firstCategory = categoryList.stream().findFirst();

		if (firstCategory.isPresent()) {
			AssetCategory catego = AssetCategoryLocalServiceUtil.getCategory(firstCategory.get().getCategoryId());
			entity.setCategory(new Category(catego.getTitle(languageIdString), catego.getCategoryId()));
		}

		if (article == null || !String.valueOf(article.getDDMStructureKey()).equals(structureId)) {
			_log.debug("Not Found");
			throw new PortalException(javax.ws.rs.core.Response.Status.NOT_FOUND.toString());
		}

		return entity;
	}
}