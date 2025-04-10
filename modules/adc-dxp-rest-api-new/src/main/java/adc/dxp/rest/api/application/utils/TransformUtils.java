package adc.dxp.rest.api.application.utils;

import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.liferay.journal.model.JournalArticle;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.xml.Node;

import adc.dxp.rest.api.application.data.enumeration.ChangeTagType;


import java.util.ArrayList;
import java.util.Arrays;

import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.DocumentException;
import com.liferay.portal.kernel.xml.SAXReaderUtil;

public class TransformUtils {

	/**
	 * logging instance
	 */
	private static Log _log = LogFactoryUtil.getLog(TransformUtils.class);

	public static String getImageByContent(String imageJson) {

		String imageJsonOut = StringEscapeUtils.unescapeJava(imageJson);

		if (!StringUtils.isEmpty(imageJsonOut)) {
			try {
				ObjectMapper mapper = new ObjectMapper();

				ImageJSON obj = mapper.readValue(imageJsonOut, ImageJSON.class);
				String groupId = obj.getGroupId();
				String uuid = obj.getUuid();
				String fileName = obj.getName();

				//return String.format(Constants.IMAGE_URL, uuid, Long.valueOf(groupId));

				return String.format(Constants.IMAGE_URL_FOLDER, Long.valueOf(groupId), fileName, uuid );

			} catch (JsonMappingException e) {
				// TODO ana.cavadas
				e.printStackTrace();
			} catch (JsonProcessingException e) {
				// TODO ana.cavadas
				e.printStackTrace();
			}
		}

		return null;
	}


	public static Map<String, DynamicAttribute> getDynamicElement(String content) {
		JAXBContext jaxbContext;
		try
		{
			jaxbContext = JAXBContext.newInstance(Root.class);

			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

			Root root = (Root) jaxbUnmarshaller.unmarshal(new StringReader(content));

			List<DynamicElement> dynamicElementList = root.getDynamicElementList();
			Map<String, DynamicAttribute> attributeList = new HashMap<>();
			for (DynamicElement de: dynamicElementList) {
				attributeList.put(de.getName(), new DynamicAttribute(de.getName(),  de.getDynamicContent() != null ? de.getDynamicContent().get__cdata() : null));

			}

			return attributeList;
		}
		catch (JAXBException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public static Map<String, List<DynamicAttribute>> getListDynamicElement(String content) {
		JAXBContext jaxbContext;
		try
		{
			jaxbContext = JAXBContext.newInstance(Root.class);

			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

			Root root = (Root) jaxbUnmarshaller.unmarshal(new StringReader(content));

			List<DynamicElement> dynamicElementList = root.getDynamicElementList();

			Map<String, List<DynamicAttribute>> attributeList = new HashMap<>();
			for (DynamicElement de: dynamicElementList) {
				if (attributeList.containsKey(de.getName())) {
					List<DynamicAttribute> list = new ArrayList<>(attributeList.get(de.getName()));
					list.add(new DynamicAttribute(de.getName(), de.getDynamicContent().get__cdata()));
					attributeList.put(de.getName(), list);
				}
				else {

					List<DynamicAttribute> list = Arrays.asList(new DynamicAttribute(de.getName(), de.getDynamicContent() != null ? de.getDynamicContent().get__cdata() : null));
					attributeList.put(de.getName(), list);
				}


			}

			return attributeList;
		}
		catch (JAXBException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public static String getElement(String content) {
		JAXBContext jaxbContext;
		try
		{
			jaxbContext = JAXBContext.newInstance(RootName.class);

			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

			RootName root = (RootName) jaxbUnmarshaller.unmarshal(new StringReader(content));



			return root.getName();
		}
		catch (JAXBException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public static String updateTags(String content, ChangeTagType... tags) {

		if (content == null) {
			return content;
		}

		// Remove all styles
		//String result = content.replaceAll("(?<=\\bstyle=\\\")[^\\\"]*", "");

		for (ChangeTagType ct : tags) {
			content = content.replaceAll(ct.getTag() + ".",
					ct.getTag().concat(" class=\"").concat(Constants.STYLE_FOR.get(ct.toString())).concat("\" "));
		}

		return content;
	}

	/**
	 *
	 * @param model
	 * @param languageId
	 * @param fieldName
	 * @return
	 * @author Daniel Dias
	 */
//	public static String getLocalizableField(JournalArticle model, String languageId, String fieldName) {
//
//		String value = "";
//		try {
//
//			Document document = SAXReaderUtil.read(model.getContentByLocale(languageId));
//
//			Node node = document.selectSingleNode("/root/dynamic-element[@name='"+fieldName+"']/dynamic-content");
//			value = node.getText();
//
//
//		} catch (DocumentException e) {
//			LOGGER.error(e.getMessage());
//		}
//
//		return value;
//
//	}

	/**
	 *
	 * @param model
	 * @param languageId
	 * @param fieldName
	 * @return
	 * @author Daniel Dias
	 */
	public static List<String> getLocalizableField(JournalArticle model, String languageId, String fieldName) {

		List<String> result= new ArrayList<String>();

		try {

			Document document = SAXReaderUtil.read(model.getContentByLocale(languageId));
			List<Node> nodes = document.selectNodes("/root/dynamic-element[@name='"+fieldName+"']/dynamic-content");

			for (Node node : nodes) {
				result.add(node.getText());
			}


		} catch (DocumentException e) {
			_log.error(e.getMessage());
		}

		return result;

	}

	/**
	 *
	 * @param model
	 * @param languageId
	 * @param fieldName
	 * @return
	 * @author Daniel Dias
	 */
	public static List<Map<String,String>> getLocalizableFieldMap(JournalArticle model, String languageId, String fieldName) {

		List<Map<String,String>> result= new ArrayList<Map<String,String>>();

		try {

			Document document = SAXReaderUtil.read(model.getContentByLocale(languageId));
			List<Node> nodes = document.selectNodes("/root/dynamic-element[@name='"+fieldName+"']");

			for (Node node : nodes) {
				Map<String, String> obj = new HashMap<String,String>();
				List<Node> fieldNodes = node.selectNodes("Number");
				obj.put(node.selectSingleNode("./dynamic-content").getText(), node.selectSingleNode("./dynamic-element/dynamic-content").getText());
				result.add(obj);
			}


		} catch (DocumentException e) {
			_log.error(e.getMessage());
		}

		return result;

	}

	public static List<Map<String,List<String>>> getLocalizableFieldMap(JournalArticle model, String languageId, String fieldNameParent, String fieldName) {

		List<Map<String,List<String>>> result= new ArrayList<>();

		try {

			Document document = SAXReaderUtil.read(model.getContentByLocale(languageId));
			List<Node> nodes = document.selectNodes("/root/dynamic-element[@name='"+fieldNameParent+"']");
			Map<String, List<String>> obj = new HashMap<>();
			for (Node node : nodes) {
				List<Node> fieldNodes = node.selectNodes("./dynamic-element[@name='" + fieldName+ "']/dynamic-content");
				String mapKey = node.selectSingleNode("./dynamic-content").getText();
				List<String> list = new ArrayList<>();
				for (Node n: fieldNodes) {
					list.add(n.getText());
				}
				obj.put(mapKey, list);
			}
			result.add(obj);


		} catch (DocumentException e) {
			_log.error(e.getMessage());
		}

		return result;

	}


}