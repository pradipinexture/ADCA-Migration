package adc.dxp.rest.api.application.data;

import com.liferay.asset.kernel.model.AssetCategory;

/**
 * @author ana.cavadas
 *
 */
public class Category {
	
	private String name;
	private long categoryId;
	
	public Category() {}
	
	public Category(AssetCategory category) {
		name = category.getName();
		categoryId = category.getCategoryId();
	}
	
	public Category(String name, long categoryId) {
		super();
		this.name = name;
		this.categoryId = categoryId;
	}
	
	
	// getters + setters	
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public long getCategoryId() {
		return categoryId;
	}
	
	public void setCategoryId(long categoryId) {
		this.categoryId = categoryId;
	}
	
}
