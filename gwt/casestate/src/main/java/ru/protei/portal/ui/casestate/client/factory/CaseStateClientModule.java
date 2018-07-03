package ru.protei.portal.ui.casestate.client.factory;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;
import ru.protei.portal.ui.casestate.client.activity.page.CaseStatePage;
import ru.protei.portal.ui.casestate.client.activity.preview.AbstractCaseStatePreviewView;
import ru.protei.portal.ui.casestate.client.activity.preview.CaseStatePreviewActivity;
import ru.protei.portal.ui.casestate.client.activity.table.AbstractCaseStateTableView;
import ru.protei.portal.ui.casestate.client.activity.table.CaseStateTableActivity;
import ru.protei.portal.ui.casestate.client.view.table.CaseStateTableView;
import ru.protei.portal.ui.casestate.client.view.preview.CaseStatePreviewView;


/**
 * Описание классов фабрики
 */
public class CaseStateClientModule extends AbstractGinModule {
    @Override
    protected void configure() {
        bind( CaseStatePage.class ).asEagerSingleton();

        bind(CaseStateTableActivity.class).asEagerSingleton();
        bind(CaseStatePreviewActivity.class).asEagerSingleton();
        bind(AbstractCaseStateTableView.class).to(CaseStateTableView.class).in(Singleton.class);
        bind(AbstractCaseStatePreviewView.class).to(CaseStatePreviewView.class).in(Singleton.class);
    }
}

