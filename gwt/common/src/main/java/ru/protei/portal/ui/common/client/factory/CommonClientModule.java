package ru.protei.portal.ui.common.client.factory;

import com.google.gwt.inject.client.AbstractGinModule;
import ru.protei.portal.ui.common.client.notify.activity.AbstractNotifyView;
import ru.protei.portal.ui.common.client.notify.activity.NotifyActivity;
import ru.protei.portal.ui.common.client.notify.view.NotifyView;

/**
 * Описание классов фабрики
 */
public class CommonClientModule extends AbstractGinModule {
    @Override
    protected void configure()    {

        bind( NotifyActivity.class ).asEagerSingleton();
        bind( AbstractNotifyView.class ).to( NotifyView.class );

    }
}

