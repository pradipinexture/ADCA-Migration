<#assign img='/o/ad-customs-theme/images/placeholder-logo.svg' />
<#-- Retrieve the published date meta data field of the web content -->
<#assign displaydate=.vars['reserved-article-display-date'].data>
<#-- Parse the date to a date object -->
<#assign displaydate=displaydate?datetime("EEE, d MMM yyyy HH:mm:ss Z")>
<div style="height: 400px" class="wow pulse">
    <a href="/group/portal/news/detail?id=${.vars['reserved-article-id'].data}" class="card-link">
        <div class="bg-white card h-100 overflow-hidden">
            <div class="col mx-auto hover-box position-relative d-flex flex-column justify-content-end overflow-hidden p-3 h-100">
                <#if (parentStructureFieldSet2427557.PreviewImage.getData())?? && parentStructureFieldSet2427557.PreviewImage.getData() !="">
                    <img class="img-object-fit" alt="${parentStructureFieldSet2427557.PreviewImage.getAttribute("alt")}" data-fileentryid="${parentStructureFieldSet2427557.PreviewImage.getAttribute("fileEntryId")}" src="${parentStructureFieldSet2427557.PreviewImage.getData()}" />
                    <#else>
                        <img class="img-object-fit" src="${img}">
                </#if>
                <div class="flex-column z-index-10 text-white">
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
                <div class="more-div z-index-10 internal-card-body h-auto">
                    <p class="card-text text-white v-align-gap justify-content-end mt-3">
                        <@liferay.language key="read-more" />
                        <button class="btn btn-outline-light rounded-circle">
                            <i class="icon-arrow-right"></i>
                        </button
                            </p>
                </div>
            </div>
        </div>
    </a>
</div>