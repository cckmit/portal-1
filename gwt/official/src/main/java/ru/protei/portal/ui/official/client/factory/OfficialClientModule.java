package ru.protei.portal.ui.official.client.factory;

import com.google.gwt.inject.client.AbstractGinModule;
import ru.protei.portal.ui.official.client.activity.page.OfficialPage;

/**
 * Описание классов фабрики
 */
public class OfficialClientModule extends AbstractGinModule{
    @Override
    protected void configure() {
        bind(OfficialPage.class).asEagerSingleton();
    }
}
