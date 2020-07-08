package ru.protei.portal.ui.contract.client.widget.contractor.search;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_Organization;
import ru.protei.portal.core.model.ent.Contractor;
import ru.protei.portal.core.model.util.ContractorUtils;
import ru.protei.portal.ui.common.client.widget.selector.contractor.contractor.ContractorSelector;
import ru.protei.portal.ui.common.client.widget.selector.contractor.organizationselector.OrganizationSelector;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;

import java.util.ArrayList;
import java.util.List;

import static ru.protei.portal.core.model.helper.CollectionUtils.isEmpty;
import static ru.protei.portal.core.model.util.CrmConstants.Masks.*;

public class ContractorSearchView extends Composite implements AbstractContractorSearchView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        contractorINN.setRegexp(CONTRACTOR_INN);
        contractorKPP.setRegexp(CONTRACTOR_KPP);
        organization.setValidation(true);
        organization.setValue(null);
    }

    @Override
    public void setActivity(AbstractContractorSearchActivity activity){
        this.activity = activity;
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
    public HasValue<Contractor> contractor() {
        return contractor;
    }

    @Override
    public HasValue<En_Organization> organization() {
        return organization;
    }

    @Override
    public void setSearchResult(List<Contractor> result) {
        contractor.fill(result);
        if (!isEmpty(result)) {
            contractor.setValue(result.get(0));
        }
    }

    @Override
    public void reset() {
        contractorINN.setValue(null);
        contractorKPP.setValue(null);
        contractor.setValue(null);
        organization.setValue(null);
        contractor.fill(new ArrayList<>());
    }

    @Override
    public void setValid(boolean isValid) {
        // do nothing
    }

    @Override
    public boolean isValid() {
        return contractorINN.isValid() &
                contractorKPP.isValid() &&
                organization.isValid() &&
                isValidInn(contractorINN);
    }

    private boolean isValidInn(ValidableTextBox inn) {
        boolean isValid = ContractorUtils.checkInn(inn.getValue());
        inn.setValid(isValid);
        return isValid;
    }

    @UiHandler( "search" )
    public void onSearchClicked ( ClickEvent event ) {
        activity.onSearchClicked();
    }

    @UiHandler( "create" )
    public void onCreateClicked ( ClickEvent event ) {
        activity.onCreateClicked();
    }

    @UiHandler("contractorINN")
    public void onChangeClause(KeyUpEvent event) {
        if (contractorINN.isValid()) {
            contractorINN.setValid( ContractorUtils.checkInn(contractorINN.getValue()));
        }
    }

    @Inject
    @UiField(provided = true)
    OrganizationSelector organization;

    @UiField
    ValidableTextBox contractorINN;

    @UiField
    ValidableTextBox contractorKPP;

    @UiField
    Button search;

    @UiField
    HTMLPanel root;

    @UiField
    Button create;

    @Inject
    @UiField(provided = true)
    ContractorSelector contractor;

    AbstractContractorSearchActivity activity;

    private static ContractorSearchViewUiBinder ourUiBinder = GWT.create(ContractorSearchViewUiBinder.class);
    interface ContractorSearchViewUiBinder extends UiBinder<HTMLPanel, ContractorSearchView> {}
}
