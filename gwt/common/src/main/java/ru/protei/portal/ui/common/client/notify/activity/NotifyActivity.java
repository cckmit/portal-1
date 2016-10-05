package ru.protei.portal.ui.common.client.notify.activity;

import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.ui.common.client.animation.NotifyAnimation;
import ru.protei.portal.ui.common.client.events.NotifyEvents;

/**
 * Активность уведомлений
 */
public abstract class NotifyActivity
        implements Activity, AbstractNotifyActivity {

    @Event
    public void onInit(NotifyEvents.Init event) {
        event.parent.clear();
        animation.setWrapper(event.parent);
    }

    @Event
    public void onShow( NotifyEvents.Show event ) {

        AbstractNotifyView view = provider.get();
        view.setActivity(this);

        if (event.type == null)
            view.setType( "default" );
        else
            view.setType( event.type );

        view.setTitle( event.title );
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
}
