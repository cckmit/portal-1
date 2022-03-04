package ru.protei.portal.ui.web.client.factory;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;
import ru.protei.portal.ui.web.client.activity.TypescriptWebActivity;
import ru.protei.portal.ui.web.client.integration.NativeWebIntegration;
import ru.protei.portal.ui.web.client.integration.NativeWebIntegrationImpl;
import ru.protei.portal.ui.web.client.page.Test1Page;
import ru.protei.portal.ui.web.client.view.TypescriptWebView;

public class TypescriptWebModule extends AbstractGinModule {
    @Override
    protected void configure() {
        bind(TypescriptWebActivity.class).asEagerSingleton();
        bind(TypescriptWebView.class);
        bind(NativeWebIntegration.class).to(NativeWebIntegrationImpl.class).in(Singleton.class);

        bind(Test1Page.class).asEagerSingleton();
    }
}
