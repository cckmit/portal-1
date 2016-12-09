package ru.protei.portal.ui.crm.client.factory;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;
import ru.protei.portal.ui.crm.client.activity.app.AbstractAppView;
import ru.protei.portal.ui.crm.client.activity.app.AppActivity;
import ru.protei.portal.ui.crm.client.activity.auth.AbstractAuthView;
import ru.protei.portal.ui.crm.client.activity.auth.AuthActivity;
import ru.protei.portal.ui.crm.client.activity.dashboard.AbstractDashboardView;
import ru.protei.portal.ui.crm.client.activity.dashboard.DashboardActivity;
import ru.protei.portal.ui.crm.client.activity.dashboardblocks.table.AbstractDashboardTableView;
import ru.protei.portal.ui.crm.client.activity.dashboardblocks.table.DashboardTableActivity;
import ru.protei.portal.ui.crm.client.activity.page.DashboardPage;
import ru.protei.portal.ui.crm.client.view.app.AppView;
import ru.protei.portal.ui.crm.client.view.auth.AuthView;
import ru.protei.portal.ui.crm.client.view.dashboard.DashboardView;
import ru.protei.portal.ui.crm.client.view.dashboardblocks.table.DashboardTableView;

/**
 * Клиентский модуль
 */
public class ClientModule extends AbstractGinModule {

    @Override
    protected void configure() {
        bind( DashboardPage.class ).asEagerSingleton();
        bind(AppActivity.class).asEagerSingleton();
        bind( AbstractAppView.class ).to(AppView.class).in(Singleton.class);

        bind(AuthActivity.class).asEagerSingleton();
        bind( AbstractAuthView.class ).to(AuthView.class).in(Singleton.class);

        bind(DashboardActivity.class).asEagerSingleton();
        bind( AbstractDashboardView.class ).to(DashboardView.class).in(Singleton.class);

        bind(DashboardTableActivity.class).asEagerSingleton();
        bind(AbstractDashboardTableView.class).to(DashboardTableView.class);
    }
}
