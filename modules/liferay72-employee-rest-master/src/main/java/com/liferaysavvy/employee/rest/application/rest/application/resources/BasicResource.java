package com.liferaysavvy.employee.rest.application.rest.application.resources;

import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.portal.kernel.dao.orm.*;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ResourcePermissionLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferaysavvy.employee.rest.application.rest.application.utils.Constants;
import com.liferaysavvy.employee.rest.application.rest.application.utils.PageUtils;
import com.liferaysavvy.employee.rest.application.rest.application.utils.RequestUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class BasicResource {



    protected String getLanguageId(HttpServletRequest request) {
        ThemeDisplay themeDisplay = RequestUtil.getTheme(request);
        return themeDisplay != null ? themeDisplay.getLanguageId() : Constants.DEFAULT_VALUE_LANGUAGE;
    }

    protected Page<JournalArticle> searchJournalArticles(
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
            OrderByComparator<JournalArticle> orderByComparator) {

        DynamicQuery query = JournalArticleLocalServiceUtil.dynamicQuery();
        query.add(PropertyFactoryUtil.forName("companyId").eq(companyId));
        query.add(PropertyFactoryUtil.forName("groupId").eq(groupId));
        query.add(PropertyFactoryUtil.forName("status").eq(WorkflowConstants.STATUS_APPROVED));

        if (structureId != null) {
            query.add(PropertyFactoryUtil.forName("DDMStructureKey").eq(structureId));
        }

        if (keywords != null && !keywords.isEmpty()) {
            Disjunction disjunction = RestrictionsFactoryUtil.disjunction();
            disjunction.add(PropertyFactoryUtil.forName("title").like("%" + keywords + "%"));
            disjunction.add(PropertyFactoryUtil.forName("description").like("%" + keywords + "%"));
            disjunction.add(PropertyFactoryUtil.forName("content").like("%<![CDATA[" + keywords + "]]%"));
            query.add(disjunction);
        }

        if (startDate != null) {
            query.add(PropertyFactoryUtil.forName("displayDate").ge(startDate));
        }

        if (endDate != null) {
            query.add(PropertyFactoryUtil.forName("displayDate").le(endDate));
        }

        int start = pagination.getStartPosition();
        int end = pagination.getEndPosition();

        List<JournalArticle> articles = JournalArticleLocalServiceUtil.dynamicQuery(query, start, end, orderByComparator);
        long total = JournalArticleLocalServiceUtil.dynamicQueryCount(query);

        return PageUtils.createPage(articles, pagination, total);
    }

    protected boolean isAllowed(String name, Long primaryKey, User currentUser, Long companyId) throws PortalException {
        List<Role> userRoles = RoleLocalServiceUtil.getUserRoles(currentUser.getUserId());
        List<Long> userRoleIds = userRoles.stream().map(Role::getRoleId).collect(Collectors.toList());

        List<com.liferay.portal.kernel.model.ResourcePermission> permissions =
                ResourcePermissionLocalServiceUtil.getResourcePermissions(
                        companyId,
                        name,
                        ResourceConstants.SCOPE_INDIVIDUAL,
                        String.valueOf(primaryKey));

        List<Long> permissionRoleIds = permissions.stream().map(permission -> permission.getRoleId()).collect(Collectors.toList());
        return !Collections.disjoint(userRoleIds, permissionRoleIds);
    }
}
