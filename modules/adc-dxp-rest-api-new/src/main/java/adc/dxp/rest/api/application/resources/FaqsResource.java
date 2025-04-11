package adc.dxp.rest.api.application.resources;

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import adc.dxp.rest.api.application.AdcDxpRestApiConfiguration;
import adc.dxp.rest.api.application.data.Contact;
import adc.dxp.rest.api.application.utils.JournalArticleUtil;
import adc.dxp.rest.api.application.utils.StructureUtil;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetCategoryLocalServiceUtil;
import com.liferay.asset.kernel.service.AssetEntryLocalServiceUtil;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalServiceUtil;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import adc.dxp.rest.api.application.AdcDxpRestApiApplication;
import adc.dxp.rest.api.application.data.Category;
import adc.dxp.rest.api.application.data.Faqs;
import adc.dxp.rest.api.application.data.News;
import adc.dxp.rest.api.application.utils.Constants;
import adc.dxp.rest.api.application.utils.PageUtils;
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
		service = FaqsResource.class
)

@Path("/faqs")
public class FaqsResource extends BasicResource {

	/**
	 * logging instance
	 */
	private static Log _log = LogFactoryUtil.getLog(FaqsResource.class);

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
		description = "Retrieves the list of the Faqs. Results can be paginated, filtered, searched, and sorted."
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
	public Page<Faqs> search(
			@Parameter(hidden = true) @QueryParam("search") String search,
			@QueryParam("pageSize") Integer pageSize,
			@Parameter(hidden = true) @QueryParam("categoryId") String categoryIdParam,
			@Context Filter filter, 
			@Context Pagination pagination,
			@Context Sort[] sorts, 
			@Context HttpServletRequest request) throws PortalException {
		
		int paginationSize = pageSize == null ? _dxpRESTConfiguration.paginationSize() : pageSize;
		int paginationPage = pagination.getPage();
		
		long companyId = PortalUtil.getCompanyId(request);
		
		long categoryId = categoryIdParam != null && !categoryIdParam.isEmpty() && !categoryIdParam.equalsIgnoreCase("null") ? 
				Long.valueOf(categoryIdParam).longValue() : -1;
		
		String groupIdString = request.getHeader("groupId");
		
		long groupId = Long.valueOf(groupIdString).longValue();
		String languageIdString = request.getHeader("languageId");
		DDMStructure structureByNameEn = StructureUtil.getStructureByNameEn(Constants.STRUCTURE_NEWS_NAME_EN);

		List<JournalArticle> results = JournalArticleUtil.searchJournalArticles(companyId, groupId, search, structureByNameEn.getStructureId(), null, null, null);;
		
		List<Faqs> lastResults = new ArrayList<>();
		
		for (JournalArticle article: results) {
			
			Faqs faqs = new Faqs(article, 
					languageIdString);
			
			if (lastResults.indexOf(faqs) != -1) {
				continue;
			}
			
			AssetEntry assetUtil = AssetEntryLocalServiceUtil.getEntry("com.liferay.journal.model.JournalArticle", article.getResourcePrimKey());
			faqs.setEntryId(assetUtil.getEntryId());

			List<AssetCategory> categoryList = AssetCategoryLocalServiceUtil.getCategories(JournalArticle.class.getName(), assetUtil.getClassPK());
			
			Optional<AssetCategory> firstCategory = categoryList.stream().findFirst();
			
			if (firstCategory.isPresent()) {
				AssetCategory catego = AssetCategoryLocalServiceUtil.getCategory(firstCategory.get().getCategoryId());
				faqs.setCategory(new Category(catego.getTitle(languageIdString), catego.getCategoryId()));
			}
			
			
			if (categoryIdParam == null || categoryIdParam.equalsIgnoreCase("-1") || Long.compare(categoryId,faqs.getCategory().getCategoryId()) == 0) {
				lastResults.add(faqs);
			}
		
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
	@Operation(description = "Get faqs by id")
	@Parameters(
			value = {
				@Parameter(in = ParameterIn.QUERY, name = "id")
			}
		)
	@Produces(MediaType.APPLICATION_JSON)
	public Faqs getNewsById(
			@Parameter(hidden = true) @QueryParam("id") String idString,
			@Context HttpServletRequest request
			) throws PortalException {

		_log.debug("Call get news");

		String groupIdString = request.getHeader("groupId");
		String languageIdString = request.getHeader("languageId");
		
		long groupId = Long.valueOf(groupIdString).longValue();
		
		long id = Long.valueOf(idString).longValue();
		
		String structureId = _structureResource.getStructure(groupId, Constants.STRUCTURE_FAQ_NAME_EN);

		JournalArticle article = JournalArticleLocalServiceUtil.getLatestArticle(id);
		Faqs faqsDetail = new Faqs(article, languageIdString);
		
		AssetEntry assetUtil = AssetEntryLocalServiceUtil.getEntry("com.liferay.journal.model.JournalArticle", article.getResourcePrimKey());

		_log.debug("JournalArticle: " + article);
		
		_log.debug("article.getResourcePrimKey(): " + article.getResourcePrimKey());


		List<AssetCategory> categoryList = AssetCategoryLocalServiceUtil.getCategories(JournalArticle.class.getName(), assetUtil.getClassPK());
		
		Optional<AssetCategory> firstCategory = categoryList.stream().findFirst();
		
		if (firstCategory.isPresent()) {
			AssetCategory catego = AssetCategoryLocalServiceUtil.getCategory(firstCategory.get().getCategoryId());
			_log.debug("catego: " + catego);
			_log.debug("languageIdString: " + languageIdString);
			faqsDetail.setCategoryName(catego.getTitle(languageIdString));
			_log.debug("categoryName: " + catego.getName());
			_log.debug("categoryName: " + catego.getTitle(languageIdString));
		}
		// TODO: ver se d� probs getName e getTitle
		
		if (article == null || !String.valueOf(article.getDDMStructureKey()).equals(structureId)) {
			// TODO: improve this
			_log.debug("Not Found");
			throw new PortalException(javax.ws.rs.core.Response.Status.NOT_FOUND.toString());
		}

		
		return faqsDetail;

	}
	

}
