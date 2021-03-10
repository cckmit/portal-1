package ru.protei.portal.ui.issue.client.factory;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;
import ru.protei.portal.ui.common.client.activity.filter.AbstractIssueCollapseFilterView;
import ru.protei.portal.ui.common.client.activity.filter.IssueFilterService;
import ru.protei.portal.ui.common.client.common.LocalStorageService;
import ru.protei.portal.ui.common.client.view.filter.IssueFilterCollapseView;
import ru.protei.portal.ui.issue.client.activity.create.AbstractIssueCreateActivity;
import ru.protei.portal.ui.issue.client.activity.create.AbstractIssueCreateView;
import ru.protei.portal.ui.issue.client.activity.create.IssueCreateActivity;
import ru.protei.portal.ui.issue.client.activity.create.subtask.AbstractSubtaskCreateActivity;
import ru.protei.portal.ui.issue.client.activity.create.subtask.AbstractSubtaskCreateView;
import ru.protei.portal.ui.issue.client.activity.create.subtask.SubtaskCreateActivity;
import ru.protei.portal.ui.issue.client.activity.edit.AbstractIssueEditActivity;
import ru.protei.portal.ui.issue.client.activity.edit.AbstractIssueEditView;
import ru.protei.portal.ui.issue.client.activity.edit.IssueEditActivity;
import ru.protei.portal.ui.issue.client.activity.issuecommenthelp.AbstractAddingIssueCommentHelpActivity;
import ru.protei.portal.ui.issue.client.activity.issuecommenthelp.AbstractAddingIssueCommentHelpView;
import ru.protei.portal.ui.issue.client.activity.issuecommenthelp.AddingIssueCommentHelpActivity;
import ru.protei.portal.ui.issue.client.activity.meta.AbstractIssueMetaActivity;
import ru.protei.portal.ui.issue.client.activity.meta.AbstractIssueMetaView;
import ru.protei.portal.ui.issue.client.activity.meta.IssueMetaActivity;
import ru.protei.portal.ui.issue.client.activity.page.IssuePage;
import ru.protei.portal.ui.issue.client.activity.table.AbstractIssueTableView;
import ru.protei.portal.ui.issue.client.activity.table.IssueTableFilterActivity;
import ru.protei.portal.ui.issue.client.common.CaseStateFilterProvider;
import ru.protei.portal.ui.issue.client.view.create.IssueCreateView;
import ru.protei.portal.ui.issue.client.view.create.subtask.SubtaskCreateView;
import ru.protei.portal.ui.issue.client.view.edit.IssueEditView;
import ru.protei.portal.ui.issue.client.view.addingissuecommenthelp.AddingIssueCommentHelpView;
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
        bind( AbstractIssueCollapseFilterView.class ).to(IssueFilterCollapseView.class).in(Singleton.class);

        bind( AbstractSubtaskCreateActivity.class ).to( SubtaskCreateActivity.class ).asEagerSingleton();
        bind( AbstractSubtaskCreateView.class ).to( SubtaskCreateView.class ).in( Singleton.class );

        bind(AbstractAddingIssueCommentHelpView.class).to(AddingIssueCommentHelpView.class).in(Singleton.class);
        bind(AbstractAddingIssueCommentHelpActivity.class).to(AddingIssueCommentHelpActivity.class).asEagerSingleton();
    }
}

