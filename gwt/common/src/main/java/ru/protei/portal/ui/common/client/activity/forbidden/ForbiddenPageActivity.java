package ru.protei.portal.ui.common.client.activity.forbidden;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.ForbiddenEvents;

public abstract class ForbiddenPageActivity implements Activity, AbstractForbiddenPageActivity {
    @Event
    public void onInitDetails( AppEvents.InitDetails initDetails ) {
        this.initDetails = initDetails;
    }

    @Event
    public void onShow(ForbiddenEvents.Show event) {
        initDetails.parent.clear();
        initDetails.parent.add(view.asWidget());
    }

    @Inject
    AbstractForbiddenPageView view;

    private AppEvents.InitDetails initDetails;
}
