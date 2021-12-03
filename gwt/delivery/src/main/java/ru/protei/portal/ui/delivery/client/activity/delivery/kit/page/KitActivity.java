package ru.protei.portal.ui.delivery.client.activity.delivery.kit.page;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.brainworm.factory.context.client.annotation.ContextAware;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.activity.client.enums.Type;
import ru.protei.portal.core.model.dict.En_CustomerType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dto.Project;
import ru.protei.portal.core.model.ent.CaseState;
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
import ru.protei.portal.ui.delivery.client.activity.delivery.kit.handler.KitActionsHandler;
import ru.protei.portal.ui.delivery.client.view.delivery.module.table.ModuleTableView;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.CollectionUtils.*;

public abstract class KitActivity implements Activity, AbstractKitActivity {

    @Inject
    public void onInit() {
        CREATE_ACTION = lang.addKits();
        view.setActivity(this);
        moduleView.setActivity(this);
        view.getModulesContainer().add(moduleView.asWidget());
        view.setHandler(kitActionsHandler);
        view.setKitNotSelectedMessageVisible(true);
        view.setModuleNotSelectedMessageVisible(true);
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails initDetails) {
        this.initDetails = initDetails;
    }

    @Event(Type.FILL_CONTENT)
    public void onShow(KitEvents.Show event) {
        HasWidgets container = initDetails.parent;
        if (!hasViewPrivileges()) {
            fireEvent(new ErrorPageEvents.ShowForbidden(container));
            return;
        }
        container.clear();
        requestDelivery(event.deliveryId, event.kitId, container);
    }

    @Event
    public void onAddKitsClicked(ActionBarEvents.Clicked event) {
        if (!UiConstants.ActionBarIdentity.KIT.equals(event.identity)) {
            return;
        }
        if (!hasCreatePrivileges()) {
            return;
        }
        if (delivery == null || !isMilitaryProject(delivery.getProject())) {
            return;
        }
        fireEvent(new KitEvents.Add(delivery.getId())
                .withBackHandler(kits -> {
                    if (kits.stream().anyMatch(kit -> !Objects.equals(kit.getDeliveryId(), delivery.getId()))) {
                        return;
                    }
                    delivery.setKits(kits);
                    fillKits(kits, kitId);
                }));
    }

    @Event
    public void onCancelCreatingModule(ModuleEvents.CancelCreating event) {
        moduleView.clearSelectedRows();
        view.setModuleNotSelectedMessageVisible(true);
        view.modulesContainerVisibility().setVisible(true);
    }

    @Event
    public void onModuleChanged(ModuleEvents.Change event) {
        moduleService.getModule(event.id, new FluentCallback<Module>()
                .withSuccess(module -> moduleView.updateRow(module))
        );
    }

    @Override
    public void onKitClicked(Long kitId) {
        this.kitId = kitId;
        fillModules(kitId);
    }

    @Override
    public void onModulesStateChangeClicked(CaseState state) {
        List<Long> modulesIds = stream(moduleView.getSelectedModules()).map(Module::getId).collect(Collectors.toList());

        if (isEmpty(modulesIds)){
            fireEvent(new NotifyEvents.Show(lang.moduleNotSelectedMessage(), NotifyEvents.NotifyType.ERROR));
            return;
        }

        moduleService.updateModuleListStates(modulesIds, state.getId(), new FluentCallback<Void>()
                .withSuccess((Void) -> {
                    fillModules(kitId);
                    fireEvent(new NotifyEvents.Show(lang.modulesStatesUpdated(), NotifyEvents.NotifyType.SUCCESS));
                }));
    }

    @Override
    public void onAddModuleClicked() {
        if (delivery == null) {
            return;
        }
        view.setModuleNotSelectedMessageVisible(false);
        view.modulesContainerVisibility().setVisible(false);
        fireEvent(new ModuleEvents.Create(view.getModuleEditContainer(), kitId, delivery.getId()));
    }

    @Override
    public void onItemClicked(Module module) {
        view.setModuleNotSelectedMessageVisible(false);
        fireEvent(new ModuleEvents.ShowPreview(view.getModuleEditContainer(), module.getId()));
    }

    @Override
    public void onCheckModuleClicked(ModuleTableView moduleTableView) {
        moduleTableView.setDeleteEnabled(isDeleteEnabled());
    }

    @Override
    public void onRemoveModuleClicked(AbstractModuleTableView modulesTableView) {
        if (delivery == null) {
            return;
        }
        if (hasRemovePrivileges()) {
            fireEvent(!modulesTableView.hasSelectedModules()
                    ? new NotifyEvents.Show(lang.selectModulesToRemove(), NotifyEvents.NotifyType.ERROR)
                    : new ConfirmDialogEvents.Show(lang.moduleRemoveConfirmMessage(), removeModuleAction(modulesTableView)));
        }
    }

    private void requestDelivery(Long deliveryId, final Long selectedKitId, HasWidgets container) {
        deliveryService.getDelivery(deliveryId, new FluentCallback<Delivery>()
                .withError((throwable, defaultErrorHandler, status) -> defaultErrorHandler.accept(throwable))
                .withSuccess(delivery -> {
                    this.delivery = delivery;
                    kitId = delivery.getKits().stream().anyMatch(kit -> Objects.equals(kit.getId(), selectedKitId)) ? selectedKitId : null;
                    addCreatingKitsButton(delivery.getProject());
                    fillKits(delivery.getKits(), kitId);
                    fillModules(kitId);
                    attachToContainer(container);
                })
        );
    }

