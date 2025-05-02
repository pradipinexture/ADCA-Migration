package adc.dxp.rest.api.application.resources;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import adc.dxp.rest.api.application.data.News;
import adc.dxp.rest.api.application.utils.JournalArticleUtil;
import adc.dxp.rest.api.application.utils.PageUtils;
import adc.dxp.rest.api.application.utils.StructureUtil;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.portal.kernel.dao.orm.*;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.util.Validator;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.service.JournalArticleService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import adc.dxp.rest.api.application.AdcDxpRestApiConfiguration;
import adc.dxp.rest.api.application.data.Category;

import adc.dxp.rest.api.application.utils.Constants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * Endpoints of the News
 *
 * @author ana.cavadas
 */
@Component(
        property = {
                "osgi.jaxrs.application.select=(osgi.jaxrs.name=ADC.Services)",
                "osgi.jaxrs.resource=true"
        },
        scope = ServiceScope.PROTOTYPE,
        service = NewsResource.class
)
@Path("/news")
@Tag(name = "News")
public class NewsResource {
    /**
     * logging instance
     */
    private static final Log _log = LogFactoryUtil.getLog(NewsResource.class);

    @Reference
    private Portal _portal;

    @Reference
    private JournalArticleService _journalArticleService;

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
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    public Page<News> search(
            @Parameter(hidden = true) @QueryParam("search") String search,
            @Parameter(hidden = true) @QueryParam("categoryId") String categoryIdParam,
            @Parameter(hidden = true) @QueryParam("achievement") String achievementParam,
            @QueryParam("pageSize") Integer pageSize,
            @Context Pagination pagination,
            @Context HttpServletRequest request) throws PortalException {

        System.out.println("Inside the Search Module");

        // Pagination setup
        int paginationSize = pageSize == null ? _dxpRESTConfiguration.paginationSize() : pageSize;
        int paginationPage = pagination.getPage();

        // Get basic parameters
        long companyId = _portal.getCompanyId(request);
        String groupIdString = request.getHeader("groupId");
        String languageIdString = request.getHeader("languageId");

        if (groupIdString == null || groupIdString.isEmpty()) {
            throw new PortalException("Missing required header: groupId");
        }

        System.out.println("Group Id: " + groupIdString);
        long groupId = Long.valueOf(groupIdString);

        // Category filter
        long categoryId = categoryIdParam != null && !categoryIdParam.isEmpty() && !categoryIdParam.equalsIgnoreCase("null") ?
                Long.valueOf(categoryIdParam) : -1;

        String structureId = _structureResource.getStructure(groupId, Constants.STRUCTURE_NEWS_NAME_EN);
        System.out.println("Structure Id: " + structureId);

        // Achievement filter
        Boolean achievement = achievementParam != null && !achievementParam.isEmpty() && !achievementParam.equalsIgnoreCase("null") ?
                Boolean.valueOf(achievementParam) : null;

        // Get all articles for the group
        List<JournalArticle> allArticles = _journalArticleLocalService.getArticles(groupId);
        System.out.println("Total articles in group: " + allArticles.size());

        // Filter by approved status and structure ID
        List<JournalArticle> filteredArticles = new ArrayList<>();
        for (JournalArticle article : allArticles) {
            if (article.getStatus() == WorkflowConstants.STATUS_APPROVED &&
                    structureId.equals(article.getDDMStructureKey())) {
                filteredArticles.add(article);
            }
        }
        System.out.println("Articles matching structure: " + filteredArticles.size());


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
            System.out.println("Articles after search filter: " + filteredArticles.size());
        }

        // Process results
        List<News> lastResults = new ArrayList<>();

        for (JournalArticle article : filteredArticles) {
            try {
                // Check if this is the latest approved version
                if (_journalArticleLocalService.isLatestVersion(
                        groupId, article.getArticleId(), article.getVersion(), WorkflowConstants.STATUS_APPROVED)) {

                    News news = new News(article, request.getHeader(Constants.HEADER_LANGUAGE_ID));
                    // Check achievement filter
                    if (achievement == null || !achievement || achievement.compareTo(news.isAchievement()) == 0) {

                        // Get asset entry
                        AssetEntry assetUtil = _assetEntryLocalService.getEntry(
                                "com.liferay.journal.model.JournalArticle", article.getResourcePrimKey());
                        news.setEntryId(assetUtil.getEntryId());

                        // Get categories
                        List<AssetCategory> categoryList = _assetCategoryLocalService.getCategories(
                                "com.liferay.journal.model.JournalArticle", article.getResourcePrimKey());

                        Optional<AssetCategory> firstCategory = categoryList.stream().findFirst();

                        if (firstCategory.isPresent()) {
                            AssetCategory catego = _assetCategoryLocalService.getCategory(firstCategory.get().getCategoryId());
                            news.setCategory(new Category(catego.getTitle(languageIdString), catego.getCategoryId()));
                        }

                        // Check category filter
                        if ((categoryIdParam == null || categoryIdParam.equalsIgnoreCase("-1")
                                || Long.compare(categoryId, news.getCategory().getCategoryId()) == 0)
                                && (lastResults.indexOf(news) == -1)) {
                            lastResults.add(news);
                        }
                    }
                }
            } catch (Exception e) {
                _log.error("Error processing article: " + article.getArticleId(), e);
            }
        }

