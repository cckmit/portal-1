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
import ru.protei.portal.app.portal.client.activity.dashboardblocks.table.AbstractDashboardIssueTableView;
import ru.protei.portal.app.portal.client.activity.dashboardblocks.table.AbstractDashboardProjectTableView;
import ru.protei.portal.app.portal.client.activity.page.DashboardPage;
import ru.protei.portal.app.portal.client.activity.profile.AbstractProfilePageView;
import ru.protei.portal.app.portal.client.activity.profile.ProfilePageActivity;
import ru.protei.portal.app.portal.client.activity.profile.general.AbstractProfileGeneralView;
import ru.protei.portal.app.portal.client.activity.profile.general.ProfileGeneralActivity;
import ru.protei.portal.app.portal.client.activity.profile.general.changepassword.AbstractChangePasswordView;
import ru.protei.portal.app.portal.client.activity.profile.subscription.AbstractProfileSubscriptionView;
import ru.protei.portal.app.portal.client.activity.profile.subscription.ProfileSubscriptionActivity;
import ru.protei.portal.app.portal.client.view.app.AppView;
import ru.protei.portal.app.portal.client.view.auth.AuthView;
import ru.protei.portal.app.portal.client.view.dashboard.DashboardView;
import ru.protei.portal.app.portal.client.view.dashboardblocks.edit.DashboardTableEditView;
import ru.protei.portal.app.portal.client.view.dashboardblocks.table.issue.DashboardIssueTableView;
import ru.protei.portal.app.portal.client.view.dashboardblocks.table.project.DashboardProjectTableView;
import ru.protei.portal.app.portal.client.view.profile.ProfilePageView;
import ru.protei.portal.app.portal.client.view.profile.general.ProfileGeneralView;
import ru.protei.portal.app.portal.client.view.profile.general.changepassword.ChangePasswordView;
import ru.protei.portal.app.portal.client.view.profile.subscription.ProfileSubscriptionView;

/**
 * Клиентский модуль
 */
public class ClientModule extends AbstractGinModule {

    @Override
    protected void configure() {
        bind(DashboardPage.class).asEagerSingleton();
        bind(AppActivity.class).asEagerSingleton();
        bind(AbstractAppView.class).to(AppView.class).in(Singleton.class);

        bind(AuthActivity.class).asEagerSingleton();
        bind(AbstractAuthView.class).to(AuthView.class).in(Singleton.class);

        bind(DashboardActivity.class).asEagerSingleton();
        bind(AbstractDashboardView.class).to(DashboardView.class).in(Singleton.class);
        bind(AbstractDashboardIssueTableView.class).to(DashboardIssueTableView.class);
        bind(AbstractDashboardProjectTableView.class).to(DashboardProjectTableView.class);
        bind(DashboardTableEditActivity.class).asEagerSingleton();
        bind(AbstractDashboardTableEditView.class).to(DashboardTableEditView.class);

        bind(ProfilePageActivity.class).asEagerSingleton();
        bind(AbstractProfilePageView.class).to(ProfilePageView.class).in(Singleton.class);

        bind(ProfileGeneralActivity.class).asEagerSingleton();
        bind(AbstractProfileGeneralView.class).to(ProfileGeneralView.class).in(Singleton.class);
        bind(AbstractChangePasswordView.class).to(ChangePasswordView.class).in(Singleton.class);

        bind(ProfileSubscriptionActivity.class).asEagerSingleton();
        bind(AbstractProfileSubscriptionView.class).to(ProfileSubscriptionView.class).in(Singleton.class);
    }
}
