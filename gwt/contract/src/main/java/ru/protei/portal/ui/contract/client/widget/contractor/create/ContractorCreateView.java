package ru.protei.portal.ui.contract.client.widget.contractor.create;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.ContractorCountry;
import ru.protei.portal.core.model.util.ContractorUtils;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.widget.selector.contractor.country.ContractorCountrySelector;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;

import static ru.protei.portal.core.model.util.CrmConstants.Masks.*;

public class ContractorCreateView extends Composite implements AbstractContractorCreateView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        contractorInn.setRegexp(CONTRACTOR_INN);
        contractorKpp.setRegexp(CONTRACTOR_KPP);
        contractorName.setRegexp(CONTRACTOR_NAME);
        contractorFullName.setRegexp(CONTRACTOR_FULL_NAME);
        contractorCountry.setValidation(true);
        contractorCountry.setValue(null);
        ensureDebugIds();
    }

    @Override
    public HasValue<String> contractorInn() {
        return contractorInn;
    }

    @Override
    public HasValue<String> contractorKpp() {
        return contractorKpp;
    }

    @Override
    public HasValue<String> contractorName() {
        return contractorName;
    }

    @Override
    public HasValue<String> contractorFullName() {
        return contractorFullName;
    }

    @Override
    public HasValue<ContractorCountry> contractorCountry() {
        return contractorCountry;
    }

    @Override
    public void setOrganization(String organization) {
        contractOrganization.setInnerText(organization);
        contractorCountry.setOrganization(organization);
    }

    @Override
    public void reset() {
        contractOrganization.setInnerText(null);
        contractorInn.setValue(null);
        contractorKpp.setValue(null);
        contractorName.setValue(null);
        contractorFullName.setValue(null);
        contractorCountry.setValue(null);
    }

    @Override
    public void setValid(boolean isValid) {
        // do nothing
    }

    @Override
    public boolean isValid() {
        return contractorInn.isValid() &
                contractorKpp.isValid() &
                contractorName.isValid() &
                contractorFullName.isValid() &
                contractorCountry.isValid() &
                isValidInn(contractorInn);
    }

    private boolean isValidInn(ValidableTextBox inn) {
        boolean isValid = ContractorUtils.checkInn(inn.getValue());
        inn.setValid(isValid);
        return isValid;
    }

    @UiHandler("contractorInn")
    public void onChangeClause(KeyUpEvent event) {
        if (contractorInn.isValid()) {
            contractorInn.setValid( ContractorUtils.checkInn(contractorInn.getValue()));
        }
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }

        contractorInn.ensureDebugId(DebugIds.CONTRACT.CONTRACTOR.INN_INPUT);
        contractorKpp.ensureDebugId(DebugIds.CONTRACT.CONTRACTOR.KPP_INPUT);
        contractorName.ensureDebugId(DebugIds.CONTRACT.CONTRACTOR.NAME_INPUT);
        contractorFullName.ensureDebugId(DebugIds.CONTRACT.CONTRACTOR.FULL_NAME_INPUT);
        contractorCountry.setEnsureDebugId(DebugIds.CONTRACT.CONTRACTOR.COUNTRY_SELECTOR);
    }

    @UiField
    SpanElement contractOrganization;

    @UiField
    ValidableTextBox contractorInn;

    @UiField
    ValidableTextBox contractorKpp;

    @UiField
    ValidableTextBox contractorName;

    @UiField
    ValidableTextBox contractorFullName;

    @Inject
    @UiField(provided = true)
    ContractorCountrySelector contractorCountry;

    @UiField
    HTMLPanel root;

    private static ContractorSearchViewUiBinder ourUiBinder = GWT.create(ContractorSearchViewUiBinder.class);
    interface ContractorSearchViewUiBinder extends UiBinder<HTMLPanel, ContractorCreateView> {}
}
