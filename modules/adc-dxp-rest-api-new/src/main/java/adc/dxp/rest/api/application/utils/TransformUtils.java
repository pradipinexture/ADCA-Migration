package adc.dxp.rest.api.application.utils;

import java.io.StringReader;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.liferay.journal.model.JournalArticle;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.DocumentException;
import com.liferay.portal.kernel.xml.Node;
import com.liferay.portal.kernel.xml.SAXReaderUtil;

import adc.dxp.rest.api.application.data.enumeration.ChangeTagType;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Utility class for transforming and extracting structured content
 */
public class TransformUtils {

	/**
	 * Logging instance
	 */
	private static final Log _log = LogFactoryUtil.getLog(TransformUtils.class);

	/**
	 * Extracts image URL from JSON content
	 *
	 * @param imageJson the JSON containing image data
	 * @return the formatted image URL or null if extraction fails
	 */
	public static String getImageByContent(String imageJson) {
		String imageJsonOut = StringEscapeUtils.unescapeJava(imageJson);

		if (!StringUtils.isEmpty(imageJsonOut)) {
			try {
				ObjectMapper mapper = new ObjectMapper();

				ImageJSON obj = mapper.readValue(imageJsonOut, ImageJSON.class);
				String groupId = obj.getGroupId();
				String uuid = obj.getUuid();
				String fileName = obj.getName();

				// Return the image URL using the proper pattern for Liferay 7.4
				return String.format(Constants.IMAGE_URL_FOLDER, Long.valueOf(groupId), fileName, uuid);

			} catch (JsonMappingException e) {
				_log.error("Error mapping JSON to object", e);
			} catch (JsonProcessingException e) {
				_log.error("Error processing JSON", e);
			}
		}

		return null;
	}

	/**
	 * Extracts dynamic elements from content and returns them as a map
	 *
	 * @param content the XML content
	 * @return a map of dynamic attributes
	 */
	public static Map<String, DynamicAttribute> getDynamicElement(String content) {
		if (content == null || content.trim().isEmpty()) {
			return new HashMap<>();
		}

		JAXBContext jaxbContext;
		try {
			jaxbContext = JAXBContext.newInstance(Root.class);

			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

			Root root = (Root) jaxbUnmarshaller.unmarshal(new StringReader(content));

			List<DynamicElement> dynamicElementList = root.getDynamicElementList();
			Map<String, DynamicAttribute> attributeList = new HashMap<>();

			for (DynamicElement de: dynamicElementList) {
				attributeList.put(de.getName(), new DynamicAttribute(
						de.getName(),
						de.getDynamicContent() != null ? de.getDynamicContent().get__cdata() : null
				));
			}

			return attributeList;
		} catch (JAXBException e) {
			_log.error("Error unmarshalling content", e);
		}

		return new HashMap<>();
	}

	/**
	 * Extracts list of dynamic elements from content and returns them as a map
	 *
	 * @param content the XML content
	 * @return a map of dynamic attribute lists
	 */
	public static Map<String, List<DynamicAttribute>> getListDynamicElement(String content) {
		if (content == null || content.trim().isEmpty()) {
			return new HashMap<>();
		}

		JAXBContext jaxbContext;
		try {
			jaxbContext = JAXBContext.newInstance(Root.class);

			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

			Root root = (Root) jaxbUnmarshaller.unmarshal(new StringReader(content));

			List<DynamicElement> dynamicElementList = root.getDynamicElementList();

			Map<String, List<DynamicAttribute>> attributeList = new HashMap<>();
			for (DynamicElement de: dynamicElementList) {
				if (de.getDynamicContent() == null) {
					continue;
				}

				if (attributeList.containsKey(de.getName())) {
					List<DynamicAttribute> list = new ArrayList<>(attributeList.get(de.getName()));
					list.add(new DynamicAttribute(de.getName(), de.getDynamicContent().get__cdata()));
					attributeList.put(de.getName(), list);
				} else {
					List<DynamicAttribute> list = new ArrayList<>();
					list.add(new DynamicAttribute(de.getName(), de.getDynamicContent().get__cdata()));
					attributeList.put(de.getName(), list);
				}
			}

			return attributeList;
		} catch (JAXBException e) {
			_log.error("Error unmarshalling content", e);
		}

		return new HashMap<>();
	}

