package adc.dxp.rest.api.application.utils;

import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.portal.kernel.dao.orm.*;
import com.liferay.portal.kernel.language.LanguageUtil;
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
import java.util.stream.Collectors;

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

//            if (startDate != null) {
//                dq.add(RestrictionsFactoryUtil.ge("displayDate", startDate));
//            }
//            if (endDate != null) {
//                dq.add(RestrictionsFactoryUtil.le("displayDate", endDate));
//            }

            List<JournalArticle> allArticles = JournalArticleLocalServiceUtil.dynamicQuery(dq);
            Map<String, JournalArticle> latestVersions = new HashMap<>();

            // Normalize search term for better matching
            String normalizedSearch = normalizeSearchTerm(search);

            for (JournalArticle article : allArticles) {
                // Check structure key first
                if (Validator.isNotNull(structureKey) &&
                        !structureKey.equals(article.getDDMStructureKey())) {
                    continue;
                }

                boolean matchesSearch = true;
                if (Validator.isNotNull(normalizedSearch)) {
                    matchesSearch = performSearchMatch(article, normalizedSearch);
                    if (!matchesSearch) {
                        continue;
                    }
                }

                // Keep only the latest version of each article
                String key = article.getArticleId();
                if (!latestVersions.containsKey(key) ||
                        article.getVersion() > latestVersions.get(key).getVersion()) {
                    latestVersions.put(key, article);
                }
            }

            List<JournalArticle> results = new ArrayList<>(latestVersions.values());

            if (startDate != null || endDate != null) {
                results = latestVersions.values().stream()
                        .filter(article -> {
                            Date displayDate = article.getDisplayDate();
                            if (displayDate == null) {
                                return false;
                            }
                            if (startDate != null && displayDate.before(startDate)) {
                                return false;
                            }
                            if (endDate != null && displayDate.after(endDate)) {
                                return false;
                            }
                            return true;
                        })
                        .collect(Collectors.toList());
            }

            // Apply sorting
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

    /**
     * Normalizes search term for better matching
     */
    private static String normalizeSearchTerm(String search) {
        if (Validator.isNull(search)) {
            return null;
        }

        return search.trim()
                .toLowerCase()
                .replaceAll("\\s+", " "); // Replace multiple spaces with single space
    }

    /**
     * Performs comprehensive search matching across title, description, and content
     */
    private static boolean performSearchMatch(JournalArticle article, String normalizedSearch) {
        try {
            // Get available locales for the article
            Set<Locale> availableLocales = LanguageUtil.getAvailableLocales(article.getGroupId());

            // 1. Search in titles across all available locales
            if (searchInTitles(article, normalizedSearch, availableLocales)) {
                return true;
            }

            // 2. Search in descriptions across all available locales
            if (searchInDescriptions(article, normalizedSearch, availableLocales)) {
                return true;
            }

            // 3. Search in dynamic content
            if (searchInDynamicContent(article, normalizedSearch, availableLocales)) {
                return true;
            }

            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Search in article titles across multiple locales
     */
    private static boolean searchInTitles(JournalArticle article, String normalizedSearch,
                                          Set<Locale> availableLocales) {
        try {
            // Check default locale first
            String defaultTitle = article.getTitle();
            if (containsIgnoreCaseAndNormalized(defaultTitle, normalizedSearch)) {
                return true;
            }

            // Check current value title
            String currentTitle = article.getTitleCurrentValue();
            if (containsIgnoreCaseAndNormalized(currentTitle, normalizedSearch)) {
                return true;
            }

            // Check all available locales
            for (Locale locale : availableLocales) {
                try {
                    String localizedTitle = article.getTitle(locale);
                    if (containsIgnoreCaseAndNormalized(localizedTitle, normalizedSearch)) {
                        return true;
                    }
                } catch (Exception e) {
                    // Continue checking other locales
                }
            }

            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Search in article descriptions across multiple locales
     */
    private static boolean searchInDescriptions(JournalArticle article, String normalizedSearch,
                                                Set<Locale> availableLocales) {
        try {
            // Check default description
            String defaultDescription = article.getDescription();
            if (containsIgnoreCaseAndNormalized(defaultDescription, normalizedSearch)) {
                return true;
            }

            // Check current value description
            String currentDescription = article.getDescriptionCurrentValue();
            if (containsIgnoreCaseAndNormalized(currentDescription, normalizedSearch)) {
                return true;
            }

            // Check all available locales
            for (Locale locale : availableLocales) {
                try {
                    String localizedDescription = article.getDescription(locale);
                    if (containsIgnoreCaseAndNormalized(localizedDescription, normalizedSearch)) {
                        return true;
                    }
                } catch (Exception e) {
                    // Continue checking other locales
                }
            }

            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Search in dynamic content fields across multiple locales
     */
    private static boolean searchInDynamicContent(JournalArticle article, String normalizedSearch,
                                                  Set<Locale> availableLocales) {
        try {
            // Check default locale content
            String defaultContent = article.getContent();
            if (searchInContentXML(defaultContent, normalizedSearch)) {
                return true;
            }

            // Check all available locales
            for (Locale locale : availableLocales) {
                try {
                    String localizedContent = article.getContentByLocale(LocaleUtil.toLanguageId(locale));
                    if (searchInContentXML(localizedContent, normalizedSearch)) {
                        return true;
                    }
                } catch (Exception e) {
                    // Continue checking other locales
                }
            }

            // Fallback: try with default language ID
            try {
                String fallbackContent = article.getContentByLocale(article.getDefaultLanguageId());
                if (searchInContentXML(fallbackContent, normalizedSearch)) {
                    return true;
                }
            } catch (Exception e) {
                // Log error but continue
            }

            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Search within XML content for dynamic fields
     */
    private static boolean searchInContentXML(String content, String normalizedSearch) {
        if (Validator.isNull(content)) {
            return false;
        }

        try {
            Document document = SAXReaderUtil.read(content);

            // Search in dynamic-content elements
            List<Node> dynamicContentNodes = document.selectNodes("//dynamic-content");
            for (Node node : dynamicContentNodes) {
                String fieldValue = node.getText();
                if (containsIgnoreCaseAndNormalized(fieldValue, normalizedSearch)) {
                    return true;
                }

                // Also check CDATA content
                if (node instanceof Element) {
                    Element element = (Element) node;
                    String cdataContent = element.getTextTrim();
                    if (containsIgnoreCaseAndNormalized(cdataContent, normalizedSearch)) {
                        return true;
                    }
                }
            }

            // Search in any text content (as fallback)
            List<Node> allTextNodes = document.selectNodes("//text()");
            for (Node node : allTextNodes) {
                String textValue = node.getText();
                if (containsIgnoreCaseAndNormalized(textValue, normalizedSearch)) {
                    return true;
                }
            }

        } catch (Exception e) {
            // Fallback: simple string search if XML parsing fails
            return containsIgnoreCaseAndNormalized(content, normalizedSearch);
        }

        return false;
    }

    /**
     * Enhanced case-insensitive contains check with normalization
     */
    private static boolean containsIgnoreCaseAndNormalized(String text, String search) {
        if (Validator.isNull(text) || Validator.isNull(search)) {
            return false;
        }

        // Normalize the text (remove extra spaces, convert to lowercase)
        String normalizedText = text.trim()
                .toLowerCase()
                .replaceAll("\\s+", " ")
                .replaceAll("&nbsp;", " ") // Remove HTML entities
                .replaceAll("<[^>]+>", " ") // Remove HTML tags
                .replaceAll("\\p{Punct}", " "); // Replace punctuation with spaces

        return normalizedText.contains(search);
    }

    /**
     * Alternative search method with debug information
     */
    public static List<JournalArticle> searchJournalArticlesWithDebug(
            long companyId, long groupId, String search, String structureKey,
            Date startDate, Date endDate,
            OrderByComparator<JournalArticle> orderByComparator) {

        System.out.println("=== Search Debug Info ===");
        System.out.println("Search term: '" + search + "'");
        System.out.println("Normalized search: '" + normalizeSearchTerm(search) + "'");
        System.out.println("Structure key: " + structureKey);
        System.out.println("Group ID: " + groupId);

        List<JournalArticle> results = searchJournalArticles(
                companyId, groupId, search, structureKey, startDate, endDate, orderByComparator);

        System.out.println("Results found: " + results.size());
        for (JournalArticle article : results) {
            System.out.println("- Article: " + article.getTitle() + " (ID: " + article.getArticleId() + ")");
        }
        System.out.println("=== End Debug Info ===");

        return results;
    }

    /**
     * Simple test method to validate search functionality
     */
    public static void testSearch(long companyId, long groupId, String structureKey) {
        System.out.println("=== Testing Search Functionality ===");

        // Get all articles first
        List<JournalArticle> allArticles = searchJournalArticles(
                companyId, groupId, null, structureKey, null, null, null);

        System.out.println("Total articles found: " + allArticles.size());

        // Test with some sample searches
        String[] testSearches = {"demo", "test", "Demo", "TEST", "demo test"};

        for (String testSearch : testSearches) {
            List<JournalArticle> searchResults = searchJournalArticles(
                    companyId, groupId, testSearch, structureKey, null, null, null);

            System.out.println("Search '" + testSearch + "' found: " + searchResults.size() + " results");

            for (JournalArticle article : searchResults) {
                System.out.println("  - " + article.getTitle() + " (matches: " +
                        getMatchReason(article, normalizeSearchTerm(testSearch)) + ")");
            }
        }

        System.out.println("=== End Test ===");
    }

    /**
     * Helper method to determine why an article matched the search
     */
    private static String getMatchReason(JournalArticle article, String normalizedSearch) {
        List<String> reasons = new ArrayList<>();

        if (containsIgnoreCaseAndNormalized(article.getTitle(), normalizedSearch)) {
            reasons.add("title");
        }
        if (containsIgnoreCaseAndNormalized(article.getDescription(), normalizedSearch)) {
            reasons.add("description");
        }

        try {
            String content = article.getContentByLocale(article.getDefaultLanguageId());
            if (searchInContentXML(content, normalizedSearch)) {
                reasons.add("content");
            }
        } catch (Exception e) {
            // Ignore
        }

        return String.join(", ", reasons);
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