package ru.protei.portal.ui.common.client.lang;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_ContractCalculationType;

public class En_ContractCalculationTypeLang {
    public String getName(En_ContractCalculationType value) {
        if (value == null) {
            return "";
        }

        switch (value) {
            case OS_OS:
                return lang.contractCalculationTypeOsOs();
            case PC_PC:
                return lang.contractCalculationTypePcPc();
            case OS_PC:
                return lang.contractCalculationTypeOsPc();
        }

        return lang.unknownField();
    }

    @Inject
    private Lang lang;
}