	/**
	 * Extracts the name element from content
	 *
	 * @param content the XML content
	 * @return the extracted name or null
	 */
	public static String getElement(String content) {
		if (content == null || content.trim().isEmpty()) {
			return null;
		}

		JAXBContext jaxbContext;
		try {
			jaxbContext = JAXBContext.newInstance(RootName.class);

			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

			RootName root = (RootName) jaxbUnmarshaller.unmarshal(new StringReader(content));

			return root.getName();
		} catch (JAXBException e) {
			_log.error("Error unmarshalling content", e);
		}

		return null;
	}

	/**
	 * Updates HTML tags with specified CSS classes
	 *
	 * @param content the HTML content
	 * @param tags the tag types to update
	 * @return the updated content
	 */
	public static String updateTags(String content, ChangeTagType... tags) {
		if (content == null) {
			return content;
		}

		String result = content;

		for (ChangeTagType ct : tags) {
			result = result.replaceAll(
					ct.getTag() + ".",
					ct.getTag() + " class=\"" + Constants.STYLE_FOR.get(ct.toString()) + "\" "
			);
		}

		return result;
	}

	/**
	 * Gets localized fields from a JournalArticle
	 *
	 * @param model the JournalArticle
	 * @param languageId the language ID
	 * @param fieldName the field name
	 * @return list of field values
	 */
	public static List<String> getLocalizableField(JournalArticle model, String languageId, String fieldName) {
		List<String> result = new ArrayList<>();

		if (model == null || languageId == null || fieldName == null) {
			return result;
		}

		try {
			Document document = SAXReaderUtil.read(model.getContentByLocale(languageId));
			List<Node> nodes = document.selectNodes("/root/dynamic-element[@name='" + fieldName + "']/dynamic-content");

			for (Node node : nodes) {
				result.add(node.getText());
			}
		} catch (DocumentException e) {
			_log.error("Error parsing document content", e);
		}

		return result;
	}

	/**
	 * Gets localized fields as a map from a JournalArticle
	 *
	 * @param model the JournalArticle
	 * @param languageId the language ID
	 * @param fieldName the field name
	 * @return list of field value maps
	 */
	public static List<Map<String, String>> getLocalizableFieldMap(JournalArticle model, String languageId, String fieldName) {
		List<Map<String, String>> result = new ArrayList<>();

		if (model == null || languageId == null || fieldName == null) {
			return result;
		}

		try {
			Document document = SAXReaderUtil.read(model.getContentByLocale(languageId));
			List<Node> nodes = document.selectNodes("/root/dynamic-element[@name='" + fieldName + "']");

			for (Node node : nodes) {
				Map<String, String> obj = new HashMap<>();
				Node contentNode = node.selectSingleNode("./dynamic-content");
				Node elementContentNode = node.selectSingleNode("./dynamic-element/dynamic-content");

				if (contentNode != null && elementContentNode != null) {
					obj.put(contentNode.getText(), elementContentNode.getText());
					result.add(obj);
				}
			}
		} catch (DocumentException e) {
			_log.error("Error parsing document content", e);
		}

		return result;
	}

	/**
	 * Gets nested localized fields as a map from a JournalArticle
	 *
	 * @param model the JournalArticle
	 * @param languageId the language ID
	 * @param fieldNameParent the parent field name
	 * @param fieldName the child field name
	 * @return list of field value maps with lists
	 */
	public static List<Map<String, List<String>>> getLocalizableFieldMap(
			JournalArticle model, String languageId, String fieldNameParent, String fieldName) {

		List<Map<String, List<String>>> result = new ArrayList<>();

		if (model == null || languageId == null || fieldNameParent == null || fieldName == null) {
			return result;
		}

		try {
			Document document = SAXReaderUtil.read(model.getContentByLocale(languageId));
			List<Node> nodes = document.selectNodes("/root/dynamic-element[@name='" + fieldNameParent + "']");
			Map<String, List<String>> obj = new HashMap<>();

			for (Node node : nodes) {
				List<Node> fieldNodes = node.selectNodes("./dynamic-element[@name='" + fieldName + "']/dynamic-content");
				Node contentNode = node.selectSingleNode("./dynamic-content");

				if (contentNode != null) {
					String mapKey = contentNode.getText();
					List<String> list = new ArrayList<>();

					for (Node n : fieldNodes) {
						list.add(n.getText());
					}

					obj.put(mapKey, list);
				}
			}

			result.add(obj);
		} catch (DocumentException e) {
			_log.error("Error parsing document content", e);
		}

		return result;
	}
}
