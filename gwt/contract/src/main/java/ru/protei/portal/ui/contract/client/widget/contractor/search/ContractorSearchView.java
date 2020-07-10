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
import ru.protei.portal.core.model.ent.ContractorAPI;
import ru.protei.portal.core.model.struct.ContractorPair;
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
        contractorINN.setRegexp(CONTRACTOR_INN);
        contractorKPP.setRegexp(CONTRACTOR_KPP);
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
    public HasValue<ContractorPair> contractor() {
        return contractor;
    }

    @Override
    public void setSearchResult(List<ContractorPair> result) {
        contractor.fill(result);
        if (!isEmpty(result)) {
            contractor.setValue(result.get(0), true);
        }
    }

    @Override
    public void reset() {
        contractorINN.setValue(null);
        contractorKPP.setValue(null);
        contractor.setValue(null);
        contractor.fill(new ArrayList<>());
        descriptionInn.setInnerText(null);
        descriptionKpp.setInnerText(null);
        descriptionName.setInnerText(null);
        descriptionFullname.setInnerText(null);
        descriptionCountry.setInnerText(null);
    }

    @Override
    public void setValid(boolean isValid) {
        // do nothing
    }

    @Override
    public boolean isValid() {
        return contractorINN.isValid() &
                contractorKPP.isValid() &&
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

    @UiHandler("contractorINN")
    public void onChangeClause(KeyUpEvent event) {
        if (contractorINN.isValid()) {
            contractorINN.setValid( ContractorUtils.checkInn(contractorINN.getValue()));
        }
    }

    @UiHandler("contractor")
    public void onContractorChanged(ValueChangeEvent<ContractorPair> event) {
        if (event.getValue() != null) {
            ContractorAPI contractorApi = event.getValue().getContractorAPI();
            descriptionInn.setInnerText(contractorApi.getInn());
            descriptionKpp.setInnerText(contractorApi.getKpp());
            descriptionName.setInnerText(contractorApi.getName());
            descriptionFullname.setInnerText(contractorApi.getFullname());
            descriptionCountry.setInnerText(contractorApi.getCountry());
        }
    }

    @UiField
    HTMLPanel root;

    @UiField
    ValidableTextBox contractorINN;

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
    SpanElement descriptionFullname;
    @UiField
    SpanElement descriptionCountry;

    @UiField
    Lang lang;

    AbstractContractorSearchActivity activity;

    private static ContractorSearchViewUiBinder ourUiBinder = GWT.create(ContractorSearchViewUiBinder.class);
    interface ContractorSearchViewUiBinder extends UiBinder<HTMLPanel, ContractorSearchView> {}
}
