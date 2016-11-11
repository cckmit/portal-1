package ru.protei.portal.ui.issue.client.factory;

import com.google.gwt.inject.client.AbstractGinModule;
import ru.protei.portal.ui.issue.client.activity.edit.AbstractIssueEditiew;
import ru.protei.portal.ui.issue.client.activity.edit.IssueEditActivity;
import ru.protei.portal.ui.issue.client.activity.table.AbstractIssueTableView;
import ru.protei.portal.ui.issue.client.activity.table.IssueTableActivity;
import ru.protei.portal.ui.issue.client.view.edit.IssueEditView;
import ru.protei.portal.ui.issue.client.view.table.IssueTableView;


/**
 * Описание классов фабрики
 */
public class IssueClientModule extends AbstractGinModule {
    @Override
    protected void configure() {
        bind( IssueTableActivity.class ).asEagerSingleton();
        bind( AbstractIssueTableView.class ).to( IssueTableView.class );

        bind( IssueEditActivity.class ).asEagerSingleton();
        bind( AbstractIssueEditiew.class ).to( IssueEditView.class );
    }
}

