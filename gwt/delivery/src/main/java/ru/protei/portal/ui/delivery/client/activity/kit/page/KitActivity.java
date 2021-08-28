package ru.protei.portal.ui.delivery.client.activity.kit.page;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.Delivery;
import ru.protei.portal.core.model.ent.Kit;
import ru.protei.portal.core.model.ent.Module;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.DeliveryControllerAsync;
import ru.protei.portal.ui.common.client.service.ModuleControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.delivery.client.activity.kit.handler.KitActionsHandler;
import ru.protei.portal.ui.delivery.client.view.module.table.ModuleTableView;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.CollectionUtils.stream;

public abstract class KitActivity implements Activity, AbstractKitActivity {

    @Inject
    public void onInit() {
        CREATE_ACTION = lang.addKits();
        view.setActivity(this);
        moduleView.setActivity(this);
        view.getModulesContainer().add(moduleView.asWidget());
        view.setHandler(kitActionsHandler);
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails initDetails) {
        this.initDetails = initDetails;
    }

    @Event
    public void onShow(KitEvents.Show event) {
        if (!hasViewPrivileges()) {
            fireEvent(new NotifyEvents.Show(lang.errAccessDenied(), NotifyEvents.NotifyType.ERROR));
            return;
        }
        fireEvent(new ActionBarEvents.Clear());
        if (hasCreatePrivileges()){
            fireEvent(new ActionBarEvents.Add(CREATE_ACTION, null, UiConstants.ActionBarIdentity.KIT));
        }

        deliveryId = event.deliveryId;
        fillKits(deliveryId, event.kitId);
    }

    @Event
    public void onCancelCreatingModule(ModuleEvents.CancelCreating event) {
        moduleView.clearSelectedRows();
        view.modulesContainerVisibility().setVisible(true);
    }

    @Event
    public void onKitChanged(KitEvents.Changed event) {
        if (deliveryId == null || !Objects.equals(deliveryId, event.deliveryId)) {
            return;
        }

        fillKits(deliveryId, kitId);
    }

    @Event
    public void onKitAdded(KitEvents.Added event) {
        if (deliveryId == null || !Objects.equals(deliveryId, event.deliveryId)) {
            return;
        }

        fillKits(deliveryId, kitId);
    }

    @Event
    public void onChangeRow(ModuleEvents.ChangeModule event) {
        moduleService.getModule(event.id, new FluentCallback<Module>()
                .withSuccess(module -> moduleView.updateRow(module))
        );
    }

    @Override
    public void onKitClicked(Long kitId) {
        view.modulesContainerVisibility().setVisible(true);
        view.getModuleEditContainer().clear();
        this.kitId = kitId;
        fillModules(kitId);
    }

    @Override
    public void onItemClicked(Module module) {
        fireEvent(new ModuleEvents.Show(view.getModuleEditContainer(), module.getId()));
    }

    @Override
    public void onAddModuleClicked() {
        view.modulesContainerVisibility().setVisible(false);
        fireEvent(new ModuleEvents.Create(view.getModuleEditContainer(), kitId, deliveryId));
    }

    private void fillKits(Long deliveryId, Long kitId) {
        deliveryService.getDelivery(deliveryId, new FluentCallback<Delivery>()
                .withError((throwable, defaultErrorHandler, status) -> defaultErrorHandler.accept(throwable))
                .withSuccess(delivery -> {
                    view.modulesContainerVisibility().setVisible(true);
                    view.getModuleEditContainer().clear();
                    initDetails.parent.clear();
                    initDetails.parent.add(view.asWidget());
                    view.fillKits(delivery.getKits());
                    view.setKitsActionsEnabled(hasEditPrivileges());
                    if (kitId != null) {
                        this.kitId = kitId;
                        view.makeKitSelected(kitId);
                        fillModules(kitId);
                    }
                })
        );
    }

    private void fillModules(Long kitId) {
        moduleView.clearModules();
        moduleView.clearSelectedRows();
        moduleView.setDeleteEnabled(isDeleteEnabled());
        moduleService.getModulesByKitId(kitId, new FluentCallback<Map<Module, List<Module>>>()
                .withError((throwable, defaultErrorHandler, status) -> defaultErrorHandler.accept(throwable))
                .withSuccess(modules -> {
                    moduleView.fillTable(modules);
                })
        );
    }

    private boolean hasViewPrivileges() {
        return policyService.hasPrivilegeFor(En_Privilege.DELIVERY_VIEW);
    }

