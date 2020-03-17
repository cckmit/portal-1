package ru.protei.portal.ui.issue.client.factory;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;
import ru.protei.portal.ui.common.client.activity.filter.*;
import ru.protei.portal.ui.common.client.common.LocalStorageService;
import ru.protei.portal.ui.common.client.view.filter.IssueFilterCollapseView;
import ru.protei.portal.ui.issue.client.activity.create.AbstractIssueCreateActivity;
import ru.protei.portal.ui.issue.client.activity.create.AbstractIssueCreateView;
import ru.protei.portal.ui.issue.client.activity.create.IssueCreateActivity;
import ru.protei.portal.ui.issue.client.activity.edit.AbstractIssueEditActivity;
import ru.protei.portal.ui.issue.client.activity.edit.AbstractIssueEditView;
import ru.protei.portal.ui.issue.client.activity.edit.IssueEditActivity;
import ru.protei.portal.ui.issue.client.activity.meta.AbstractIssueMetaActivity;
import ru.protei.portal.ui.issue.client.activity.meta.AbstractIssueMetaView;
import ru.protei.portal.ui.issue.client.activity.meta.IssueMetaActivity;
import ru.protei.portal.ui.issue.client.activity.page.IssuePage;
import ru.protei.portal.ui.issue.client.activity.table.AbstractIssueTableView;
import ru.protei.portal.ui.issue.client.activity.table.IssueTableFilterActivity;
import ru.protei.portal.ui.issue.client.common.CaseStateFilterProvider;
import ru.protei.portal.ui.issue.client.view.create.IssueCreateView;
import ru.protei.portal.ui.issue.client.view.edit.IssueEditView;
import ru.protei.portal.ui.common.client.view.filter.IssueFilterView;
import ru.protei.portal.ui.issue.client.view.meta.IssueMetaView;
import ru.protei.portal.ui.issue.client.view.table.IssueTableView;

/**
 * Описание классов фабрики
 */
public class IssueClientModule extends AbstractGinModule {
    @Override
    protected void configure() {
        bind( IssueFilterService.class ).asEagerSingleton();
        bind( LocalStorageService.class ).asEagerSingleton();
        bind( IssuePage.class ).asEagerSingleton();

        bind( CaseStateFilterProvider.class ).asEagerSingleton();
        bind( IssueTableFilterActivity.class ).asEagerSingleton();
        bind( AbstractIssueMetaActivity.class ).to(IssueMetaActivity.class).asEagerSingleton();
        bind( AbstractIssueTableView.class ).to(IssueTableView.class);

        bind( AbstractIssueEditActivity.class ).to( IssueEditActivity.class ).asEagerSingleton();
        bind( AbstractIssueEditView.class ).to( IssueEditView.class ).in( Singleton.class );

        bind( AbstractIssueCreateActivity.class ).to( IssueCreateActivity.class ).asEagerSingleton();
        bind( AbstractIssueCreateView.class ).to( IssueCreateView.class ).in( Singleton.class );

        bind( AbstractIssueMetaView.class ).to( IssueMetaView.class );

        bind( AbstractIssueFilterView.class ).to(IssueFilterView.class).in(Singleton.class);
        bind( AbstractIssueCollapseFilterView.class ).to(IssueFilterCollapseView.class).in(Singleton.class);

        bind( AbstractIssueFilterActivity.class ).to(IssueFilterActivity.class).in(Singleton.class);
    }
}

