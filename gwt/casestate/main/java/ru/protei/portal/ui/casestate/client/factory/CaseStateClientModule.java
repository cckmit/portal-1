package ru.protei.portal.ui.casestate.client.factory;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;
import ru.protei.portal.ui.casestate.client.activity.page.CaseStatePage;
import ru.protei.portal.ui.casestate.client.activity.table.AbstractCaseStateTableView;
import ru.protei.portal.ui.casestate.client.activity.table.CaseStateTableActivity;
import ru.protei.portal.ui.casestate.client.view.CaseStateTableView;


/**
 * Описание классов фабрики
 */
public class CaseStateClientModule extends AbstractGinModule {
    @Override
    protected void configure() {
        bind( CaseStatePage.class ).asEagerSingleton();

        bind(CaseStateTableActivity.class).asEagerSingleton();
        bind(AbstractCaseStateTableView.class).to(CaseStateTableView.class).in(Singleton.class);
    }
}

