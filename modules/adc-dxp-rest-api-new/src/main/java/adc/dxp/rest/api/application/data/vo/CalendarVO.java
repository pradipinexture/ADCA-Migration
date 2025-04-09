package adc.dxp.rest.api.application.data.vo;

public class CalendarVO {

	Long primaryKey;

	String name;

	public CalendarVO(Long primaryKey, String name) {
		super();
		this.primaryKey = primaryKey;
		this.name = name;
	}

	public Long getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(Long primaryKey) {
		this.primaryKey = primaryKey;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
