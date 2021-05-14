package ru.protei.portal.ui.delivery.client.activity.edit;

import com.google.inject.Inject;
import ru.brainworm.factory.context.client.annotation.ContextAware;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_DeliveryAttribute;
import ru.protei.portal.core.model.dict.En_DeliveryType;
import ru.protei.portal.core.model.dto.ProjectInfo;
import ru.protei.portal.core.model.ent.CaseObjectMetaNotifiers;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.ent.Contract;
import ru.protei.portal.core.model.ent.Delivery;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.events.DeliveryEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.En_CustomerTypeLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.DeliveryControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.delivery.client.view.edit.meta.DeliveryMetaView;

import java.util.Date;

import static ru.protei.portal.core.model.helper.CollectionUtils.joining;

public abstract class DeliveryMetaActivity implements Activity, AbstractDeliveryMetaActivity {

    @PostConstruct
    public void onInit() {
        deliveryMetaView.setActivity(this);
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
        onCaseMetaChanged(delivery, () -> fireEvent(new DeliveryEvents.ChangeModel()));
    }

    @Override
    public void onTypeChange() {
        En_DeliveryType type = deliveryMetaView.type().getValue();
        delivery.setType(type);
        onCaseMetaChanged(delivery, () -> fireEvent(new DeliveryEvents.ChangeModel()));
    }

    @Override
    public void onProjectChanged() {
        ProjectInfo projectInfo = deliveryMetaView.project().getValue();
        fillProjectSpecificFields(projectInfo);

        delivery.setProjectId(projectInfo == null? null : projectInfo.getId());
        delivery.setInitiatorId(null);
        delivery.setInitiator(null);
        onCaseMetaChanged(delivery, () -> fireEvent(new DeliveryEvents.ChangeModel()));
    }

    @Override
    public void onInitiatorChange() {
        PersonShortView initiator = deliveryMetaView.initiator().getValue();
        delivery.setInitiatorId(initiator.getId());
        delivery.setInitiator(initiator);
        onCaseMetaChanged(delivery, () -> fireEvent(new DeliveryEvents.ChangeModel()));
    }

    @Override
    public void onAttributeChanged() {
        if (En_DeliveryAttribute.DELIVERY.equals(deliveryMetaView.attribute().getValue())) {
            if (deliveryMetaView.project().getValue() != null) {
                deliveryMetaView.contractEnable().setEnabled(true);
            }
            deliveryMetaView.setContractFieldMandatory(true);
        } else {
            deliveryMetaView.contractEnable().setEnabled(false);
            deliveryMetaView.setContractFieldMandatory(false);
            deliveryMetaView.contract().setValue(null);
        }

        delivery.setAttribute(deliveryMetaView.attribute().getValue());
        delivery.setContractId(deliveryMetaView.contract().getValue() != null ? deliveryMetaView.contract().getValue().getId() : null);
        onCaseMetaChanged(delivery, () -> fireEvent(new DeliveryEvents.ChangeModel()));
    }

    @Override
    public void onContractChanged() {
        delivery.setContractId(deliveryMetaView.contract().getValue() != null ? deliveryMetaView.contract().getValue().getId() : null);
        onCaseMetaChanged(delivery, () -> fireEvent(new DeliveryEvents.ChangeModel()));
    }

