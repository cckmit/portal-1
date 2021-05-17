package ru.protei.portal.ui.delivery.client.factory;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;
import ru.protei.portal.ui.common.client.activity.deliveryfilter.AbstractDeliveryCollapseFilterView;
import ru.protei.portal.ui.common.client.view.deliveryfilter.DeliveryFilterCollapseView;
import ru.protei.portal.ui.delivery.client.activity.create.AbstractDeliveryCreateView;
import ru.protei.portal.ui.delivery.client.activity.create.DeliveryCreateActivity;
import ru.protei.portal.ui.delivery.client.activity.edit.AbstractDeliveryEditView;
import ru.protei.portal.ui.delivery.client.activity.edit.DeliveryEditActivity;
import ru.protei.portal.ui.delivery.client.activity.meta.AbstractDeliveryMetaView;
import ru.protei.portal.ui.delivery.client.activity.meta.DeliveryMetaActivity;
import ru.protei.portal.ui.delivery.client.activity.page.DeliveryPage;
import ru.protei.portal.ui.delivery.client.activity.table.AbstractDeliveryTableView;
import ru.protei.portal.ui.delivery.client.activity.table.DeliveryTableActivity;
import ru.protei.portal.ui.delivery.client.view.create.DeliveryCreateView;
import ru.protei.portal.ui.delivery.client.view.edit.DeliveryEditView;
import ru.protei.portal.ui.delivery.client.view.meta.DeliveryMetaView;
import ru.protei.portal.ui.delivery.client.view.table.DeliveryTableView;
import ru.protei.portal.ui.delivery.client.widget.kit.activity.AbstractDeliveryKitListActivity;
import ru.protei.portal.ui.delivery.client.widget.kit.activity.DeliveryKitListActivity;

public class DeliveryClientModule extends AbstractGinModule {
    @Override
    protected void configure()    {
        bind(DeliveryPage.class).asEagerSingleton();

        bind(DeliveryTableActivity.class).asEagerSingleton();
        bind(AbstractDeliveryTableView.class).to(DeliveryTableView.class).in(Singleton.class);

        bind(DeliveryCreateActivity.class).asEagerSingleton();
        bind(AbstractDeliveryCreateView.class).to(DeliveryCreateView.class).in(Singleton.class);

        bind(DeliveryEditActivity.class).asEagerSingleton();
        bind(DeliveryMetaActivity.class).asEagerSingleton();

        bind(AbstractDeliveryEditView.class).to(DeliveryEditView.class).in(Singleton.class);
        bind(AbstractDeliveryMetaView.class).to(DeliveryMetaView.class).in(Singleton.class);

        bind(AbstractDeliveryKitListActivity.class).to(DeliveryKitListActivity.class).asEagerSingleton();

        bind( AbstractDeliveryCollapseFilterView.class ).to(DeliveryFilterCollapseView.class).in(Singleton.class);
    }
}

