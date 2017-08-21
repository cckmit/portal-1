package ru.protei.portal.ui.decision.client.factory;

import com.google.gwt.inject.client.AbstractGinModule;
import ru.protei.portal.ui.decision.client.activity.page.DecisionPage;

/**
 * Описание классов фабрики
 */
public class DecisionClientModule extends AbstractGinModule{
    @Override
    protected void configure() {
        bind(DecisionPage.class).asEagerSingleton();
    }
}
