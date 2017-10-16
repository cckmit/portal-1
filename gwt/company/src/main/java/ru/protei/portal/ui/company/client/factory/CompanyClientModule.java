package ru.protei.portal.ui.company.client.factory;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;
import ru.protei.portal.ui.company.client.activity.edit.AbstractCompanyEditView;
import ru.protei.portal.ui.company.client.activity.edit.CompanyEditActivity;
import ru.protei.portal.ui.company.client.activity.filter.AbstractCompanyFilterView;
import ru.protei.portal.ui.company.client.activity.item.AbstractCompanyItemView;
import ru.protei.portal.ui.company.client.activity.list.AbstractCompanyListView;
import ru.protei.portal.ui.company.client.activity.list.CompanyListActivity;
import ru.protei.portal.ui.company.client.activity.page.CompanyPage;
import ru.protei.portal.ui.company.client.activity.preview.AbstractCompanyPreviewView;
import ru.protei.portal.ui.company.client.activity.preview.CompanyPreviewActivity;
import ru.protei.portal.ui.company.client.view.edit.CompanyEditView;
import ru.protei.portal.ui.company.client.view.filter.CompanyFilterView;
import ru.protei.portal.ui.company.client.view.item.CompanyItemView;
import ru.protei.portal.ui.company.client.view.list.CompanyListView;
import ru.protei.portal.ui.company.client.view.preview.CompanyPreviewView;
import ru.protei.portal.ui.company.client.widget.group.buttonselector.GroupModel;

/**
 * Описание классов фабрики
 */
public class CompanyClientModule extends AbstractGinModule {
    @Override
    protected void configure() {
        bind( CompanyPage.class ).asEagerSingleton();
        bind( GroupModel.class ).asEagerSingleton();

        bind( CompanyListActivity.class ).asEagerSingleton();
        bind( AbstractCompanyListView.class ).to( CompanyListView.class ).in( Singleton.class );
        bind ( AbstractCompanyItemView.class ).to( CompanyItemView.class );

        bind( CompanyEditActivity.class ).asEagerSingleton();
        bind ( AbstractCompanyEditView.class ).to(CompanyEditView.class).in(Singleton.class);

        bind( CompanyPreviewActivity.class ).asEagerSingleton();
        bind( AbstractCompanyPreviewView.class ).to( CompanyPreviewView.class ).in( Singleton.class );

        bind( AbstractCompanyFilterView.class ).to( CompanyFilterView.class ).in( Singleton.class );
    }
}

