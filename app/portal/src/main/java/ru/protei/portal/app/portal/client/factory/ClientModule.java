package ru.protei.portal.app.portal.client.factory;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;
import ru.protei.portal.app.portal.client.activity.app.AbstractAppView;
import ru.protei.portal.app.portal.client.activity.app.AppActivity;
import ru.protei.portal.app.portal.client.activity.auth.AbstractAuthView;
import ru.protei.portal.app.portal.client.activity.auth.AuthActivity;
import ru.protei.portal.app.portal.client.activity.dashboard.AbstractDashboardView;
import ru.protei.portal.app.portal.client.activity.dashboard.DashboardActivity;
import ru.protei.portal.app.portal.client.activity.dashboardblocks.edit.AbstractDashboardTableEditView;
import ru.protei.portal.app.portal.client.activity.dashboardblocks.edit.DashboardTableEditActivity;
import ru.protei.portal.app.portal.client.activity.dashboardblocks.table.AbstractDashboardTableView;
import ru.protei.portal.app.portal.client.activity.page.DashboardPage;
import ru.protei.portal.app.portal.client.activity.profile.AbstractProfilePageView;
import ru.protei.portal.app.portal.client.activity.profile.ProfilePageActivity;
import ru.protei.portal.app.portal.client.view.app.AppView;
import ru.protei.portal.app.portal.client.view.auth.AuthView;
import ru.protei.portal.app.portal.client.view.dashboard.DashboardView;
import ru.protei.portal.app.portal.client.view.dashboardblocks.edit.DashboardTableEditView;
import ru.protei.portal.app.portal.client.view.dashboardblocks.table.DashboardTableView;
import ru.protei.portal.app.portal.client.view.profile.ProfilePageView;

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
        bind(AbstractDashboardTableView.class).to(DashboardTableView.class);
        bind(DashboardTableEditActivity.class).asEagerSingleton();
        bind(AbstractDashboardTableEditView.class).to(DashboardTableEditView.class);

        bind( ProfilePageActivity.class ).asEagerSingleton();
        bind( AbstractProfilePageView.class ).to( ProfilePageView.class ).in( Singleton.class );
    }
}
