package ru.protei.portal.ui.decision.client.factory;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;
import ru.protei.portal.ui.decision.client.activity.page.OfficialPage;
import ru.protei.portal.ui.decision.client.activity.table.AbstractOfficialTableView;
import ru.protei.portal.ui.decision.client.activity.table.OfficialTableActivity;
import ru.protei.portal.ui.decision.client.view.table.OfficialTableView;

/**
 * Описание классов фабрики
 */
public class OfficialClientModule extends AbstractGinModule{
    @Override
    protected void configure() {
        bind(OfficialPage.class).asEagerSingleton();

        bind ( OfficialTableActivity.class ).asEagerSingleton();
        bind ( AbstractOfficialTableView.class ).to( OfficialTableView.class ).in( Singleton.class );

    }
}
