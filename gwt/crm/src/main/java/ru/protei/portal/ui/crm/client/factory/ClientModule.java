package ru.protei.portal.ui.crm.client.factory;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;
import ru.protei.portal.ui.crm.client.activity.app.AbstractAppView;
import ru.protei.portal.ui.crm.client.activity.app.AppActivity;
import ru.protei.portal.ui.crm.client.view.app.AppView;

/**
 * Клиентский модуль
 */
public class ClientModule extends AbstractGinModule {

    @Override
    protected void configure() {
        bind( AppActivity.class ).asEagerSingleton();
        bind( AbstractAppView.class ).to( AppView.class ).in( Singleton.class );
    }
}
