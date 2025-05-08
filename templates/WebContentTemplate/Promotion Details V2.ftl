<#-- Retrieve the published date meta data field of the web content -->
<#assign
    catLocalService=serviceLocator.findService("com.liferay.asset.kernel.service.AssetCategoryLocalService")
    JournalArticleService=serviceLocator.findService("com.liferay.journal.service.JournalArticleResourceLocalService")
    resourcePrimKey=JournalArticleService.getArticleResourcePrimKey(groupId,
    .vars['reserved-article-id'].data)
    articleCatNames=catLocalService.getCategories("com.liferay.journal.model.JournalArticle",
    resourcePrimKey)>
<div class="rounded-10 bg-white p-4 h-100">
    <div class="row">
        <div class="col-md-5">
            <#if (parentStructureFieldSet2427557.PreviewImage.getData())?? && parentStructureFieldSet2427557.PreviewImage.getData() !="">
                <a href="${parentStructureFieldSet2427557.PreviewImage.getData()}" target="_blank">
                    <img alt="${parentStructureFieldSet2427557.PreviewImage.getAttribute("alt")}" data-fileentryid="${parentStructureFieldSet2427557.PreviewImage.getAttribute("fileEntryId")}" src="${parentStructureFieldSet2427557.PreviewImage.getData()}" class="mw-100 d-block m-auto rounded-10" style="max-height:700px" />
                </a>
            </#if>
        </div>
        <div class="col-md-7">
            <h3>
                ${.vars['reserved-article-title'].data}
            </h3>
            <#list articleCatNames as catName>
                <span class="badge gold-bg text-white mb-2">
                    ${catName.getTitle(locale)}
                </span>
            </#list>
            <p>
                <#if (parentStructureFieldSet2427557.Summary.getData())??>
                    ${parentStructureFieldSet2427557.Summary.getData()}
                </#if>
            </p>
            <b class="date-time">
                <#assign StartDate_Data=getterUtil.getString(StartDate.getData())>
                    <#if validator.isNotNull(StartDate_Data)>
                        <#assign StartDate_DateObj=dateUtil.parseDate("yyyy-MM-dd", StartDate_Data, locale)>
                            <i class="icon-calendar mr-2"></i>
                            <span>
                                <@liferay.language key='from' />
                            </span>
                            ${dateUtil.getDate(StartDate_DateObj, "dd MMMM yyyy", locale)}
                    </#if>
                    <#assign EndDate_Data=getterUtil.getString(EndDate.getData())>
                        <#if validator.isNotNull(EndDate_Data)>
                            <#assign EndDate_DateObj=dateUtil.parseDate("yyyy-MM-dd", EndDate_Data, locale)>
                                <span class="mx-2">
                                    <@liferay.language key='to' />
                                </span> ${dateUtil.getDate(EndDate_DateObj, "dd MMMM yyyy", locale)}
                        </#if>
            </b>
            <#if validator.isNotNull(Link.getData())>
                <div class="mt-4 d-block">
                    <a href="${Link.getData()}" class="btn btn-sm btn-secondary-outline rounded-20" target="_blank">
                        ${ButtonLabel.getData()}
                    </a>
                </div>
            </#if>
        </div>
    </div>
</div>