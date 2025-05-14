package adc.dxp.rest.api.application.resources;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PATCH;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import adc.dxp.rest.api.application.data.Promotion;
import adc.dxp.rest.api.application.utils.*;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

import com.liferay.announcements.kernel.model.AnnouncementsFlag;
import com.liferay.announcements.kernel.model.AnnouncementsFlagConstants;
import com.liferay.announcements.kernel.service.AnnouncementsFlagLocalService;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.util.comparator.ArticleDisplayDateComparator;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.NestableException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import adc.dxp.rest.api.application.AdcDxpRestApiConfiguration;
import adc.dxp.rest.api.application.data.Category;
import adc.dxp.rest.api.application.data.Media;
import adc.dxp.rest.api.application.data.comparator.JournalArticleTitleComparator;
import adc.dxp.rest.api.application.data.vo.GalleryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Endpoints of galleries
 *
 * @author ricardo.gomes
 */
@Component(
		property = {
				"osgi.jaxrs.application.select=(osgi.jaxrs.name=ADC.Services)",
				"osgi.jaxrs.resource=true"
		},
		scope = ServiceScope.PROTOTYPE,
		service = MediaResource.class
)
@Path("/medias")
@Tag(name = "Media")
public class MediaResource {

	/**
	 * logging instance
	 */
	private static final Log _log = LogFactoryUtil.getLog(MediaResource.class);

	@Reference
	private Portal _portal;

	@Reference
	private JournalArticleLocalService _journalArticleLocalService;

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	@Reference
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Reference
	private DDMStructureLocalService _ddmStructureLocalService;

