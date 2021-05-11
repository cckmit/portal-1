package ru.protei.portal.ui.delivery.client.view.create;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.client.view.input.single.SinglePicker;
import ru.protei.portal.core.model.dict.En_DeliveryAttribute;
import ru.protei.portal.core.model.dict.En_DeliveryType;
import ru.protei.portal.core.model.dto.ProjectInfo;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.ent.Kit;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.issuestate.IssueStateFormSelector;
import ru.protei.portal.ui.common.client.widget.issuestate.StateModel;
import ru.protei.portal.ui.common.client.widget.project.ProjectWidget;
import ru.protei.portal.ui.common.client.widget.selector.base.Selector;
import ru.protei.portal.ui.common.client.widget.selector.contract.ContractFormSelector;
import ru.protei.portal.ui.common.client.widget.selector.contract.ContractModel;
import ru.protei.portal.ui.common.client.widget.selector.delivery.attribute.DeliveryAttributeFormSelector;
import ru.protei.portal.ui.common.client.widget.selector.delivery.type.DeliveryTypeFormSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.PersonFormSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.PersonModel;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;
import ru.protei.portal.ui.delivery.client.activity.create.AbstractDeliveryCreateActivity;
import ru.protei.portal.ui.delivery.client.activity.create.AbstractDeliveryCreateView;
import ru.protei.portal.ui.delivery.client.widget.kit.view.list.DeliveryKitList;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.CollectionUtils.setOf;

/**
 * Вид создания и редактирования проекта
 */
