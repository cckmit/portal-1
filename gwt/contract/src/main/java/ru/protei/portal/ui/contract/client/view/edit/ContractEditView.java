package ru.protei.portal.ui.contract.client.view.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.client.view.input.single.SinglePicker;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.dto.ProductDirectionInfo;
import ru.protei.portal.core.model.dto.ProjectInfo;
import ru.protei.portal.core.model.ent.ContractDate;
import ru.protei.portal.core.model.ent.ContractSpecification;
import ru.protei.portal.core.model.ent.Contractor;
import ru.protei.portal.core.model.query.EmployeeQuery;
import ru.protei.portal.core.model.struct.MoneyWithCurrencyWithVat;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.En_ContractKindLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.autoresizetextarea.ValiableAutoResizeTextArea;
import ru.protei.portal.ui.common.client.widget.homecompany.HomeCompanyButtonSelector;
import ru.protei.portal.ui.common.client.widget.money.MoneyCurrencyVatWidget;
import ru.protei.portal.ui.common.client.widget.project.ProjectWidget;
import ru.protei.portal.ui.common.client.widget.selector.contract.ContractButtonSelector;
import ru.protei.portal.ui.common.client.widget.selector.contract.state.ContractStateSelector;
import ru.protei.portal.ui.common.client.widget.selector.contract.type.ContractTypeSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeButtonSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeCustomButtonSelector;
import ru.protei.portal.ui.common.client.widget.selector.productdirection.ProductDirectionButtonSelector;
import ru.protei.portal.ui.common.client.widget.tab.TabWidget;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableLongBox;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;
import ru.protei.portal.ui.contract.client.activity.edit.AbstractContractEditActivity;
import ru.protei.portal.ui.contract.client.activity.edit.AbstractContractEditView;
import ru.protei.portal.ui.contract.client.widget.contractdates.list.ContractDatesList;
import ru.protei.portal.ui.contract.client.widget.contractor.ContractorWidget;
import ru.protei.portal.ui.contract.client.widget.contractspecification.list.ContractSpecificationList;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import static ru.protei.portal.core.model.helper.CollectionUtils.listOf;
import static ru.protei.portal.core.model.struct.Vat.*;

