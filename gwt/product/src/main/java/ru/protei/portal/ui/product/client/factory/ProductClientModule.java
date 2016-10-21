package ru.protei.portal.ui.product.client.factory;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;
import ru.protei.portal.ui.product.client.activity.edit.AbstractProductEditView;
import ru.protei.portal.ui.product.client.activity.edit.ProductEditActivity;
import ru.protei.portal.ui.product.client.activity.item.AbstractProductItemView;
import ru.protei.portal.ui.product.client.activity.list.AbstractProductListView;
import ru.protei.portal.ui.product.client.activity.list.ProductListActivity;
import ru.protei.portal.ui.product.client.view.edit.ProductEditView;
import ru.protei.portal.ui.product.client.view.item.ProductItemView;
import ru.protei.portal.ui.product.client.view.list.ProductListView;

/**
 * Описание классов фабрики
 */
public class ProductClientModule extends AbstractGinModule {
    @Override
    protected void configure()    {
        bind( ProductListActivity.class ).asEagerSingleton ();
        bind( AbstractProductListView.class ).to(ProductListView.class).in(Singleton.class);
        bind( AbstractProductItemView.class).to(ProductItemView.class);

        bind( ProductEditActivity.class ).asEagerSingleton();
        bind( AbstractProductEditView.class ).to(ProductEditView.class).in(Singleton.class);;
    }
}

