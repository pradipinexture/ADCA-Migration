<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>

<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui"%><%@
taglib
	uri="http://liferay.com/tld/clay" prefix="clay"%><%@
taglib
	uri="http://liferay.com/tld/ddm" prefix="liferay-ddm"%><%@
taglib
	uri="http://liferay.com/tld/react" prefix="react"%><%@
taglib
	uri="http://liferay.com/tld/theme" prefix="liferay-theme"%><%@
taglib
	uri="http://liferay.com/tld/ui" prefix="liferay-ui"%>

<%@ page import="com.liferay.petra.string.StringPool"%><%@
page
	import="com.liferay.portal.kernel.language.LanguageUtil"%><%@
page
	import="com.liferay.portal.kernel.util.HashMapBuilder"%><%@
page
	import="com.liferay.portal.kernel.util.HtmlUtil"%><%@
page
	import="com.liferay.portal.kernel.util.PortalUtil"%><%@
page
	import="com.liferay.portal.kernel.util.ReleaseInfo"%><%@
page
	import="com.liferay.portal.kernel.util.Validator"%><%@
page
	import="com.liferay.portal.kernel.util.WebKeys"%><%@
page
	import="com.liferay.portal.search.web.internal.search.bar.portlet.SearchBarPortlet"%><%@
page
	import="com.liferay.portal.search.web.internal.search.bar.portlet.configuration.SearchBarPortletInstanceConfiguration"%><%@
page
	import="com.liferay.portal.search.web.internal.search.bar.portlet.display.context.SearchBarPortletDisplayContext"%>

<%@ page import="java.util.ArrayList"%>

<liferay-theme:defineObjects />

<portlet:defineObjects />
<%
String randomNamespace = PortalUtil.generateRandomKey(request, "portlet_search_bar") + StringPool.UNDERLINE;

SearchBarPortletDisplayContext searchBarPortletDisplayContext = (SearchBarPortletDisplayContext) java.util.Objects
		.requireNonNull(request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT));
