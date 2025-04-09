package adc.dxp.rest.api.application.resources;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.liferay.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.journal.service.JournalArticleServiceUtil;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchContextFactory;
import com.liferay.portal.kernel.service.ResourcePermissionLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import adc.dxp.rest.api.application.utils.Constants;
import adc.dxp.rest.api.application.utils.PageUtils;
import adc.dxp.rest.api.application.utils.RequestUtil;

/**
 * Base resource class providing common functionality for all REST endpoints.
 *
 * @author ricardo.gomes
 */
public class BasicResource {

    /**
     * Gets the company ID from the request.
     *
     * @param request HTTP request
     * @return company ID
     */
    protected long getCompanyId(HttpServletRequest request) {
        return PortalUtil.getCompanyId(request);
    }

    /**
     * Gets the language ID from the request.
     *
     * @param request HTTP request
     * @return language ID
     */
    protected String getLanguageId(HttpServletRequest request) {
        ThemeDisplay themeDisplay = RequestUtil.getTheme(request);
        return themeDisplay != null ? themeDisplay.getLanguageId() : Constants.DEFAULT_VALUE_LANGUAGE;
    }

    /**
     * Searches for journal articles using the provided criteria.
     *
     * @param companyId Company ID
     * @param groupId Group ID
     * @param folderIds List of folder IDs
     * @param classNameId Class name ID
     * @param keywords Search keywords
     * @param structureId Structure ID
     * @param startDate Start date
     * @param endDate End date
     * @param status Workflow status
     * @param pagination Pagination object
     * @param orderByComparator Order by comparator
     * @return Page of journal articles
     */
    protected Page<com.liferay.journal.model.JournalArticle> searchJournalArticles(
            long companyId,
            long groupId,
            List<Long> folderIds,
            long classNameId,
            String keywords,
            String structureId,
            java.util.Date startDate,
            java.util.Date endDate,
            int status,
            Pagination pagination,
            com.liferay.portal.kernel.util.OrderByComparator<com.liferay.journal.model.JournalArticle> orderByComparator) {

        DynamicQuery query = JournalArticleLocalServiceUtil.dynamicQuery();
        query.add(PropertyFactoryUtil.forName("companyId").eq(companyId));
        query.add(PropertyFactoryUtil.forName("groupId").eq(groupId));
        query.add(PropertyFactoryUtil.forName("status").eq(WorkflowConstants.STATUS_APPROVED));

        if (structureId != null) {
            query.add(PropertyFactoryUtil.forName("DDMStructureKey").eq(structureId));
        }

        if (keywords != null && !keywords.isEmpty()) {
            query.add(PropertyFactoryUtil.forName("title").like("%" + keywords + "%"));
            query.add(PropertyFactoryUtil.forName("description").like("%" + keywords + "%"));
            query.add(PropertyFactoryUtil.forName("content").like("%<![CDATA[%" + keywords + "%]]%"));
        }

        if (startDate != null) {
            query.add(PropertyFactoryUtil.forName("displayDate").ge(startDate));
        }

        if (endDate != null) {
            query.add(PropertyFactoryUtil.forName("displayDate").le(endDate));
        }

        int start = pagination.getStartPosition();
        int end = pagination.getEndPosition();

        List<com.liferay.journal.model.JournalArticle> articles =
                JournalArticleLocalServiceUtil.dynamicQuery(query, start, end, orderByComparator);
        long total = JournalArticleLocalServiceUtil.dynamicQueryCount(query);

        return PageUtils.createPage(articles, pagination, total);
    }

    /**
     * Checks if a user has permission for a resource.
     *
     * @param name Resource name
     * @param primaryKey Resource primary key
     * @param currentUser Current user
     * @param companyId Company ID
     * @return true if allowed, false otherwise
     */
    protected boolean isAllowed(String name, Long primaryKey, User currentUser, Long companyId) throws PortalException {
        List<Role> userRoles = RoleLocalServiceUtil.getUserRoles(currentUser.getUserId());
        List<Long> userRoleIds = userRoles.stream()
                .map(Role::getRoleId)
                .collect(Collectors.toList());

        List<com.liferay.portal.kernel.model.ResourcePermission> permissions =
                ResourcePermissionLocalServiceUtil.getResourcePermissions(
                        companyId,
                        name,
                        ResourceConstants.SCOPE_INDIVIDUAL,
                        String.valueOf(primaryKey));

        List<Long> permissionRoleIds = permissions.stream()
                .map(permission -> permission.getRoleId())
                .collect(Collectors.toList());

        return !Collections.disjoint(userRoleIds, permissionRoleIds);
    }
}