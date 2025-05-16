package com.liferay.demo.asset.article.service.template.context.contributor;

import com.liferay.demo.asset.article.service.template.context.contributor.api.ArticleServiceUtil;
import com.liferay.portal.kernel.template.TemplateContextContributor;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.osgi.service.component.annotations.Component;

@Component(immediate = true, property = {"type=GLOBAL"}, service = {TemplateContextContributor.class})
public class ArticleServiceTemplateContextContributor implements TemplateContextContributor {
    public void prepare(Map<String, Object> contextObjects, HttpServletRequest httpServletRequest) {
        ArticleServiceUtil articleService = new ArticleServiceUtil(httpServletRequest);
        contextObjects.put("articleService", articleService);
    }
}