%>
<c:choose>
	<c:when
		test="<%=searchBarPortletDisplayContext.isDestinationUnreachable()%>">
		<div class="alert alert-info c-mb-0 text-center">
			<liferay-ui:message key="this-search-bar-is-not-visible-to-users-yet" />

			<clay:link href="javascript:void(0);"
				label='<%=LanguageUtil.get(request, "set-up-its-destination-to-make-it-visible")%>'
				onClick="<%=portletDisplay.getURLConfigurationJS()%>" />
		</div>
	</c:when>
	<c:otherwise>
		<aui:form
			action="<%=searchBarPortletDisplayContext.getSearchURL()%>"
			method="get" name="fm">
			<c:if
				test="<%=!Validator.isBlank(searchBarPortletDisplayContext.getPaginationStartParameterName())%>">
				<input class="search-bar-reset-start-page"
					name="<%=searchBarPortletDisplayContext.getPaginationStartParameterName()%>"
					type="hidden" value="0" />
			</c:if>

			<%
			SearchBarPortletInstanceConfiguration searchBarPortletInstanceConfiguration = searchBarPortletDisplayContext
					.getSearchBarPortletInstanceConfiguration();
			%>

			<liferay-ddm:template-renderer
				className="<%=SearchBarPortlet.class.getName()%>"
				contextObjects='<%=HashMapBuilder.<String, Object>put("namespace", liferayPortletResponse.getNamespace())
		.put("searchBarPortletDisplayContext", searchBarPortletDisplayContext).build()%>'
				displayStyle="<%=searchBarPortletInstanceConfiguration.displayStyle()%>"
				displayStyleGroupId="<%=searchBarPortletDisplayContext.getDisplayStyleGroupId()%>"
				entries="<%=new ArrayList<>()%>">
				<c:choose>
					<c:when
						test="<%=searchBarPortletDisplayContext.isSuggestionsEnabled()%>">
						<div id="<portlet:namespace />reactSearchBar">
							<react:component module="js/components/SearchBar"
								props='<%=HashMapBuilder
		.<String, Object>put("destinationFriendlyURL", searchBarPortletDisplayContext.getDestinationFriendlyURL())
		.put("emptySearchEnabled", searchBarPortletDisplayContext.isEmptySearchEnabled())
		.put("isDXP", ReleaseInfo.isDXP())
		.put("isSearchExperiencesSupported", searchBarPortletDisplayContext.isSearchExperiencesSupported())
		.put("keywords", searchBarPortletDisplayContext.getKeywords())
		.put("keywordsParameterName", searchBarPortletDisplayContext.getKeywordsParameterName())
		.put("letUserChooseScope", searchBarPortletDisplayContext.isLetTheUserChooseTheSearchScope())
		.put("paginationStartParameterName", searchBarPortletDisplayContext.getPaginationStartParameterName())
		.put("scopeParameterName", searchBarPortletDisplayContext.getScopeParameterName())
		.put("scopeParameterStringCurrentSite",
				searchBarPortletDisplayContext.getCurrentSiteSearchScopeParameterString())
		.put("scopeParameterStringEverything", searchBarPortletDisplayContext.getEverythingSearchScopeParameterString())
		.put("searchURL", searchBarPortletDisplayContext.getSearchURL())
		.put("selectedEverythingSearchScope", searchBarPortletDisplayContext.isSelectedEverythingSearchScope())
		.put("suggestionsContributorConfiguration",
				searchBarPortletDisplayContext.getSuggestionsContributorConfiguration())
		.put("suggestionsDisplayThreshold", searchBarPortletDisplayContext.getSuggestionsDisplayThreshold())
		.put("suggestionsURL", searchBarPortletDisplayContext.getSuggestionsURL()).build()%>' />
						</div>
					</c:when>
					<c:otherwise>
						<div class="search-bar">
							<aui:input cssClass="search-bar-empty-search-input"
								name="emptySearchEnabled" type="hidden"
								value="<%=searchBarPortletDisplayContext.isEmptySearchEnabled()%>" />

							<div
								class="input-group <%=searchBarPortletDisplayContext.isLetTheUserChooseTheSearchScope() ? "search-bar-scope" : "search-bar-simple"%>">
								<c:choose>
									<c:when
										test="<%=searchBarPortletDisplayContext.isLetTheUserChooseTheSearchScope()%>">
										<aui:input autocomplete="off"
											cssClass="search-bar-keywords-input" data-qa-id="searchInput"
											id="<%=randomNamespace + HtmlUtil.escapeAttribute(searchBarPortletDisplayContext.getKeywordsParameterName())%>"
											label=""
											name="<%=HtmlUtil.escapeAttribute(searchBarPortletDisplayContext.getKeywordsParameterName())%>"
											placeholder='<%=LanguageUtil.get(request, "search-...")%>'
											title='<%=LanguageUtil.get(request, "search")%>'
											type="text" useNamespace="<%=false%>"
											value="<%=HtmlUtil.escapeAttribute(searchBarPortletDisplayContext.getKeywords())%>"
											wrapperCssClass="input-group-item input-group-prepend search-bar-keywords-input-wrapper Hello-world-test" />

										<aui:select cssClass="search-bar-scope-select"
											id="<%=randomNamespace + HtmlUtil.escapeAttribute(searchBarPortletDisplayContext.getScopeParameterName())%>"
											label=""
											name="<%=HtmlUtil.escapeAttribute(searchBarPortletDisplayContext.getScopeParameterName())%>"
											title="scope" useNamespace="<%=false%>"
											wrapperCssClass="input-group-item input-group-item-shrink input-group-prepend search-bar-search-select-wrapper">
											<aui:option label="this-site"
												selected="<%=searchBarPortletDisplayContext.isSelectedCurrentSiteSearchScope()%>"
												value="<%=searchBarPortletDisplayContext.getCurrentSiteSearchScopeParameterString()%>" />

											<c:if
												test="<%=searchBarPortletDisplayContext.isAvailableEverythingSearchScope()%>">
												<aui:option label="everything"
													selected="<%=searchBarPortletDisplayContext.isSelectedEverythingSearchScope()%>"
													value="<%=searchBarPortletDisplayContext.getEverythingSearchScopeParameterString()%>" />
											</c:if>
										</aui:select>

										<div
											class="input-group-append input-group-item input-group-item-shrink">
											<clay:button
												aria-label='<%=LanguageUtil.get(request, "search")%>'
												displayType="secondary" icon="search" type="submit" />
										</div>
									</c:when>
									<c:otherwise>
										<div
											class="input-group-item search-bar-keywords-input-wrapper">

											<!-- adc-dxp custom tag -->
											<h1>Hello Time Pass</h1>
											<div
												class="input-group-inset-item input-group-inset-item-before search-bar-search-button-wrapper">

												<div class="search-info-left input-group-text">Search</div>

											</div>

											<input
												aria-label="<%=LanguageUtil.get(request, "search")%>"
												autocomplete="off"
												class="form-control input-group-inset input-group-inset-after search-bar-keywords-input new-jellll"
												data-qa-id="searchInput"
												id="<%=randomNamespace%><%=HtmlUtil.escapeAttribute(searchBarPortletDisplayContext.getKeywordsParameterName())%>"
												name="<%=HtmlUtil.escapeAttribute(searchBarPortletDisplayContext.getKeywordsParameterName())%>"
												placeholder="<%=LanguageUtil.get(request, "search-...")%>"
												title="<%=LanguageUtil.get(request, "search")%>"
												type="text"
												value="<%=HtmlUtil.escapeAttribute(searchBarPortletDisplayContext.getKeywords())%>" />

											<aui:input
												name="<%=HtmlUtil.escapeAttribute(searchBarPortletDisplayContext.getScopeParameterName())%>"
												type="hidden"
												value="<%=searchBarPortletDisplayContext.getScopeParameterValue()%>" />

											<div
												class="input-group-inset-item input-group-inset-item-after">
												<clay:button
													aria-label='<%=LanguageUtil.get(request, "search")%>'
													displayType="unstyled" icon="search" type="submit" />
											</div>
										</div>
									</c:otherwise>
								</c:choose>
							</div>
						</div>
					</c:otherwise>
				</c:choose>
			</liferay-ddm:template-renderer>
		</aui:form>

		<aui:script use="aui-base,liferay-search-bar">
			if (!A.one('#<portlet:namespace />reactSearchBar')) {
				new Liferay.Search.SearchBar(A.one('#<portlet:namespace />fm'));
			}
		</aui:script>
	</c:otherwise>
</c:choose>