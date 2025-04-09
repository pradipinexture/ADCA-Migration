<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://liferay.com/tld/ddm" prefix="liferay-ddm" %>
<%@ taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>
<%@ taglib uri="http://liferay.com/tld/journal" prefix="liferay-journal" %>
<%@ taglib uri="http://liferay.com/tld/asset" prefix="liferay-asset" %>

<%@ page import="com.liferay.portal.kernel.dao.search.SearchContainer" %>
<%@ page import="com.liferay.portal.kernel.language.LanguageUtil" %>
<%@ page import="com.liferay.portal.kernel.model.User" %>
<%@ page import="com.liferay.portal.kernel.search.Document" %>
<%@ page import="com.liferay.portal.kernel.util.HashMapBuilder" %>
<%@ page import="com.liferay.portal.kernel.util.HtmlUtil" %>
<%@ page import="com.liferay.portal.kernel.util.WebKeys" %>
<%@ page import="com.liferay.portal.search.web.internal.result.display.context.SearchResultSummaryDisplayContext" %>
<%@ page import="com.liferay.portal.search.web.internal.search.results.configuration.SearchResultsPortletInstanceConfiguration" %>
<%@ page import="com.liferay.portal.search.web.internal.search.results.portlet.SearchResultsPortletDisplayContext" %>
<%@ page import="com.liferay.journal.model.JournalArticle" %>
<%@ page import="com.liferay.journal.service.JournalArticleLocalServiceUtil" %>
<%@ page import="java.util.List" %>

<%@ include file="view-extension.jsp" %>

<portlet:defineObjects />

<%
SearchResultsPortletDisplayContext searchResultsPortletDisplayContext = (SearchResultsPortletDisplayContext) java.util.Objects.requireNonNull(request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT));

if (searchResultsPortletDisplayContext.isRenderNothing()) {
    return;
}

SearchResultsPortletInstanceConfiguration searchResultsPortletInstanceConfiguration = searchResultsPortletDisplayContext.getSearchResultsPortletInstanceConfiguration();

List<SearchResultSummaryDisplayContext> searchResultSummaryDisplayContexts = searchResultsPortletDisplayContext.getSearchResultSummaryDisplayContexts();

// Variables like ddmTemplateKeyForCustomWebcontents, defaultGroupId, structureIdFaqs, journalArticleClass, and calendarBookingClass
// are assumed to be defined in view-extension.jsp
%>