    private void addCreatingKitsButton(Project project) {
        fireEvent(new ActionBarEvents.Clear());
        if (hasCreatePrivileges() && isMilitaryProject(project)){
            fireEvent(new ActionBarEvents.Add(CREATE_ACTION, null, UiConstants.ActionBarIdentity.KIT));
        }
    }

    private void fillKits(List<Kit> kits, Long selectedKitId) {
        view.setKitsActionsEnabled(hasEditPrivileges());
        view.fillKits(kits);
        view.makeKitSelected(selectedKitId);
    }

    private void fillModules(Long selectedKitId) {
        view.getModuleEditContainer().clear();
        view.setModuleNotSelectedMessageVisible(true);
        view.modulesContainerVisibility().setVisible(true);

        moduleView.clearModules();
        moduleView.clearSelectedRows();
        moduleView.setDeleteEnabled(isDeleteEnabled());

        if (selectedKitId == null) {
            view.setKitNotSelectedMessageVisible(true);
            return;
        }

        moduleService.getModulesByKitId(selectedKitId, new FluentCallback<Map<Module, List<Module>>>()
                .withError((throwable, defaultErrorHandler, status) -> defaultErrorHandler.accept(throwable))
                .withSuccess(modules -> {
                    view.setKitNotSelectedMessageVisible(false);
                    moduleView.fillTable(modules);
                })
        );
    }

    private void attachToContainer(HasWidgets container) {
        container.add(view.asWidget());
    }

    private boolean isMilitaryProject(Project project) {
        if (project == null) {
            return false;
        }
        return project.getCustomerType() == En_CustomerType.MINISTRY_OF_DEFENCE;
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
        return policyService.hasPrivilegeFor(En_Privilege.DELIVERY_CREATE);
    }

    private Runnable removeModuleAction(AbstractModuleTableView modulesTableView) {
        Set<Long> modulesToRemoveIds = modulesTableView.getSelectedModules().stream().map(Module::getId)
                                                       .collect(Collectors.toSet());

        return () -> moduleService.removeModules(kitId, modulesToRemoveIds, new FluentCallback<Set<Long>>()
                .withError(throwable -> fireEvent(new NotifyEvents.Show(throwable.getMessage(),
                                                      NotifyEvents.NotifyType.ERROR)))
                .withSuccess(result -> {
                    fireEvent(new NotifyEvents.Show(lang.modulesRemoved(), NotifyEvents.NotifyType.SUCCESS));
                    fillModules(kitId);
                }));
    }

    private boolean isDeleteEnabled() {
        return hasRemovePrivileges() && moduleView.hasSelectedModules();
    }

    private boolean isOneKitSelected(Set<Kit> kitsSelected) {
        return kitsSelected != null && kitsSelected.size() == 1;
    }

    private void updateKit(final Kit newKit) {
        delivery.getKits().stream()
                .filter(kit -> Objects.equals(kit, newKit))
                .findFirst()
                .ifPresent(kit -> {
                    kit.setState(newKit.getState());
                    kit.setName(newKit.getName());
                    view.updateKit(kit);
                });
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
        public void onGroupChangeState(CaseState state) {

            List<Long> kitsIds = toList(view.getKitsSelected(), Kit::getId);

            if (isEmpty(kitsIds)){
                fireEvent(new NotifyEvents.Show(lang.kitNotSelectedMessage(), NotifyEvents.NotifyType.ERROR));
                return;
            }

            deliveryService.updateKitListStates(kitsIds, state.getId(), new FluentCallback<Void>()
                    .withSuccess((Void) -> deliveryService.getDelivery(delivery.getId(),
                            new FluentCallback<Delivery>()
                                    .withSuccess(delivery -> {
                                        view.fillKits(delivery.getKits());
                                        fireEvent(new NotifyEvents.Show(lang.kitsStatesUpdated(), NotifyEvents.NotifyType.SUCCESS));
                                    }))));
        }

        @Override
        public void onGroupRemove() {
            Set<Kit> kitsSelected = view.getKitsSelected();

            if (isEmpty(kitsSelected)){
                fireEvent(new NotifyEvents.Show(lang.kitNotSelectedMessage(), NotifyEvents.NotifyType.ERROR));
                return;
            }

            String selectedNumbers = stream(kitsSelected).map(Kit::getSerialNumber).collect(Collectors.joining(","));
            fireEvent(new NotifyEvents.Show("On remove Kits clicked, selected kits: " + selectedNumbers, NotifyEvents.NotifyType.SUCCESS));
        }

        @Override
        public void onBack() {
            if (delivery == null) {
                return;
            }
            fireEvent(new DeliveryEvents.ShowFullScreen(delivery.getId()));
        }

        @Override
        public void onEdit() {
            if (isOneKitSelected(view.getKitsSelected())) {
                Long selectedKitId = stream(view.getKitsSelected()).findFirst().map(Kit::getId).get();
                fireEvent(new KitEvents.Edit(selectedKitId)
                        .withBackHandler(kit -> {
                            if (delivery == null || !Objects.equals(delivery.getId(), kit.getDeliveryId())) {
                                return;
                            }
                            updateKit(kit);
                        }));
                return;
            }
            fireEvent(new NotifyEvents.Show(lang.warnOneKitAllowedForTheOperation(), NotifyEvents.NotifyType.ERROR));
        }
    };

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

    @ContextAware
    Delivery delivery;
    @ContextAware
    Long kitId;

    private static String CREATE_ACTION;
    private AppEvents.InitDetails initDetails;
}
