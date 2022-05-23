package ru.protei.portal.ui.webts.client.factory;

import com.google.gwt.inject.client.AbstractGinModule;
import ru.protei.portal.ui.webts.client.activity.DeliverySpecificationActivity;
import ru.protei.portal.ui.webts.client.page.DeliverySpecificationPage;

public class TypescriptWebModuleDeliverySpecification extends AbstractGinModule {
    @Override
    protected void configure() {
        bind(DeliverySpecificationPage.class).asEagerSingleton();
        bind(DeliverySpecificationActivity.class).asEagerSingleton();
    }
}
