package ru.protei.portal.ui.plan.client.factory;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;
import ru.protei.portal.ui.plan.client.activity.table.AbstractPlanTableView;
import ru.protei.portal.ui.plan.client.activity.table.PlanTableActivity;
import ru.protei.portal.ui.plan.client.page.PlanPage;
import ru.protei.portal.ui.plan.client.view.table.PlanTableView;

public class PlanClientModule extends AbstractGinModule {

    @Override
    protected void configure() {
        bind( PlanPage.class ).asEagerSingleton();

        bind( PlanTableActivity.class ).asEagerSingleton ();
        bind( AbstractPlanTableView.class ).to(PlanTableView.class).in(Singleton.class);

    }
}
