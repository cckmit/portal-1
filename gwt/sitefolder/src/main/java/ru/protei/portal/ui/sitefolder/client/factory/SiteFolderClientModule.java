package ru.protei.portal.ui.sitefolder.client.factory;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;
import ru.protei.portal.ui.sitefolder.client.activity.app.edit.AbstractApplicationEditView;
import ru.protei.portal.ui.sitefolder.client.activity.app.edit.ApplicationEditActivity;
import ru.protei.portal.ui.sitefolder.client.activity.app.filter.AbstractApplicationFilterView;
import ru.protei.portal.ui.sitefolder.client.activity.app.list.AbstractApplicationListView;
import ru.protei.portal.ui.sitefolder.client.activity.app.list.ApplicationListActivity;
import ru.protei.portal.ui.sitefolder.client.activity.app.list.item.AbstractApplicationListItemView;
import ru.protei.portal.ui.sitefolder.client.activity.app.preview.AbstractApplicationPreviewView;
import ru.protei.portal.ui.sitefolder.client.activity.app.preview.ApplicationPreviewActivity;
import ru.protei.portal.ui.sitefolder.client.activity.app.table.AbstractApplicationTableView;
import ru.protei.portal.ui.sitefolder.client.activity.app.table.ApplicationTableActivity;
import ru.protei.portal.ui.sitefolder.client.activity.page.SiteFolderPage;
import ru.protei.portal.ui.sitefolder.client.activity.plaform.edit.AbstractPlatformEditView;
import ru.protei.portal.ui.sitefolder.client.activity.plaform.edit.PlatformEditActivity;
import ru.protei.portal.ui.sitefolder.client.activity.plaform.filter.AbstractPlatformFilterView;
import ru.protei.portal.ui.sitefolder.client.activity.plaform.preview.AbstractPlatformPreviewView;
import ru.protei.portal.ui.sitefolder.client.activity.plaform.preview.PlatformPreviewActivity;
import ru.protei.portal.ui.sitefolder.client.activity.plaform.table.AbstractPlatformTableView;
import ru.protei.portal.ui.sitefolder.client.activity.plaform.table.PlatformTableActivity;
import ru.protei.portal.ui.sitefolder.client.activity.plaform.table.concise.AbstractPlatformConciseTableView;
import ru.protei.portal.ui.sitefolder.client.activity.plaform.table.concise.PlatformConciseTableActivity;
import ru.protei.portal.ui.sitefolder.client.activity.server.edit.AbstractServerEditView;
import ru.protei.portal.ui.sitefolder.client.activity.server.edit.ServerEditActivity;
import ru.protei.portal.ui.sitefolder.client.activity.server.filter.AbstractServerFilterView;
import ru.protei.portal.ui.sitefolder.client.activity.server.preview.AbstractServerPreviewView;
import ru.protei.portal.ui.sitefolder.client.activity.server.preview.ServerPreviewActivity;
import ru.protei.portal.ui.sitefolder.client.activity.server.summarytable.AbstractServerSummaryTableView;
import ru.protei.portal.ui.sitefolder.client.activity.server.summarytable.ServerSummaryTableActivity;
import ru.protei.portal.ui.sitefolder.client.activity.server.table.AbstractServerTableView;
import ru.protei.portal.ui.sitefolder.client.activity.server.table.ServerTableActivity;
import ru.protei.portal.ui.sitefolder.client.activity.servergroup.edit.AbstractServerGroupEditActivity;
import ru.protei.portal.ui.sitefolder.client.activity.servergroup.edit.AbstractServerGroupEditView;
import ru.protei.portal.ui.sitefolder.client.activity.servergroup.edit.ServerGroupEditActivity;
import ru.protei.portal.ui.sitefolder.client.view.app.edit.ApplicationEditView;
import ru.protei.portal.ui.sitefolder.client.view.app.filter.ApplicationFilterView;
import ru.protei.portal.ui.sitefolder.client.view.app.list.ApplicationListView;
import ru.protei.portal.ui.sitefolder.client.view.app.list.item.ApplicationListItemView;
import ru.protei.portal.ui.sitefolder.client.view.app.preview.ApplicationPreviewView;
import ru.protei.portal.ui.sitefolder.client.view.app.table.ApplicationTableView;
import ru.protei.portal.ui.sitefolder.client.view.platform.edit.PlatformEditView;
import ru.protei.portal.ui.sitefolder.client.view.platform.filter.PlatformFilterView;
import ru.protei.portal.ui.sitefolder.client.view.platform.preview.PlatformPreviewView;
import ru.protei.portal.ui.sitefolder.client.view.platform.table.PlatformTableView;
import ru.protei.portal.ui.sitefolder.client.view.platform.table.concise.PlatformConciseTableView;
import ru.protei.portal.ui.sitefolder.client.view.server.edit.ServerEditView;
import ru.protei.portal.ui.sitefolder.client.view.server.filter.ServerFilterView;
import ru.protei.portal.ui.sitefolder.client.view.server.preview.ServerPreviewView;
import ru.protei.portal.ui.sitefolder.client.view.server.summarytable.ServerSummaryTableView;
import ru.protei.portal.ui.sitefolder.client.view.server.table.ServerTableView;
import ru.protei.portal.ui.sitefolder.client.view.servergroup.edit.ServerGroupEditView;

