package ru.protei.portal.ui.delivery.client.activity.module.create;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.ent.Delivery;
import ru.protei.portal.core.model.ent.Kit;
import ru.protei.portal.core.model.ent.Module;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.ErrorPageEvents;
import ru.protei.portal.ui.common.client.events.KitEvents;
import ru.protei.portal.ui.common.client.events.ModuleEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.CaseStateControllerAsync;
import ru.protei.portal.ui.common.client.service.DeliveryControllerAsync;
import ru.protei.portal.ui.common.client.service.ModuleControllerAsync;
import ru.protei.portal.ui.common.shared.model.DefaultErrorHandler;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.Date;
import java.util.Objects;
import java.util.function.Consumer;

import static ru.protei.portal.core.model.helper.StringUtils.isBlank;

public abstract class ModuleCreateActivity implements Activity, AbstractModuleCreateActivity {

    @Inject
    public void onInit() {
        view.setActivity(this);
    }

    @Event
    public void onShow(ModuleEvents.Create event) {
        if (!hasPrivileges()) {
            fireEvent(new ErrorPageEvents.ShowForbidden(event.parent));
            return;
        }
        event.parent.clear();
        event.parent.add(view.asWidget());
        this.kitId = event.kitId;
        this.deliveryId = event.deliveryId;
        deliveryService.getDelivery(event.deliveryId, new FluentCallback<Delivery>()
                .withError(defaultErrorHandler)
                .withSuccess(delivery -> {
                            delivery.getKits().stream()
                                    .filter(k -> Objects.equals(k.getId(), event.kitId))
                                    .findFirst()
                                    .ifPresent(kit -> fillView(delivery, kit));
                        }
                ));
    }

    @Override
    public void onSaveClicked() {
        String error = getValidationError();
        if (error != null) {
            showValidationError(error);
            return;
        }
        Module module = fillDto();
        save(module);
    }

    @Override
    public void onDepartureDateChanged() {
        view.setDepartureDateValid(isDepartureDateFieldValid());
    }

    public boolean isDepartureDateFieldValid() {
        Date departureDate = view.departureDate().getValue();
        if (departureDate == null) {
            return view.isDepartureDateEmpty();
        }

        return departureDate.getTime() > System.currentTimeMillis();
    }

    private void updateSerialNumber(Long kitId) {
        moduleService.generateSerialNumber(kitId, new FluentCallback<String>()
                .withError((throwable, defaultErrorHandler, status) -> defaultErrorHandler.accept(throwable))
                .withSuccess(serialNumber -> view.serialNumber().setValue(serialNumber))
        );
    }

    private void fillView(Delivery delivery, Kit kit) {
        updateSerialNumber(kit.getId());
        view.name().setValue(null);
        view.description().setValue(null);
        fillStateSelector(CrmConstants.State.PRELIMINARY);
        view.setAllowChangingState(kit.getStateId() != CrmConstants.State.PRELIMINARY);
        view.setManager(delivery.getProject().getManagerFullName());
        view.hwManager().setValue(delivery.getHwManager());
        view.qcManager().setValue(delivery.getQcManager());
        view.setCustomerCompany(delivery.getProject().getCustomer().getCname());
        view.setBuildDateValid(true);
        view.setDepartureDateValid(true);
    }

    private Module fillDto() {
        Module module = new Module();
        module.setSerialNumber(view.serialNumber().getValue());
        module.setName(view.name().getValue());
        module.setDescription(view.description().getValue());
        module.setStateId(view.state().getValue().getId());
        module.setHwManagerId(view.hwManager().getValue() == null ? null : view.hwManager().getValue().getId());
        module.setQcManagerId(view.qcManager().getValue() == null ? null : view.qcManager().getValue().getId());
        module.setBuildDate(view.buildDate().getValue());
        module.setDepartureDate(view.departureDate().getValue());
        module.setKitId(kitId);
        return module;
    }

    private void showValidationError(String error) {
        fireEvent(new NotifyEvents.Show(error, NotifyEvents.NotifyType.ERROR));
    }

    private String getValidationError() {
        if (isBlank(view.name().getValue())) {
            return lang.moduleValidationEmptyName();
        }

        if (view.state() == null) {
            return lang.moduleValidationEmptyState();
        }

        if (!isDateValid(view.buildDate().getValue())) {
            return lang.moduleValidationInvalidBuildDate();
        }

        if (!isDateValid(view.departureDate().getValue())) {
            return lang.moduleValidationInvalidDepartureDate();
        }

        return null;
    }

    private void save(Module module) {
        save(module, throwable -> {}, () -> {
            fireEvent(new NotifyEvents.Show(lang.moduleCreatedSuccessfully(), NotifyEvents.NotifyType.SUCCESS));
            fireEvent(new KitEvents.Show(deliveryId, kitId));
        });
    }

    private void save(Module module, Consumer<Throwable> onFailure, Runnable onSuccess) {
        view.saveEnabled().setEnabled(false);
        moduleService.saveModule(module, new FluentCallback<Module>()
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

    private void fillStateSelector(Long id) {
        view.state().setValue(new CaseState(id));
        caseStateController.getCaseStateWithoutCompaniesOmitPrivileges(id, new FluentCallback<CaseState>()
                .withSuccess(caseState -> view.state().setValue(caseState)));
    }

    private boolean isDateValid(Date date) {
        return date == null || date.getTime() > System.currentTimeMillis();
    }

    private Long kitId;
    private Long deliveryId;

    @Inject
    Lang lang;
    @Inject
    AbstractModuleCreateView view;
    @Inject
    private ModuleControllerAsync moduleService;
    @Inject
    private DeliveryControllerAsync deliveryService;
    @Inject
    private CaseStateControllerAsync caseStateController;
    @Inject
    private PolicyService policyService;
    @Inject
    private DefaultErrorHandler defaultErrorHandler;
}

