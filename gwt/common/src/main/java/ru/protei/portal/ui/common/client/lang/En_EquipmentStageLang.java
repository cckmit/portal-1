package ru.protei.portal.ui.common.client.lang;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_EquipmentStage;

/**
 * Названия статусов
 */
public class En_EquipmentStageLang {

    public String getName( En_EquipmentStage value){
        if(value == null)
            return lang.errUnknownResult();

        switch (value) {
            case DRAFT_PROJECT:
                return lang.equipmentStageDraftProject();
            case RKD_PRODUCT:
                return lang.equipmentStageRKD_Product();
            case RKD_PROTOTYPE:
                return lang.equipmentStageRKD_Prototype();
            case TECHNICAL_PROJECT:
                return lang.equipmentStageTechnicalProject();
            default:
                return lang.errUnknownResult();
        }
    }

    @Inject
    Lang lang;

}
