package ru.protei.portal.ui.contract.client.widget.contractor.search;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
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
import ru.protei.portal.core.model.ent.ContractorCountry;
import ru.protei.portal.core.model.util.ContractorUtils;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.contractor.contractor.ContractorSelector;
import ru.protei.portal.ui.common.client.widget.selector.contractor.country.ContractorCountrySelector;
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
        contractorInn.setNotNull(false);
        contractorInn.setRegexp(CONTRACTOR_INN);
        contractorInn.setValidationFunction(ContractorUtils::checkInn);
        contractorKPP.setNotNull(false);
        contractorKPP.setRegexp(CONTRACTOR_KPP);
        contractorName.setNotNull(false);
        contractorFullName.setNotNull(false);
        contractorCountry.setValidation(false);
        ensureDebugIds();
    }

    @Override
    public void setActivity(AbstractContractorSearchActivity activity){
        this.activity = activity;
    }

    @Override
    public void setOrganization(String value) {
        contractOrganization.setInnerText(value);
        contractorCountry.setOrganization(value);
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
        contractorName.setValue(null);
        contractorFullName.setValue(null);
        contractorCountry.setValue(null);
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
        contractorInn.setValid(isValid);
        contractorKPP.setValid(isValid);
        contractorFullName.setValid(isValid);
    }

    @Override
    public boolean isValid() {
        return contractorInn.isValid() &
               contractorKPP.isValid() &
               contractorName.isValid() &
               contractorFullName.isValid();
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

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }

        contractorInn.ensureDebugId(DebugIds.CONTRACT.CONTRACTOR.INN_INPUT);
        contractorKPP.ensureDebugId(DebugIds.CONTRACT.CONTRACTOR.KPP_INPUT);
        contractorName.ensureDebugId(DebugIds.CONTRACT.CONTRACTOR.NAME_INPUT);
        contractorFullName.ensureDebugId(DebugIds.CONTRACT.CONTRACTOR.FULL_NAME_INPUT);
        contractorCountry.ensureDebugId(DebugIds.CONTRACT.CONTRACTOR.COUNTRY_SELECTOR);
        search.ensureDebugId(DebugIds.CONTRACT.CONTRACTOR.SEARCH_BUTTON);
        contractor.ensureDebugId(DebugIds.CONTRACT.CONTRACTOR.CONTRACTOR_SEARCH_SELECTOR);
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
    ValidableTextBox contractorName;
    @UiField
    ValidableTextBox contractorFullName;
    @Inject
    @UiField(provided = true)
    ContractorCountrySelector contractorCountry;

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
