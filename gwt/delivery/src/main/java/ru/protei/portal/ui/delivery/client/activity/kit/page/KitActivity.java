package ru.protei.portal.ui.delivery.client.activity.kit.page;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.ent.Delivery;
import ru.protei.portal.core.model.ent.Module;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.KitEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.service.DeliveryControllerAsync;
import ru.protei.portal.ui.common.client.service.ModuleControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.List;
import java.util.Map;

public abstract class KitActivity implements Activity, AbstractKitActivity {

    @Inject
    public void onInit() {
        view.setActivity(this);
        moduleView.setActivity(this);
        view.getModulesContainer().add(moduleView.asWidget());
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails initDetails) {
        this.initDetails = initDetails;
    }

    @Event
    public void onShow(KitEvents.Show event) {
        moduleView.clearModules();
        deliveryService.getDelivery(event.deliveryId, new FluentCallback<Delivery>()
                .withError((throwable, defaultErrorHandler, status) -> defaultErrorHandler.accept(throwable))
                .withSuccess(delivery -> {
                            initDetails.parent.clear();
                            initDetails.parent.add(view.asWidget());
                            view.fillKits(delivery.getKits());
                            if (event.kitId != null) {
                                fillModules(event.kitId);
                            }
                        }
                )
        );
    }

    @Override
    public void onKitClicked(Long kitId) {
        moduleView.clearModules();
        fillModules(kitId);
    }

    @Override
    public void onItemClicked(Module module) {
        fireEvent(new NotifyEvents.Show("Module name edit clicked: " + module.getSerialNumber() + " " + module.getDescription(),
                NotifyEvents.NotifyType.SUCCESS));
    }

    private void fillModules(Long kitId) {
        moduleView.clearSelectedRows();
        moduleService.getModulesByKitId(kitId, new FluentCallback<Map<Module, List<Module>>>()
                .withError((throwable, defaultErrorHandler, status) -> defaultErrorHandler.accept(throwable))
                .withSuccess(modules -> {
                    moduleView.fillTable(modules);
                })
        );
    }

    @Inject
    AbstractKitView view;
    @Inject
    AbstractModuleTableView moduleView;

    @Inject
    private DeliveryControllerAsync deliveryService;
    @Inject
    private ModuleControllerAsync moduleService;
    
    private AppEvents.InitDetails initDetails;
}
