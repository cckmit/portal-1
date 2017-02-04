package ru.protei.portal.ui.common.client.lang;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_EquipmentType;
import ru.protei.portal.ui.common.shared.model.OrganizationCode;


/**
 * Тип оборудования
 */
public class En_EquipmentTypeLang {

    public String getName( En_EquipmentType value ) {
        switch (value) {
            case ASSEMBLY_UNIT:
                return lang.equipmentTypeAssemblyUnit();
            case COMPLEX:
                return lang.equipmentTypeComplex();
            case DETAIL:
                return lang.equipmentTypeDetail();
            case PRODUCT:
                return lang.equipmentTypeProduct();

            default:
                return lang.unknownField();
        }
    }

    @Inject
    Lang lang;
}
