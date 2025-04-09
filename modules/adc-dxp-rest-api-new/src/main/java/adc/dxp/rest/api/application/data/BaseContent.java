package adc.dxp.rest.api.application.data;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import adc.dxp.rest.api.application.utils.*;
import com.liferay.journal.model.JournalArticle;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import org.apache.commons.lang3.StringEscapeUtils;

/**
 * Base class for content types in the ADC DXP application
 */
public class BaseContent extends Content {

	/**
	 * logging instance
	 */
	private static final Log _log = LogFactoryUtil.getLog(BaseContent.class);

	private long id;
	private long primaryKey;
	private long resourcePrimaryKey;
	private String articleId;
	private String title;
	private String content;
	private long entryId;
	private String previewImage;
	private String summary;
	private String url;
	private String fiendlyUrlAsXML;
	private Date displayDate;
	private Date expirationDate;
	private Map<Locale, String> fiendlyUrl;
	private Date createDate;
	private Map<String, DynamicAttribute> attributes;
	private Map<String, List<DynamicAttribute>> listAttributes;
	private Category category;
	private StructureType contentType;

	/**
	 * Default constructor
	 */
	public BaseContent() {
		category = new Category();
	}

	/**
	 * Constructor with content type
	 *
	 * @param type the content type
	 */
	public BaseContent(ContentType type) {
		super(type);
		category = new Category();
	}

	/**
	 * Constructor with basic properties
	 *
	 * @param title the title
	 * @param summary the summary
	 * @param previewImage the preview image URL
	 * @param displayDate the display date
	 */
	public BaseContent(String title, String summary, String previewImage, Date displayDate) {
		this();
		this.title = title;
		this.summary = summary;
		this.previewImage = previewImage;
		this.displayDate = displayDate;
	}

	/**
	 * Constructor from a JournalArticle
	 *
	 * @param article the journal article
	 */
	public BaseContent(JournalArticle article) {
		this();
		id = article.getId();
		primaryKey = article.getPrimaryKey();
		resourcePrimaryKey = article.getResourcePrimKey();
		articleId = article.getArticleId();
		content = article.getContent();
		url = article.getUrlTitle();
		createDate = article.getCreateDate();
		title = article.getTitle();
		displayDate = article.getDisplayDate();
		expirationDate = article.getExpirationDate();

		try {
			// In Liferay 7.4, we handle friendly URLs differently
			fiendlyUrlAsXML = article.getFriendlyURLsXML();
			fiendlyUrl = article.getFriendlyURLMap();
		} catch (PortalException e) {
			_log.error("Error getting friendly URLs", e);
		}

		// Process content to extract attributes
		String contentUnescape = StringEscapeUtils.unescapeJava(content);
		attributes = TransformUtils.getDynamicElement(contentUnescape);

		if (attributes != null) {
			if (attributes.get(Constants.STRUNCTURE_FIELD_NAME_IMAGE) != null) {
				String imageJson = attributes.get(Constants.STRUNCTURE_FIELD_NAME_IMAGE).getValue();
				previewImage = TransformUtils.getImageByContent(imageJson);
			}

			if (attributes.get(Constants.STRUNCTURE_FIELD_NAME_SUMMARY) != null) {
				summary = attributes.get(Constants.STRUNCTURE_FIELD_NAME_SUMMARY).getValue();
			}
		}
	}

