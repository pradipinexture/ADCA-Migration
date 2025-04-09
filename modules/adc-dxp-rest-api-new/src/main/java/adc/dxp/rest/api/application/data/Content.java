package adc.dxp.rest.api.application.data;


import adc.dxp.rest.api.application.utils.ContentType;

/**
 * Base class for content objects
 */
public class Content {

	private ContentType type;

	/**
	 * Default constructor
	 */
	public Content() {
	}

	/**
	 * Constructor with content type
	 *
	 * @param type the content type
	 */
	public Content(ContentType type) {
		this.type = type;
	}

	/**
	 * Gets the content type
	 *
	 * @return the content type
	 */
	public ContentType getType() {
		return type;
	}

	/**
	 * Sets the content type
	 *
	 * @param type the content type to set
	 */
	public void setType(ContentType type) {
		this.type = type;
	}
}