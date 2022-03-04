package ru.protei.portal.ui.contract.client.view.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.dom.client.SpanElement;
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
import ru.protei.portal.core.model.dto.ProjectInfo;
import ru.protei.portal.core.model.ent.ContractSpecification;
import ru.protei.portal.core.model.ent.Contractor;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.query.EmployeeQuery;
import ru.protei.portal.core.model.struct.ContractInfo;
import ru.protei.portal.core.model.struct.MoneyWithCurrencyWithVat;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.En_ContractKindLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.autoresizetextarea.ValiableAutoResizeTextArea;
import ru.protei.portal.ui.common.client.widget.homecompany.HomeCompanyFormSelector;
import ru.protei.portal.ui.common.client.widget.money.MoneyCurrencyVatWidget;
import ru.protei.portal.ui.common.client.widget.project.ProjectWidget;
import ru.protei.portal.ui.common.client.widget.selector.contract.ContractFormSelector;
import ru.protei.portal.ui.common.client.widget.selector.contract.state.ContractStateSelector;
import ru.protei.portal.ui.common.client.widget.selector.contract.type.ContractTypeSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeCustomFormSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeFormSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeMultiSelector;
import ru.protei.portal.ui.common.client.widget.tab.TabWidget;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableLongBox;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;
import ru.protei.portal.ui.contract.client.activity.edit.AbstractContractEditActivity;
import ru.protei.portal.ui.contract.client.activity.edit.AbstractContractEditView;
import ru.protei.portal.ui.contract.client.widget.contractor.ContractorWidget;
import ru.protei.portal.ui.contract.client.widget.contractspecification.list.ContractSpecificationList;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.CollectionUtils.listOf;
import static ru.protei.portal.core.model.struct.Vat.*;
import static ru.protei.portal.ui.common.client.util.ClientTransliterationUtils.transliterateNotifiers;