public class DeliveryCreateView extends Composite implements AbstractDeliveryCreateView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        ensureDebugIds();
        customerInitiator.setAsyncModel(customerInitiatorModel);
        contract.setAsyncModel(contractModel);
        state.setStateModel( stateModel );
    }

    @Override
    public void setActivity(AbstractDeliveryCreateActivity activity) {
        this.activity = activity;
        kits.setEmptyItemProvider(activity::createEmptyKit);
    }

    @Override
    public HasEnabled saveEnabled() {
        return saveButton;
    }

    @Override
    public HasValue<String> name() { return name; }

    @Override
    public HasText description() { return description; }

    @Override
    public HasValue<List<Kit>> kits() {
        return kits;
    }

    @Override
    public void kitsClear() {
        kits.prepare();
    }

    @Override
    public void updateKitByProject(boolean isArmyProject) {
        kits.setArmyProject(isArmyProject);
    }

    @Override
    public HasValidable kitsValidate() {
        return kits;
    }

    @Override
    public HasValue<CaseState> state() {
        return state;
    }

    @Override
    public void setStateFilter(Selector.SelectorFilter<CaseState> filter) {
        state.setFilter(filter);
    }

    @Override
    public HasValue<En_DeliveryType> type() {
        return type;
    }

    @Override
    public HasValue<ProjectInfo> project() {
        return projectWidget;
    }

    @Override
    public void setCustomerCompany(String value) {
        customerCompany.setValue(value);
    }

    @Override
    public void setCustomerType(String value) {
        customerType.setValue(value);
    }

    @Override
    public void updateInitiatorModel(Long companyId) {
        customerInitiatorModel.updateCompanies( null, setOf(companyId) );
    }

    @Override
    public HasValue<PersonShortView> initiator() {
        return customerInitiator;
    }

    @Override
    public HasEnabled initiatorEnable() {
        return customerInitiator;
    }

    @Override
    public void setManagerCompany(String value) {
        managerCompany.setValue(value);
    }

    @Override
    public void setManager(String value) {
        manager.setValue(value);
    }

    @Override
    public HasValue<En_DeliveryAttribute> attribute() {
        return attribute;
    }

    @Override
    public HasValue<EntityOption> contract() {
        return contract;
    }

    @Override
    public HasEnabled contractEnable() {
        return contract;
    }

    @Override
    public void setContractFieldMandatory(boolean isMandatory) {
        contract.setMandatory(isMandatory);
    }

    @Override
    public void updateContractModel(Long projectId) {
        contractModel.updateProject(null, projectId);
    }

    @Override
    public void setProducts(String value) {
        products.setValue(value);
    }

    @Override
    public HasValue<Date> departureDate() {
        return departureDate;
    }

    @Override
    public boolean isDepartureDateEmpty() {
        return HelperFunc.isEmpty(departureDate.getInputValue());
    }

    @Override
    public void setDepartureDateValid(boolean isValid) {
        departureDate.markInputValid(isValid);
    }

    @Override
    public void setSubscribers(Set<Person> persons) {
        subscribers.setValue(persons.stream().map(PersonShortView::new).collect(Collectors.toSet()));
    }

    @Override
    public Set<Person> getSubscribers() {
        return subscribers.getValue().stream().map(Person::fromPersonShortView).collect(Collectors.toSet());
    }

    @UiHandler("saveButton")
    public void onSaveClicked(ClickEvent event) {
        if (activity != null) {
            activity.onSaveClicked();
        }
    }

    @UiHandler({"cancelButton", "backButton"})
    public void onCancelClicked(ClickEvent event) {
        if (activity != null) {
            activity.onCancelClicked();
        }
    }

    @UiHandler("projectWidget")
    public void onProjectWidgetChanged(ValueChangeEvent<ProjectInfo> event) {
        if (activity != null) {
            activity.onProjectChanged();
        }
    }

    @UiHandler("attribute")
    public void onAttributeChanged(ValueChangeEvent<En_DeliveryAttribute> event) {
        if (activity != null) {
            activity.onAttributeChanged();
        }
    }

    @UiHandler("departureDate")
    public void onDepartureDateChanged(ValueChangeEvent<Date> event) {
        if (activity != null) {
            activity.onDepartureDateChanged();
        }
    }


    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }
        name.ensureDebugId(DebugIds.DELIVERY.NAME_INPUT);
        description.ensureDebugId(DebugIds.DELIVERY.DESCRIPTION_INPUT);
        kits.setEnsureDebugId(DebugIds.DELIVERY.KITS);
        state.setEnsureDebugId(DebugIds.DELIVERY.STATE_SELECTOR);
        type.setEnsureDebugId(DebugIds.DELIVERY.TYPE_SELECTOR);
        projectWidget.setEnsureDebugId(DebugIds.DELIVERY.PROJECT_WIDGET);
        customerType.ensureDebugId(DebugIds.DELIVERY.CUSTOMER_TYPE);
        customerCompany.ensureDebugId(DebugIds.DELIVERY.CUSTOMER_COMPANY);
        customerInitiator.ensureDebugId(DebugIds.DELIVERY.CUSTOMER_INITIATOR);
        managerCompany.ensureDebugId(DebugIds.DELIVERY.MANAGER_COMPANY);
        manager.ensureDebugId(DebugIds.DELIVERY.MANAGER);
        attribute.ensureDebugId(DebugIds.DELIVERY.ATTRIBUTE);
        contract.ensureDebugId(DebugIds.DELIVERY.CONTRACT);
        products.ensureDebugId(DebugIds.DELIVERY.PRODUCTS);
        departureDate.ensureDebugId(DebugIds.DELIVERY.DEPARTURE_DATE);
        subscribers.setItemContainerEnsureDebugId(DebugIds.DELIVERY.SUBSCRIBERS);

        backButton.ensureDebugId(DebugIds.DELIVERY.BACK_BUTTON);
        saveButton.ensureDebugId(DebugIds.DELIVERY.SAVE_BUTTON);
        cancelButton.ensureDebugId(DebugIds.DELIVERY.CANCEL_BUTTON);
    }

    @UiField
    HTMLPanel root;
    @UiField
    ValidableTextBox name;
    @UiField
    TextArea description;
    @Inject
    @UiField(provided = true)
    DeliveryKitList kits;
    @Inject
    @UiField( provided = true )
    IssueStateFormSelector state;
    @Inject
    @UiField( provided = true )
    DeliveryTypeFormSelector type;
    @Inject
    @UiField(provided = true)
    ProjectWidget projectWidget;
    @UiField
    ValidableTextBox customerCompany;
    @UiField
    ValidableTextBox customerType;
    @Inject
    @UiField( provided = true )
    PersonFormSelector customerInitiator;
    @UiField
    ValidableTextBox managerCompany;
    @UiField
    ValidableTextBox manager;
    @Inject    @UiField( provided = true )
    DeliveryAttributeFormSelector attribute;
    @Inject
    @UiField(provided = true)
    ContractFormSelector contract;
    @UiField
    ValidableTextBox products;
    @Inject
    @UiField(provided = true)
    SinglePicker departureDate;
    @Inject
    @UiField( provided = true )
    EmployeeMultiSelector subscribers;

    @UiField
    Button backButton;
    @UiField
    Button saveButton;
    @UiField
    Button cancelButton;
    @Inject
    @UiField
    Lang lang;
    @Inject
    PersonModel customerInitiatorModel;
    @Inject
    ContractModel contractModel;
    @Inject
    StateModel stateModel;

    private AbstractDeliveryCreateActivity activity;

    interface ViewUiBinder extends UiBinder<HTMLPanel, DeliveryCreateView> {}
    private static ViewUiBinder ourUiBinder = GWT.create(ViewUiBinder.class);
}
