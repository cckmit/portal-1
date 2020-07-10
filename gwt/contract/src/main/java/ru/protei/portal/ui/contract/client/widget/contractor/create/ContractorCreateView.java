package ru.protei.portal.ui.contract.client.widget.contractor.create;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.ContractorCountryAPI;
import ru.protei.portal.core.model.util.ContractorUtils;
import ru.protei.portal.ui.common.client.widget.selector.contractor.country.ContractorCountrySelector;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;

import static ru.protei.portal.core.model.util.CrmConstants.Masks.*;

public class ContractorCreateView extends Composite implements AbstractContractorCreateView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        contractorINN.setRegexp(CONTRACTOR_INN);
        contractorKPP.setRegexp(CONTRACTOR_KPP);
        contractorName.setRegexp(CONTRACTOR_NAME);
        contractorFullname.setRegexp(CONTRACTOR_FULLNAME);
        contractorCountry.setValidation(true);
        contractorCountry.setValue(null);
    }

    @Override
    public HasValue<String> contractorINN() {
        return contractorINN;
    }

    @Override
    public HasValue<String> contractorKPP() {
        return contractorKPP;
    }

    @Override
    public HasValue<String> contractorName() {
        return contractorName;
    }

    @Override
    public HasValue<String> contractorFullname() {
        return contractorFullname;
    }

    @Override
    public HasValue<ContractorCountryAPI> contractorCountry() {
        return contractorCountry;
    }

    @Override
    public void reset() {
        contractorINN.setValue(null);
        contractorKPP.setValue(null);
        contractorName.setValue(null);
        contractorFullname.setValue(null);
        contractorCountry.setValue(null);
    }

    @Override
    public void setValid(boolean isValid) {
        // do nothing
    }

    @Override
    public boolean isValid() {
        return contractorINN.isValid() &
                contractorKPP.isValid() &
                contractorName.isValid() &
                contractorFullname.isValid() &
                contractorCountry.isValid() &
                isValidInn(contractorINN);
    }

    private boolean isValidInn(ValidableTextBox inn) {
        boolean isValid = ContractorUtils.checkInn(inn.getValue());
        inn.setValid(isValid);
        return isValid;
    }

    @UiHandler("contractorINN")
    public void onChangeClause(KeyUpEvent event) {
        if (contractorINN.isValid()) {
            contractorINN.setValid( ContractorUtils.checkInn(contractorINN.getValue()));
        }
    }

    @UiField
    ValidableTextBox contractorINN;

    @UiField
    ValidableTextBox contractorKPP;

    @UiField
    ValidableTextBox contractorName;

    @UiField
    ValidableTextBox contractorFullname;

    @Inject
    @UiField(provided = true)
    ContractorCountrySelector contractorCountry;

    @UiField
    HTMLPanel root;

    private static ContractorSearchViewUiBinder ourUiBinder = GWT.create(ContractorSearchViewUiBinder.class);
    interface ContractorSearchViewUiBinder extends UiBinder<HTMLPanel, ContractorCreateView> {}
}
