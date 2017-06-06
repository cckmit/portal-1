package ru.protei.portal.ui.account.client.factory;

import com.google.gwt.inject.client.AbstractGinModule;
import ru.protei.portal.ui.account.client.activity.filter.AbstractAccountFilterView;
import ru.protei.portal.ui.account.client.activity.page.AccountPage;
import ru.protei.portal.ui.account.client.activity.table.AbstractAccountTableView;
import ru.protei.portal.ui.account.client.activity.table.AccountTableActivity;
import ru.protei.portal.ui.account.client.view.filter.AccountFilterView;
import ru.protei.portal.ui.account.client.view.table.AccountTableView;

/**
 * Описание классов фабрики
 */
public class AccountClientModule extends AbstractGinModule {
    @Override
    protected void configure() {
        bind( AccountPage.class ).asEagerSingleton();

        bind( AccountTableActivity.class ).asEagerSingleton();
        bind( AbstractAccountTableView.class ).to( AccountTableView.class );

        bind( AbstractAccountFilterView.class ).to( AccountFilterView.class );
    }
}
