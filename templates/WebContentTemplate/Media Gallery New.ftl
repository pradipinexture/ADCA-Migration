<#-- Retrieve the published date meta data field of the web content -->
<#assign displaydate=.vars['reserved-article-display-date'].data>
    <#-- Parse the date to a date object -->
<#assign displaydate=displaydate?datetime("EEE, d MMM yyyy HH:mm:ss Z")>
<#assign catLocalService=serviceLocator.findService("com.liferay.asset.kernel.service.AssetCategoryLocalService")
    JournalArticleService=serviceLocator.findService("com.liferay.journal.service.JournalArticleResourceLocalService")
    resourcePrimKey=JournalArticleService.getArticleResourcePrimKey(groupId, .vars['reserved-article-id'].data)
    articleCatNames=catLocalService.getCategories("com.liferay.journal.model.JournalArticle", resourcePrimKey) />
<#if Type.getData()=='G'>
    <#if (parentStructureFieldSet2427557.PreviewImage.getData())?? && parentStructureFieldSet2427557.PreviewImage.getData() !="">
        <a href="${parentStructureFieldSet2427557.PreviewImage.getData()}" class="col-md-4 v-space" style="height:250px">
            <div class="position-relative h-100 w-100 rounded-10 card-shadowed overflow-hidden d-flex justify-content-end text-white p-3 flex-column" style="background:linear-gradient(to top ,var(--primary-color),transparent),url(${PreviewImage.getData()}) center; background-size:cover;">
                <b class="two-lines mb-2">
                    ${.vars['reserved-article-title'].data}
                </b>
                <small> <i class="icon-calendar mr-2"></i>
                    <#if locale !='en_US'>
                        <#setting locale='ar_AE'>
                    </#if>
                    ${displaydate?string["dd MMMM yyyy"]}
                </small>
            </div>
        </a>
    </#if>
    <#elseif Type.getData()=='V'>
        <#if (Video.getData()) !=''>
            <a data-sub-html="${.vars['reserved-article-title'].data}"
                data-html="#video-${.vars['reserved-article-id'].data}" class="col-md-4 v-space v-gallery-item" style="height:250px">
                <div style="display:none;" id="video-${.vars['reserved-article-id'].data}">
                    <video class="lg-video-object lg-html5" controls preload="none">
                        <source src="${Video.getData()}" type="video/mp4">
                        Your browser does not support HTML5 video.
                    </video>
                </div>
                <div class="position-relative h-100 w-100 rounded-10 card-shadowed overflow-hidden d-flex justify-content-end text-white p-3 flex-column" style="background:linear-gradient(to top ,var(--primary-color),transparent),url(${parentStructureFieldSet2427557.PreviewImage.getData()}) center; background-size:cover;">
                    <i class="icon-play play-icon"></i>
                    <b class="two-lines mb-2">
                        ${.vars['reserved-article-title'].data}
                    </b>
                    <small> <i class="icon-calendar mr-2"></i>
                        <#if locale !='en_US'>
                            <#setting locale='ar_AE'>
                        </#if>
                        ${displaydate?string["dd MMMM yyyy"]}
                    </small>
                </div>
            </a>
        </#if>
</#if>