package adc.dxp.rest.api.application.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import adc.dxp.rest.api.application.AdcDxpRestApiConfiguration;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetLink;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalServiceUtil;
import com.liferay.asset.kernel.service.AssetEntryLocalServiceUtil;
import com.liferay.asset.kernel.service.AssetEntryServiceUtil;
import com.liferay.asset.kernel.service.AssetLinkLocalServiceUtil;
import com.liferay.asset.kernel.service.AssetVocabularyLocalServiceUtil;
import com.liferay.asset.kernel.service.persistence.AssetEntryQuery;
import com.liferay.calendar.model.CalendarBooking;
import com.liferay.calendar.service.CalendarBookingLocalServiceUtil;
import com.liferay.calendar.service.CalendarLocalServiceUtil;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.service.DLFileEntryLocalServiceUtil;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.journal.service.JournalArticleServiceUtil;
import com.liferay.portal.kernel.dao.orm.Criterion;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import adc.dxp.rest.api.application.AdcDxpRestApiApplication;
import adc.dxp.rest.api.application.data.Announcements;
import adc.dxp.rest.api.application.data.BaseContent;
import adc.dxp.rest.api.application.data.Content;
import adc.dxp.rest.api.application.data.Document;
import adc.dxp.rest.api.application.data.Highlight;
import adc.dxp.rest.api.application.data.Knowledge;
import adc.dxp.rest.api.application.data.Media;
import adc.dxp.rest.api.application.data.News;
import adc.dxp.rest.api.application.data.Newspaper;
import adc.dxp.rest.api.application.data.Promotion;
import adc.dxp.rest.api.application.data.QuickLinks;
import adc.dxp.rest.api.application.data.Contacts;
import adc.dxp.rest.api.application.data.RelatedBaseCategories;
import adc.dxp.rest.api.application.data.TellaStory;
import adc.dxp.rest.api.application.data.vo.CalendarBookingVO;
import adc.dxp.rest.api.application.utils.Constants;
import adc.dxp.rest.api.application.utils.ContentType;
import adc.dxp.rest.api.application.utils.LookupValue;
import adc.dxp.rest.api.application.utils.PageUtils;
import adc.dxp.rest.api.application.utils.StructureType;
import adc.dxp.rest.api.application.utils.TransformUtils;
import adc.dxp.rest.api.application.utils.VocabularyType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 *
 * Endpoints  of the Related
 *
 * @author ana.cavadas
 *
 */
@Component(
		property = {
				"osgi.jaxrs.application.select=(osgi.jaxrs.name=ADC.Services)",
				"osgi.jaxrs.resource=true"
		},
		scope = ServiceScope.PROTOTYPE,
		service = RelatedResource.class
)
@Path("/related")
public class RelatedResource {


	/**
	 * app instance
	 */
	private AdcDxpRestApiApplication _app;

