package ru.protei.portal.ui.delivery.client.view.delivery.meta;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.dom.client.DivElement;
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
import ru.protei.portal.ui.delivery.client.activity.delivery.meta.AbstractDeliveryCommonMeta;
import ru.protei.portal.ui.delivery.client.activity.delivery.meta.AbstractDeliveryMetaActivity;
import ru.protei.portal.ui.delivery.client.activity.delivery.meta.AbstractDeliveryMetaView;

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
    public HasEnabled typeEnabled() {
        return type;
    }

    @Override
    public HasValue<ProjectInfo> project() {
        return projectWidget;
    }

    @Override
    public HasEnabled projectEnabled() {
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
    public HasValue<En_DeliveryAttribute> attribute() {
        return attribute;
    }

    @Override
    public HasEnabled attributeEnabled() {
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
    public void updateContractModel(Long projectId) {
        contractModel.updateProject(null, projectId);
    }

    @Override
    public void setProducts(String value) {
        products.setValue(value);
    }

    @Override
    public void setTeam(String value) { this.team.setInnerHTML(value); }

    @Override
    public HasValue<Date> departureDate() {
        return departureDate;
    }

    @Override
    public HasEnabled departureDateEnabled() {
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

    @Override
    public HasEnabled subscribersEnabled() {
        return subscribers;
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
        attribute.ensureDebugId(DebugIds.DELIVERY.ATTRIBUTE);
        contract.ensureDebugId(DebugIds.DELIVERY.CONTRACT);
        products.ensureDebugId(DebugIds.DELIVERY.PRODUCTS);
        team.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DELIVERY.TEAM);
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
    @Inject
    @UiField( provided = true )
    PersonFormSelector customerInitiator;
    @Inject
    @UiField( provided = true )
    DeliveryAttributeFormSelector attribute;
    @Inject
    @UiField(provided = true)
    ContractFormSelector contract;
    @Inject
    @UiField(provided = true)
    SinglePicker departureDate;
    @Inject
    @UiField( provided = true )
    EmployeeMultiSelector subscribers;
    @Inject
    @UiField
    Lang lang;
    @UiField
    DivElement team;
    @UiField
    TextBox contractCompany;
    @UiField
    TextBox customerType;
    @UiField
    TextBox customerCompany;
    @UiField
    TextBox products;
    @Inject
    PersonModel customerInitiatorModel;
    @Inject
    ContractModel contractModel;

    private AbstractDeliveryCommonMeta commonActivity;

    interface ViewUiBinder extends UiBinder<HTMLPanel, DeliveryMetaView> {}
    private static ViewUiBinder ourUiBinder = GWT.create(ViewUiBinder.class);
}
