package ru.protei.portal.ui.common.client.lang;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CustomerType;

public class En_CustomerTypeLang {
    public String getName(En_CustomerType type) {
        if (type == null) {
            return null;
        }
        switch (type) {
            case MINISTRY_OF_DEFENCE:
                return lang.customerTypeMinistryOfDefence();
            case STATE_BUDGET:
                return lang.customerTypeStageBudget();
            case COMMERCIAL_RF:
                return lang.customerTypeCommercialRf();
            case COMMERCIAL_NEAR_ABROAD:
                return lang.customerTypeCommercialNearAbroad();
            case COMMERCIAL_FAR_ABROAD:
                return lang.customerTypeCommercialFarAbroad();
            case COMMERCIAL_PROTEI:
                return lang.customerTypeCommercialProtei();
            default:
                return null;
        }
    }

    @Inject
    private Lang lang;
}