public class ContractEditView extends Composite implements AbstractContractEditView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        dateValidDays.getElement().setAttribute("placeholder", lang.days());
        dateValidDays.setValidationFunction(value -> value == null || value >= 0);
        costWithCurrency.setVatOptions(listOf(Vat20, Vat0, NoVat));
        initCuratorSelector();
        ensureDebugIds();
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
    public HasValue<MoneyWithCurrencyWithVat> cost() {
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
    public void setKind(En_ContractKind value) {
        kind.setValue(contractKindLang.getName(value));
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
    public HasValue<Date> dateValidDate() {
        return dateValidDate;
    }

    @Override
    public HasValue<Long> dateValidDays() {
        return dateValidDays;
    }

    @Override
    public HasValue<List<ContractDate>> contractDates() {
        return dateList;
    }

    @Override
    public HasValue<List<ContractSpecification>> contractSpecifications() {
        return specificationList;
    }

    @Override
    public HasValidable validateContractSpecifications() {
        return specificationList;
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
    public HasValue<ProjectInfo> project() {
        return projectWidget;
    }

    @Override
    public HasValue<PersonShortView> manager() {
        return manager;
    }

    @Override
    public HasValue<Contractor> contractor() {
        return contractorWidget;
    }

    @Override
    public HasEnabled contractorEnabled() {
        return contractorWidget;
    }

    @Override
    public HasValue<ProductDirectionInfo> direction() {
        return direction;
    }

    @Override
    public void setOrganization(String organization) {
        contractorWidget.setOrganization(organization);
    }

    @Override
    public HasValue<Boolean> secondContractCheckbox() {
        return secondContractCheckbox;
    }

    @Override
    public HasVisibility secondContractCheckboxVisibility() {
        return secondContractCheckbox;
    }

    @Override
    public HasVisibility secondContractVisibility() {
        return secondContract;
    }

    @Override
    public HasValue<String> secondContractNumber() {
        return secondContractNumber;
    }

    @Override
    public HasValue<EntityOption> secondContractOrganization() {
        return secondContractOrganization;
    }

    @Override
    public HasValue<Contractor> secondContractContractor() {
        return secondContractContractor;
    }

    @Override
    public HasEnabled secondContractContractorEnabled() {
        return secondContractContractor;
    }

    @Override
    public void setSecondContractOrganization(String organization) {
        secondContractContractor.setOrganization(organization);
    }

    @Override
    public HasWidgets expenditureContractsContainer() {
        return expenditureContractsContainer;
    }

    @Override
    public HasVisibility expenditureContractsVisibility() {
        return tabs.tabVisibility(lang.contractListOfExpenditureHeader());
    }

    @Override
    public void setNotAvailableOrganizations(List<Long> organizationsToHide) {
        organization.setIdsToHide(organizationsToHide);
    }

    @Override
    public void setSecondContractNotAvailableOrganizations(List<Long> organizationsToHide) {
        secondContractOrganization.setIdsToHide(organizationsToHide);
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

    @UiHandler("projectWidget")
    public void onProjectWidgetChanged(ValueChangeEvent<ProjectInfo> event) {
        if (activity != null) {
            activity.onProjectChanged();
        }
    }

    @UiHandler("organization")
    public void onOrganizationChanged(ValueChangeEvent<EntityOption> event) {
        if (activity != null) {
            activity.onOrganizationChanged();
        }
    }

    @UiHandler("contractParent")
    public void onContractParentChanged(ValueChangeEvent<EntityOption> event) {
        if (activity != null) {
            activity.onContractParentChanged();
        }
    }

    @UiHandler("secondContractCheckbox")
    public void onValueChange(ValueChangeEvent<Boolean> event) {
        if (activity != null) {
            activity.onCreateSecondContractToggle(event.getValue());
        }
    }

    @UiHandler("secondContractOrganization")
    public void onSecondContractOrganizationChanged(ValueChangeEvent<EntityOption> event) {
        if (activity != null) {
            activity.onSecondContractOrganizationChanged();
        }
    }

    @UiHandler("dateSigning")
    public void onDateSigningChanged(ValueChangeEvent<Date> event) {
        if (activity != null) {
            activity.onDateSigningChanged(dateSigning.getValue());
        }
    }

    @UiHandler("dateValidDate")
    public void onDateValidDateChanged(ValueChangeEvent<Date> event) {
        if (activity != null) {
            activity.onDateValidChanged(dateValidDate.getValue());
        }
    }

    @UiHandler("dateValidDays")
    public void onDateValidDaysChanged(KeyUpEvent event) {
        if (activity != null) {
            activity.onDateValidChanged(dateValidDays.getValue());
        }
    }

    private void initCuratorSelector() {
        EmployeeQuery query = new EmployeeQuery(null, false, true, En_SortField.person_full_name, En_SortDir.ASC);
        query.setDepartmentIds(new HashSet<>(Collections.singletonList(CrmConstants.Department.CONTRACT)));
        curator.setEmployeeQuery(query);
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }

        commonHeader.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.CONTRACT.LABEL.COMMON_HEADER);

        numberLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.CONTRACT.LABEL.NUMBER);
        number.ensureDebugId(DebugIds.CONTRACT.NUMBER_INPUT);

        typeLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.CONTRACT.LABEL.TYPE);
        type.ensureDebugId(DebugIds.CONTRACT.TYPE_SELECTOR);

        stateLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.CONTRACT.LABEL.STATE);
        state.setEnsureDebugId(DebugIds.CONTRACT.STATE_SELECTOR);

        contractParentLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.CONTRACT.LABEL.PARENT);
        contractParent.setEnsureDebugId(DebugIds.CONTRACT.PARENT_SELECTOR);

        descriptionLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.CONTRACT.LABEL.DESCRIPTION);
        description.ensureDebugId(DebugIds.CONTRACT.DESCRIPTION_INPUT);

        dateSigningLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.CONTRACT.LABEL.DATE_SIGNING);
        dateSigning.setEnsureDebugId(DebugIds.CONTRACT.DATE_SIGNING_CONTAINER);

        dateValidLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.CONTRACT.LABEL.DATE_VALID);
        dateValid.ensureDebugId(DebugIds.CONTRACT.DATE_VALID_CONTAINER);

        costWithCurrencyLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.CONTRACT.LABEL.COST_WITH_CURRENCY);
        costWithCurrency.setEnsureDebugId(DebugIds.CONTRACT.COST_WITH_CURRENCY_CONTAINER);

        projectLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.CONTRACT.LABEL.PROJECT);
        projectWidget.setEnsureDebugId(DebugIds.CONTRACT.PROJECT_SELECTOR);

        directionLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.CONTRACT.LABEL.DIRECTION);
        direction.setEnsureDebugId(DebugIds.CONTRACT.DIRECTION_SELECTOR);

        organizationLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.CONTRACT.LABEL.ORGANIZATION);
        organization.setEnsureDebugId(DebugIds.CONTRACT.ORGANIZATION_SELECTOR);

        curatorLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.CONTRACT.LABEL.CURATOR);
        curator.setEnsureDebugId(DebugIds.CONTRACT.CURATOR_SELECTOR);

        managerLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.CONTRACT.LABEL.MANAGER);
        manager.setEnsureDebugId(DebugIds.CONTRACT.MANAGER_SELECTOR);

        contractorLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.CONTRACT.LABEL.CONTRACTOR);
        contractorWidget.setEnsureDebugId(DebugIds.CONTRACT.CONTRACTOR_SELECTOR);

        dateList.setEnsureDebugId(DebugIds.CONTRACT.ADD_DATES_BUTTON);
        specificationList.setEnsureDebugId(DebugIds.CONTRACT.ADD_SPECIFICATIONS_BUTTON);

        saveButton.ensureDebugId(DebugIds.CONTRACT.SAVE_BUTTON);
        cancelButton.ensureDebugId(DebugIds.CONTRACT.CANCEL_BUTTON);
    }


    @Inject
    En_ContractKindLang contractKindLang;

    @UiField
    @Inject
    Lang lang;

    @Inject
    @UiField(provided = true)
    HomeCompanyButtonSelector organization;
    @Inject
    @UiField(provided = true)
    EmployeeCustomButtonSelector curator;
    @Inject
    @UiField(provided = true)
    ContractStateSelector state;
    @Inject
    @UiField(provided = true)
    ContractTypeSelector type;
    @UiField
    TextBox kind;
    @Inject
    @UiField(provided = true)
    MoneyCurrencyVatWidget costWithCurrency;
    @UiField
    ValidableTextBox number;
    @UiField
    ValiableAutoResizeTextArea description;
    @Inject
    @UiField(provided = true)
    SinglePicker dateSigning;
    @UiField
    HTMLPanel dateValid;
    @UiField
    ValidableLongBox dateValidDays;
    @Inject
    @UiField(provided = true)
    SinglePicker dateValidDate;

    @UiField
    TabWidget tabs;
    @Inject
    @UiField(provided = true)
    ContractDatesList dateList;
    @Inject
    @UiField(provided = true)
    ContractSpecificationList specificationList;
    @UiField
    HTMLPanel expenditureContractsContainer;

    @Inject
    @UiField(provided = true)
    ContractButtonSelector contractParent;
    @Inject
    @UiField(provided = true)
    ProjectWidget projectWidget;
    @Inject
    @UiField(provided = true)
    ProductDirectionButtonSelector direction;
    @Inject
    @UiField(provided = true)
    EmployeeButtonSelector manager;
    @Inject
    @UiField(provided = true)
    ContractorWidget contractorWidget;
    @UiField
    LabelElement numberLabel;
    @UiField
    LabelElement typeLabel;
    @UiField
    LabelElement kindLabel;
    @UiField
    LabelElement stateLabel;
    @UiField
    LabelElement contractParentLabel;
    @UiField
    LabelElement descriptionLabel;
    @UiField
    LabelElement dateSigningLabel;
    @UiField
    LabelElement dateValidLabel;
    @UiField
    LabelElement costWithCurrencyLabel;
    @UiField
    LabelElement projectLabel;
    @UiField
    LabelElement directionLabel;
    @UiField
    LabelElement organizationLabel;
    @UiField
    LabelElement curatorLabel;
    @UiField
    LabelElement managerLabel;
    @UiField
    LabelElement contractorLabel;
    @UiField
    DivElement commonHeader;
    @UiField
    DivElement workGroupHeader;

    @UiField
    HTMLPanel secondContract;
    @UiField
    ValidableTextBox secondContractNumber;
    @Inject
    @UiField(provided = true)
    HomeCompanyButtonSelector secondContractOrganization;
    @Inject
    @UiField(provided = true)
    ContractorWidget secondContractContractor;

    @UiField
    CheckBox secondContractCheckbox;
    @UiField
    Button saveButton;
    @UiField
    Button cancelButton;

    private AbstractContractEditActivity activity;

    private static ContractViewUiBinder ourUiBinder = GWT.create(ContractViewUiBinder.class);
    interface ContractViewUiBinder extends UiBinder<HTMLPanel, ContractEditView> {}
}