	@Reference
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Reference
	private AnnouncementsFlagLocalService _announcementsFlagLocalService;

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
	/**
	 * Returns all Galleries
	 */
	@GET
	@Path("/galleries")
	@Operation(description = "Get all galleries.")
	@Parameters(value = {
			@Parameter(in = ParameterIn.QUERY, name = "search"),
			@Parameter(in = ParameterIn.QUERY, name = "filter"),
			@Parameter(in = ParameterIn.QUERY, name = "page"),
			@Parameter(in = ParameterIn.QUERY, name = "pageSize"),
			@Parameter(in = ParameterIn.QUERY, name = "sort")
	})
	@Produces(MediaType.APPLICATION_JSON)
	public Page<Media> getAllGalleries(
			@QueryParam("pageSize") Integer pageSize,
			@Context HttpServletRequest request,
			@Parameter(hidden = true) @QueryParam("search") String search,
			@Parameter(hidden = true) @QueryParam("categoryId") String categoryIdParam,
			@Parameter(hidden = true) @QueryParam("startDate") String startDateParam,
			@Parameter(hidden = true) @QueryParam("endDate") String endDateParam,
			@Context Filter filter,
			@Context Pagination pagination,
			@Context Sort[] sorts,
			@HeaderParam(Constants.HEADER_GROUP_ID) long groupId) throws PortalException {

		_log.debug("Call all galleries " + groupId);

		// Pagination setup
		int paginationSize = pageSize == null ? _dxpRESTConfiguration.paginationSize() : pageSize;
		int paginationPage = pagination.getPage();

		// Get basic parameters
		long companyId = _portal.getCompanyId(request);
		String languageIdString = request.getHeader("languageId");

		if (groupId == 0) {
			String groupIdString = request.getHeader("groupId");
			if (groupIdString == null || groupIdString.isEmpty()) {
				throw new PortalException("Missing required header: groupId");
			}
			groupId = Long.valueOf(groupIdString);
		}

		// Category filter
		long categoryId = categoryIdParam != null && !categoryIdParam.isEmpty() && !categoryIdParam.equalsIgnoreCase("null") ?
				Long.valueOf(categoryIdParam) : -1;

		// Find structure ID
		String structureId = "";
		List<DDMStructure> structures = _ddmStructureLocalService.getStructures();
		for (DDMStructure structure : structures) {
			if (structure.getName(LocaleUtil.ENGLISH).equalsIgnoreCase(Constants.STRUCTURE_MEDIA_NAME_EN)) {
				structureId = structure.getStructureKey();
				_log.debug("Found structure ID: " + structureId);
				break;
			}
		}

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

		// Set up sorting
		OrderByComparator<JournalArticle> orderByComparator = null;

		if (sorts != null && sorts.length > 0) {
			Sort sort = sorts[0];
			if (sort.getFieldName().equalsIgnoreCase("displayDate")) {
				orderByComparator = new ArticleDisplayDateComparator(!sort.isReverse());
			} else if (sort.getFieldName().equalsIgnoreCase("title")) {
				orderByComparator = new JournalArticleTitleComparator(!sort.isReverse());
			}
		}

//		// Get all articles for the group
//		List<JournalArticle> allArticles = _journalArticleLocalService.getArticles(groupId);
//		_log.debug("Total articles in group: " + allArticles.size());
//
//		// Filter by approved status and structure ID
//		List<JournalArticle> filteredArticles = new ArrayList<>();
//		for (JournalArticle article : allArticles) {
//			if (article.getStatus() == WorkflowConstants.STATUS_APPROVED &&
//					structureId.equals(article.getDDMStructureKey())) {
//				filteredArticles.add(article);
//			}
//		}
//		_log.debug("Articles matching structure: " + filteredArticles.size());
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
//		// Process results
//		List<Media> lastResults = new ArrayList<>();

		DDMStructure structure = StructureUtil.getStructureByNameEn(Constants.STRUCTURE_MEDIA_NAME_EN);

		List<JournalArticle> results = JournalArticleUtil.searchJournalArticles(companyId, groupId, search, structure.getStructureKey(), startDate, endDate, orderByComparator);
		List<Media> lastResults = new ArrayList<>();

		for (JournalArticle article : results) {
			try {
				Media media = new Media(article, request.getHeader(Constants.HEADER_LANGUAGE_ID));
				// Get asset entry
				AssetEntry assetUtil = _assetEntryLocalService.getEntry(
						"com.liferay.journal.model.JournalArticle", article.getResourcePrimKey());
				media.setEntryId(assetUtil.getEntryId());

				// Get categories
				List<AssetCategory> categoryList = _assetCategoryLocalService.getCategories(
						"com.liferay.journal.model.JournalArticle", article.getResourcePrimKey());

				Optional<AssetCategory> firstCategory = categoryList.stream().findFirst();

				if (firstCategory.isPresent()) {
					AssetCategory catego = _assetCategoryLocalService.getCategory(firstCategory.get().getCategoryId());
					media.setCategory(new Category(catego.getTitle(languageIdString), catego.getCategoryId()));
				}

				// Apply category filter
				if ((categoryIdParam == null || categoryIdParam.equalsIgnoreCase("-1")
						|| Long.compare(categoryId, media.getCategory().getCategoryId()) == 0)
						&& (lastResults.indexOf(media) == -1)) {
					lastResults.add(media);
				}
			} catch (Exception e) {
				_log.error("Error processing article: " + article.getArticleId(), e);
			}
		}

		// Order by category if needed
		if (sorts != null && sorts.length > 0 && sorts[0].getFieldName().equalsIgnoreCase("category")) {
			if (!sorts[0].isReverse()) {
				// asc
				lastResults.sort((media1, media2) -> media1.getCategory().getName().compareTo(media2.getCategory().getName()));
			} else {
				// desc
				lastResults.sort((media1, media2) -> media2.getCategory().getName().compareTo(media1.getCategory().getName()));
			}
		}

//		// Handle pagination
//		int paginationSize = pageSize == null ? _dxpRESTConfiguration.paginationSize() : pageSize;
//		int paginationPage = pagination.getPage();
//		int fromIndex = paginationPage != 1 ? ((paginationPage - 1) * paginationSize) : 0;
//		int toIndex = paginationPage != 1 ? paginationPage * paginationSize : paginationSize;
//
//		if (toIndex > lastResults.size()) {
//			toIndex = lastResults.size();
//		}
//
//		if (fromIndex >= lastResults.size()) {
//			return Page.of(new ArrayList<>(), pagination, lastResults.size());
//		}

		// Handle pagination
		int start = (paginationPage - 1) * paginationSize;
		int end = Math.min(start + paginationSize, lastResults.size());

		List<Media> pageResults = start < lastResults.size() ?
				lastResults.subList(start, end) : Collections.emptyList();

		return Page.of(pageResults, pagination, lastResults.size());

//		return Page.of(lastResults.subList(fromIndex, toIndex), pagination, lastResults.size());

	}

