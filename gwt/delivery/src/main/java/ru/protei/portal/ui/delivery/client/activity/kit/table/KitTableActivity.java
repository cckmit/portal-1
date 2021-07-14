package ru.protei.portal.ui.delivery.client.activity.kit.table;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.KitEvents;

public abstract class KitTableActivity implements Activity, AbstractKitTableActivity {

    @Inject
    public void onInit() {
        view.setActivity(this);
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails initDetails) {
        this.initDetails = initDetails;
    }

    @Event
    public void onShow(KitEvents.Show event) {
        initDetails.parent.clear();
        initDetails.parent.add(view.asWidget());
    }

    @Inject
    AbstractKitTableView view;

    private AppEvents.InitDetails initDetails;
}
