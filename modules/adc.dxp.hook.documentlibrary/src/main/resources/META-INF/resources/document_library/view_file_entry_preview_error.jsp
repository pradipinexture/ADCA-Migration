<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/document_library/init.jsp" %>

<%
Exception exception = (Exception)request.getAttribute(DLWebKeys.DOCUMENT_LIBRARY_PREVIEW_EXCEPTION);
FileVersion fileVersion = (FileVersion)request.getAttribute(WebKeys.DOCUMENT_LIBRARY_FILE_VERSION);
%>

<c:choose>
	<c:when test="<%= exception instanceof DLPreviewSizeException %>">
		<div class="preview-file-error-container">
			<h3><liferay-ui:message key="file-too-big-to-preview" /></h3>

			<p class="text-secondary">
				<liferay-ui:message key="file-exceeds-size-limit-to-preview-download-to-view-it" />
			</p>

			<clay:link
				displayType="secondary"
				href="<%= DLURLHelperUtil.getDownloadURL(fileVersion.getFileEntry(), fileVersion, themeDisplay, StringPool.BLANK) %>"
				label="download"
				title='<%= LanguageUtil.format(resourceBundle, "file-size-x", LanguageUtil.formatStorageSize(fileVersion.getSize(), locale), false) %>'
				type="button"
			/>
		</div>
	</c:when>
	<c:when test="<%= exception instanceof DLPreviewGenerationInProcessException %>">
		<clay:alert
			message="generating-preview-will-take-a-few-minutes"
		/>
	</c:when>
	<c:otherwise>
		<div class="preview-file-error-container">
			<h3><liferay-ui:message key="no-preview-available" /></h3>

			<p class="text-secondary">
				<liferay-ui:message key="hmm-looks-like-this-item-doesnt-have-a-preview-we-can-show-you" />
			</p>
		</div>
	</c:otherwise>
</c:choose>