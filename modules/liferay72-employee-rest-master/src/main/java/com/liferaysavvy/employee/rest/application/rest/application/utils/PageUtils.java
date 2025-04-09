package com.liferaysavvy.employee.rest.application.rest.application.utils;

import java.util.Collections;
import java.util.List;

import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

/**
 * Utility class for handling pagination in REST APIs.
 * Provides methods to create paginated responses using Liferay's Page API.
 */
public class PageUtils {

    /**
     * Creates a Page object from a list of items and pagination parameters.
     * This method handles the pagination logic for a pre-fetched list of items.
     * 
     * @param <T> Type of items in the list
     * @param items Full list of items to paginate
     * @param pagination Pagination object containing page size and number
     * @param totalCount Total number of items (may be different from items.size())
     * @return Page object containing the paginated subset of items
     */
    public static <T> Page<T> createPage(List<T> items, Pagination pagination, long totalCount) {
        if (items == null || items.isEmpty()) {
            return Page.of(Collections.emptyList(), pagination, 0);
        }

        int pageSize = pagination.getPageSize();
        int pageNumber = pagination.getPageSize();
        int fromIndex = (pageNumber - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, items.size());

        if (fromIndex >= items.size()) {
            return Page.of(Collections.emptyList(), pagination, totalCount);
        }

        return Page.of(items.subList(fromIndex, toIndex), pagination, totalCount);
    }

    /**
     * Creates a Page object from a list of items and pagination parameters.
     * This method assumes the total count is equal to the size of the items list.
     * 
     * @param <T> Type of items in the list
     * @param items Full list of items to paginate
     * @param pagination Pagination object containing page size and number
     * @return Page object containing the paginated subset of items
     */
    public static <T> Page<T> createPage(List<T> items, Pagination pagination) {
        return createPage(items, pagination, items != null ? items.size() : 0);
    }

    /**
     * Creates a Page object from a list of items and explicit pagination parameters.
     * This method is useful when page size and number are known but not wrapped in a Pagination object.
     * 
     * @param <T> Type of items in the list
     * @param items Full list of items to paginate
     * @param pageSize Number of items per page
     * @param pageNumber Current page number (1-based)
     * @return Page object containing the paginated subset of items
     */
    public static <T> Page<T> createPage(List<T> items, int pageSize, int pageNumber) {
        return createPage(items, Pagination.of(pageNumber, pageSize));
    }
}
