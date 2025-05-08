<a class="btn btn-light applications-btn btn-block font-b" target="_blank" href="${quickLinksLink.getData()}">
  <#if (parentStructureFieldSet2427557.PreviewImage.getData())?? && parentStructureFieldSet2427557.PreviewImage.getData() !="">
    <img alt="${parentStructureFieldSet2427557.PreviewImage.getAttribute("alt")}" data-fileentryid="${parentStructureFieldSet2427557.PreviewImage.getAttribute("fileEntryId")}" src="${parentStructureFieldSet2427557.PreviewImage.getData()}" />
  </#if>
  ${.vars['reserved-article-title'].data}
</a>