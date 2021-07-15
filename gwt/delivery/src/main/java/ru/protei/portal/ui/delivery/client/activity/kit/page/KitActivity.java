package ru.protei.portal.ui.delivery.client.activity.kit.page;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.ent.Delivery;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.KitEvents;
import ru.protei.portal.ui.common.client.service.DeliveryControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

public abstract class KitActivity implements Activity, AbstractKitActivity {

    @Inject
    public void onInit() {
        view.setActivity(this);
        moduleView.setActivity(this);
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails initDetails) {
        this.initDetails = initDetails;
    }

    @Event
    public void onShow(KitEvents.Show event) {
        deliveryService.getDelivery(event.deliveryId, new FluentCallback<Delivery>()
                .withError((throwable, defaultErrorHandler, status) -> defaultErrorHandler.accept(throwable))
                .withSuccess(delivery -> {
                            initDetails.parent.clear();
                            initDetails.parent.add(view.asWidget());
                            view.fillKits(delivery.getKits());
                        }
                )
        );
    }

    @Override
    public void onKitClicked(Long kitId) {

    }

    @Inject
    AbstractKitView view;
    @Inject
    AbstractModuleView moduleView;

    @Inject
    private DeliveryControllerAsync deliveryService;

    private AppEvents.InitDetails initDetails;
}
