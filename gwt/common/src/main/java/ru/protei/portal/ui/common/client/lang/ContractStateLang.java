package ru.protei.portal.ui.common.client.lang;

import com.google.inject.Inject;

public class ContractStateLang {
    public String getName(String state) {
        if (state == null) {
            return lang.unknownField();
        }

        switch (state.toLowerCase()) {
            case "agreement":
                return lang.contractStateAgreement();
            case "have an original":
                return lang.contractStateHaveOriginal();
            case "copies send to customer":
                return lang.contractStateCopiesSendToCustomer();
            case "waiting for copies from customer":
                return lang.contractWaitingCopiesFromCustomer();
            case "waiting for original":
                return lang.contractStateWaitOriginal();
            case "canceled":
                return lang.contractCancelled();
            case "signed on site":
                return lang.contractSignedOnSite();
            case "eds signed":
                return lang.contractEDSSigned();
        }
        return lang.unknownField();
    }

    @Inject
    private Lang lang;
}