    @Override
    public void onDepartureDateChanged() {
        boolean departureDateFieldValid = isDepartureDateFieldValid(deliveryMetaView.isDepartureDateEmpty(), deliveryMetaView.departureDate().getValue());
        deliveryMetaView.setDepartureDateValid(departureDateFieldValid);
        if (!departureDateFieldValid) {
            return;
        }
        delivery.setDepartureDate(deliveryMetaView.departureDate().getValue());
        onCaseMetaChanged(delivery, () -> fireEvent(new DeliveryEvents.ChangeModel()));
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

    private void onCaseMetaChanged(Delivery delivery, Runnable runAfterUpdate) {
        String error = getValidationError();
        if (error != null) {
            showValidationError(error);
            return;
        }

        controller.updateMeta(delivery, new FluentCallback<Delivery>()
                .withSuccess(caseMetaUpdated -> {
                    fireEvent(new NotifyEvents.Show(lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS));
                    fillView( caseMetaUpdated );
                    if (runAfterUpdate != null) {
                        runAfterUpdate.run();
                    }
                }));
    }

    private boolean isDepartureDateFieldValid(boolean isEmptyDeadlineField, Date date) {
        if (date == null) {
            return isEmptyDeadlineField;
        }

        return true;
    }

    private void fillProjectSpecificFields(ProjectInfo projectInfo) {
        if (projectInfo == null) {
            clearProjectSpecificFields();
            return;
        }
        deliveryMetaView.setCustomerCompany(projectInfo.getContragent().getDisplayText());
        deliveryMetaView.setCustomerType(customerTypeLang.getName(projectInfo.getCustomerType()));
        deliveryMetaView.updateInitiatorModel(projectInfo.getContragent().getId());
        deliveryMetaView.initiator().setValue(null);
        deliveryMetaView.initiatorEnable().setEnabled(true);
        deliveryMetaView.setManagerCompany(projectInfo.getManagerCompany());
        deliveryMetaView.setManager(projectInfo.getManager().getDisplayText());
        deliveryMetaView.setProducts(joining(projectInfo.getProducts(), ", ", ProductShortView::getName));
        deliveryMetaView.contract().setValue(null);
        deliveryMetaView.updateContractModel(projectInfo.getId());
        if (En_DeliveryAttribute.DELIVERY.equals(deliveryMetaView.attribute().getValue())) {
            deliveryMetaView.contractEnable().setEnabled(true);
        }
    }

    private void clearProjectSpecificFields() {
        deliveryMetaView.setCustomerCompany(null);
        deliveryMetaView.setCustomerType(null);
        deliveryMetaView.initiatorEnable().setEnabled(false);
        deliveryMetaView.updateInitiatorModel(null);
        deliveryMetaView.initiator().setValue(null);
        deliveryMetaView.setManagerCompany(null);
        deliveryMetaView.setManager(null);
        deliveryMetaView.setProducts(null);
        deliveryMetaView.contract().setValue(null);
        deliveryMetaView.contractEnable().setEnabled(false);
        deliveryMetaView.updateContractModel(null);
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
        deliveryMetaView.setManagerCompany(projectInfo.getManagerCompany());
        deliveryMetaView.setManager(projectInfo.getManager().getDisplayText());
        deliveryMetaView.setProducts(joining(projectInfo.getProducts(), ", ", ProductShortView::getName));
        deliveryMetaView.updateContractModel(projectInfo.getId());

        Contract contract = delivery.getContract();
        deliveryMetaView.contract().setValue(contract != null ? new EntityOption(contract.getNumber(), delivery.getContract().getId()) : null);
        if (En_DeliveryAttribute.DELIVERY.equals(delivery.getAttribute())) {
            deliveryMetaView.contractEnable().setEnabled(true);
        }
        deliveryMetaView.attribute().setValue(delivery.getAttribute());
        deliveryMetaView.departureDate().setValue(delivery.getDepartureDate());
        deliveryMetaView.setDepartureDateValid(true);
    }

    private void fillNotifiersView(CaseObjectMetaNotifiers caseMetaNotifiers) {
        deliveryMetaView.setSubscribers(caseMetaNotifiers.getNotifiers());
    }

    private void showValidationError(String error) {
        fireEvent(new NotifyEvents.Show(error, NotifyEvents.NotifyType.ERROR));
    }

    private String getValidationError() {
        CaseState state = deliveryMetaView.state().getValue();
        if (state == null) {
            return lang.deliveryValidationEmptyState();
        }
        if (deliveryMetaView.type().getValue() == null) {
            return lang.deliveryValidationEmptyType();
        }
        if (deliveryMetaView.project().getValue() == null) {
            return lang.deliveryValidationEmptyProject();
        }
        En_DeliveryAttribute attribute = deliveryMetaView.attribute().getValue();
         if (En_DeliveryAttribute.DELIVERY == attribute && deliveryMetaView.contract().getValue() == null) {
            return lang.deliveryValidationEmptyContractAtAttributeDelivery();
        }

        return null;
    }


    @Inject
    private Lang lang;
    @Inject
    private DeliveryMetaView deliveryMetaView;
    @Inject
    private DeliveryControllerAsync controller;
    @Inject
    En_CustomerTypeLang customerTypeLang;

    @ContextAware
    Delivery delivery;
    @ContextAware
    CaseObjectMetaNotifiers metaNotifiers;
}