public class ContractEditView extends Composite implements AbstractContractEditView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        dateValidDays.getElement().setAttribute("placeholder", lang.days());
        dateValidDays.setValidationFunction(value -> value == null || value >= 0);
        costWithCurrency.setVatOptions(listOf(Vat20, Vat0, NoVat));
        notifiers.setItemRenderer( PersonShortView::getName );
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
    public HasValue<Date> dateEndWarranty() {
        return dateEndWarranty;
    }

    @Override
    public HasValue<Date> dateExecution() {
        return dateExecution;
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
    public HasWidgets getContractDateTableContainer() {
        return datesTableContainer;
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
    public HasValue<ContractInfo> contractParent() {
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
    public void setProjectManager(String value) {
        projectManager.setInnerText(value);
    }

    @Override
    public HasValue<PersonShortView> contractSignManager() {
        return contractSignManager;
    }

    @Override
    public HasValue<Contractor> contractor() {
        return contractorWidget;
    }

    @Override
    public HasValue<String> deliveryNumber() {
        return deliveryNumber;
    }

    @Override
    public HasEnabled contractorEnabled() {
        return contractorWidget;
    }

    @Override
    public void setDirections(String value) {
        directions.setInnerText(value);
    }

    @Override
    public void setOrganization(String organization) {
        contractorWidget.setOrganization(organization);
    }

    @Override
    public HasVisibility tagsVisibility() {
        return tags;
    }

    @Override
    public HasVisibility tagsButtonVisibility() {
        return addTagButton;
    }

    @Override
    public HasWidgets tagsContainer() {
        return tagsContainer;
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
    public HasValue<String> fileLocation() {
        return fileLocation;
    }

    @Override
    public void setNotifiers(Set<Person> notifiers) {
        this.notifiers.setValue(transliterateNotifiers(notifiers));
    }

    @Override
    public Set<Person> getNotifiers() {
        return notifiers.getValue().stream().map(Person::fromPersonShortView).collect(Collectors.toSet());
    }

    @UiHandler("saveButton")
    public void onSaveClicked(ClickEvent event) {
        if (activity != null) {
            activity.onSaveClicked();
        }
    }

    @UiHandler("addTagButton")
    public void onTagAddClicked(ClickEvent event) {
        if (activity != null) {
            activity.onAddTagsClicked(addTagButton);
        }
    }

    @UiHandler({"cancelButton", "backButton"})
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
    public void onContractParentChanged(ValueChangeEvent<ContractInfo> event) {
        if (activity != null) {
            activity.onContractParentChanged();
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

    @UiHandler("costWithCurrency")
    public void onChangeCostWithCurrency(ValueChangeEvent<MoneyWithCurrencyWithVat> event) {
        if (activity != null) {
            activity.onCostChanged();
        }
    }

    @UiHandler( "addDate" )
    public void onAddDateClicked( ClickEvent event ) {
        if (activity != null) {
            activity.onAddDateClicked();
        }
    }

    @Override
    public void initCuratorsSelector(List<String> contractCuratorsDepartmentsIds) {
        EmployeeQuery query = new EmployeeQuery(null, false, true, En_SortField.person_full_name, En_SortDir.ASC);
        query.setDepartmentIds(contractCuratorsDepartmentsIds);
        curator.setEmployeeQuery(query);
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }

        numberLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.CONTRACT.LABEL.NUMBER);
        number.ensureDebugId(DebugIds.CONTRACT.NUMBER_INPUT);
        type.ensureDebugId(DebugIds.CONTRACT.TYPE_SELECTOR);
        state.setEnsureDebugId(DebugIds.CONTRACT.STATE_SELECTOR);
        contractParent.setEnsureDebugId(DebugIds.CONTRACT.PARENT_SELECTOR);
        deliveryNumber.ensureDebugId(DebugIds.CONTRACT.DELIVERY_NUMBER_INPUT);
        descriptionLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.CONTRACT.LABEL.DESCRIPTION);
        description.ensureDebugId(DebugIds.CONTRACT.DESCRIPTION_INPUT);
        fileLocation.ensureDebugId(DebugIds.CONTRACT.FILE_LOCATION_INPUT);
        dateSigningLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.CONTRACT.LABEL.DATE_SIGNING);
        dateSigning.setEnsureDebugId(DebugIds.CONTRACT.DATE_SIGNING_CONTAINER);
        dateValidLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.CONTRACT.LABEL.DATE_VALID);
        dateValidDays.ensureDebugId(DebugIds.CONTRACT.DATE_VALID_DAYS_INPUT);
        dateValidDate.ensureDebugId(DebugIds.CONTRACT.DATE_VALID_DATE_SELECTOR);
        costWithCurrencyLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.CONTRACT.LABEL.COST_WITH_CURRENCY);
        costWithCurrency.setEnsureDebugId(DebugIds.CONTRACT.COST_WITH_CURRENCY_CONTAINER);
        projectWidget.setEnsureDebugId(DebugIds.CONTRACT.PROJECT_SELECTOR);
        directions.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.CONTRACT.DIRECTION_INPUT);
        organization.setEnsureDebugId(DebugIds.CONTRACT.ORGANIZATION_SELECTOR);
        curator.setEnsureDebugId(DebugIds.CONTRACT.CURATOR_SELECTOR);
        projectManager.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.CONTRACT.MANAGER_FIELD);
        contractSignManager.setEnsureDebugId(DebugIds.CONTRACT.CONTRACT_SIGN_MANAGER_SELECTOR);
        contractorWidget.setEnsureDebugId(DebugIds.CONTRACT.CONTRACTOR_SELECTOR);
        addDate.ensureDebugId(DebugIds.CONTRACT.ADD_DATES_BUTTON);
        specificationList.setEnsureDebugId(DebugIds.CONTRACT.ADD_SPECIFICATIONS_BUTTON);
        saveButton.ensureDebugId(DebugIds.CONTRACT.SAVE_BUTTON);
        cancelButton.ensureDebugId(DebugIds.CONTRACT.CANCEL_BUTTON);
        backButton.ensureDebugId(DebugIds.CONTRACT.BACK_BUTTON);
        addTagButton.ensureDebugId(DebugIds.CONTRACT.ADD_TAG_BUTTON);
        tabs.setTabNameDebugId(lang.contractDeliveryAndPaymentsPeriodHeader(), DebugIds.CONTRACT.DELIVERY_AND_PAYMENTS_PERIOD_TAB);
        tabs.setTabNameDebugId(lang.contractSpecificationHeader(), DebugIds.CONTRACT.SPECIFICATION_TAB);
        tabs.setTabNameDebugId(lang.contractListOfExpenditureHeader(), DebugIds.CONTRACT.EXPENDITURE_CONTRACTS_TAB);
        notifiers.setAddEnsureDebugId(DebugIds.CONTRACT.NOTIFIERS_SELECTOR_ADD_BUTTON);
        notifiers.setClearEnsureDebugId(DebugIds.CONTRACT.NOTIFIERS_SELECTOR_CLEAR_BUTTON);
        notifiers.setItemContainerEnsureDebugId(DebugIds.CONTRACT.NOTIFIERS_SELECTOR_ITEM_CONTAINER);
        notifiers.setLabelEnsureDebugId(DebugIds.CONTRACT.NOTIFIERS_SELECTOR_LABEL);
        notifiers.ensureDebugId(DebugIds.CONTRACT.NOTIFIERS_SELECTOR);
        notifiersLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.CONTRACT.LABEL.NOTIFIERS);
    }


    @Inject
    En_ContractKindLang contractKindLang;

    @UiField
    @Inject
    Lang lang;

    @Inject
    @UiField(provided = true)
    HomeCompanyFormSelector organization;
    @Inject
    @UiField(provided = true)
    EmployeeCustomFormSelector curator;
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
    @UiField
    TextBox fileLocation;
    @Inject
    @UiField(provided = true)
    SinglePicker dateSigning;
    @UiField
    ValidableLongBox dateValidDays;
    @Inject
    @UiField(provided = true)
    SinglePicker dateValidDate;

    @UiField
    TabWidget tabs;
    @Inject
    @UiField(provided = true)
    ContractSpecificationList specificationList;
    @UiField
    HTMLPanel expenditureContractsContainer;

    @Inject
    @UiField(provided = true)
    ContractFormSelector contractParent;
    @Inject
    @UiField(provided = true)
    ProjectWidget projectWidget;
    @UiField
    DivElement directions;
    @UiField
    SpanElement projectManager;
    @Inject
    @UiField(provided = true)
    EmployeeFormSelector contractSignManager;
    @Inject
    @UiField(provided = true)
    ContractorWidget contractorWidget;
    @UiField
    HTMLPanel notifiersContainer;
    @UiField
    LabelElement notifiersLabel;
    @Inject
    @UiField(provided = true)
    EmployeeMultiSelector notifiers;
    @UiField
    HTMLPanel tags;
    @UiField
    HTMLPanel tagsContainer;
    @UiField
    LabelElement numberLabel;
    @UiField
    LabelElement kindLabel;
    @UiField
    LabelElement descriptionLabel;
    @UiField
    LabelElement dateSigningLabel;
    @UiField
    LabelElement dateValidLabel;
    @UiField
    LabelElement costWithCurrencyLabel;

    @UiField
    Button saveButton;
    @UiField
    Button cancelButton;
    @UiField
    Button backButton;
    @UiField
    Button addTagButton;
    @UiField
    HTMLPanel datesTableContainer;
    @UiField
    Button addDate;
    @UiField
    TextBox deliveryNumber;
    @Inject
    @UiField(provided = true)
    SinglePicker dateEndWarranty;
    @Inject
    @UiField(provided = true)
    SinglePicker dateExecution;

    private AbstractContractEditActivity activity;

    private static ContractViewUiBinder ourUiBinder = GWT.create(ContractViewUiBinder.class);
    interface ContractViewUiBinder extends UiBinder<HTMLPanel, ContractEditView> {}
}
