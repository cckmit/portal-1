package ru.protei.portal.ui.delivery.client.activity.module.create;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dto.Project;
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
import ru.protei.portal.ui.delivery.client.activity.module.meta.AbstractModuleMetaActivity;
import ru.protei.portal.ui.delivery.client.activity.module.meta.AbstractModuleMetaView;

import java.util.Date;
import java.util.Objects;
import java.util.function.Consumer;

import static ru.protei.portal.core.model.helper.StringUtils.isBlank;

public abstract class ModuleCreateActivity implements Activity, AbstractModuleCreateActivity,
        AbstractModuleMetaActivity {

    @Inject
    public void onInit() {
        view.setActivity(this);
        metaView.setActivity(this);
        view.getMetaViewContainer().add(metaView.asWidget());
    }

    @Event
    public void onShow(ModuleEvents.Create event) {
        if (!hasEditPrivileges()) {
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
    public void onCancelClicked() {
        view.asWidget().removeFromParent();
        fireEvent(new ModuleEvents.CancelCreating());
    }

    @Override
    public void onBuildDateChanged() {
        metaView.setBuildDateValid(isBuildDateFieldValid());
    }

    @Override
    public void onDepartureDateChanged() {
        metaView.setDepartureDateValid(isDepartureDateFieldValid());
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

    public boolean isBuildDateFieldValid() {
        Date buildDate = metaView.buildDate().getValue();
        if (buildDate == null) {
            return metaView.isBuildDateEmpty();
        }

        return buildDate.getTime() > System.currentTimeMillis();
    }

    public boolean isDepartureDateFieldValid() {
        Date departureDate = metaView.departureDate().getValue();
        if (departureDate == null) {
            return metaView.isDepartureDateEmpty();
        }

        return departureDate.getTime() > System.currentTimeMillis();
    }

    private void fillView(Delivery delivery, Kit kit) {
        updateSerialNumber(kit.getId());
        view.name().setValue(null);
        view.description().setValue(null);
        fillStateSelector(CrmConstants.State.PRELIMINARY);
        metaView.setAllowChangingState(kit.getStateId() != CrmConstants.State.PRELIMINARY);
        Project project = delivery.getProject();
        metaView.setCustomerCompany(project.getCustomer().getCname());
        metaView.setManager(project.getManagerFullName());
        metaView.hwManager().setValue(project.getHardwareCurator());
        metaView.qcManager().setValue(project.getQualityControlCurator());
        metaView.buildDate().setValue(null);
        metaView.setBuildDateValid(true);
        metaView.departureDate().setValue(null);
        metaView.setDepartureDateValid(true);
    }

    private Module fillDto() {
        Module module = new Module();
        module.setSerialNumber(view.serialNumber().getValue());
        module.setName(view.name().getValue());
        module.setDescription(view.description().getValue());
        module.setStateId(metaView.state().getValue().getId());
        module.setHwManagerId(metaView.hwManager().getValue() == null ? null : metaView.hwManager().getValue().getId());
        module.setQcManagerId(metaView.qcManager().getValue() == null ? null : metaView.qcManager().getValue().getId());
        module.setBuildDate(metaView.buildDate().getValue());
        module.setDepartureDate(metaView.departureDate().getValue());
        module.setKitId(kitId);
        return module;
    }

    private void updateSerialNumber(Long kitId) {
        moduleService.generateSerialNumber(kitId, new FluentCallback<String>()
                .withError((throwable, defaultErrorHandler, status) -> defaultErrorHandler.accept(throwable))
                .withSuccess(serialNumber -> view.serialNumber().setValue(serialNumber))
        );
    }

    private void fillStateSelector(Long id) {
        metaView.state().setValue(new CaseState(id));
        caseStateController.getCaseStateWithoutCompaniesOmitPrivileges(id, new FluentCallback<CaseState>()
                .withSuccess(caseState -> metaView.state().setValue(caseState)));
    }

    private void showValidationError(String error) {
        fireEvent(new NotifyEvents.Show(error, NotifyEvents.NotifyType.ERROR));
    }

    private String getValidationError() {
        if (isBlank(view.name().getValue())) {
            return lang.moduleValidationEmptyName();
        }

        if (metaView.state() == null) {
            return lang.moduleValidationEmptyState();
        }

        if (!isDateValid(metaView.buildDate().getValue())) {
            return lang.moduleValidationInvalidBuildDate();
        }

        if (!isDateValid(metaView.departureDate().getValue())) {
            return lang.moduleValidationInvalidDepartureDate();
        }

        return null;
    }

    private boolean isDateValid(Date date) {
        return date == null || date.getTime() > System.currentTimeMillis();
    }

    private boolean hasEditPrivileges() {
        return policyService.hasPrivilegeFor(En_Privilege.DELIVERY_EDIT);
    }

    @Inject
    Lang lang;
    @Inject
    AbstractModuleCreateView view;
    @Inject
    AbstractModuleMetaView metaView;
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

    private Long kitId;
    private Long deliveryId;
}

