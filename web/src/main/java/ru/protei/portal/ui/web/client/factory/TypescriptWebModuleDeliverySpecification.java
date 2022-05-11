package ru.protei.portal.ui.web.client.factory;

import com.google.gwt.inject.client.AbstractGinModule;
import ru.protei.portal.ui.web.client.activity.DeliverySpecificationActivity;
import ru.protei.portal.ui.web.client.page.DeliverySpecificationPage;

public class TypescriptWebModuleDeliverySpecification extends AbstractGinModule {
    @Override
    protected void configure() {
        bind(DeliverySpecificationPage.class).asEagerSingleton();
        bind(DeliverySpecificationActivity.class).asEagerSingleton();
    }
}
