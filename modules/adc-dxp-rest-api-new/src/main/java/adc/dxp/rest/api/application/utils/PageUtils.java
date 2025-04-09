package adc.dxp.rest.api.application.utils;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.util.Collections;
import java.util.List;
public class PageUtils {
    /**
     * Creates a Page object from a list of items with pagination and total count
     *
     * @param <T> the type of items in the page
     * @param items the list of items to include in the page
     * @param pagination the pagination parameters
     * @param totalCount the total count of items (regardless of pagination)
     * @return a Page object containing the items
     */
    public static <T> Page<T> createPage(List<T> items, Pagination pagination, long totalCount) {
        if (items == null) {
            return Page.of(Collections.emptyList(), pagination, 0);
        }

        return Page.of(items, pagination, totalCount);
    }
}
