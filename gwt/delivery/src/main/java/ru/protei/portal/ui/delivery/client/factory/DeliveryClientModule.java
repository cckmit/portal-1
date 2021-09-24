package ru.protei.portal.ui.delivery.client.factory;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;
import ru.protei.portal.ui.delivery.client.activity.card.filter.AbstractCardFilterView;
import ru.protei.portal.ui.delivery.client.activity.card.table.AbstractCardTableView;
import ru.protei.portal.ui.delivery.client.activity.card.table.CardTableActivity;
import ru.protei.portal.ui.delivery.client.activity.cardbatch.common.AbstractCardBatchCommonInfoView;
import ru.protei.portal.ui.delivery.client.activity.cardbatch.create.AbstractCardBatchCreateView;
import ru.protei.portal.ui.delivery.client.activity.cardbatch.create.CardBatchCreateActivity;
import ru.protei.portal.ui.delivery.client.activity.cardbatch.meta.AbstractCardBatchMetaView;
import ru.protei.portal.ui.delivery.client.activity.delivery.create.AbstractDeliveryCreateView;
import ru.protei.portal.ui.delivery.client.activity.delivery.create.DeliveryCreateActivity;
import ru.protei.portal.ui.delivery.client.activity.delivery.edit.AbstractDeliveryEditView;
import ru.protei.portal.ui.delivery.client.activity.delivery.edit.DeliveryEditActivity;
import ru.protei.portal.ui.delivery.client.activity.delivery.kit.add.DeliveryKitAddActivity;
import ru.protei.portal.ui.delivery.client.activity.delivery.kit.edit.AbstractDeliveryKitEditView;
import ru.protei.portal.ui.delivery.client.activity.delivery.kit.edit.DeliveryKitEditActivity;
import ru.protei.portal.ui.delivery.client.activity.delivery.kit.page.AbstractKitView;
import ru.protei.portal.ui.delivery.client.activity.delivery.kit.page.AbstractModuleTableView;
import ru.protei.portal.ui.delivery.client.activity.delivery.kit.page.KitActivity;
import ru.protei.portal.ui.delivery.client.activity.delivery.meta.AbstractDeliveryMetaView;
import ru.protei.portal.ui.delivery.client.activity.delivery.meta.DeliveryMetaActivity;
import ru.protei.portal.ui.delivery.client.activity.delivery.module.create.AbstractModuleCreateView;
import ru.protei.portal.ui.delivery.client.activity.delivery.module.create.ModuleCreateActivity;
import ru.protei.portal.ui.delivery.client.activity.delivery.module.edit.AbstractModuleEditView;
import ru.protei.portal.ui.delivery.client.activity.delivery.module.edit.ModuleEditActivity;
import ru.protei.portal.ui.delivery.client.activity.delivery.module.meta.AbstractModuleMetaView;
import ru.protei.portal.ui.delivery.client.activity.delivery.module.meta.ModuleMetaActivity;
import ru.protei.portal.ui.delivery.client.page.CardBatchPage;
import ru.protei.portal.ui.delivery.client.page.CardPage;
import ru.protei.portal.ui.delivery.client.page.DeliveryPage;
import ru.protei.portal.ui.delivery.client.page.StoreAndDeliveryPage;
import ru.protei.portal.ui.delivery.client.activity.delivery.table.AbstractDeliveryTableView;
import ru.protei.portal.ui.delivery.client.activity.delivery.table.DeliveryTableActivity;
import ru.protei.portal.ui.delivery.client.view.card.filter.CardFilterView;
import ru.protei.portal.ui.delivery.client.view.card.table.CardTableView;
import ru.protei.portal.ui.delivery.client.view.cardbatch.common.CardBatchCommonInfoView;
import ru.protei.portal.ui.delivery.client.view.cardbatch.create.CardBatchCreateView;
import ru.protei.portal.ui.delivery.client.view.cardbatch.meta.CardBatchMetaView;
import ru.protei.portal.ui.delivery.client.view.delivery.create.DeliveryCreateView;
import ru.protei.portal.ui.delivery.client.view.delivery.edit.DeliveryEditView;
import ru.protei.portal.ui.delivery.client.view.delivery.kit.edit.DeliveryKitEditView;
import ru.protei.portal.ui.delivery.client.view.delivery.kit.page.KitView;
import ru.protei.portal.ui.delivery.client.view.delivery.meta.DeliveryMetaView;
import ru.protei.portal.ui.delivery.client.view.delivery.module.create.ModuleCreateView;
import ru.protei.portal.ui.delivery.client.view.delivery.module.edit.ModuleEditView;
import ru.protei.portal.ui.delivery.client.view.delivery.module.meta.ModuleMetaView;
import ru.protei.portal.ui.delivery.client.view.delivery.module.table.ModuleTableView;
import ru.protei.portal.ui.delivery.client.view.delivery.table.DeliveryTableView;

public class DeliveryClientModule extends AbstractGinModule {
    @Override
    protected void configure()    {
        bind(StoreAndDeliveryPage.class).asEagerSingleton();
        bind(DeliveryPage.class).asEagerSingleton();
        bind(CardPage.class).asEagerSingleton();
        bind(CardBatchPage.class).asEagerSingleton();

        bind(DeliveryTableActivity.class).asEagerSingleton();
        bind(AbstractDeliveryTableView.class).to(DeliveryTableView.class).in(Singleton.class);

        bind(DeliveryCreateActivity.class).asEagerSingleton();
        bind(AbstractDeliveryCreateView.class).to(DeliveryCreateView.class).in(Singleton.class);

        bind(DeliveryEditActivity.class).asEagerSingleton();
        bind(DeliveryMetaActivity.class).asEagerSingleton();

        bind(AbstractDeliveryEditView.class).to(DeliveryEditView.class).in(Singleton.class);
        bind(AbstractDeliveryMetaView.class).to(DeliveryMetaView.class).in(Singleton.class);

        bind(DeliveryKitAddActivity.class).asEagerSingleton();
        bind(DeliveryKitEditActivity.class).asEagerSingleton();
        bind(AbstractDeliveryKitEditView.class).to(DeliveryKitEditView.class).in(Singleton.class);

        bind(KitActivity.class).asEagerSingleton();
        bind(AbstractKitView.class).to(KitView.class).in(Singleton.class);
        bind(AbstractModuleTableView.class).to(ModuleTableView.class).in(Singleton.class);

        bind(ModuleEditActivity.class).asEagerSingleton();
        bind(ModuleMetaActivity.class).asEagerSingleton();
        bind(ModuleCreateActivity.class).asEagerSingleton();

        bind(AbstractModuleEditView.class).to(ModuleEditView.class).in(Singleton.class);
        bind(AbstractModuleCreateView.class).to(ModuleCreateView.class).in(Singleton.class);
        bind(AbstractModuleMetaView.class).to(ModuleMetaView.class);

        bind(CardTableActivity.class).asEagerSingleton();
        bind(AbstractCardTableView.class).to(CardTableView.class).in(Singleton.class);
        bind(AbstractCardFilterView.class).to(CardFilterView.class).in(Singleton.class);

        bind(CardBatchCreateActivity.class).asEagerSingleton();
        bind(AbstractCardBatchCreateView.class).to(CardBatchCreateView.class).in(Singleton.class);
        bind(AbstractCardBatchCommonInfoView.class).to(CardBatchCommonInfoView.class).in(Singleton.class);
        bind(AbstractCardBatchMetaView.class).to(CardBatchMetaView.class).in(Singleton.class);
    }
}

