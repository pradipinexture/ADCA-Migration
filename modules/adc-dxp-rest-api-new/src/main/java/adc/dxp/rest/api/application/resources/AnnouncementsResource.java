package adc.dxp.rest.api.application.resources;

import adc.dxp.rest.api.application.AdcDxpRestApiConfiguration;
import adc.dxp.rest.api.application.data.Announcements;
import adc.dxp.rest.api.application.data.Category;
import adc.dxp.rest.api.application.utils.Constants;
import adc.dxp.rest.api.application.utils.PageUtils;
import adc.dxp.rest.api.application.utils.UserUtil;
import com.liferay.announcements.kernel.model.AnnouncementsEntry;
import com.liferay.announcements.kernel.model.AnnouncementsFlag;
import com.liferay.announcements.kernel.service.AnnouncementsEntryLocalServiceUtil;
import com.liferay.announcements.kernel.service.AnnouncementsFlagLocalServiceUtil;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetCategoryLocalServiceUtil;
import com.liferay.asset.kernel.service.AssetEntryLocalServiceUtil;
import com.liferay.asset.kernel.service.AssetEntryServiceUtil;
import com.liferay.asset.kernel.service.persistence.AssetEntryQuery;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import io.swagger.v3.oas.annotations.Operation;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsResource;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component(
		property = {
				"osgi.jaxrs.application.select=(osgi.jaxrs.name=ADC.Services)",
				"osgi.jaxrs.resource=true"
		},
		scope = ServiceScope.PROTOTYPE,
		service = AnnouncementsResource.class
)
@JaxrsResource
@Path("/announcements")
public class AnnouncementsResource {

	private static final Log _log = LogFactoryUtil.getLog(AnnouncementsResource.class);
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
	@GET
	@Path("")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(description = "Get all announcements.")
	public List<AnnouncementsEntry> getAllAnnouncements(
			@QueryParam("isRead") Boolean isRead,
			@Context HttpServletRequest request) throws PortalException {

		User currentUser = UserUtil.getCurrentUser(request, null);
		List<AnnouncementsEntry> result = new ArrayList<>();

		DynamicQuery adq = AnnouncementsEntryLocalServiceUtil.dynamicQuery()
				.add(PropertyFactoryUtil.forName("userId").eq(currentUser.getUserId()))
				.add(PropertyFactoryUtil.forName("companyId").eq(currentUser.getCompanyId()))
				.add(PropertyFactoryUtil.forName("displayDate").le(new Date()))
				.add(PropertyFactoryUtil.forName("expirationDate").ge(new Date()));

		List<AnnouncementsEntry> entries = AnnouncementsEntryLocalServiceUtil.dynamicQuery(adq);

		for (AnnouncementsEntry e : entries) {
			if (isRead == null || isRead.equals(isRead(e.getEntryId(), currentUser.getUserId()))) {
				result.add(e);
			}
		}

		return result;
	}

	@PATCH
	@Path("{entryId}/markRead")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(description = "Mark announcement as read.")
	public AnnouncementsFlag markAnnouncementRead(
			@PathParam("entryId") long entryId,
			@Context HttpServletRequest request) throws PortalException {

		User currentUser = UserUtil.getCurrentUser(request, null);
		return AnnouncementsFlagLocalServiceUtil.addFlag(
				currentUser.getUserId(), entryId,
				com.liferay.announcements.kernel.model.AnnouncementsFlagConstants.HIDDEN
		);
	}

	private Boolean isRead(long entryId, long userId) {
		DynamicQuery dq = AnnouncementsFlagLocalServiceUtil.dynamicQuery()
				.add(PropertyFactoryUtil.forName("entryId").eq(entryId))
				.add(PropertyFactoryUtil.forName("userId").eq(userId))
				.add(PropertyFactoryUtil.forName("value").eq(com.liferay.announcements.kernel.model.AnnouncementsFlagConstants.HIDDEN));
		return !AnnouncementsFlagLocalServiceUtil.dynamicQuery(dq).isEmpty();
	}

