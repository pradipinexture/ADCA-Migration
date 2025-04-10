package adc.dxp.rest.api.application.resources;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalServiceUtil;
import com.liferay.portal.kernel.dao.orm.Criterion;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

/**
 * Utilities for working with DDM Structures
 *
 * @author ana.cavadas
 */
@Component(service = StructureResource.class)
public class StructureResource {

	/**
	 * Logging instance
	 */
	private static final Log _log = LogFactoryUtil.getLog(StructureResource.class);

	@Reference
	private DDMStructureLocalService _ddmStructureLocalService;

	/**
	 * Gets the structure key for a structure by name
	 *
	 * @param groupId the group ID
	 * @param structureName the structure name
	 * @return the structure key
	 * @throws PortalException if structure is not found
	 */
	public String getStructure(long groupId, String structureName) throws PortalException {
		Property structureIdProperty = PropertyFactoryUtil.forName("name");

		DynamicQuery dynamicQuery = _ddmStructureLocalService.dynamicQuery();
		Criterion criterion = structureIdProperty.like("%>" + structureName + "</Name>%");
		dynamicQuery.add(criterion);

		List<DDMStructure> structures = _ddmStructureLocalService.dynamicQuery(
				dynamicQuery, QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		if (!structures.isEmpty()) {
			String structureKey = structures.get(0).getStructureKey();
			_log.debug("Found structureKey: " + structureKey);
			return structureKey;
		}

		_log.error("Structure not found: " + structureName);
		throw new PortalException("Structure not found: " + structureName);
	}


}