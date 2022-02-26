package ru.protei.portal.ui.common.client.lang;

import com.google.inject.Inject;

public class En_ContractStateLang {
    public String getName(String state) {
        if (state == null)
            return "";

        switch (state.toLowerCase()) {
            case "agreement":
                return lang.contractStateAgreement();
            case "copies send to customer":
                return lang.contractStateCopiesSendToCustomer();
            case "have an original":
                return lang.contractStateHaveOriginal();
            case "waiting for original":
                return lang.contractStateWaitOriginal();
            case "waiting for copies from customer":
                return lang.contractWaitingCopiesFromCustomer();
            case "cancelled":
                return lang.contractCancelled();
            case "eds signed":
                return lang.contractEDSSigned();
            case "signed on site":
                return lang.contractSignedOnSite();
        }
        return lang.unknownField();
    }

    @Inject
    private Lang lang;
}
