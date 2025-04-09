package adc.dxp.rest.api.application.data;

import com.liferay.portal.vulcan.pagination.Page;

public class RelatedBaseCategories {
	
	private Page<? extends Content> baseContentList;
	private long categoryId;
	private String categoryName;
	
	public RelatedBaseCategories() {}
	
	public RelatedBaseCategories(Page<Content> baseContentList, long categoryId, String categoryName) {
		super();
		this.baseContentList = baseContentList;
		this.categoryId = categoryId;
		this.categoryName = categoryName;
	}
	
	
	
	//getters + setters

	public Page<? extends Content> getBaseContentList() {
		return baseContentList;
	}

	public void setBaseContentList(Page<? extends Content> baseContentList) {
		this.baseContentList = baseContentList;
	}

	public long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(long categoryId) {
		this.categoryId = categoryId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	
	
	
	
	

}
