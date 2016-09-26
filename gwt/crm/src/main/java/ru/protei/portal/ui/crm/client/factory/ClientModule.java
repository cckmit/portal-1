package ru.protei.portal.ui.crm.client.factory;

import com.google.gwt.inject.client.AbstractGinModule;
import ru.protei.portal.ui.crm.client.activity.auth.AbstractAuthView;
import ru.protei.portal.ui.crm.client.activity.auth.AuthActivity;
import ru.protei.portal.ui.crm.client.view.auth.AuthView;

/**
 * Клиентский модуль
 */
public class ClientModule extends AbstractGinModule {

    @Override
    protected void configure() {
        bind (AuthActivity.class ).asEagerSingleton ();
        bind(AbstractAuthView.class).to (AuthView.class);
    }
}
