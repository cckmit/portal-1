package ru.protei.portal.ui.plan.client.factory;

import com.google.gwt.inject.client.AbstractGinModule;
import ru.protei.portal.ui.plan.client.page.PlanPage;

public class PlanClientModule extends AbstractGinModule {

    @Override
    protected void configure() {
        bind( PlanPage.class ).asEagerSingleton();
    }
}
