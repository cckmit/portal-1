package ru.protei.portal.ui.common.client.lang;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_ContractKind;

public class En_ContractKindLang {

    public String getName(En_ContractKind value) {
        if (value == null)
            return "";

        switch (value) {
            case RECEIPT: return lang.contractKindReceipt();
            case EXPENDITURE: return lang.contractKindExpenditure();
        }
        return lang.unknownField();
    }

    @Inject
    private Lang lang;
}
