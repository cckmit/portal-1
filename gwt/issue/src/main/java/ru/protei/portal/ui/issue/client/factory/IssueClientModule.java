package ru.protei.portal.ui.issue.client.factory;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;
import ru.protei.portal.ui.issue.client.activity.preview.AbstractIssuePreviewView;
import ru.protei.portal.ui.issue.client.activity.preview.IssuePreviewActivity;
import ru.protei.portal.ui.issue.client.activity.table.AbstractIssueTableView;
import ru.protei.portal.ui.issue.client.activity.table.IssueTableActivity;
import ru.protei.portal.ui.issue.client.view.preview.IssuePreviewView;
import ru.protei.portal.ui.issue.client.view.table.IssueTableView;


/**
 * Описание классов фабрики
 */
public class IssueClientModule extends AbstractGinModule {
    @Override
    protected void configure() {
        bind( IssueTableActivity.class ).asEagerSingleton();
        bind( AbstractIssueTableView.class ).to( IssueTableView.class );

        bind( IssuePreviewActivity.class ).asEagerSingleton();
        bind( AbstractIssuePreviewView.class ).to( IssuePreviewView.class ).in( Singleton.class );

    }
}

