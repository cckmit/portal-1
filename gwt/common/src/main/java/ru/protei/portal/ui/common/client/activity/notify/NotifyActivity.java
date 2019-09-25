package ru.protei.portal.ui.common.client.activity.notify;

import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.ui.common.client.animation.NotifyAnimation;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;

/**
 * Активность уведомлений
 */
public abstract class NotifyActivity
        implements Activity, AbstractNotifyActivity {

    @Event
    public void onInit(NotifyEvents.Init event) {
        this.init = event;
        animation.setWrapper(event.parent);
    }

    @Event
    public void onAuthSuccess( AuthEvents.Success event ) {
        init.parent.clear();
    }

    @Event
    public void onShow( NotifyEvents.Show event ) {
        AbstractNotifyView view = provider.get();
        view.setActivity(this);

        view.setType( event.type );
        view.setMessage( event.message );

        animation.show(view);
    }

    @Override
    public void onCloseClicked( AbstractNotifyView view ) {
        animation.close(view.asWidget());
    }

    @Inject
    NotifyAnimation animation;
    @Inject
    Provider<AbstractNotifyView > provider;

    private NotifyEvents.Init init;
}
