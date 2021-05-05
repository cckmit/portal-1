package ru.protei.portal.ui.delivery.client.activity.create;

import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.activity.client.enums.Type;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_DeliveryAttribute;
import ru.protei.portal.core.model.dict.En_DeliveryState;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dto.ProjectInfo;
import ru.protei.portal.core.model.ent.Delivery;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.DeliveryEvents;
import ru.protei.portal.ui.common.client.events.ErrorPageEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.En_CustomerTypeLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.DeliveryControllerAsync;
import ru.protei.portal.ui.common.shared.model.DefaultErrorHandler;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.Collections;
import java.util.Date;
import java.util.function.Consumer;

import static ru.protei.portal.core.model.helper.CollectionUtils.joining;

public abstract class DeliveryCreateActivity implements Activity, AbstractDeliveryCreateActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails initDetails) {
        this.initDetails = initDetails;
    }

    @Event(Type.FILL_CONTENT)
    public void onShow(DeliveryEvents.Create event) {
        if (!hasPrivileges()) {
            fireEvent(new ErrorPageEvents.ShowForbidden(initDetails.parent));
            return;
        }

        initDetails.parent.clear();
        Window.scrollTo(0, 0);
        initDetails.parent.add(view.asWidget());

        prepare();
    }

    @Override
    public void onSaveClicked() {
        Delivery delivery = fillDto();
        if (getValidationError() != null) {
            showValidationError();
            return;
        }
        save(delivery);
    }

    @Override
    public void onCancelClicked() {
        fireEvent(new Back());
    }

    @Override
    public void onProjectChanged() {
        ProjectInfo project = view.project().getValue();
        fillProjectSpecificFields(project);
    }

    @Override
    public void onAttributeChanged() {
        if (En_DeliveryAttribute.DELIVERY.equals(view.attribute().getValue())) {
            view.contractEnable().setEnabled(true);
        } else {
            view.contractEnable().setEnabled(false);
            view.contract().setValue(null);
        }
    }

    @Override
    public void onDepartureDateChanged() {
        view.setDepartureDateValid(
                isDepartureDateFieldValid(view.isDepartureDateEmpty(), view.departureDate().getValue()));
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
        view.setCustomerCompany(projectInfo.getContragent().getDisplayText());
        view.setCustomerType(customerTypeLang.getName(projectInfo.getCustomerType()));
        view.initiator().setValue(null);
        view.updateInitiatorModel(projectInfo.getContragent().getId());
        view.initiatorEnable().setEnabled(true);
        view.setManagerCompany(projectInfo.getManagerCompany());
        view.setManager(projectInfo.getManager().getDisplayText());
        view.setProducts(joining(projectInfo.getProducts(), ", ", ProductShortView::getName));
        view.contract().setValue(null);
        view.updateContractModel(projectInfo.getId());
    }

    private void clearProjectSpecificFields() {
        view.setCustomerCompany(null);
        view.setCustomerType(null);
        view.initiatorEnable().setEnabled(false);
        view.initiator().setValue(null);
        view.updateInitiatorModel(-999L);      // todo
        view.setManagerCompany(null);
        view.setManager(null);
        view.setProducts(null);
        view.contract().setValue(null);
        view.contractEnable().setEnabled(false);
        view.updateContractModel(-999L); // todo
    }

    private void prepare() {
        view.name().setValue(null);
        view.description().setText(null);
        // view.kitContainer();
        view.state().setValue(En_DeliveryState.PRELIMINARY);
        view.type().setValue(null);
        view.project().setValue(null);
        view.setCustomerCompany(null);
        view.setCustomerType(null);
        view.updateInitiatorModel(-999L);      // todo
        view.initiator().setValue(null);
        view.initiatorEnable().setEnabled(false);
        view.setManagerCompany(null);
        view.setManager(null);
        view.attribute().setValue(null);
        view.contract().setValue(null);
        view.contractEnable().setEnabled(false);
        view.updateContractModel(-999L); // todo
        view.setProducts(null);
        view.departureDate().setValue(null);
        view.setDepartureDateValid(true);
        view.setSubscribers(Collections.emptySet());
    }

    private Delivery fillDto() {
        Delivery delivery = new Delivery();
        delivery.setName(view.name().getValue());
        delivery.setDescription(view.description().getText());
        delivery.setProjectId(view.project().getValue().getId());
        delivery.setAttribute(view.attribute().getValue());
        delivery.setState(view.state().getValue());
        delivery.setType(view.type().getValue());
        delivery.setDepartureDate(view.departureDate().getValue());
        delivery.setSubscribers(view.getSubscribers());

        return delivery;
    }

    private void showValidationError() {
        fireEvent(new NotifyEvents.Show(getValidationError(), NotifyEvents.NotifyType.ERROR));
    }

    private String getValidationError() {
        /*if (isBlank(contract.getNumber()))
            return lang.contractValidationEmptyNumber();

        if (isBlank(contract.getDescription()))
            return lang.contractValidationEmptyDescription();

        if (contract.getContractType() == null)
            return lang.contractValidationEmptyType();

        if (contract.getStateId() == null)
            return lang.contractValidationEmptyState();

        if (contract.getDateSigning() == null && !(contract.getState().equals(En_ContractState.AGREEMENT)))
            return lang.contractValidationEmptyDateSigning();

        if (contract.getDateSigning() != null && contract.getDateValid() != null &&
                isBefore(contract.getDateSigning(), contract.getDateValid()))
            return lang.contractValidationInvalidDateValid();

        if (contract.getCost() == null || contract.getCost().getFull() < 0)
            return lang.contractValidationInvalidCost();

        if (contract.getProjectId() == null)
            return lang.contractValidationEmptyProject();

        if (!view.validateContractSpecifications().isValid())
            return lang.contractValidationContractSpecification();*/

        return null;
    }

    private void save(Delivery delivery) {
        saveContract(delivery, throwable -> {}, () -> fireEvent(new DeliveryEvents.ChangeModel()));
    }

    private void saveContract(Delivery delivery, Consumer<Throwable> onFailure, Runnable onSuccess) {
        view.saveEnabled().setEnabled(false);
        controller.saveDelivery(delivery, new FluentCallback<Delivery>()
            .withError(throwable -> {
                view.saveEnabled().setEnabled(true);
                defaultErrorHandler.accept(throwable);
                onFailure.accept(throwable);
            })
            .withSuccess(id -> {
                view.saveEnabled().setEnabled(true);
                onSuccess.run();
            }));
    }

    private boolean hasPrivileges() {
        return policyService.hasPrivilegeFor(En_Privilege.DELIVERY_CREATE);
    }

    @Inject
    private Lang lang;
    @Inject
    private AbstractDeliveryCreateView view;
    @Inject
    private DeliveryControllerAsync controller;
    @Inject
    PolicyService policyService;
    @Inject
    DefaultErrorHandler defaultErrorHandler;
    @Inject
    En_CustomerTypeLang customerTypeLang;

    private AppEvents.InitDetails initDetails;
}