	/**
	 * logging instance
	 */
	private static Log _log = LogFactoryUtil.getLog(RelatedResource.class);

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
			description = "Retrieves the list of the News. Results can be paginated, filtered, searched, and sorted."
	)
	@Parameters(
			value = {
					@Parameter(in = ParameterIn.QUERY, name = "id")
			}
	)
	@Path("/categories")
	@Produces(MediaType.APPLICATION_JSON)
	public RelatedBaseCategories categories(
			@Parameter(hidden = true) @QueryParam("id") String idString,
			@DefaultValue(value = "com.liferay.journal.model.JournalArticle") @QueryParam("className") String className,
			@QueryParam("pageSize") Integer pageSize,
			@Context Filter filter,
			@Context Pagination pagination,
			@Context Sort[] sorts,
			@Context HttpServletRequest request
	) throws PortalException {

		int paginationSize = _dxpRESTConfiguration.paginationSize();

		String languageIdRequest = request.getHeader(Constants.HEADER_LANGUAGE_ID);

		String languageId = languageIdRequest != null ? languageIdRequest : Constants.DEFAULT_VALUE_LANGUAGE;


		long id = Long.valueOf(idString).longValue();

		List<Content> baseContentResults = new ArrayList<>();

		AssetEntry assetUtil = AssetEntryLocalServiceUtil.getEntry(className, id);

		long categoryId = 0;

		String categoryName = "";

		if (className.contains("JournalArticle")) {

			List<AssetCategory> categoryList = AssetCategoryLocalServiceUtil.getCategories(JournalArticle.class.getName(), assetUtil.getClassPK());

			Optional<AssetCategory> categoryListOptional = categoryList.stream().findFirst();

			if (!categoryListOptional.isPresent()) return new RelatedBaseCategories();

			categoryId = categoryListOptional.get().getCategoryId();

			_log.debug("categoryId: " + categoryId);

			AssetCategory catego = AssetCategoryLocalServiceUtil.getCategory(categoryId);

			_log.debug("categoryName: " + catego.getTitle(languageId));

			categoryName = catego.getTitle(languageId);

			AssetEntryQuery assetEntryQuery = new AssetEntryQuery();
			assetEntryQuery.setAnyCategoryIds(new long[] { categoryId }); //category Id
			assetEntryQuery.setOrderByCol1("publishDate");
			assetEntryQuery.setOrderByType1("DESC");
			Integer pageSizeNew = pageSize != null ? new Integer(pageSize.intValue() + 1) : _dxpRESTConfiguration.paginationSize();
			assetEntryQuery.setEnd(pageSizeNew);

			List<AssetEntry> assetEntryList = AssetEntryLocalServiceUtil.getEntries(assetEntryQuery);

			_log.debug("ids: " + assetEntryList.size());



			for (AssetEntry ae: assetEntryList) {
				_log.debug("ae: " + ae);


				AssetEntry assetEntry = AssetEntryLocalServiceUtil.getEntry(ae.getEntryId());
				JournalArticle articleCateg = JournalArticleServiceUtil.getLatestArticle(assetEntry.getClassPK());

				String structureName = TransformUtils.getElement(articleCateg.getDDMStructure().getName());
				StructureType type = StructureType.getEnumByValue(structureName);

				Optional<String> language = Optional.of(languageId == null ? Constants.DEFAULT_VALUE_LANGUAGE : languageId);
				BaseContent newsCategory = new BaseContent(articleCateg, language);

				switch(type) {
					case HIGHLIGHT:
						newsCategory.setType(ContentType.HIGHLIGHT);
						break;
					case NEWS:
						newsCategory = new News(articleCateg, request.getHeader(Constants.HEADER_LANGUAGE_ID));
						break;
					case NEWSPAPER:
						newsCategory = new Newspaper(articleCateg, request.getHeader(Constants.HEADER_LANGUAGE_ID));
						break;
					case TELL_A_STORY:
						newsCategory.setType(ContentType.TELL_A_STORY);
						break;
					case MEDIA:
						newsCategory.setType(ContentType.MEDIA);
						break;
					case PROMOTION:
						newsCategory.setType(ContentType.PROMOTION);
						break;
					case QUICK_LINK:
						newsCategory.setType(ContentType.QUICK_LINK);
						break;
					case CONTACT:
						newsCategory.setType(ContentType.CONTACT);
						break;
					case ANNOUNCEMENT:
						newsCategory.setType(ContentType.ANNOUNCEMENT);
						break;
					case KNOWLEDGE_SHARING:
						newsCategory.setType(ContentType.KNOWLEDGE_SHARING);
						break;
					default:
						break;

				}


				if (newsCategory.getResourcePrimaryKey() != id) {
					baseContentResults.add(newsCategory);
				}

				_log.debug("articleCateg: " + newsCategory.toString());
				_log.debug("articleCateg: " + newsCategory.getTitle());


			}
		}
		if (className.contains("CalendarBooking")) {
			//Events

			CalendarBooking booking = CalendarBookingLocalServiceUtil.fetchCalendarBooking(assetUtil.getClassPK());

			categoryId = booking.getCalendarId();
			categoryName = booking.getCalendar().getName(languageId);

			DynamicQuery bookingsDQ = CalendarBookingLocalServiceUtil.dynamicQuery();
			Criterion calendarsCriterion = RestrictionsFactoryUtil.eq("calendarId", categoryId);
			bookingsDQ.add(calendarsCriterion);

			Criterion statusCriterion = RestrictionsFactoryUtil.eq("status", WorkflowConstants.STATUS_APPROVED);
			bookingsDQ.add(statusCriterion);

			List<CalendarBooking> bookings = CalendarLocalServiceUtil.dynamicQuery(bookingsDQ, QueryUtil.ALL_POS,QueryUtil.ALL_POS);

			for (CalendarBooking event: bookings) {

				if (event.getCalendarBookingId() != id) {
					CalendarBookingVO eventVO = new CalendarBookingVO(event, request.getHeader(Constants.HEADER_LANGUAGE_ID));
					_log.debug("Calendar: " + eventVO);

					baseContentResults.add(eventVO);
				}

			}

		}

		return new RelatedBaseCategories(PageUtils.createPage(baseContentResults, pagination, baseContentResults.size()), categoryId, categoryName);
	}

	@GET
	@Operation(
			description = "Retrieves the list of the related contents. Results can be paginated, filtered, searched, and sorted."
	)
	@Parameters(
			value = {
					@Parameter(in = ParameterIn.QUERY, name = "id")
			}
	)
	@Path("/assets")
	@Produces(MediaType.APPLICATION_JSON)
	public Page<Content> related(
			@Parameter(hidden = true) @QueryParam("id") String idString,
			@DefaultValue(value = "com.liferay.journal.model.JournalArticle") @QueryParam("className") String className,
			@Context HttpServletRequest request
	) throws PortalException {

		//int paginationSize = _app._dxpRESTConfiguration.paginationSize();
		String groupIdString = request.getHeader("groupId");

		long id = Long.valueOf(idString).longValue();

		AssetEntry assetUtil = AssetEntryLocalServiceUtil.getEntry(className, id);

		List<AssetLink> assetLinkList = AssetLinkLocalServiceUtil.getDirectLinks(assetUtil.getEntryId());

		List<Content> baseContentList = new ArrayList<>();

		for (AssetLink ae: assetLinkList) {

			AssetEntry assetEntry = AssetEntryServiceUtil.getEntry(ae.getEntryId2());

			if (assetEntry.getClassName().contains("JournalArticle")) {

				JournalArticle articleRelated = JournalArticleLocalServiceUtil.fetchLatestArticle(assetEntry.getClassPK(), WorkflowConstants.STATUS_APPROVED);

				String structureName = articleRelated.getDDMStructure().getName(Locale.ENGLISH);
				StructureType type = StructureType.getEnumByValue(structureName);

				if (type == null) {
					continue;
				}

				String languageId = request.getHeader(Constants.HEADER_LANGUAGE_ID);

				BaseContent base = null;
				switch(type) {
					case HIGHLIGHT:
						base = new Highlight(articleRelated, languageId);
						break;
					case NEWS:
						base = new News(articleRelated, languageId);
						break;
					case NEWSPAPER:
						base = new Newspaper(articleRelated, languageId);
						break;
					case TELL_A_STORY:
						base = new TellaStory(articleRelated, languageId);
						break;
					case MEDIA:
						base = new Media(articleRelated, languageId);
						break;
					case PROMOTION:
						base = new Promotion(articleRelated, languageId);
						break;
					case QUICK_LINK:
						base = new QuickLinks(articleRelated, languageId);
						break;
					case CONTACT:
						base = new Contacts(articleRelated, languageId);
						break;
					case ANNOUNCEMENT:
						base = new Announcements(articleRelated, languageId);
						break;
					case KNOWLEDGE_SHARING:
						//base = new Knowledge(articleRelated, languageId);
						base.setType(ContentType.KNOWLEDGE_SHARING);
						break;
					default:
						break;

				}

				if (base != null) {
					baseContentList.add(base);
				}
			}
			if (assetEntry.getClassName().contains("CalendarBooking")) {
				//Events

				CalendarBooking booking = CalendarBookingLocalServiceUtil.fetchCalendarBooking(assetEntry.getClassPK());
				baseContentList.add(new CalendarBookingVO(booking, request.getHeader(Constants.HEADER_LANGUAGE_ID)));

			}

			if (assetEntry.getClassName().contains("com.liferay.document.library.kernel.model.DLFileEntry")) {
				//Documents
				DLFileEntry file = DLFileEntryLocalServiceUtil.getFileEntry(assetEntry.getClassPK());

				baseContentList.add(new Document(file, groupIdString));
			}

		}

		return PageUtils.createPage(baseContentList, Pagination.of(1,999), baseContentList.size());
	}

	@GET
	@Operation(
			description = "Retrieves the list of the News. Results can be paginated, filtered, searched, and sorted."
	)
	@Parameters(
			value = {
					@Parameter(in = ParameterIn.QUERY, name = "id")
			}
	)
	@Path("/categoriesList")
	@Produces(MediaType.APPLICATION_JSON)
	public List<LookupValue> categoriesList(
			@Parameter(hidden = true) @QueryParam("type") String type,
			@Parameter(hidden = true) @QueryParam("excludeCategoryId") String excludeCategoryIdParam,
			@Context HttpServletRequest request
	) throws PortalException {

		_log.debug("Related Resource - categoriesList");

		String languageId = request.getHeader(Constants.HEADER_LANGUAGE_ID);

		List<LookupValue> categoryList = new ArrayList<>();

		if (!type.equalsIgnoreCase("events")) {
			DynamicQuery dynamicQuery = AssetVocabularyLocalServiceUtil.dynamicQuery();

			Property structureIdProperty = PropertyFactoryUtil.forName("name");

			VocabularyType vocabularyType = VocabularyType.getEnumByType(type);

			if (vocabularyType != null) {
				Criterion criterion = structureIdProperty.like(vocabularyType.getValue());
				dynamicQuery.add(criterion);
			}

			//To exclude a category
			DynamicQuery query = DynamicQueryFactoryUtil.forClass(AssetCategory.class,AssetCategory.class.getClassLoader()).add(PropertyFactoryUtil.forName("name").eq(excludeCategoryIdParam));
			long excludeCatID = -1;

			List<AssetCategory> categories = AssetCategoryLocalServiceUtil.dynamicQuery(query, 0, 1); // we only want to first one

			if (categories.size() > 0) {
				AssetCategory cat = categories.get(0);
				excludeCatID = cat.getPrimaryKey();
			}

			//Get all categories by type
			List<AssetVocabulary> vocabularyList = AssetVocabularyLocalServiceUtil.dynamicQuery(dynamicQuery, QueryUtil.ALL_POS, QueryUtil.ALL_POS);

			Optional<AssetVocabulary> vocabulary = vocabularyList.stream().findFirst();

			if (vocabulary.isPresent()) {

				for (AssetCategory cat: vocabulary.get().getCategories()) {

					if (!(Long.compare(cat.getPrimaryKey(), excludeCatID) == 0)) {
						categoryList.add(new LookupValue(cat.getTitle(languageId), cat.getCategoryId()));
					}

				}

			}
		}
		return categoryList;
	}

	@GET
	@Operation(
			description = "Retrieves category detail."
	)
	@Parameters(
			value = {
					@Parameter(in = ParameterIn.QUERY, name = "name")
			}
	)
	@Path("/categoryDetail")
	@Produces(MediaType.APPLICATION_JSON)
	public LookupValue categoryDetail(
			@Parameter(hidden = true) @QueryParam("name") String name,
			@Context HttpServletRequest request,
			@HeaderParam(Constants.HEADER_GROUP_ID) long groupId
	) throws PortalException {


		List<AssetCategory> categories = new ArrayList<>();

		DynamicQuery query = DynamicQueryFactoryUtil.forClass(AssetCategory.class, AssetCategory.class.getClassLoader()).add(PropertyFactoryUtil.forName("name").eq(name));

		try {
			categories = AssetCategoryLocalServiceUtil.dynamicQuery(query, 0, 1); // we only want to first one
			//categories = AssetCategoryLocalServiceUtil.search(groupId, name, null, 0, 1);
			if (categories.size() > 0) {
				AssetCategory cat = categories.get(0);
				return new LookupValue(cat.getName(), cat.getPrimaryKey());
			}

		} catch (SystemException e) {
			_log.error(e);
		}
		return null;
	}
}