<c:choose>
    <c:when test="<%= searchResultSummaryDisplayContexts.isEmpty() && searchResultsPortletDisplayContext.isShowEmptyResultMessage() %>">
        <div class="sheet">
            <liferay-frontend:empty-result-message
                description='<%= LanguageUtil.format(request, "no-results-were-found-that-matched-the-keywords-x", "<strong>" + HtmlUtil.escape(searchResultsPortletDisplayContext.getKeywords()) + "</strong>", false) %>'
                title='<%= LanguageUtil.format(request, "no-results-were-found", false) %>'
            />
        </div>
    </c:when>
    <c:otherwise>
        <liferay-ui:search-container searchContainer="<%= searchResultsPortletDisplayContext.getSearchContainer() %>">
            <liferay-ui:search-container-results results="<%= searchContainer.getResults() %>" />

            <liferay-ui:search-container-row
                className="com.liferay.portal.search.web.internal.result.display.context.SearchResultSummaryDisplayContext"
                modelVar="searchResultSummaryDisplayContext"
            >
                <%
                boolean isCustomLayout = false;
                %>

                <c:if test="<%= searchResultSummaryDisplayContext.getClassName().equalsIgnoreCase(journalArticleClass) %>">
                    <%
                    JournalArticle article = JournalArticleLocalServiceUtil.getLatestArticle(searchResultSummaryDisplayContext.getClassPK());

                    if (article != null) {
                        try {
                            long ddmStructureKeyLong = Long.parseLong(article.getDDMStructureKey());
                            if (!article.getDDMStructureKey().equalsIgnoreCase("BASIC-WEB-CONTENT") &&
                                (ddmStructureKeyLong + 1) != structureIdFaqs) {
                                isCustomLayout = true;
                            }
                        } catch (NumberFormatException e) {
                            // Handle case where DDMStructureKey is not a number
                            if (!article.getDDMStructureKey().equalsIgnoreCase("BASIC-WEB-CONTENT")) {
                                isCustomLayout = true;
                            }
                        }
                    }
                    %>

                    <c:if test="<%= isCustomLayout %>">
                        <liferay-ui:search-container-column-text colspan="<%= 2 %>">
                            <liferay-journal:journal-article
                                articleId="<%= article.getArticleId() %>"
                                groupId="<%= defaultGroupId %>"
                                ddmTemplateKey="<%= ddmTemplateKeyForCustomWebcontents %>"
                            />
                        </liferay-ui:search-container-column-text>
                    </c:if>
                </c:if>

                <%-- Temporarily comment out Calendar logic due to missing module
                <c:if test="<%= searchResultSummaryDisplayContext.getClassName().equalsIgnoreCase(calendarBookingClass) %>">
                    <%
                    CalendarBooking booking = CalendarBookingLocalServiceUtil.fetchCalendarBooking(searchResultSummaryDisplayContext.getClassPK());
                    String image = "<img class=\"card_image\" src=\"/o/adc-dxp-theme/images/event1.jpg\" alt=\"Card image cap\"/>";
                    String title = "";
                    String month = "";
                    int day = 0;
                    String descriptionWithoutImage = "";

                    if (booking != null) {
                        isCustomLayout = true;

                        String description = booking.getDescription("en");
                        descriptionWithoutImage = description;
                        title = booking.getTitle("en");

                        Date startTime = new Date(booking.getStartTime());
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(startTime);
                        day = cal.get(Calendar.DAY_OF_MONTH);

                        DateFormat df5 = new SimpleDateFormat("MMM");
                        month = df5.format(startTime);

                        Pattern pattern = Pattern.compile("(<img ).*?(>)");
                        Matcher matcher = pattern.matcher(description);
                        while (matcher.find()) {
                            image = matcher.group();
                            image = image.replaceAll("(<img )", "<img class=\"card_image\"");
                        }
                        descriptionWithoutImage = description.replaceAll("(<img ).*?(>)", "");
                    }
                    %>

                    <c:if test="<%= isCustomLayout %>">
                        <liferay-ui:search-container-column-text colspan="<%= 2 %>">
                            <div class="card card_horiz_picture">
                                <%= image %>
                                <div class="card-body">
                                    <h5><%= title %></h5>
                                    <div class="day-month">
                                        <div class="month"><%= month %></div>
                                        <div class="day">
                                            <p><%= day %></p>
                                        </div>
                                    </div>
                                    <div class="card-text summary"><%= descriptionWithoutImage %></div>
                                    <a href="/group/portal/events/detail?id=<%= searchResultSummaryDisplayContext.getClassPK() %>" class="stretched-link">Go to Event</a>
                                </div>
                            </div>
                        </liferay-ui:search-container-column-text>
                    </c:if>
                </c:if>
                --%>

                <c:if test="<%= !isCustomLayout %>">
                    <liferay-ui:search-container-column-text>
                        <c:if test="<%= searchResultSummaryDisplayContext.isThumbnailVisible() %>">
                            <img alt="thumbnail" class="img-rounded search-result-thumbnail-img" src="<%= searchResultSummaryDisplayContext.getThumbnailURLString() %>" />
                        </c:if>

                        <c:if test="<%= searchResultSummaryDisplayContext.isIconVisible() %>">
                            <span class="search-asset-type-sticker sticker sticker-rounded sticker-secondary sticker-static">
                                <svg class="lexicon-icon">
                                    <use xlink:href="<%= searchResultSummaryDisplayContext.getPathThemeImages() %>/lexicon/icons.svg#<%= searchResultSummaryDisplayContext.getIconId() %>" />
                                    <title><%= searchResultSummaryDisplayContext.getIconId() %></title>
                                </svg>
                            </span>
                        </c:if>
                    </liferay-ui:search-container-column-text>

                    <liferay-ui:search-container-column-text colspan="<%= 2 %>">
                        <div class="card card_horiz_picture">
                            <div class="card-body">
                                <h4>
                                    <a href="<%= searchResultSummaryDisplayContext.getViewURL() %>">
                                        <strong><%= searchResultSummaryDisplayContext.getHighlightedTitle() %></strong>
                                    </a>
                                </h4>

                                <h6 class="text-default">
                                    <c:if test="<%= searchResultSummaryDisplayContext.isModelResourceVisible() %>">
                                        <strong><%= searchResultSummaryDisplayContext.getModelResource() %></strong>
                                    </c:if>

                                    <c:if test="<%= searchResultSummaryDisplayContext.isLocaleReminderVisible() %>">
                                        <liferay-ui:icon
                                            image='<%= "../language/" + searchResultSummaryDisplayContext.getLocaleLanguageId() %>'
                                            message="<%= searchResultSummaryDisplayContext.getLocaleReminder() %>"
                                        />
                                    </c:if>

                                    <c:if test="<%= searchResultSummaryDisplayContext.isCreatorVisible() %>">
                                        · <liferay-ui:message key="written-by" /> <strong><%= searchResultSummaryDisplayContext.getCreatorUserName() %></strong>
                                    </c:if>

                                    <c:if test="<%= searchResultSummaryDisplayContext.isCreationDateVisible() %>">
                                        <liferay-ui:message key="on-date" /> <%= searchResultSummaryDisplayContext.getCreationDateString() %>
                                    </c:if>
                                </h6>

                                <c:if test="<%= searchResultSummaryDisplayContext.isContentVisible() %>">
                                    <h6 class="search-document-content text-default">
                                        <%= searchResultSummaryDisplayContext.getContent() %>
                                    </h6>
                                </c:if>

                                <c:if test="<%= searchResultSummaryDisplayContext.isFieldsVisible() %>">
                                    <h6 class="search-document-content text-default">
                                        <%
                                        boolean separate = false;
                                        for (com.liferay.portal.search.web.internal.result.display.context.SearchResultFieldDisplayContext fieldContext : searchResultSummaryDisplayContext.getFieldDisplayContexts()) {
                                            if (separate) {
                                                out.print(" · ");
                                            }
                                        %>
                                            <span class="badge"><%= fieldContext.getName() %></span>
                                            <span><%= fieldContext.getValuesToString() %></span>
                                        <%
                                            separate = true;
                                        }
                                        %>
                                    </h6>
                                </c:if>

                                <c:if test="<%= searchResultSummaryDisplayContext.isAssetCategoriesOrTagsVisible() %>">
                                    <h6 class="search-document-tags text-default">
                                        <liferay-asset:asset-tags-summary
                                            className="<%= searchResultSummaryDisplayContext.getClassName() %>"
                                            classPK="<%= searchResultSummaryDisplayContext.getClassPK() %>"
                                            paramName="<%= searchResultSummaryDisplayContext.getFieldAssetTagNames() %>"
                                            portletURL="<%= searchResultSummaryDisplayContext.getPortletURL() %>"
                                        />

                                        <liferay-asset:asset-categories-summary
                                            className="<%= searchResultSummaryDisplayContext.getClassName() %>"
                                            classPK="<%= searchResultSummaryDisplayContext.getClassPK() %>"
                                            paramName="<%= searchResultSummaryDisplayContext.getFieldAssetCategoryIds() %>"
                                            portletURL="<%= searchResultSummaryDisplayContext.getPortletURL() %>"
                                        />
                                    </h6>
                                </c:if>

                                <c:if test="<%= searchResultSummaryDisplayContext.isDocumentFormVisible() %>">
                                    <h6 class="expand-details text-default">
                                        <span style="font-size:xx-small;"><a href="javascript:;"><liferay-ui:message key="details" />...</a></span>
                                    </h6>

                                    <div class="hide table-details table-responsive">
                                        <table class="table">
                                            <thead>
                                                <tr>
                                                    <th style="text-align:right;">
                                                        <liferay-ui:message key="key" />
                                                    </th>
                                                    <th>
                                                        <liferay-ui:message key="value" />
                                                    </th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <%
                                                for (com.liferay.portal.search.web.internal.result.display.context.SearchResultFieldDisplayContext fieldContext : searchResultSummaryDisplayContext.getDocumentFormFieldDisplayContexts()) {
                                                %>
                                                    <tr>
                                                        <td style="padding-bottom:0; padding-top:0; text-align:right; word-break:break-all;" width="15%">
                                                            <strong><%= HtmlUtil.escape(fieldContext.getName()) %></strong>
                                                        </td>
                                                        <td style="padding-bottom:0; padding-top:0;">
                                                            <code><%= fieldContext.getValuesToString() %></code>
                                                        </td>
                                                    </tr>
                                                <%
                                                }
                                                %>
                                            </tbody>
                                        </table>
                                    </div>
                                </c:if>
                            </div>
                        </div>
                    </liferay-ui:search-container-column-text>
                </c:if>
            </liferay-ui:search-container-row>

            <c:if test="<%= searchResultsPortletDisplayContext.isShowPagination() %>">
                <aui:form action="#" useNamespace="<%= false %>">
                    <liferay-ui:search-paginator
                        id='<%= liferayPortletResponse.getNamespace() + "searchContainerTag" %>'
                        markupView="lexicon"
                        searchContainer="<%= searchResultsPortletDisplayContext.getSearchContainer() %>"
                    />
                </aui:form>
            </c:if>
        </liferay-ui:search-container>
    </c:otherwise>
</c:choose>