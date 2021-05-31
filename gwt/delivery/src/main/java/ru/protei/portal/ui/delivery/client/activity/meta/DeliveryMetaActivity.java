package ru.protei.portal.ui.delivery.client.activity.meta;

import com.google.inject.Inject;
import ru.brainworm.factory.context.client.annotation.ContextAware;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_DeliveryAttribute;
import ru.protei.portal.core.model.dict.En_DeliveryState;
import ru.protei.portal.core.model.dict.En_DeliveryType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dto.ProjectInfo;
import ru.protei.portal.core.model.ent.CaseObjectMetaNotifiers;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.ent.Contract;
import ru.protei.portal.core.model.ent.Delivery;
import ru.protei.portal.core.model.struct.ContractInfo;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.DeliveryEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.En_CustomerTypeLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.DeliveryControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.delivery.client.view.meta.DeliveryMetaView;

import java.util.Objects;

import static ru.protei.portal.core.model.helper.CollectionUtils.joining;

public abstract class DeliveryMetaActivity extends DeliveryCommonMeta implements Activity, AbstractDeliveryMetaActivity {

    @PostConstruct
    public void onInit() {
        deliveryMetaView.setActivity(this);
        setDeliveryMetaView(deliveryMetaView, ignore -> {});
    }

    @Event
    public void onShow(DeliveryEvents.EditDeliveryMeta event) {
        event.parent.clear();
        event.parent.add(deliveryMetaView.asWidget());

        delivery = event.delivery;
        metaNotifiers = event.metaNotifiers;

        fillView( event.delivery );
        fillNotifiersView( metaNotifiers );
        deliveryMetaView.stateEnable().setEnabled(hasPrivilegesChangeStatus(event.delivery.getState()));
    }

    @Override
    public void onStateChange() {
        CaseState caseState = deliveryMetaView.state().getValue();
        delivery.setState(caseState);
        delivery.setStateId(caseState.getId());
        onCaseMetaChanged(delivery);
    }

    @Override
    public void onTypeChange() {
        En_DeliveryType type = deliveryMetaView.type().getValue();
        delivery.setType(type);
        onCaseMetaChanged(delivery);
    }

    @Override
    public void onProjectChanged() {
        super.onProjectChanged();

        ProjectInfo projectInfo = deliveryMetaView.project().getValue();
        delivery.setProjectId(projectInfo == null? null : projectInfo.getId());
        delivery.setInitiatorId(null);
        delivery.setInitiator(null);
        delivery.setContractId(null);
        onCaseMetaChanged(delivery);
    }

    @Override
    public void onInitiatorChange() {
        PersonShortView initiator = deliveryMetaView.initiator().getValue();
        delivery.setInitiatorId(initiator.getId());
        delivery.setInitiator(initiator);
        onCaseMetaChanged(delivery);
    }

    @Override
    public void onAttributeChanged() {
        super.onAttributeChanged();

        delivery.setAttribute(deliveryMetaView.attribute().getValue());
        delivery.setContractId(deliveryMetaView.contract().getValue() != null ? deliveryMetaView.contract().getValue().getId() : null);
        onCaseMetaChanged(delivery);
    }

    @Override
    public void onContractChanged() {
        delivery.setContractId(deliveryMetaView.contract().getValue() != null ? deliveryMetaView.contract().getValue().getId() : null);
        onCaseMetaChanged(delivery);
    }

    @Override
    public void onDepartureDateChanged() {
        super.onDepartureDateChanged();
        if (!isDepartureDateFieldValid()) {
            return;
        }
        delivery.setDepartureDate(deliveryMetaView.departureDate().getValue());
        onCaseMetaChanged(delivery);
    }

    @Override
    public void onCaseMetaNotifiersChanged() {
        metaNotifiers.setNotifiers( deliveryMetaView.getSubscribers() );
        controller.updateMetaNotifiers(metaNotifiers, new FluentCallback<CaseObjectMetaNotifiers>()
                .withSuccess(caseMetaNotifiersUpdated -> {
                    fireEvent(new NotifyEvents.Show(lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS));
                    deliveryMetaView.setSubscribers(caseMetaNotifiersUpdated.getNotifiers());
                }));
    }

    private void onCaseMetaChanged(Delivery delivery) {
        String error = getValidationError();
        if (error != null) {
            showValidationError(error);
            return;
        }

        controller.updateMeta(delivery, new FluentCallback<Delivery>()
                .withSuccess(caseMetaUpdated -> {
                    fireEvent(new NotifyEvents.Show(lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS));
                    fireEvent(new DeliveryEvents.ChangeDelivery(delivery.getId()));
                    fillView( caseMetaUpdated );
                }));
    }

    private void fillView(Delivery delivery) {
        deliveryMetaView.state().setValue(delivery.getState());
        deliveryMetaView.type().setValue(delivery.getType());

        ProjectInfo projectInfo = ProjectInfo.fromProject(delivery.getProject());
        deliveryMetaView.project().setValue(projectInfo);
        deliveryMetaView.setCustomerCompany(projectInfo.getContragent().getDisplayText());
        deliveryMetaView.setCustomerType(customerTypeLang.getName(projectInfo.getCustomerType()));
        deliveryMetaView.updateInitiatorModel(projectInfo.getContragent().getId());
        deliveryMetaView.initiator().setValue(delivery.getInitiator());
        deliveryMetaView.initiatorEnable().setEnabled(true);
        deliveryMetaView.setManager(projectInfo.getManager().getDisplayText());
        deliveryMetaView.setProducts(joining(projectInfo.getProducts(), ", ", ProductShortView::getName));
        deliveryMetaView.updateContractModel(projectInfo.getId());

        Contract contract = delivery.getContract();
        deliveryMetaView.contract().setValue(contract != null ? new ContractInfo(delivery.getContract().getId(), contract.getNumber(), contract.getOrganizationName() ) : null);
        if (En_DeliveryAttribute.DELIVERY.equals(delivery.getAttribute())) {
            deliveryMetaView.contractEnable().setEnabled(true);
        }
        deliveryMetaView.attribute().setValue(delivery.getAttribute());
        deliveryMetaView.contractEnable().setEnabled(delivery.getAttribute() == En_DeliveryAttribute.DELIVERY);
        deliveryMetaView.setContractCompany(contract != null ? contract.getOrganizationName() : null);
        deliveryMetaView.departureDate().setValue(delivery.getDepartureDate());
        deliveryMetaView.setDepartureDateValid(true);
    }

    private void fillNotifiersView(CaseObjectMetaNotifiers caseMetaNotifiers) {
        deliveryMetaView.setSubscribers(caseMetaNotifiers.getNotifiers());
    }

    private void showValidationError(String error) {
        fireEvent(new NotifyEvents.Show(error, NotifyEvents.NotifyType.ERROR));
    }

    private boolean hasPrivilegesChangeStatus(CaseState caseState) {
        return !Objects.equals(caseState.getId(), (long)En_DeliveryState.PRELIMINARY.getId())
                || policyService.hasPrivilegeFor(En_Privilege.DELIVERY_CHANGE_PRELIMINARY_STATUS);
    }

    @Inject
    private Lang lang;
    @Inject
    private DeliveryMetaView deliveryMetaView;
    @Inject
    private DeliveryControllerAsync controller;
    @Inject
    private En_CustomerTypeLang customerTypeLang;
    @Inject
    private PolicyService policyService;
    @ContextAware
    Delivery delivery;
    @ContextAware
    CaseObjectMetaNotifiers metaNotifiers;
}
