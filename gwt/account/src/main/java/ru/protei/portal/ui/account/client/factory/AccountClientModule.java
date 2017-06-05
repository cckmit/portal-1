package ru.protei.portal.ui.account.client.factory;

import com.google.gwt.inject.client.AbstractGinModule;
import ru.protei.portal.ui.account.client.activity.page.AccountPage;

/**
 * Описание классов фабрики
 */
public class AccountClientModule extends AbstractGinModule {
    @Override
    protected void configure() {
        bind( AccountPage.class ).asEagerSingleton();
    }
}
