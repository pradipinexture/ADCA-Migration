package adc.dxp.rest.api.application.utils;

import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.portal.kernel.dao.orm.*;
import com.liferay.portal.kernel.search.*;
import com.liferay.portal.kernel.search.generic.BooleanClauseImpl;
import com.liferay.portal.kernel.search.generic.BooleanQueryImpl;
import com.liferay.portal.kernel.search.generic.TermQueryImpl;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.Date;
import java.util.List;

/**
 *
 * @author ricardo.gomes
 *
 */
public class JournalArticleUtil {
    public static List<JournalArticle> searchJournalArticles(
            long companyId, long groupId, String search, long structureId,
            Date startDate, Date endDate,
            OrderByComparator<JournalArticle> orderByComparator) {

        DynamicQuery dq = JournalArticleLocalServiceUtil.dynamicQuery();

        dq.add(RestrictionsFactoryUtil.eq("groupId", groupId));
        dq.add(RestrictionsFactoryUtil.eq("companyId", companyId));
        dq.add(RestrictionsFactoryUtil.eq("DDMStructureId", structureId));
        dq.add(RestrictionsFactoryUtil.eq("status", WorkflowConstants.STATUS_APPROVED));

        if (Validator.isNotNull(search)) {
            dq.add(RestrictionsFactoryUtil.ilike("title", "%" + search + "%"));
        }
        if (startDate != null) {
            dq.add(RestrictionsFactoryUtil.ge("displayDate", startDate));
        }
        if (endDate != null) {
            dq.add(RestrictionsFactoryUtil.le("displayDate", endDate));
        }
        if (Validator.isNotNull(orderByComparator)) {
            dq.addOrder((Order) orderByComparator);
        } else {
            dq.addOrder(OrderFactoryUtil.desc("displayDate"));
        }

        try {
            return JournalArticleLocalServiceUtil.dynamicQuery(dq);
        } catch (Exception e) {
            e.printStackTrace();
            return java.util.Collections.emptyList();
        }
    }

    public static List<Document> searchArticles(long companyId, long groupId, String structureKey, String searchKeyword) throws SearchException, ParseException {
        // Setup Search Context
        SearchContext searchContext = new SearchContext();
        searchContext.setCompanyId(companyId);
        searchContext.setGroupIds(new long[]{groupId});
        searchContext.setKeywords(searchKeyword);
        searchContext.setLocale(LocaleUtil.getSiteDefault());
        searchContext.setStart(QueryUtil.ALL_POS);
        searchContext.setEnd(QueryUtil.ALL_POS);
        searchContext.setAttribute("head", true);
        searchContext.setAttribute("status", WorkflowConstants.STATUS_APPROVED);

        // Build Boolean Query with TermQueryImpl for ddmStructureKey
        BooleanQuery booleanQuery = new BooleanQueryImpl();
        TermQuery structureQuery = new TermQueryImpl("ddmStructureKey", structureKey);
        booleanQuery.add(structureQuery, BooleanClauseOccur.MUST);

        // Optional: add more filters like category, displayDate, etc.

        // Apply query to SearchContext
        searchContext.setBooleanClauses(new BooleanClause[]{
                new BooleanClauseImpl<>(structureQuery, BooleanClauseOccur.MUST)
        });

        // Get JournalArticle Indexer
        Indexer<JournalArticle> indexer = IndexerRegistryUtil.getIndexer(JournalArticle.class);

        // Execute search
        Hits hits = indexer.search(searchContext);
        List<Document> documents = hits.toList();

        // Print search result details
        System.out.println("Total Hits: " + hits.getLength());
        for (Document doc : documents) {
            String title = doc.get("title_" + LocaleUtil.toLanguageId(LocaleUtil.getSiteDefault()));
            String articleId = doc.get("articleId");
            String resourcePrimKey = doc.get(Field.ENTRY_CLASS_PK);
            String categories = doc.get(Field.ASSET_CATEGORY_IDS); // comma-separated

            System.out.println("---------------------------------------------------");
            System.out.println("Title: " + title);
            System.out.println("Article ID: " + articleId);
            System.out.println("Resource PK: " + resourcePrimKey);
            System.out.println("Categories: " + categories);
        }
        return documents;
    }

}