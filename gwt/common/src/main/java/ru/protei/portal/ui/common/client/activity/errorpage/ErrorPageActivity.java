package ru.protei.portal.ui.common.client.activity.errorpage;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.ErrorPageEvents;
import ru.protei.portal.ui.common.client.lang.Lang;

public abstract class ErrorPageActivity implements Activity, AbstractErrorPageActivity {
    @Event
    public void onInitDetails( AppEvents.InitDetails initDetails ) {
        this.initDetails = initDetails;
    }

    @Event
    public void onShow(ErrorPageEvents.ShowForbidden event) {
        prepareContainer(event.container, initDetails.parent, view);

        fillView(event.msg == null ? lang.errAccessDenied() : event.msg);
    }

    @Event
    public void onShow(ErrorPageEvents.ShowNotFound event) {
        prepareContainer(event.container, initDetails.parent, view);

        fillView(event.msg == null ? lang.errNotFound() : event.msg);
    }

    private void prepareContainer(HasWidgets receivedContainer, HasWidgets defaultContainer, AbstractErrorPageView view) {
        if (receivedContainer != null) {
            receivedContainer.clear();
            receivedContainer.add(view.asWidget());
            return;
        }

        defaultContainer.clear();
        defaultContainer.add(view.asWidget());
    }

    private void fillView(String msg) {
        view.label().setText(msg);
    }

    @Inject
    AbstractErrorPageView view;

    @Inject
    Lang lang;

    private AppEvents.InitDetails initDetails;
}
