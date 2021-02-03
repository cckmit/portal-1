package ru.protei.portal.ui.common.client.lang;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.ContractCostType;

public class ContractCostTypeLang {

    public String getName( ContractCostType value ) {
        if (value == null)
            return lang.unknownField();
        switch (value) {
            case EQUIPMENT:
                return lang.contractCostTypeEquipment();
            case SOFTWARE:
                return lang.contractCostTypeSoftware();
            case SERVICES:
                return lang.contractCostTypeServices();
        }
        return lang.unknownField();
    }

    @Inject
    Lang lang;
}
