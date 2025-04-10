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
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import adc.dxp.rest.api.application.AdcDxpRestApiConfiguration;
import adc.dxp.rest.api.application.data.Faqs;
import adc.dxp.rest.api.application.utils.*;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetCategoryLocalServiceUtil;
import com.liferay.asset.kernel.service.AssetEntryLocalServiceUtil;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.journal.util.comparator.ArticleDisplayDateComparator;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import adc.dxp.rest.api.application.AdcDxpRestApiApplication;
import adc.dxp.rest.api.application.data.Category;
import adc.dxp.rest.api.application.data.GenericContent;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Reference;

/**
 * 
 * Endpoints  of the generic content
 * 
 * @author ana.cavadas
 *
 */
@Path("/content")
public class GenericContentResource extends BasicResource {

	/**
	 * logging instance
	 */
	private static Log _log = LogFactoryUtil.getLog(GenericContentResource.class);

	@Reference
	private ConfigurationProvider _configurationProvider;

	public volatile AdcDxpRestApiConfiguration _dxpRESTConfiguration;

	@Activate
	protected void activate() {
		try {
			_dxpRESTConfiguration = _configurationProvider.getCompanyConfiguration(AdcDxpRestApiConfiguration.class, 0);
		} catch (ConfigurationException e) {}
	}

	@Reference
	private StructureResource _structureResource;
	
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
	@Path("/search")
	@Produces(MediaType.APPLICATION_JSON)
	public Page<GenericContent> search(
			@Parameter(hidden = true) @QueryParam("type") String type,
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
		
		long groupId = Long.valueOf(groupIdString).longValue();
		
		long categoryId = categoryIdParam != null && !categoryIdParam.isEmpty() && !categoryIdParam.equalsIgnoreCase("null") ? 
				Long.valueOf(categoryIdParam).longValue() : -1;
		
		ContentType content = ContentType.getEnumByValue(type);

		//Date
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
			// TODO: ana.cavadas
			e.printStackTrace();
		}
		
		OrderByComparator<JournalArticle> orderByComparator = null;
		
		if (sorts != null && sorts[0].getFieldName().equalsIgnoreCase("displayDate")) {
			orderByComparator = new ArticleDisplayDateComparator(!sorts[0].isReverse());	
		}
		
		else if (sorts != null && sorts[0].getFieldName().equalsIgnoreCase("title")) {
			orderByComparator = OrderByComparatorFactoryUtil.create("JournalArticleLocalization", sorts[0].getFieldName(), false);
		}

		_log.debug("startDate2: "+ startDate);
		_log.debug("endDate: "+ endDate);

		DDMStructure structureByNameEn = StructureUtil.getStructureByNameEn(Constants.STRUCTURE_NEWS_NAME_EN);

		List<JournalArticle> results = JournalArticleUtil.searchJournalArticles(companyId, groupId, search, structureByNameEn.getStructureId(), startDate, endDate, orderByComparator);

		List<GenericContent> lastResults = new ArrayList<>();
		
		for (JournalArticle article: results) {
			_log.debug("JournalArticle: "+ article);
			
			if (JournalArticleLocalServiceUtil.isLatestVersion(groupId, article.getArticleId(), article.getVersion(), WorkflowConstants.STATUS_APPROVED)) {
				
				GenericContent news = new GenericContent(article, request.getHeader(Constants.HEADER_LANGUAGE_ID), content);				
				
				AssetEntry assetUtil = AssetEntryLocalServiceUtil.getEntry("com.liferay.journal.model.JournalArticle", article.getResourcePrimKey());
				news.setEntryId(assetUtil.getEntryId());

				List<AssetCategory> categoryList = AssetCategoryLocalServiceUtil.getCategories(JournalArticle.class.getName(), assetUtil.getClassPK());
				
				Optional<AssetCategory> firstCategory = categoryList.stream().findFirst();
				
				if (firstCategory.isPresent()) {
					AssetCategory catego = AssetCategoryLocalServiceUtil.getCategory(firstCategory.get().getCategoryId());
					news.setCategory(new Category(catego.getTitle(languageIdString), catego.getCategoryId()));
				}
				
				_log.debug("DisplayDate: " + news.getDisplayDate() );
				_log.debug("ExpirationDate: " + news.getExpirationDate());

				if ((categoryIdParam == null || categoryIdParam.equalsIgnoreCase("-1")
						|| Long.compare(categoryId, news.getCategory().getCategoryId()) == 0)
						&& (lastResults.indexOf(news) == -1)) {
					lastResults.add(news);
				}	
			}
		}
		
		
		if (sorts != null && sorts[0] != null && !sorts[0].isReverse()) {
			Collections.reverse(lastResults);
		}

		return PageUtils.createPage(lastResults, pagination, lastResults.size());
	}
	
	/**
	 * 
	 * Returns news by id
	 * 
	 * @param request hidden parameter
	 * @return
	 * @throws PortalException
	 */
	@GET
	@Path("/detail")
	@Operation(description = "Get content by id")
	@Parameters(
			value = {
				@Parameter(in = ParameterIn.QUERY, name = "id")
			}
		)
	@Produces(MediaType.APPLICATION_JSON)
	public GenericContent getNewsById(
			@Parameter(hidden = true) @QueryParam("id") String idString,
			@Parameter(hidden = true) @QueryParam("type") String type,
			@Context HttpServletRequest request
			) throws PortalException {

		_log.debug("Call get news");

		String groupIdString = request.getHeader("groupId");
		String languageIdString = request.getHeader("languageId");
		
		long groupId = Long.valueOf(groupIdString).longValue();
		
		long id = Long.valueOf(idString).longValue();
		
		ContentType content = ContentType.getEnumByValue(type);
		
		String structureId = _structureResource.getStructure(groupId, content.getStructure());

		JournalArticle article = JournalArticleLocalServiceUtil.getLatestArticle(id);
		GenericContent newsDetail = new GenericContent(article, request.getHeader(Constants.HEADER_LANGUAGE_ID), content);
		
		
		
		AssetEntry assetUtil = AssetEntryLocalServiceUtil.getEntry("com.liferay.journal.model.JournalArticle", article.getResourcePrimKey());

		_log.debug("JournalArticle: " + article);
		
		_log.debug("article.getResourcePrimKey(): " + article.getResourcePrimKey());

		List<AssetCategory> categoryList = AssetCategoryLocalServiceUtil.getCategories(JournalArticle.class.getName(), assetUtil.getClassPK());
		
		Optional<AssetCategory> firstCategory = categoryList.stream().findFirst();
		
		if (firstCategory.isPresent()) {
			AssetCategory catego = AssetCategoryLocalServiceUtil.getCategory(firstCategory.get().getCategoryId());
			_log.debug("catego: " + catego);
			_log.debug("languageIdString: " + languageIdString);
			newsDetail.setCategory(new Category(catego.getTitle(languageIdString), catego.getCategoryId()));
			_log.debug("categoryName: " + catego.getName());
			_log.debug("categoryName: " + catego.getTitle(languageIdString));
		}
		
		if (article == null || !String.valueOf(article.getDDMStructureId()).equals(structureId)) {
			// TODO: improve this
			_log.debug("Not Found");
			throw new PortalException(javax.ws.rs.core.Response.Status.NOT_FOUND.toString());
		}

		
		return newsDetail;

	}
	
	
	

}
