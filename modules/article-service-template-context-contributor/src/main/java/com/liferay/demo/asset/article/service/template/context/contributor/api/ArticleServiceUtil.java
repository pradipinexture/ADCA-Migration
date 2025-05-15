package com.liferay.demo.asset.article.service.template.context.contributor.api;

import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.journal.service.JournalArticleServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.PortletRequestModel;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletRequest;

public class ArticleServiceUtil {
    private long _groupId;

    private HttpServletRequest _httpServletRequest;

    private String _languageId;

    private PortletRequestModel _portletRequestModel;

    private ThemeDisplay _themeDisplay;

    public ArticleServiceUtil(HttpServletRequest httpServletRequest) {
        this._httpServletRequest = httpServletRequest;
        this._themeDisplay = (ThemeDisplay)this._httpServletRequest.getAttribute("LIFERAY_SHARED_THEME_DISPLAY");
        this._groupId = this._themeDisplay.getSiteGroupId();
        this._languageId = this._themeDisplay.getLanguageId();
        this._portletRequestModel = getPortletRequestModel(this._httpServletRequest);
    }

    public String getContentByClassPK(long classPK) {
        try {
            JournalArticle journalArticle = getArticleByClassPK(classPK);
            return getContent(journalArticle, journalArticle
                    .getDDMTemplateKey());
        } catch (Exception e) {
            return getNoArticleWithClassPKMessage(classPK);
        }
    }

    public String getContentByClassPK(long classPK, String ddmTemplateKey) {
        try {
            JournalArticle journalArticle = getArticleByClassPK(classPK);
            return getContent(journalArticle, ddmTemplateKey);
        } catch (Exception e) {
            return getNoArticleWithClassPKMessage(classPK);
        }
    }

    public String getContentByPrimaryKey(String primaryKey) {
        try {
            return JournalArticleServiceUtil.getArticleContent(this._groupId, primaryKey, this._languageId, this._portletRequestModel, this._themeDisplay);
        } catch (Exception e) {
            return getNoArticleWithPrimaryKeyMessage(primaryKey);
        }
    }

    public String getContentByPrimaryKey(String primaryKey, String ddmTemplateKey) {
        try {
            JournalArticle journalArticle = JournalArticleServiceUtil.getArticle(this._groupId, primaryKey);
            return getContent(journalArticle, ddmTemplateKey);
        } catch (Exception e) {
            return getNoArticleWithPrimaryKeyMessage(primaryKey);
        }
    }

    protected JournalArticle getArticleByClassPK(long classPK) {
        return JournalArticleLocalServiceUtil.fetchLatestArticle(classPK);
    }

    protected String getContent(JournalArticle journalArticle, String ddmTemplateKey) throws PortalException {
        return JournalArticleLocalServiceUtil.getArticleContent(journalArticle, ddmTemplateKey, "view", this._languageId, this._portletRequestModel, this._themeDisplay);
    }

    protected String getNoArticleWithClassPKMessage(long classPK) {
        return "Unable to get article with classPK: " + classPK + ".";
    }

    protected String getNoArticleWithPrimaryKeyMessage(String primaryKey) {
        return "Unable to get article with primaryKey: " + primaryKey + ".";
    }

    protected PortletRequestModel getPortletRequestModel(HttpServletRequest httpServletRequest) {
        PortletRequest portletRequest = (PortletRequest)httpServletRequest.getAttribute("javax.portlet.request");
        PortletResponse portletResponse = (PortletResponse)httpServletRequest.getAttribute("javax.portlet.response");
        if (portletRequest == null || portletResponse == null)
            return null;
        return new PortletRequestModel(portletRequest, portletResponse);
    }
}