	@GET
	@Path("/web-content")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(description = "Get all announcements from web content.")
	public Page<Announcements> getAllAnnouncementsFromWebContent(
			@QueryParam("onlyIsNotRead") Boolean onlyIsNotRead,
			@QueryParam("search") String search,
			@QueryParam("categoryId") String categoryIdParam,
			@QueryParam("startDate") String startDateParam,
			@QueryParam("endDate") String endDateParam,
			@QueryParam("pageSize") Integer pageSize,
			@Context Filter filter,
			@Context Pagination pagination,
			@Context Sort[] sorts,
			@Context HttpServletRequest request) throws PortalException {

		long groupId = GetterUtil.getLong(request.getHeader("groupId"));
		User currentUser = UserUtil.getCurrentUser(request, null);
		String languageId = currentUser.getLanguageId();

		List<JournalArticle> allArticles = JournalArticleLocalServiceUtil.getArticles(groupId);

		List<JournalArticle> filteredArticles = new ArrayList<>();

		for (JournalArticle article : allArticles) {
			if (article.getStatus() != WorkflowConstants.STATUS_APPROVED) {
				continue;
			}
			if (Validator.isNotNull(categoryIdParam)) {
				long categoryId = GetterUtil.getLong(categoryIdParam);
				List<Long> entryIds = getAssetEntryIdsByCategoryId(categoryId);
				if (!entryIds.contains(article.getResourcePrimKey())) {
					continue;
				}
			}
			try {
				Date displayDate = article.getDisplayDate();
				if (Validator.isNotNull(startDateParam)) {
					Date startDate = new SimpleDateFormat("yyyy-MM-dd").parse(startDateParam);
					if (displayDate.before(startDate)) continue;
				}
				if (Validator.isNotNull(endDateParam)) {
					Date endDate = new SimpleDateFormat("yyyy-MM-dd").parse(endDateParam);
					if (displayDate.after(endDate)) continue;
				}
			} catch (ParseException e) {
				_log.error("Date parse error", e);
			}

			filteredArticles.add(article);
		}

		// Manual search by title/description
		if (Validator.isNotNull(search)) {
			String searchLower = search.toLowerCase();
			filteredArticles = filteredArticles.stream()
					.filter(article -> {
						String title = article.getTitle(languageId).toLowerCase();
						String desc = article.getDescription(languageId).toLowerCase();
						return title.contains(searchLower) || desc.contains(searchLower);
					})
					.collect(Collectors.toList());
		}

		int total = filteredArticles.size();

		// Paginate manually
		int start = pagination.getStartPosition();
		int end = Math.min(pagination.getEndPosition(), total);
		List<JournalArticle> paginatedArticles = filteredArticles.subList(start, end);

		List<Announcements> announcements = new ArrayList<>();
		for (JournalArticle article : paginatedArticles) {
			announcements.add(convertToAnnouncement(article, languageId));
		}

		return PageUtils.createPage(announcements, pagination, total);
	}


	@GET
	@Path("/web-content/{entryId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(description = "Fetch announcement by entryId.")
	public Announcements fetchAnnouncement(
			@PathParam("entryId") long entryId,
			@Context HttpServletRequest request) throws PortalException {

		JournalArticle article = JournalArticleLocalServiceUtil.getLatestArticle(entryId);
		return convertToAnnouncement(article, request.getHeader(Constants.HEADER_LANGUAGE_ID));
	}

	private Announcements convertToAnnouncement(JournalArticle article, String languageId) throws PortalException {
		Announcements announcement = new Announcements(article, languageId);
		AssetEntry assetUtil = AssetEntryLocalServiceUtil.getEntry("com.liferay.journal.model.JournalArticle", article.getResourcePrimKey());

		List<AssetCategory> categoryList = AssetCategoryLocalServiceUtil.getCategories(JournalArticle.class.getName(), assetUtil.getClassPK());
		Optional<AssetCategory> firstCategory = categoryList.stream().findFirst();

		firstCategory.ifPresent(c -> announcement.setCategory(new Category(c.getTitle(languageId), c.getCategoryId())));

		return announcement;
	}

	private List<Long> getAssetEntryIdsByCategoryId(long categoryId) throws PortalException {
		List<Long> assetEntryIds = new ArrayList<>();

		AssetEntryQuery assetEntryQuery = new AssetEntryQuery();
		assetEntryQuery.setAnyCategoryIds(new long[] { categoryId });

		List<AssetEntry> assetEntries = AssetEntryServiceUtil.getEntries(assetEntryQuery);

		for (AssetEntry assetEntry : assetEntries) {
			assetEntryIds.add(assetEntry.getEntryId());
		}

		return assetEntryIds;
	}
}