	/**
	 * Constructor from a JournalArticle with language ID
	 *
	 * @param article the journal article
	 * @param languageId the language ID
	 */
	public BaseContent(JournalArticle article, Optional<String> languageId) {
		// Initialize basic properties
		id = article.getId();
		primaryKey = article.getPrimaryKey();
		resourcePrimaryKey = article.getResourcePrimKey();
		articleId = article.getArticleId();

		// Get content in the specified language
		String langId = languageId.orElse(Constants.DEFAULT_VALUE_LANGUAGE);
		content = article.getContentByLocale(langId);

		url = article.getUrlTitle();
		createDate = article.getCreateDate();
		title = article.getTitle(langId);
		displayDate = article.getDisplayDate();
		expirationDate = article.getExpirationDate();

		try {
			fiendlyUrlAsXML = article.getFriendlyURLsXML();
			fiendlyUrl = article.getFriendlyURLMap();
		} catch (PortalException e) {
			_log.error("Error getting friendly URLs", e);
		}

		// Process content to extract attributes
		String contentUnescape = StringEscapeUtils.unescapeJava(content);
		attributes = TransformUtils.getDynamicElement(contentUnescape);
		if (attributes != null) {
			if (attributes.get(Constants.STRUNCTURE_FIELD_NAME_IMAGE) != null) {
				String imageJson = attributes.get(Constants.STRUNCTURE_FIELD_NAME_IMAGE).getValue();
				previewImage = TransformUtils.getImageByContent(imageJson);
			}

			if (attributes.get(Constants.STRUNCTURE_FIELD_NAME_SUMMARY) != null) {
				summary = attributes.get(Constants.STRUNCTURE_FIELD_NAME_SUMMARY).getValue();
			}
		}

		// Initialize category
		category = new Category();
	}

	/**
	 * Constructor from a JournalArticle with language ID and content type
	 *
	 * @param article the journal article
	 * @param languageId the language ID
	 * @param type the content type
	 */
	public BaseContent(JournalArticle article, Optional<String> languageId, ContentType type) {
		this(article, languageId);
		setType(type);
	}

	/**
	 * Constructor from a JournalArticle with language ID, content type, and dynamic list flag
	 *
	 * @param article the journal article
	 * @param languageId the language ID
	 * @param type the content type
	 * @param dynamicList whether to process dynamic list elements
	 */
	public BaseContent(JournalArticle article, Optional<String> languageId, ContentType type, boolean dynamicList) {
		this(article, languageId);
		setType(type);

		if (dynamicList) {
			String contentUnescape = StringEscapeUtils.unescapeJava(content);
			listAttributes = TransformUtils.getListDynamicElement(contentUnescape);
		}
	}

	// Getters and setters

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(long primaryKey) {
		this.primaryKey = primaryKey;
	}

	public String getArticleId() {
		return articleId;
	}

	public void setArticleId(String articleId) {
		this.articleId = articleId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public long getEntryId() {
		return entryId;
	}

	public void setEntryId(long entryId) {
		this.entryId = entryId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getFiendlyUrlAsXML() {
		return fiendlyUrlAsXML;
	}

	public void setFiendlyUrlAsXML(String fiendlyUrlAsXML) {
		this.fiendlyUrlAsXML = fiendlyUrlAsXML;
	}

	public Map<Locale, String> getFiendlyUrl() {
		return fiendlyUrl;
	}

	public void setFiendlyUrl(Map<Locale, String> fiendlyUrl) {
		this.fiendlyUrl = fiendlyUrl;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Map<String, DynamicAttribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, DynamicAttribute> attributes) {
		this.attributes = attributes;
	}

	public Map<String, List<DynamicAttribute>> getListAttributes() {
		return listAttributes;
	}

	public void setListAttributes(Map<String, List<DynamicAttribute>> listAttributes) {
		this.listAttributes = listAttributes;
	}

	public String getPreviewImage() {
		return previewImage;
	}

	public void setPreviewImage(String previewImage) {
		this.previewImage = previewImage;
	}

	public long getResourcePrimaryKey() {
		return resourcePrimaryKey;
	}

	public void setResourcePrimaryKey(long resourcePrimaryKey) {
		this.resourcePrimaryKey = resourcePrimaryKey;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public Date getDisplayDate() {
		return displayDate;
	}

	public void setDisplayDate(Date displayDate) {
		this.displayDate = displayDate;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	public StructureType getContentType() {
		return contentType;
	}

	public void setContentType(StructureType contentType) {
		this.contentType = contentType;
	}

	// equals and hashCode

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BaseContent other = (BaseContent) obj;
		if (id != other.id)
			return false;
		return true;
	}
}
