package ru.protei.portal.ui.common.client.activity.info;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.InfoEvents;

public abstract class InfoActivity implements AbstractInfoActivity, Activity {
    @Event
    public void onInit(AppEvents.InitDetails init) {
        this.init = init;
    }

    @Event
    public void onShow(InfoEvents.Show event) {
        init.parent.clear();
        init.parent.add(view.asWidget());

        fillView();
    }

    private void fillView() {

    }

    @Inject
    AbstractInfoView view;

    private AppEvents.InitDetails init;
}
