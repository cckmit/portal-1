package ru.protei.portal.ui.delivery.client.view.meta;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.client.view.input.single.SinglePicker;
import ru.protei.portal.core.model.dict.En_DeliveryAttribute;
import ru.protei.portal.core.model.dict.En_DeliveryType;
import ru.protei.portal.core.model.dto.ProjectInfo;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.struct.ContractInfo;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.project.ProjectWidget;
import ru.protei.portal.ui.common.client.widget.selector.contract.ContractFormSelector;
import ru.protei.portal.ui.common.client.widget.selector.contract.ContractModel;
import ru.protei.portal.ui.common.client.widget.selector.delivery.attribute.DeliveryAttributeFormSelector;
import ru.protei.portal.ui.common.client.widget.selector.delivery.state.DeliveryStateFormSelector;
import ru.protei.portal.ui.common.client.widget.selector.delivery.type.DeliveryTypeFormSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.PersonFormSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.PersonModel;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;
import ru.protei.portal.ui.delivery.client.activity.meta.AbstractDeliveryCommonMeta;
import ru.protei.portal.ui.delivery.client.activity.meta.AbstractDeliveryMetaActivity;
import ru.protei.portal.ui.delivery.client.activity.meta.AbstractDeliveryMetaView;

import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.CollectionUtils.setOf;

/**
 * Вид меты Поставки
 */
public class DeliveryMetaView extends Composite implements AbstractDeliveryMetaView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        ensureDebugIds();
        customerInitiator.setAsyncModel(customerInitiatorModel);
        contract.setAsyncModel(contractModel);
    }

    @Override
    public void setActivity(AbstractDeliveryCommonMeta activity) {
        this.commonActivity = activity;
    }

    @Override
    public void setActivity(AbstractDeliveryMetaActivity activity) {
        this.commonActivity = activity;
        state.addValueChangeHandler(event -> activity.onStateChange());
        type.addValueChangeHandler(event -> activity.onTypeChange());
        customerInitiator.addValueChangeHandler(event -> activity.onInitiatorChange());
        contract.addValueChangeHandler(event -> activity.onContractChanged());
        subscribers.addValueChangeHandler(event -> activity.onCaseMetaNotifiersChanged());
    }

    @Override
    public HasValue<CaseState> state() {
        return state;
    }

    @Override
    public HasEnabled stateEnable() {
        return state;
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
        customerInitiatorModel.updateCompanies( null, companyId != null ? setOf(companyId) : null);
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
    public void setContractCompany(String value) {
        contractCompany.setValue(value);
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
    public HasValue<ContractInfo> contract() {
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

    @UiHandler("projectWidget")
    public void onProjectWidgetChanged(ValueChangeEvent<ProjectInfo> event) {
        commonActivity.onProjectChanged();
    }

    @UiHandler("attribute")
    public void onAttributeChanged(ValueChangeEvent<En_DeliveryAttribute> event) {
        commonActivity.onAttributeChanged();
    }

    @UiHandler("departureDate")
    public void onDepartureDateChanged(ValueChangeEvent<Date> event) {
        commonActivity.onDepartureDateChanged();
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }
        state.setEnsureDebugId(DebugIds.DELIVERY.STATE_SELECTOR);
        type.setEnsureDebugId(DebugIds.DELIVERY.TYPE_SELECTOR);
        projectWidget.setEnsureDebugId(DebugIds.DELIVERY.PROJECT_WIDGET);
        customerType.ensureDebugId(DebugIds.DELIVERY.CUSTOMER_TYPE);
        customerCompany.ensureDebugId(DebugIds.DELIVERY.CUSTOMER_COMPANY);
        customerInitiator.ensureDebugId(DebugIds.DELIVERY.CUSTOMER_INITIATOR);
        contractCompany.ensureDebugId(DebugIds.DELIVERY.CONTRACT_COMPANY);
        manager.ensureDebugId(DebugIds.DELIVERY.MANAGER);
        attribute.ensureDebugId(DebugIds.DELIVERY.ATTRIBUTE);
        contract.ensureDebugId(DebugIds.DELIVERY.CONTRACT);
        products.ensureDebugId(DebugIds.DELIVERY.PRODUCTS);
        departureDate.ensureDebugId(DebugIds.DELIVERY.DEPARTURE_DATE);
        subscribers.setItemContainerEnsureDebugId(DebugIds.DELIVERY.SUBSCRIBERS);
    }

    @UiField
    HTMLPanel root;
    @Inject
    @UiField( provided = true )
    DeliveryStateFormSelector state;
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
    ValidableTextBox contractCompany;
    @UiField
    ValidableTextBox manager;
    @Inject
    @UiField( provided = true )
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
    @Inject
    @UiField
    Lang lang;
    @Inject
    PersonModel customerInitiatorModel;
    @Inject
    ContractModel contractModel;

    private AbstractDeliveryCommonMeta commonActivity;

    interface ViewUiBinder extends UiBinder<HTMLPanel, DeliveryMetaView> {}
    private static ViewUiBinder ourUiBinder = GWT.create(ViewUiBinder.class);
}
