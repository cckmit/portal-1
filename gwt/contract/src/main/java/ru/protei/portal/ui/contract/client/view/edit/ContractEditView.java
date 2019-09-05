package ru.protei.portal.ui.contract.client.view.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.client.view.input.single.SinglePicker;
import ru.protei.portal.core.model.dict.En_ContractState;
import ru.protei.portal.core.model.dict.En_ContractType;
import ru.protei.portal.core.model.ent.ContractDate;
import ru.protei.portal.core.model.struct.CostWithCurrency;
import ru.protei.portal.core.model.struct.ProductDirectionInfo;
import ru.protei.portal.core.model.struct.ProjectInfo;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.autoresizetextarea.AutoResizeTextArea;
import ru.protei.portal.ui.common.client.widget.homecompany.HomeCompanyButtonSelector;
import ru.protei.portal.ui.common.client.widget.money.CostWithCurrencyView;
import ru.protei.portal.ui.common.client.widget.selector.company.CompanySelector;
import ru.protei.portal.ui.common.client.widget.selector.contract.ContractButtonSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeButtonSelector;
import ru.protei.portal.ui.common.client.widget.selector.productdirection.ProductDirectionButtonSelector;
import ru.protei.portal.ui.common.client.widget.selector.project.ProjectButtonSelector;
import ru.protei.portal.ui.common.client.widget.selector.project.ProjectEOButtonSelector;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;
import ru.protei.portal.ui.contract.client.activity.edit.AbstractContractEditActivity;
import ru.protei.portal.ui.contract.client.activity.edit.AbstractContractEditView;
import ru.protei.portal.ui.contract.client.widget.contractdates.list.ContractDatesList;
import ru.protei.portal.ui.contract.client.widget.selector.ContractStateSelector;
import ru.protei.portal.ui.contract.client.widget.selector.ContractTypeSelector;

import java.util.Date;
import java.util.List;

public class ContractEditView extends Composite implements AbstractContractEditView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }
    
    @Override
    public void setActivity(AbstractContractEditActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasEnabled saveEnabled() {
        return saveButton;
    }

    @Override
    public HasValue<CostWithCurrency> cost() {
        return costWithCurrency;
    }

    @Override
    public HasValue<String> number() {
        return number;
    }

    @Override
    public HasValue<En_ContractType> type() {
        return type;
    }

    @Override
    public HasValue<En_ContractState> state() {
        return state;
    }

    @Override
    public HasValue<String> description() {
        return description;
    }

    @Override
    public HasValue<PersonShortView> curator() {
        return curator;
    }

    @Override
    public HasValue<Date> dateSigning() {
        return dateSigning;
    }

    @Override
    public HasValue<Date> dateValid() {
        return dateValid;
    }

    @Override
    public HasValue<List<ContractDate>> contractDates() {
        return dateList;
    }

    @Override
    public HasValue<EntityOption> organization() {
        return organization;
    }

    @Override
    public HasValue<EntityOption> contractParent() {
        return contractParent;
    }

    @Override
    public HasEnabled costEnabled() {
        return costWithCurrency;
    }

    @Override
    public HasValue<EntityOption> project() {
        return project;
    }

    @UiHandler("saveButton")
    public void onSaveClicked(ClickEvent event) {
        if (activity != null) {
            activity.onSaveClicked();
        }
    }

    @UiHandler("cancelButton")
    public void onCancelClicked(ClickEvent event) {
        if (activity != null) {
            activity.onCancelClicked();
        }
    }

    @UiHandler("type")
    public void onTypeChanged(ValueChangeEvent<En_ContractType> event) {
        if ( activity != null ) {
            activity.onTypeChanged();
        }
    }
    @UiField
    Button saveButton;

    @UiField
    Lang lang;
    @Inject
    @UiField(provided = true)
    HomeCompanyButtonSelector organization;
    @Inject
    @UiField(provided = true)
    EmployeeButtonSelector curator;
    @Inject
    @UiField(provided = true)
    ContractStateSelector state;
    @Inject
    @UiField(provided = true)
    ContractTypeSelector type;
    @Inject
    @UiField(provided = true)
    CostWithCurrencyView costWithCurrency;
    @UiField
    ValidableTextBox number;
    @UiField
    AutoResizeTextArea description;
    @Inject
    @UiField(provided = true)
    SinglePicker dateValid;
    @Inject
    @UiField(provided = true)
    SinglePicker dateSigning;
    @Inject
    @UiField(provided = true)
    ContractDatesList dateList;
    @Inject
    @UiField(provided = true)
    ContractButtonSelector contractParent;
    @Inject
    @UiField(provided = true)
    ProjectEOButtonSelector project;

    private AbstractContractEditActivity activity;

    private static ContractViewUiBinder ourUiBinder = GWT.create(ContractViewUiBinder.class);
    interface ContractViewUiBinder extends UiBinder<HTMLPanel, ContractEditView> {}
}
