package ru.protei.portal.ui.sitefolder.client.factory;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;
import ru.protei.portal.ui.sitefolder.client.activity.page.SiteFolderPage;
import ru.protei.portal.ui.sitefolder.client.activity.plaform.filter.AbstractSiteFolderFilterView;
import ru.protei.portal.ui.sitefolder.client.activity.plaform.preview.AbstractSiteFolderPreviewView;
import ru.protei.portal.ui.sitefolder.client.activity.plaform.preview.SiteFolderPreviewActivity;
import ru.protei.portal.ui.sitefolder.client.activity.plaform.table.AbstractSiteFolderTableView;
import ru.protei.portal.ui.sitefolder.client.activity.plaform.table.SiteFolderTableActivity;
import ru.protei.portal.ui.sitefolder.client.view.platform.filter.SiteFolderFilterView;
import ru.protei.portal.ui.sitefolder.client.view.platform.preview.SiteFolderPreviewView;
import ru.protei.portal.ui.sitefolder.client.view.platform.table.SiteFolderTableView;

public class SiteFolderClientModule extends AbstractGinModule {

    @Override
    protected void configure() {
        bind(SiteFolderPage.class).asEagerSingleton();

        bind(SiteFolderTableActivity.class).asEagerSingleton();
        bind(SiteFolderPreviewActivity.class).asEagerSingleton();
        bind(AbstractSiteFolderTableView.class).to(SiteFolderTableView.class).in(Singleton.class);
        bind(AbstractSiteFolderFilterView.class).to(SiteFolderFilterView.class).in(Singleton.class);
        bind(AbstractSiteFolderPreviewView.class).to(SiteFolderPreviewView.class).in(Singleton.class);

    }
}
