package ru.protei.portal.ui.issue.client.factory;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;
import ru.protei.portal.ui.common.client.common.LocalStorageService;
import ru.protei.portal.ui.issue.client.activity.edit.*;
import ru.protei.portal.ui.issue.client.activity.filter.AbstractIssueFilterView;
import ru.protei.portal.ui.issue.client.activity.filter.IssueFilterService;
import ru.protei.portal.ui.issue.client.activity.meta.AbstractIssueMetaView;
import ru.protei.portal.ui.issue.client.activity.page.IssuePage;
import ru.protei.portal.ui.issue.client.activity.preview.AbstractIssuePreviewActivity;
import ru.protei.portal.ui.issue.client.activity.preview.AbstractIssuePreviewView;
import ru.protei.portal.ui.issue.client.activity.preview.IssuePreviewActivity;
import ru.protei.portal.ui.issue.client.activity.table.AbstractIssueTableView;
import ru.protei.portal.ui.issue.client.activity.table.IssueTableActivity;
import ru.protei.portal.ui.issue.client.view.edit.IssueEditView;
import ru.protei.portal.ui.issue.client.view.filter.IssueFilterView;
import ru.protei.portal.ui.issue.client.view.meta.IssueMetaView;
import ru.protei.portal.ui.issue.client.view.preview.IssuePreviewView;
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
        bind( IssueTableActivity.class ).asEagerSingleton();
        bind( AbstractIssueTableView.class ).to(IssueTableView.class);

        bind( AbstractIssuePreviewActivity.class).to(IssuePreviewActivity.class).asEagerSingleton();
        bind( AbstractIssuePreviewView.class ).to(IssuePreviewView.class).in(Singleton.class);

        bind( AbstractIssueEditActivity.class ).to( IssueEditActivity.class ).asEagerSingleton();
        bind( AbstractIssueEditView.class ).to( IssueEditView.class ).in( Singleton.class );
        bind( AbstractIssueMetaView.class ).to( IssueMetaView.class ).in( Singleton.class );

        bind( AbstractIssueFilterView.class ).to(IssueFilterView.class).in(Singleton.class);
    }
}