        System.out.println("Final news items: " + lastResults.size());

        // Handle pagination
        int start = (paginationPage - 1) * paginationSize;
        int end = Math.min(start + paginationSize, lastResults.size());

        List<News> pageResults = start < lastResults.size() ?
                lastResults.subList(start, end) : Collections.emptyList();

        return Page.of(pageResults, pagination, lastResults.size());
    }


    @GET
    @Operation(
            description = "Retrieves the list of the News. Results can be paginated, filtered, searched, and sorted."
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
    @Path("/searchArticle")
    @Produces(MediaType.APPLICATION_JSON)
    public Page<Document> searchArticle(
            @Parameter(hidden = true) @QueryParam("search") String search,
            @Parameter(hidden = true) @QueryParam("categoryId") String categoryIdParam,
            @Parameter(hidden = true) @QueryParam("achievement") String achievementParam,
            @Parameter(hidden = true) @QueryParam("startDate") String startDateParam,
            @Parameter(hidden = true) @QueryParam("endDate") String endDateParam,
            @QueryParam("pageSize") Integer pageSize,
            @Context Filter filter,
            @Context Pagination pagination,
            @Context Sort[] sorts,
            @Context HttpServletRequest request) throws PortalException {

        System.out.println("startDateParam: " + startDateParam);
        System.out.println("endDateParam: " + endDateParam);

        int paginationSize = pageSize == null ? _dxpRESTConfiguration.paginationSize() : pageSize;

        int paginationPage = pagination.getPage();
        System.out.println("paginationSize: " + paginationSize);
        System.out.println("paginationPage: " + paginationPage);

        long companyId = _portal.getCompanyId(request);

        String groupIdString = request.getHeader("groupId");
        String languageIdString = request.getHeader("languageId");

        long groupId = Long.valueOf(groupIdString).longValue();

        long categoryId = categoryIdParam != null && !categoryIdParam.isEmpty() && !categoryIdParam.equalsIgnoreCase("null") ?
                Long.valueOf(categoryIdParam).longValue() : -1;
        System.out.println("categoryId: " + categoryId);
        Boolean achievement = achievementParam != null && !achievementParam.isEmpty() && !achievementParam.equalsIgnoreCase("null") ?
                Boolean.valueOf(achievementParam) : null;
        DDMStructure structure = StructureUtil.getStructureByNameEn(Constants.STRUCTURE_NEWS_NAME_EN);
         // Date
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
            _log.error("Error parsing date", e);
        }

        List<Document> list = JournalArticleUtil.searchArticles(companyId,groupId,structure.getStructureKey(),search);

        return PageUtils.createPage(list,pagination,list.size());
    }
    /**
     * Returns news by id
     *
     * @param request hidden parameter
     * @return
     * @throws PortalException
     */
    @GET
    @Path("/detail")
    @Operation(description = "Get news by id")
    @Parameters(
            value = {
                    @Parameter(in = ParameterIn.QUERY, name = "id")
            }
    )
    @Produces(MediaType.APPLICATION_JSON)
    public News getNewsById(
            @Parameter(hidden = true) @QueryParam("id") String idString,
            @Context HttpServletRequest request
    ) throws PortalException {
        System.out.println("Call get news");
        String groupIdString = request.getHeader("groupId");
        String languageIdString = request.getHeader("languageId");

        long groupId = Long.valueOf(groupIdString).longValue();

        long id = Long.valueOf(idString).longValue();

        String structureId = _structureResource.getStructure(groupId, Constants.STRUCTURE_NEWS_NAME_EN);
        JournalArticle article = _journalArticleService.getLatestArticle(id);
        News newsDetail = new News(article, request.getHeader(Constants.HEADER_LANGUAGE_ID));

        AssetEntry assetUtil = _assetEntryLocalService.getEntry(
                "com.liferay.journal.model.JournalArticle", article.getResourcePrimKey());
        System.out.println("JournalArticle: " + article);

        System.out.println("article.getResourcePrimKey(): " + article.getResourcePrimKey());

        List<AssetCategory> categoryList = _assetCategoryLocalService.getCategories(
                "com.liferay.journal.model.JournalArticle", article.getResourcePrimKey());

        Optional<AssetCategory> firstCategory = categoryList.stream().findFirst();

        if (firstCategory.isPresent()) {
            AssetCategory catego = _assetCategoryLocalService.getCategory(firstCategory.get().getCategoryId());
            System.out.println("catego: " + catego);
            System.out.println("languageIdString: " + languageIdString);
            newsDetail.setCategory(new Category(catego.getTitle(languageIdString), catego.getCategoryId()));
            System.out.println("categoryName: " + catego.getName());
            System.out.println("categoryName: " + catego.getTitle(languageIdString));
        }

        if (article == null || !article.getDDMStructureKey().equals(structureId)) {
            System.out.println("Not Found");
            throw new PortalException(javax.ws.rs.core.Response.Status.NOT_FOUND.toString());
        }

        return newsDetail;
    }
}