package ru.protei.portal.ui.common.client.activity.forbidden;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.ForbiddenEvents;
import ru.protei.portal.ui.common.client.lang.Lang;

public abstract class ForbiddenPageActivity implements Activity, AbstractForbiddenPageActivity {
    @Event
    public void onInitDetails( AppEvents.InitDetails initDetails ) {
        this.initDetails = initDetails;
    }

    @Event
    public void onShow(ForbiddenEvents.Show event) {

        HasWidgets container = event.container;
        if (container == null) {
            container = initDetails.parent;
        }

        container.clear();
        container.add(view.asWidget());

        fillView(event.msg);
    }

    private void fillView(String msg) {
        view.label().setText(msg == null ? lang.errAccessDenied() : msg);
    }

    @Inject
    AbstractForbiddenPageView view;

    @Inject
    Lang lang;

    private AppEvents.InitDetails initDetails;
}
