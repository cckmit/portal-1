package ru.protei.portal.ui.common.client.lang;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_ContractDatesType;
import ru.protei.portal.core.model.dict.En_ContractState;

public class En_ContractDatesTypeLang {

    public String getName(En_ContractDatesType value) {
        if ( value == null ) {
            return lang.unknownField();
        }

        switch (value) {
            case PAYMENT: return lang.contractPayment();
            case SUPPLY: return lang.contractSupply();

            default: return lang.unknownField();
        }
    }

    @Inject
    private Lang lang;
}
