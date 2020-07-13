package ru.protei.portal.ui.contract.client.widget.contractor.search;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.Contractor;
import ru.protei.portal.core.model.util.ContractorUtils;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.contractor.contractor.ContractorSelector;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;

import java.util.ArrayList;
import java.util.List;

import static ru.protei.portal.core.model.helper.CollectionUtils.isEmpty;
import static ru.protei.portal.core.model.util.CrmConstants.Masks.CONTRACTOR_INN;
import static ru.protei.portal.core.model.util.CrmConstants.Masks.CONTRACTOR_KPP;

public class ContractorSearchView extends Composite implements AbstractContractorSearchView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        contractorInn.setRegexp(CONTRACTOR_INN);
        contractorKPP.setRegexp(CONTRACTOR_KPP);
    }

    @Override
    public void setActivity(AbstractContractorSearchActivity activity){
        this.activity = activity;
    }

    @Override
    public void setOrganization(String value) {
        contractOrganization.setInnerText(value);
    }

    @Override
    public HasValue<String> contractorInn() {
        return contractorInn;
    }

    @Override
    public HasValue<String> contractorKpp() {
        return contractorKPP;
    }

    @Override
    public HasValue<Contractor> contractor() {
        return contractor;
    }

    @Override
    public void setSearchResult(List<Contractor> result) {
        contractor.fill(result);
        if (!isEmpty(result)) {
            contractor.setValue(result.get(0), true);
        }
    }

    @Override
    public void reset() {
        contractOrganization.setInnerText(null);
        contractorInn.setValue(null);
        contractorKPP.setValue(null);
        contractor.setValue(null);
        contractor.fill(new ArrayList<>());
        descriptionInn.setInnerText(null);
        descriptionKpp.setInnerText(null);
        descriptionName.setInnerText(null);
        descriptionFullName.setInnerText(null);
        descriptionCountry.setInnerText(null);
    }

    @Override
    public void setValid(boolean isValid) {
        // do nothing
    }

    @Override
    public boolean isValid() {
        return contractorInn.isValid() &
                contractorKPP.isValid() &&
                isValidInn(contractorInn);
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

    @UiHandler("contractorInn")
    public void onChangeClause(KeyUpEvent event) {
        if (contractorInn.isValid()) {
            contractorInn.setValid( ContractorUtils.checkInn(contractorInn.getValue()));
        }
    }

    @UiHandler("contractor")
    public void onContractorChanged(ValueChangeEvent<Contractor> event) {
        if (event.getValue() != null) {
            Contractor contractor = event.getValue();
            descriptionInn.setInnerText(contractor.getInn());
            descriptionKpp.setInnerText(contractor.getKpp());
            descriptionName.setInnerText(contractor.getName());
            descriptionFullName.setInnerText(contractor.getFullName());
            descriptionCountry.setInnerText(contractor.getCountry());
        }
    }

    @UiField
    HTMLPanel root;

    @UiField
    SpanElement contractOrganization;

    @UiField
    ValidableTextBox contractorInn;

    @UiField
    ValidableTextBox contractorKPP;

    @UiField
    Button search;

    @Inject
    @UiField(provided = true)
    ContractorSelector contractor;

    @UiField
    SpanElement descriptionInn;
    @UiField
    SpanElement descriptionKpp;
    @UiField
    SpanElement descriptionName;
    @UiField
    SpanElement descriptionFullName;
    @UiField
    SpanElement descriptionCountry;

    @UiField
    Lang lang;

    AbstractContractorSearchActivity activity;

    private static ContractorSearchViewUiBinder ourUiBinder = GWT.create(ContractorSearchViewUiBinder.class);
    interface ContractorSearchViewUiBinder extends UiBinder<HTMLPanel, ContractorSearchView> {}
}
