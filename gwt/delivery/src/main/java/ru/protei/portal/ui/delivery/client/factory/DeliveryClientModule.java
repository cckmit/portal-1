package ru.protei.portal.ui.delivery.client.factory;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;
import ru.protei.portal.ui.common.client.activity.deliveryfilter.AbstractDeliveryFilterView;
import ru.protei.portal.ui.common.client.view.deliveryfilter.DeliveryFilterView;
import ru.protei.portal.ui.delivery.client.activity.create.AbstractDeliveryCreateView;
import ru.protei.portal.ui.delivery.client.activity.create.DeliveryCreateActivity;
import ru.protei.portal.ui.delivery.client.activity.page.DeliveryPage;
import ru.protei.portal.ui.delivery.client.activity.table.AbstractDeliveryTableView;
import ru.protei.portal.ui.delivery.client.activity.table.DeliveryTableActivity;
import ru.protei.portal.ui.delivery.client.view.create.DeliveryCreateView;
import ru.protei.portal.ui.delivery.client.view.table.DeliveryTableView;

public class DeliveryClientModule extends AbstractGinModule {
    @Override
    protected void configure()    {
        bind(DeliveryPage.class).asEagerSingleton();

        bind(DeliveryTableActivity.class).asEagerSingleton();
        bind(AbstractDeliveryTableView.class).to(DeliveryTableView.class).in(Singleton.class);

        bind(AbstractDeliveryFilterView.class).to(DeliveryFilterView.class);

        bind(DeliveryCreateActivity.class).asEagerSingleton();
        bind(AbstractDeliveryCreateView.class).to(DeliveryCreateView.class).in(Singleton.class);
    }
}

