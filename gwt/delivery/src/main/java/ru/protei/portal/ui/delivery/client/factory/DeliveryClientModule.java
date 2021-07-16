package ru.protei.portal.ui.delivery.client.factory;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;
import ru.protei.portal.ui.delivery.client.activity.create.AbstractDeliveryCreateView;
import ru.protei.portal.ui.delivery.client.activity.create.DeliveryCreateActivity;
import ru.protei.portal.ui.delivery.client.activity.edit.*;
import ru.protei.portal.ui.delivery.client.activity.kit.add.AbstractDeliveryKitAddView;
import ru.protei.portal.ui.delivery.client.activity.kit.add.DeliveryKitAddActivity;
import ru.protei.portal.ui.delivery.client.activity.kit.page.AbstractKitView;
import ru.protei.portal.ui.delivery.client.activity.kit.page.AbstractModuleView;
import ru.protei.portal.ui.delivery.client.activity.kit.page.KitActivity;
import ru.protei.portal.ui.delivery.client.activity.meta.AbstractDeliveryMetaView;
import ru.protei.portal.ui.delivery.client.activity.meta.DeliveryMetaActivity;
import ru.protei.portal.ui.delivery.client.activity.page.DeliveryPage;
import ru.protei.portal.ui.delivery.client.activity.table.AbstractDeliveryTableView;
import ru.protei.portal.ui.delivery.client.activity.table.DeliveryTableActivity;
import ru.protei.portal.ui.delivery.client.view.create.DeliveryCreateView;
import ru.protei.portal.ui.delivery.client.view.edit.DeliveryEditView;
import ru.protei.portal.ui.delivery.client.view.kit.page.KitView;
import ru.protei.portal.ui.delivery.client.view.meta.DeliveryMetaView;
import ru.protei.portal.ui.delivery.client.view.module.page.ModuleView;
import ru.protei.portal.ui.delivery.client.view.table.DeliveryTableView;
import ru.protei.portal.ui.delivery.client.view.kit.add.DeliveryKitAddView;

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

        bind(DeliveryKitAddActivity.class).asEagerSingleton();
        bind(AbstractDeliveryKitAddView.class).to(DeliveryKitAddView.class).in(Singleton.class);

        bind(KitActivity.class).asEagerSingleton();
        bind(AbstractKitView.class).to(KitView.class).in(Singleton.class);
        bind(AbstractModuleView.class).to(ModuleView.class).in(Singleton.class);
    }
}