    private boolean hasCreatePrivileges() {
        return policyService.hasPrivilegeFor(En_Privilege.DELIVERY_CREATE);
    }

    private boolean hasEditPrivileges() {
        return policyService.hasPrivilegeFor(En_Privilege.DELIVERY_EDIT);
    }

    private boolean hasRemovePrivileges() {
        return policyService.hasPrivilegeFor(En_Privilege.DELIVERY_REMOVE);
    }

    private KitActionsHandler kitActionsHandler = new KitActionsHandler() {
        @Override
        public void onCopy() {
            if (isOneKitSelected(view.getKitsSelected())) {
                fireEvent(new NotifyEvents.Show("On copy Kits clicked", NotifyEvents.NotifyType.SUCCESS));
                return;
            }
            fireEvent(new NotifyEvents.Show(lang.warnOneKitAllowedForTheOperation(), NotifyEvents.NotifyType.ERROR));
        }

        @Override
        public void onGroupChangeState() {
            Set<Kit> kitsSelected = view.getKitsSelected();
            String selectedNumbers = stream(kitsSelected).map(Kit::getSerialNumber).collect(Collectors.joining(","));
            fireEvent(new NotifyEvents.Show("On change state Kits clicked, selected kits: " + selectedNumbers, NotifyEvents.NotifyType.SUCCESS));
        }

        @Override
        public void onGroupRemove() {
            Set<Kit> kitsSelected = view.getKitsSelected();
            String selectedNumbers = stream(kitsSelected).map(Kit::getSerialNumber).collect(Collectors.joining(","));
            fireEvent(new NotifyEvents.Show("On remove Kits clicked, selected kits: " + selectedNumbers, NotifyEvents.NotifyType.SUCCESS));
        }

        @Override
        public void onBack() {
            fireEvent(new DeliveryEvents.Edit(deliveryId));
        }

        @Override
        public void onEdit() {
            if (isOneKitSelected(view.getKitsSelected())){
                fireEvent(new KitEvents.Edit(stream(view.getKitsSelected()).findFirst().map(Kit::getId).orElse(null)));
                return;
            }
            fireEvent(new NotifyEvents.Show(lang.warnOneKitAllowedForTheOperation(), NotifyEvents.NotifyType.ERROR));
        }
    };

    private boolean isOneKitSelected(Set<Kit> kitsSelected) {
        return kitsSelected != null && kitsSelected.size() == 1;
    }

    @Override
    public void onCheckModuleClicked(ModuleTableView moduleTableView) {
        moduleTableView.setDeleteEnabled(isDeleteEnabled());
    }

    @Override
    public void onRemoveModuleClicked(AbstractModuleTableView modulesTableView) {
        if (hasRemovePrivileges()) {
            fireEvent(!modulesTableView.hasSelectedModules()
                    ? new NotifyEvents.Show(lang.selectModulesToRemove(), NotifyEvents.NotifyType.ERROR)
                    : new ConfirmDialogEvents.Show(lang.moduleRemoveConfirmMessage(), removeModuleAction(modulesTableView)));
        }
    }

    private Runnable removeModuleAction(AbstractModuleTableView modulesTableView) {
        Set<Long> modulesToRemoveIds = modulesTableView.getSelectedModules().stream().map(Module::getId)
                                                       .collect(Collectors.toSet());

        return () -> moduleService.removeModules(kitId, modulesToRemoveIds, new FluentCallback<Set<Long>>()
                .withError(throwable -> fireEvent(new NotifyEvents.Show(throwable.getMessage(),
                                                      NotifyEvents.NotifyType.ERROR)))
                .withSuccess(result -> {
                    fireEvent(new NotifyEvents.Show(lang.modulesRemoved(), NotifyEvents.NotifyType.SUCCESS));
                    fillKits(deliveryId, kitId);
                }));
    }

    private boolean isDeleteEnabled() {
        return hasRemovePrivileges() && moduleView.hasSelectedModules();
    }

    @Inject
    Lang lang;
    @Inject
    AbstractKitView view;
    @Inject
    AbstractModuleTableView moduleView;
    @Inject
    PolicyService policyService;
    @Inject
    private DeliveryControllerAsync deliveryService;
    @Inject
    private ModuleControllerAsync moduleService;

    private Long kitId;
    private Long deliveryId;
    private static String CREATE_ACTION;

    private AppEvents.InitDetails initDetails;
}
