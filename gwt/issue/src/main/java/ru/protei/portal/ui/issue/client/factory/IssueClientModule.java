package ru.protei.portal.ui.issue.client.factory;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;
import ru.protei.portal.ui.issue.client.activity.edit.AbstractIssueEditView;
import ru.protei.portal.ui.issue.client.activity.edit.IssueEditActivity;
import ru.protei.portal.ui.issue.client.activity.filter.AbstractIssueFilterView;
import ru.protei.portal.ui.issue.client.activity.page.IssuePage;
import ru.protei.portal.ui.issue.client.activity.preview.AbstractIssuePreviewView;
import ru.protei.portal.ui.issue.client.activity.preview.IssuePreviewActivity;
import ru.protei.portal.ui.issue.client.activity.simpletable.IssueTableActivity;
import ru.protei.portal.ui.issue.client.activity.table.AbstractIssueTableView;
import ru.protei.portal.ui.issue.client.view.edit.IssueEditView;
import ru.protei.portal.ui.issue.client.view.filter.IssueFilterView;
import ru.protei.portal.ui.issue.client.view.preview.IssuePreviewView;
import ru.protei.portal.ui.issue.client.view.table.IssueTableView;


/**
 * Описание классов фабрики
 */
public class IssueClientModule extends AbstractGinModule {
    @Override
    protected void configure() {
        bind( IssuePage.class ).asEagerSingleton();

        bind( IssueTableActivity.class ).asEagerSingleton();
        bind( AbstractIssueTableView.class ).to( IssueTableView.class );

        bind( IssuePreviewActivity.class ).asEagerSingleton();
        bind( AbstractIssuePreviewView.class ).to( IssuePreviewView.class ).in( Singleton.class );
        
        bind( IssueEditActivity.class ).asEagerSingleton();
        bind( AbstractIssueEditView.class ).to( IssueEditView.class );

        bind( AbstractIssueFilterView.class ).to( IssueFilterView.class ).in( Singleton.class );

        bind(ru.protei.portal.ui.issue.client.activity.simpletable.IssueTableActivity.class).asEagerSingleton();
        bind(ru.protei.portal.ui.issue.client.activity.simpletable.AbstractIssueTableView.class).to(
                ru.protei.portal.ui.issue.client.view.simpletable.IssueTableView.class
        );
    }
}

