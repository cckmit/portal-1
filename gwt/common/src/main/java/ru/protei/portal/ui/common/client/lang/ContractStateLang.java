package ru.protei.portal.ui.common.client.lang;

import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.CaseState;

public class ContractStateLang {
    public String getStateName(CaseState state) {
        if (state == null || state.getState() == null)
            return lang.errUnknownResult();

        switch (state.getState().toLowerCase()) {
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
        return state.getState();
    }

    @Inject
    private Lang lang;
}
