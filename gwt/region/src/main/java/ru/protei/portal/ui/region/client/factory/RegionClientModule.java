package ru.protei.portal.ui.region.client.factory;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;
import ru.protei.portal.ui.region.client.activity.filter.AbstractRegionFilterView;
import ru.protei.portal.ui.region.client.activity.item.AbstractRegionItemView;
import ru.protei.portal.ui.region.client.activity.list.AbstractRegionListView;
import ru.protei.portal.ui.region.client.activity.list.RegionListActivity;
import ru.protei.portal.ui.region.client.page.RegionPage;
import ru.protei.portal.ui.region.client.view.filter.RegionFilterView;
import ru.protei.portal.ui.region.client.view.item.RegionItemView;
import ru.protei.portal.ui.region.client.view.list.RegionListView;

/**
 * Описание классов фабрики
 */
public class RegionClientModule extends AbstractGinModule {
    @Override
    protected void configure()    {
        bind( RegionPage.class ).asEagerSingleton();

        bind( RegionListActivity.class ).asEagerSingleton();
        bind( AbstractRegionListView.class ).to( RegionListView.class ).in( Singleton.class );
        bind( AbstractRegionItemView.class ).to( RegionItemView.class );
        bind( AbstractRegionFilterView.class ).to( RegionFilterView.class ).in( Singleton.class );

//        bind( ProductEditActivity.class ).asEagerSingleton();
//        bind( AbstractProductEditView.class ).to(ProductEditView.class).in(Singleton.class);
//
//        bind( ProductPreviewActivity.class ).asEagerSingleton();
//        bind( AbstractProductPreviewView.class ).to( ProductPreviewView.class ).in( Singleton.class );
    }
}

