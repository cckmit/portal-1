package ru.protei.portal.ui.product.client.factory;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;
import ru.protei.portal.ui.product.client.activity.edit.AbstractProductEditView;
import ru.protei.portal.ui.product.client.activity.edit.ProductEditActivity;
import ru.protei.portal.ui.product.client.activity.filter.AbstractProductFilterView;
import ru.protei.portal.ui.product.client.activity.table.*;
import ru.protei.portal.ui.product.client.activity.preview.AbstractProductPreviewView;
import ru.protei.portal.ui.product.client.activity.preview.ProductPreviewActivity;
import ru.protei.portal.ui.product.client.page.ProductPage;
import ru.protei.portal.ui.product.client.view.edit.ProductEditView;
import ru.protei.portal.ui.product.client.view.filter.ProductFilterView;
import ru.protei.portal.ui.product.client.view.preview.ProductPreviewView;
import ru.protei.portal.ui.product.client.view.table.ProductTableView;

/**
 * Описание классов фабрики
 */
public class ProductClientModule extends AbstractGinModule {
    @Override
    protected void configure()    {
        bind( ProductPage.class ).asEagerSingleton();

        bind( ProductTableActivity.class ).asEagerSingleton ();
        bind( AbstractProductTableView.class ).to(ProductTableView.class).in(Singleton.class);

        bind( ProductEditActivity.class ).asEagerSingleton();
        bind( AbstractProductEditView.class ).to(ProductEditView.class).in(Singleton.class);

        bind( ProductPreviewActivity.class ).asEagerSingleton();
        bind( AbstractProductPreviewView.class ).to( ProductPreviewView.class ).in( Singleton.class );

        bind( AbstractProductFilterView.class ).to( ProductFilterView.class ).in( Singleton.class );
    }
}

