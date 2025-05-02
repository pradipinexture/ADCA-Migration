package adc.dxp.rest.api.application.utils;

import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.portal.kernel.dao.orm.*;
import com.liferay.portal.kernel.search.*;
import com.liferay.portal.kernel.search.generic.BooleanClauseImpl;
import com.liferay.portal.kernel.search.generic.BooleanQueryImpl;
import com.liferay.portal.kernel.search.generic.TermQueryImpl;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.*;

/**
 *
 * @author ricardo.gomes
 *
 */
public class JournalArticleUtil {
    public static List<JournalArticle> searchJournalArticles(
            long companyId, long groupId, String search, String structureKey,
            Date startDate, Date endDate,
            OrderByComparator<JournalArticle> orderByComparator) {

        try {
            // Create a basic dynamic query
            DynamicQuery dq = JournalArticleLocalServiceUtil.dynamicQuery();

            // Add group and company filters
            dq.add(RestrictionsFactoryUtil.eq("groupId", groupId));
            dq.add(RestrictionsFactoryUtil.eq("companyId", companyId));
            dq.add(RestrictionsFactoryUtil.eq("status", WorkflowConstants.STATUS_APPROVED));

            // Add date filters if specified
            if (startDate != null) {
                dq.add(RestrictionsFactoryUtil.ge("displayDate", startDate));
            }
            if (endDate != null) {
                dq.add(RestrictionsFactoryUtil.le("displayDate", endDate));
            }

            // Execute query to get all matching articles
            List<JournalArticle> allArticles = JournalArticleLocalServiceUtil.dynamicQuery(dq);

            // Create a map to store only the latest version of each article
            Map<String, JournalArticle> latestVersions = new HashMap<>();

            // Process articles to find latest versions and apply remaining filters
            for (JournalArticle article : allArticles) {
                // Skip if doesn't match structure key
                if (Validator.isNotNull(structureKey) &&
                        !structureKey.equals(article.getDDMStructureKey())) {
                    continue;
                }

                // Skip if doesn't match search term
                if (Validator.isNotNull(search)) {
                    String title = article.getTitle();
                    if (title == null || !StringUtil.containsIgnoreCase(title, search)) {
                        continue;
                    }
                }

                // Keep only the latest version
                String key = article.getArticleId();
                if (!latestVersions.containsKey(key) ||
                        article.getVersion() > latestVersions.get(key).getVersion()) {
                    latestVersions.put(key, article);
                }
            }

            // Convert map values to list
            List<JournalArticle> results = new ArrayList<>(latestVersions.values());

            // Sort results
            if (orderByComparator != null) {
                Collections.sort(results, orderByComparator);
            } else {
                // Default sort by display date descending
                Collections.sort(results, new Comparator<JournalArticle>() {
                    @Override
                    public int compare(JournalArticle a1, JournalArticle a2) {
                        return a2.getDisplayDate().compareTo(a1.getDisplayDate());
                    }
                });
            }

            return results;
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