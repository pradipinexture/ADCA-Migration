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
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.xml.*;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.SAXReaderUtil;

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
            DynamicQuery dq = JournalArticleLocalServiceUtil.dynamicQuery();

            dq.add(RestrictionsFactoryUtil.eq("groupId", groupId));
            dq.add(RestrictionsFactoryUtil.eq("companyId", companyId));
            dq.add(RestrictionsFactoryUtil.eq("status", WorkflowConstants.STATUS_APPROVED));

            if (startDate != null) {
                dq.add(RestrictionsFactoryUtil.ge("displayDate", startDate));
            }
            if (endDate != null) {
                dq.add(RestrictionsFactoryUtil.le("displayDate", endDate));
            }

            List<JournalArticle> allArticles = JournalArticleLocalServiceUtil.dynamicQuery(dq);

            Map<String, JournalArticle> latestVersions = new HashMap<>();

            for (JournalArticle article : allArticles) {
                if (Validator.isNotNull(structureKey) &&
                        !structureKey.equals(article.getDDMStructureKey())) {
                    continue;
                }

                boolean matchesSearch = true;

                if (Validator.isNotNull(search)) {
                    matchesSearch = false;

                    // 1. Check title
                    String title = article.getTitleCurrentValue();
                    if (Validator.isNotNull(title) && StringUtil.containsIgnoreCase(title, search)) {
                        matchesSearch = true;
                    }

                    // 2. Check description
                    if (!matchesSearch) {
                        String description = article.getDescriptionCurrentValue();
                        if (Validator.isNotNull(description) && StringUtil.containsIgnoreCase(description, search)) {
                            matchesSearch = true;
                        }
                    }

                    // 3. Check custom fields inside <dynamic-content>
                    if (!matchesSearch) {
                        try {
                            String content = article.getContentByLocale(article.getDefaultLanguageId());

                            Document document = SAXReaderUtil.read(content);
                            List<Node> nodes = document.selectNodes("//dynamic-content");

                            for (Node node : nodes) {
                                String fieldValue = node.getText();
                                if (Validator.isNotNull(fieldValue) &&
                                        StringUtil.containsIgnoreCase(fieldValue, search)) {
                                    matchesSearch = true;
                                    break;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    if (!matchesSearch) {
                        continue;
                    }
                }

                String key = article.getArticleId();
                if (!latestVersions.containsKey(key) ||
                        article.getVersion() > latestVersions.get(key).getVersion()) {
                    latestVersions.put(key, article);
                }
            }

            List<JournalArticle> results = new ArrayList<>(latestVersions.values());

            if (orderByComparator != null) {
                results.sort(orderByComparator);
            } else {
                results.sort((a1, a2) -> a2.getDisplayDate().compareTo(a1.getDisplayDate()));
            }

            return results;

        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public static List<com.liferay.portal.kernel.search.Document> searchArticles(long companyId, long groupId, String structureKey, String searchKeyword) throws SearchException, ParseException {
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
        List<com.liferay.portal.kernel.search.Document> documents = hits.toList();

        return documents;
    }

}