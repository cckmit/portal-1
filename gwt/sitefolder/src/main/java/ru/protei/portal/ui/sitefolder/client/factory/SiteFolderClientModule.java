package ru.protei.portal.ui.sitefolder.client.factory;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;
import ru.protei.portal.ui.sitefolder.client.activity.app.edit.AbstractSiteFolderAppEditView;
import ru.protei.portal.ui.sitefolder.client.activity.app.edit.SiteFolderAppEditActivity;
import ru.protei.portal.ui.sitefolder.client.activity.app.filter.AbstractSiteFolderAppFilterView;
import ru.protei.portal.ui.sitefolder.client.activity.app.preview.AbstractSiteFolderAppPreviewView;
import ru.protei.portal.ui.sitefolder.client.activity.app.preview.SiteFolderAppPreviewActivity;
import ru.protei.portal.ui.sitefolder.client.activity.app.table.AbstractSiteFolderAppTableView;
import ru.protei.portal.ui.sitefolder.client.activity.app.table.SiteFolderAppTableActivity;
import ru.protei.portal.ui.sitefolder.client.activity.page.SiteFolderPage;
import ru.protei.portal.ui.sitefolder.client.activity.plaform.edit.AbstractSiteFolderEditView;
import ru.protei.portal.ui.sitefolder.client.activity.plaform.edit.SiteFolderEditActivity;
import ru.protei.portal.ui.sitefolder.client.activity.plaform.filter.AbstractSiteFolderFilterView;
import ru.protei.portal.ui.sitefolder.client.activity.plaform.preview.AbstractSiteFolderPreviewView;
import ru.protei.portal.ui.sitefolder.client.activity.plaform.preview.SiteFolderPreviewActivity;
import ru.protei.portal.ui.sitefolder.client.activity.plaform.table.AbstractSiteFolderTableView;
import ru.protei.portal.ui.sitefolder.client.activity.plaform.table.SiteFolderTableActivity;
import ru.protei.portal.ui.sitefolder.client.activity.server.edit.AbstractSiteFolderServerEditView;
import ru.protei.portal.ui.sitefolder.client.activity.server.edit.SiteFolderServerEditActivity;
import ru.protei.portal.ui.sitefolder.client.activity.server.filter.AbstractSiteFolderServerFilterView;
import ru.protei.portal.ui.sitefolder.client.activity.server.preview.AbstractSiteFolderServerPreviewView;
import ru.protei.portal.ui.sitefolder.client.activity.server.preview.SiteFolderServerPreviewActivity;
import ru.protei.portal.ui.sitefolder.client.activity.server.table.AbstractSiteFolderServerTableView;
import ru.protei.portal.ui.sitefolder.client.activity.server.table.SiteFolderServerTableActivity;
import ru.protei.portal.ui.sitefolder.client.view.app.edit.SiteFolderAppEditView;
import ru.protei.portal.ui.sitefolder.client.view.app.filter.SiteFolderAppFilterView;
import ru.protei.portal.ui.sitefolder.client.view.app.preview.SiteFolderAppPreviewView;
import ru.protei.portal.ui.sitefolder.client.view.app.table.SiteFolderAppTableView;
import ru.protei.portal.ui.sitefolder.client.view.platform.edit.SiteFolderEditView;
import ru.protei.portal.ui.sitefolder.client.view.platform.filter.SiteFolderFilterView;
import ru.protei.portal.ui.sitefolder.client.view.platform.preview.SiteFolderPreviewView;
import ru.protei.portal.ui.sitefolder.client.view.platform.table.SiteFolderTableView;
import ru.protei.portal.ui.sitefolder.client.view.server.edit.SiteFolderServerEditView;
import ru.protei.portal.ui.sitefolder.client.view.server.filter.SiteFolderServerFilterView;
import ru.protei.portal.ui.sitefolder.client.view.server.preview.SiteFolderServerPreviewView;
import ru.protei.portal.ui.sitefolder.client.view.server.table.SiteFolderServerTableView;

public class SiteFolderClientModule extends AbstractGinModule {

    @Override
    protected void configure() {

        // Page
        bind(SiteFolderPage.class).asEagerSingleton();

        // Platform
        bind(SiteFolderTableActivity.class).asEagerSingleton();
        bind(SiteFolderPreviewActivity.class).asEagerSingleton();
        bind(SiteFolderEditActivity.class).asEagerSingleton();
        bind(AbstractSiteFolderTableView.class).to(SiteFolderTableView.class).in(Singleton.class);
        bind(AbstractSiteFolderFilterView.class).to(SiteFolderFilterView.class).in(Singleton.class);
        bind(AbstractSiteFolderPreviewView.class).to(SiteFolderPreviewView.class).in(Singleton.class);
        bind(AbstractSiteFolderEditView.class).to(SiteFolderEditView.class).in(Singleton.class);

        // Server
        bind(SiteFolderServerTableActivity.class).asEagerSingleton();
        bind(SiteFolderServerPreviewActivity.class).asEagerSingleton();
        bind(SiteFolderServerEditActivity.class).asEagerSingleton();
        bind(AbstractSiteFolderServerTableView.class).to(SiteFolderServerTableView.class).in(Singleton.class);
        bind(AbstractSiteFolderServerFilterView.class).to(SiteFolderServerFilterView.class).in(Singleton.class);
        bind(AbstractSiteFolderServerPreviewView.class).to(SiteFolderServerPreviewView.class).in(Singleton.class);
        bind(AbstractSiteFolderServerEditView.class).to(SiteFolderServerEditView.class).in(Singleton.class);

        // App
        bind(SiteFolderAppTableActivity.class).asEagerSingleton();
        bind(SiteFolderAppPreviewActivity.class).asEagerSingleton();
        bind(SiteFolderAppEditActivity.class).asEagerSingleton();
        bind(AbstractSiteFolderAppTableView.class).to(SiteFolderAppTableView.class).in(Singleton.class);
        bind(AbstractSiteFolderAppFilterView.class).to(SiteFolderAppFilterView.class).in(Singleton.class);
        bind(AbstractSiteFolderAppPreviewView.class).to(SiteFolderAppPreviewView.class).in(Singleton.class);
        bind(AbstractSiteFolderAppEditView.class).to(SiteFolderAppEditView.class).in(Singleton.class);
    }
}
