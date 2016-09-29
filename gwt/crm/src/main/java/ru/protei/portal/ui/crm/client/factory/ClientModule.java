package ru.protei.portal.ui.crm.client.factory;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;
import ru.protei.portal.ui.crm.client.activity.app.AbstractAppView;
import ru.protei.portal.ui.crm.client.activity.app.AppActivity;
import ru.protei.portal.ui.crm.client.view.app.AppView;
import ru.protei.portal.ui.crm.client.activity.auth.AbstractAuthView;
import ru.protei.portal.ui.crm.client.activity.auth.AuthActivity;
import ru.protei.portal.ui.crm.client.view.auth.AuthView;
import ru.protei.portal.ui.crm.client.activity.company.AbstractCompanyView;
import ru.protei.portal.ui.crm.client.activity.company.CompanyActivity;
import ru.protei.portal.ui.crm.client.view.company.CompanyView;

/**
 * Клиентский модуль
 */
public class ClientModule extends AbstractGinModule {

    @Override
    protected void configure() {
        bind( AppActivity.class ).asEagerSingleton();
        bind( AbstractAppView.class ).to( AppView.class ).in( Singleton.class );

        bind( AuthActivity.class ).asEagerSingleton ();
        bind( AbstractAuthView.class ).to( AuthView.class ).in( Singleton.class );

        bind( CompanyActivity.class ).asEagerSingleton ();
        bind( AbstractCompanyView.class ).to( CompanyView.class ).in( Singleton.class );
    }
}
