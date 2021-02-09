package ru.protei.portal.ui.common.client.lang;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_ContractState;

public class En_ContractStateLang {
    public String getName(En_ContractState value) {
        if (value == null)
            return "";

        switch (value) {
            case AGREEMENT:
                return lang.contractStateAgreement();
            case COPIES_SEND_TO_CUSTOMER:
                return lang.contractStateCopiesSendToCustomer();
            case HAVE_ORIGINAL:
                return lang.contractStateHaveOriginal();
            case WAIT_ORIGINAL:
                return lang.contractStateWaitOriginal();
            case WAITING_COPIES_FROM_CUSTOMER:
                return lang.contractWaitingCopiesFromCustomer();
            case CANCELLED:
                return lang.contractCancelled();
            case EDS_SIGNED:
                return lang.contractEDSSigned();
            case SIGNED_ON_SITE:
                return lang.contractSignedOnSite();
        }
        return lang.unknownField();
    }

    @Inject
    private Lang lang;
}
