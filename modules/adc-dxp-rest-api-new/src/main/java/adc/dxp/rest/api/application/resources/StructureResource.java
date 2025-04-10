package adc.dxp.rest.api.application.resources;

import java.util.List;

import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalServiceUtil;
import com.liferay.portal.kernel.dao.orm.Criterion;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;

/**
 *
 * Utilities for the Structures
 *
 * @author ana.cavadas
 *
 */
public class StructureResource {

	/**
	 * logging instance
	 */
	private static Log _log = LogFactoryUtil.getLog(StructureResource.class);

	public static String getStructure(long groupId, String structureName) throws PortalException {

		Property structureIdProperty = PropertyFactoryUtil.forName("name");

		DynamicQuery dynamicQuery = DDMStructureLocalServiceUtil.dynamicQuery();
		Criterion criterion = structureIdProperty.like("%>" + structureName + "</Name>%");
		dynamicQuery.add(criterion);

		String structureKey  = null;

		List<DDMStructure> structures = DDMStructureLocalServiceUtil.dynamicQuery(dynamicQuery, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
		if(!structures.isEmpty()) {
			structureKey = structures.get(0).getStructureKey();
			_log.debug("structureKey" + structureKey);

		}

		if (structureKey == null) {
			//TODO: improve this
			throw new PortalException("Structure not found");
		}

		return structureKey;
	}

}