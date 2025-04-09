package adc.dxp.rest.api.application.utils;

import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.LocaleUtil;

/**
 *
 * @author ricardo.gomes
 *
 */
public class StructureUtil {


    // TODO: improve this
    /**
     *
     * @param name
     * @return
     * @throws PortalException
     */
    public static DDMStructure getStructureByNameEn(String name) throws PortalException {

        java.util.List<DDMStructure> structures = DDMStructureLocalServiceUtil.getStructures();

        for (DDMStructure s : structures) {
            if (s.getName(LocaleUtil.ENGLISH).equals(name)) {
                return s;
            }
        }

        throw new PortalException("Structure not found");

    }

}