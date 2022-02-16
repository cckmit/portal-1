package ru.protei.portal.ui.common.client.lang;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_WorkTrigger;

public class En_WorkTriggerLang {
    public String getName(En_WorkTrigger value) {
        if (value == null){
            return lang.unknownField();
        }

        switch (value) {
            case NONE:
                return lang.workTriggerNone();
            case PSGO:
                return lang.workTriggerPSGO();
            case NEW_REQUIREMENTS:
                return lang.workTriggerNewRequirements();
            case PRE_COMMISSIONING_CONTRACT:
                return lang.workTriggerPreCommissioningContract();
            case NEW_PRE_COMMISSIONING_REQUIREMENTS:
                return lang.workTriggerNewPreCommissioningRequirements();
            case MARKETING:
                return lang.workTriggerMarketing();
            case OTHER:
                return lang.workTriggerOther();
            default:
                return lang.unknownField();
        }
    }

    @Inject
    Lang lang;
}
