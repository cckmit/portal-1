package ru.protei.portal.ui.delivery.client.activity.edit;

import com.google.inject.Inject;
import ru.brainworm.factory.context.client.annotation.ContextAware;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_DeliveryAttribute;
import ru.protei.portal.core.model.dict.En_DeliveryState;
import ru.protei.portal.core.model.dto.ProjectInfo;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.ent.Contract;
import ru.protei.portal.core.model.ent.Delivery;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.DeliveryEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.En_CustomerTypeLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.DeliveryControllerAsync;
import ru.protei.portal.ui.common.shared.model.DefaultErrorHandler;
import ru.protei.portal.ui.delivery.client.view.edit.DeliveryMetaView;

import java.util.Date;
import java.util.Objects;

import static ru.protei.portal.core.model.helper.CollectionUtils.joining;

public abstract class DeliveryMetaActivity implements Activity, AbstractDeliveryMetaActivity {

    @PostConstruct
    public void onInit() {
        deliveryMetaView.setActivity(this);
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails initDetails) {
        this.initDetails = initDetails;
    }

    @Event
    public void onShow(DeliveryEvents.EditDeliveryMeta event) {
        event.parent.clear();
        event.parent.add(deliveryMetaView.asWidget());

        delivery = event.delivery;

        fillView( event.delivery );
    }

    @Override
    public void onProjectChanged() {
//        ProjectInfo project = view.project().getValue();
//        fillProjectSpecificFields(project);
    }

    @Override
    public void onAttributeChanged() {
//        if (En_DeliveryAttribute.DELIVERY.equals(view.attribute().getValue())) {
//            if (view.project().getValue() != null) {
//                view.contractEnable().setEnabled(true);
//            }
//            view.setContractFieldMandatory(true);
//        } else {
//            view.contractEnable().setEnabled(false);
//            view.setContractFieldMandatory(false);
//            view.contract().setValue(null);
//        }
    }

    @Override
    public void onDepartureDateChanged() {
//        view.setDepartureDateValid(
//                isDepartureDateFieldValid(view.isDepartureDateEmpty(), view.departureDate().getValue())
//        );
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
//        view.setCustomerCompany(projectInfo.getContragent().getDisplayText());
//        view.setCustomerType(customerTypeLang.getName(projectInfo.getCustomerType()));
//        view.initiator().setValue(null);
//        view.updateInitiatorModel(projectInfo.getContragent().getId());
//        view.initiatorEnable().setEnabled(true);
//        view.setManagerCompany(projectInfo.getManagerCompany());
//        view.setManager(projectInfo.getManager().getDisplayText());
//        view.setProducts(joining(projectInfo.getProducts(), ", ", ProductShortView::getName));
//        view.contract().setValue(null);
//        view.updateContractModel(projectInfo.getId());
//        view.updateKitByProject(projectInfo.getCustomerType() == En_CustomerType.MINISTRY_OF_DEFENCE);
//        if (En_DeliveryAttribute.DELIVERY.equals(view.attribute().getValue())) {
//            view.contractEnable().setEnabled(true);
//        }
    }

    private void clearProjectSpecificFields() {
//        view.setCustomerCompany(null);
//        view.setCustomerType(null);
//        view.initiatorEnable().setEnabled(false);
//        view.initiator().setValue(null);
//        view.updateInitiatorModel(null);
//        view.setManagerCompany(null);
//        view.setManager(null);
//        view.setProducts(null);
//        view.contract().setValue(null);
//        view.contractEnable().setEnabled(false);
//        view.updateContractModel(null);
//        view.updateKitByProject(false);
    }

    private void fillView(Delivery delivery) {
        deliveryMetaView.state().setValue(delivery.getState());
        deliveryMetaView.type().setValue(delivery.getType());

        ProjectInfo projectInfo = ProjectInfo.fromProject(delivery.getProject());
        deliveryMetaView.project().setValue(projectInfo);
        deliveryMetaView.setCustomerCompany(projectInfo.getContragent().getDisplayText());
        deliveryMetaView.setCustomerType(customerTypeLang.getName(projectInfo.getCustomerType()));
        deliveryMetaView.initiator().setValue(new PersonShortView(projectInfo.getManager().getDisplayText(), projectInfo.getManager().getId()));
        deliveryMetaView.updateInitiatorModel(projectInfo.getContragent().getId());
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
        deliveryMetaView.setSubscribers(delivery.getSubscribers());
    }

    private void showValidationError() {
        fireEvent(new NotifyEvents.Show(getValidationError(), NotifyEvents.NotifyType.ERROR));
    }

    private String getValidationError() {
//        if (isBlank(deliveryMetaView.name().getValue())) {
//            return lang.deliveryValidationEmptyName();
//        }
        CaseState state = deliveryMetaView.state().getValue();
        if (state == null) {
            return lang.deliveryValidationEmptyState();
        } else if (!Objects.equals(En_DeliveryState.PRELIMINARY.getId(), state.getId().intValue())) {
            return lang.deliveryValidationInvalidStateAtCreate();
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
//        if (!deliveryMetaView.kitsValidate().isValid()) {
//            return lang.deliveryValidationInvalidKits();
//        }

        return null;
    }


    @Inject
    private Lang lang;
    @Inject
    private DeliveryMetaView deliveryMetaView;
    @Inject
    private DeliveryControllerAsync controller;
    @Inject
    PolicyService policyService;
    @Inject
    DefaultErrorHandler defaultErrorHandler;
    @Inject
    En_CustomerTypeLang customerTypeLang;

    @ContextAware
    Delivery delivery;

    private AppEvents.InitDetails initDetails;
}
