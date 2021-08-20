package ru.protei.portal.ui.delivery.client.activity.meta;

import com.google.inject.Inject;
import ru.brainworm.factory.context.client.annotation.ContextAware;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_DeliveryAttribute;
import ru.protei.portal.core.model.dict.En_DeliveryType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dto.ProjectInfo;
import ru.protei.portal.core.model.ent.CaseObjectMetaNotifiers;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.ent.Contract;
import ru.protei.portal.core.model.ent.Delivery;
import ru.protei.portal.core.model.struct.ContractInfo;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.CommentAndHistoryEvents;
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
        deliveryMetaView.projectEnabled().setEnabled(false);
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
    public void onHwManagerChange() {
        PersonShortView hwManager = deliveryMetaView.hwManager().getValue();
        delivery.setHwManagerId(hwManager == null ? null : hwManager.getId());
        delivery.setHwManager(hwManager);
        onCaseMetaChanged(delivery);
    }

    @Override
    public void onQcManagerChange() {
        PersonShortView qcManager = deliveryMetaView.qcManager().getValue();
        delivery.setQcManagerId(qcManager == null ? null : qcManager.getId());
        delivery.setQcManager(qcManager);
        onCaseMetaChanged(delivery);
    }

    @Override
    public void onInitiatorChange() {
        PersonShortView initiator = deliveryMetaView.initiator().getValue();
        delivery.setInitiatorId(initiator == null ? null : initiator.getId());
        delivery.setInitiator(initiator);
        onCaseMetaChanged(delivery);
    }

    @Override
    public void onAttributeChanged() {
        super.onAttributeChanged();

        delivery.setAttribute(deliveryMetaView.attribute().getValue());
        delivery.setContractId(deliveryMetaView.contract().getValue() != null ? deliveryMetaView.contract().getValue().getId() : null);
        delivery.setContract(Contract.fromContractInfo(deliveryMetaView.contract().getValue()));
        onCaseMetaChanged(delivery);
    }

    @Override
    public void onContractChanged() {
        delivery.setContractId(deliveryMetaView.contract().getValue() != null ? deliveryMetaView.contract().getValue().getId() : null);
        delivery.setContract(Contract.fromContractInfo(deliveryMetaView.contract().getValue()));
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
                    fireEvent(new CommentAndHistoryEvents.Reload());
                    fillView( caseMetaUpdated );
                }));
    }

    private void fillView(Delivery delivery) {
        deliveryMetaView.state().setValue(delivery.getState());
        deliveryMetaView.stateEnable().setEnabled(hasEditPrivileges() && hasPrivilegesChangeStatus(delivery.getState()));
        deliveryMetaView.type().setValue(delivery.getType());
        deliveryMetaView.typeEnabled().setEnabled(hasEditPrivileges());
        deliveryMetaView.hwManager().setValue(delivery.getHwManager());
        deliveryMetaView.hwManagerEnabled().setEnabled(hasEditPrivileges());
        deliveryMetaView.qcManager().setValue(delivery.getQcManager());
        deliveryMetaView.qcManagerEnabled().setEnabled(hasEditPrivileges());

        ProjectInfo projectInfo = ProjectInfo.fromProject(delivery.getProject());
        deliveryMetaView.project().setValue(projectInfo);
        deliveryMetaView.setCustomerCompany(projectInfo.getContragent().getDisplayText());
        deliveryMetaView.setCustomerType(customerTypeLang.getName(projectInfo.getCustomerType()));
        deliveryMetaView.updateInitiatorModel(projectInfo.getContragent().getId());
        deliveryMetaView.initiator().setValue(delivery.getInitiator());
        deliveryMetaView.initiatorEnable().setEnabled(hasEditPrivileges());
        deliveryMetaView.setManager(delivery.getProject().getManagerFullName());
        deliveryMetaView.setProducts(joining(projectInfo.getProducts(), ", ", ProductShortView::getName));
        deliveryMetaView.updateContractModel(projectInfo.getId());

        Contract contract = delivery.getContract();
        deliveryMetaView.contract().setValue(contract != null ? new ContractInfo(delivery.getContract().getId(), contract.getNumber(), contract.getOrganizationName()) : null);
        if (En_DeliveryAttribute.DELIVERY.equals(delivery.getAttribute())) {
            deliveryMetaView.contractEnable().setEnabled(true);
        }
        deliveryMetaView.attribute().setValue(delivery.getAttribute());
        deliveryMetaView.attributeEnabled().setEnabled(hasEditPrivileges());
        deliveryMetaView.contractEnable().setEnabled(delivery.getAttribute() == En_DeliveryAttribute.DELIVERY);
        deliveryMetaView.setContractCompany(contract != null ? contract.getOrganizationName() : null);
        deliveryMetaView.departureDate().setValue(delivery.getDepartureDate());
        deliveryMetaView.departureDateEnabled().setEnabled(hasEditPrivileges());
        deliveryMetaView.setDepartureDateValid(true);
        deliveryMetaView.subscribersEnabled().setEnabled(hasEditPrivileges());
    }

    private void fillNotifiersView(CaseObjectMetaNotifiers caseMetaNotifiers) {
        deliveryMetaView.setSubscribers(caseMetaNotifiers.getNotifiers());
    }

    private void showValidationError(String error) {
        fireEvent(new NotifyEvents.Show(error, NotifyEvents.NotifyType.ERROR));
    }

    private boolean hasPrivilegesChangeStatus(CaseState caseState) {
        return !Objects.equals(caseState.getId(), CrmConstants.State.PRELIMINARY)
                || policyService.hasPrivilegeFor(En_Privilege.DELIVERY_CHANGE_PRELIMINARY_STATUS);
    }

    private boolean hasEditPrivileges() {
        return policyService.hasPrivilegeFor(En_Privilege.DELIVERY_EDIT);
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