public class SiteFolderClientModule extends AbstractGinModule {

    @Override
    protected void configure() {

        // Page
        bind(SiteFolderPage.class).asEagerSingleton();

        // Platform
        bind(PlatformTableActivity.class).asEagerSingleton();
        bind(PlatformPreviewActivity.class).asEagerSingleton();
        bind(PlatformEditActivity.class).asEagerSingleton();
        bind(PlatformConciseTableActivity.class).asEagerSingleton();
        bind(AbstractPlatformTableView.class).to(PlatformTableView.class).in(Singleton.class);
        bind(AbstractPlatformFilterView.class).to(PlatformFilterView.class).in(Singleton.class);
        bind(AbstractPlatformPreviewView.class).to(PlatformPreviewView.class).in(Singleton.class);
        bind(AbstractPlatformEditView.class).to(PlatformEditView.class).in(Singleton.class);
        bind(AbstractPlatformConciseTableView.class).to(PlatformConciseTableView.class).in(Singleton.class);

        // Server
        bind(ServerSummaryTableActivity.class).asEagerSingleton();
        bind(ServerTableActivity.class).asEagerSingleton();
        bind(ServerPreviewActivity.class).asEagerSingleton();
        bind(ServerEditActivity.class).asEagerSingleton();
        bind(AbstractServerSummaryTableView.class).to(ServerSummaryTableView.class).in(Singleton.class);
        bind(AbstractServerTableView.class).to(ServerTableView.class).in(Singleton.class);
        bind(AbstractServerFilterView.class).to(ServerFilterView.class).in(Singleton.class);
        bind(AbstractServerPreviewView.class).to(ServerPreviewView.class).in(Singleton.class);
        bind(AbstractServerEditView.class).to(ServerEditView.class).in(Singleton.class);

        // App
        bind(ApplicationTableActivity.class).asEagerSingleton();
        bind(ApplicationPreviewActivity.class).asEagerSingleton();
        bind(ApplicationEditActivity.class).asEagerSingleton();
        bind(AbstractApplicationTableView.class).to(ApplicationTableView.class).in(Singleton.class);
        bind(AbstractApplicationFilterView.class).to(ApplicationFilterView.class).in(Singleton.class);
        bind(AbstractApplicationPreviewView.class).to(ApplicationPreviewView.class).in(Singleton.class);
        bind(AbstractApplicationEditView.class).to(ApplicationEditView.class).in(Singleton.class);
        bind(ApplicationListActivity.class).asEagerSingleton();
        bind(AbstractApplicationListView.class).to(ApplicationListView.class);
        bind(AbstractApplicationListItemView.class).to(ApplicationListItemView.class);

        // Server group
        bind(AbstractServerGroupEditActivity.class).to(ServerGroupEditActivity.class).asEagerSingleton();
        bind(AbstractServerGroupEditView.class).to(ServerGroupEditView.class).in(Singleton.class);
    }
}