	/**
	 * Returns gallery by ID
	 */
	@GET
	@Path("/galleries/{id}")
	@Operation(description = "Get gallery by ID.")
	@Parameters(value = { @Parameter(in = ParameterIn.PATH, name = "id") })
	@Produces(MediaType.APPLICATION_JSON)
	public GalleryVO getGallery(
			@Parameter(hidden = true) @PathParam("id") long id,
			@Context HttpServletRequest request,
			@HeaderParam(Constants.HEADER_GROUP_ID) long groupId) throws PortalException {

		_log.debug("Call get gallery with ID: " + id);

		// Get language ID from request
		String languageIdString = request.getHeader("languageId");

		// Find structure by name
		String structureKey = "";
		List<DDMStructure> structures = _ddmStructureLocalService.getStructures();
		for (DDMStructure structure : structures) {
			if (structure.getName(LocaleUtil.ENGLISH).equalsIgnoreCase(Constants.STRUCTURE_MEDIA_NAME_EN)) {
				structureKey = structure.getStructureKey();
				break;
			}
		}

		// Get the article
		JournalArticle article = _journalArticleLocalService.getLatestArticle(id);

		if (article == null || !article.getDDMStructureKey().equals(structureKey)) {
			throw new PortalException(javax.ws.rs.core.Response.Status.NOT_FOUND.toString());
		}

		// Create gallery VO
		GalleryVO result = new GalleryVO(
				new Media(article, request.getHeader(Constants.HEADER_LANGUAGE_ID)),
				new ArrayList<String>());

		// Get asset entry
		AssetEntry assetUtil = _assetEntryLocalService.getEntry(
				"com.liferay.journal.model.JournalArticle", article.getResourcePrimKey());

		// Get categories
		List<AssetCategory> categoryList = _assetCategoryLocalService.getCategories(
				"com.liferay.journal.model.JournalArticle", article.getResourcePrimKey());

		Optional<AssetCategory> firstCategory = categoryList.stream().findFirst();

		if (firstCategory.isPresent()) {
			AssetCategory catego = _assetCategoryLocalService.getCategory(firstCategory.get().getCategoryId());
			result.getMedia().setCategory(new Category(catego.getTitle(languageIdString), catego.getCategoryId()));
		}

		// Get related files
		try {
			DLFileEntry file = _dlFileEntryLocalService.getDLFileEntry(result.getMedia().getFileClassPk());
			List<DLFileEntry> files = _dlFileEntryLocalService.getFileEntries(groupId, file.getFolderId());

			for (DLFileEntry f : files) {
				result.getSrcs().add(FileUtil.getImageURL(f));
			}
		} catch (NestableException e) {
			_log.error("Error getting related files", e);
		}

		return result;
	}

	/**
	 * Mark entity as read
	 */
	@PATCH
	@Path("{entryId}/markRead")
	@Operation(description = "Mark entity as read.")
	@Parameters(value = { @Parameter(in = ParameterIn.PATH, name = "entryId") })
	@Produces(MediaType.APPLICATION_JSON)
	public AnnouncementsFlag markEntityAsRead(
			@PathParam("entryId") long entryId,
			@Context HttpServletRequest request) throws PortalException {

		_log.debug("Mark entity as read with ID: " + entryId);

		// Get user from request
		String userId = request.getRemoteUser();
		if (userId == null || userId.isEmpty()) {
			throw new PortalException("User not authenticated");
		}

		// Mark as read
		AnnouncementsFlag result = _announcementsFlagLocalService.addFlag(
				Long.valueOf(userId), entryId, AnnouncementsFlagConstants.HIDDEN);

		return result;
	}

	/**
	 * Utility method for get if the entity is read or not
	 */
	private Boolean isRead(long entryId, long userId) {
		DynamicQuery flagDynamicQuery = _announcementsFlagLocalService.dynamicQuery();

		flagDynamicQuery.add(PropertyFactoryUtil.forName("userId").eq(userId));
		flagDynamicQuery.add(PropertyFactoryUtil.forName("entryId").eq(entryId));
		flagDynamicQuery.add(PropertyFactoryUtil.forName("value").eq(AnnouncementsFlagConstants.HIDDEN));

		List<AnnouncementsFlag> flags = _announcementsFlagLocalService.dynamicQuery(flagDynamicQuery);

		return !flags.isEmpty();
	}
}