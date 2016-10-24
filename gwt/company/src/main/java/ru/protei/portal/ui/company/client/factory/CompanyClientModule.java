package ru.protei.portal.ui.company.client.factory;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;
import ru.protei.portal.ui.company.client.activity.list.AbstractCompanyListView;
import ru.protei.portal.ui.company.client.activity.list.CompanyListActivity;
import ru.protei.portal.ui.company.client.activity.item.AbstractCompanyItemView;
import ru.protei.portal.ui.company.client.activity.preview.AbstractCompanyPreviewView;
import ru.protei.portal.ui.company.client.activity.preview.CompanyPreviewActivity;
import ru.protei.portal.ui.company.client.view.list.CompanyListView;
import ru.protei.portal.ui.company.client.view.item.CompanyItemView;
import ru.protei.portal.ui.company.client.view.preview.CompanyPreviewView;
import ru.protei.portal.ui.company.client.widget.group.selector.GroupModel;

/**
 * Описание классов фабрики
 */
public class CompanyClientModule extends AbstractGinModule {
    @Override
    protected void configure() {

        bind( GroupModel.class ).asEagerSingleton();

        bind( CompanyListActivity.class ).asEagerSingleton();
        bind( AbstractCompanyListView.class ).to( CompanyListView.class ).in( Singleton.class );
        bind ( AbstractCompanyItemView.class ).to( CompanyItemView.class );

        bind( CompanyPreviewActivity.class ).asEagerSingleton();
        bind( AbstractCompanyPreviewView.class ).to( CompanyPreviewView.class ).in( Singleton.class );
    }
}

