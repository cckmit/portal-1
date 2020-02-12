package ru.protei.portal.ui.issueassignment.client.factory;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;
import ru.protei.portal.ui.issueassignment.client.activity.desk.AbstractDeskView;
import ru.protei.portal.ui.issueassignment.client.activity.desk.DeskActivity;
import ru.protei.portal.ui.issueassignment.client.activity.desk.rowadd.AbstractDeskRowAddView;
import ru.protei.portal.ui.issueassignment.client.activity.desk.rowissue.AbstractDeskRowIssueView;
import ru.protei.portal.ui.issueassignment.client.activity.desk.rowissue.issue.AbstractDeskIssueView;
import ru.protei.portal.ui.issueassignment.client.activity.desk.rowperson.AbstractDeskRowPersonView;
import ru.protei.portal.ui.issueassignment.client.activity.desk.rowstate.AbstractDeskRowStateView;
import ru.protei.portal.ui.issueassignment.client.activity.issueassignment.AbstractIssueAssignmentView;
import ru.protei.portal.ui.issueassignment.client.activity.issueassignment.IssueAssignmentActivity;
import ru.protei.portal.ui.issueassignment.client.activity.page.IssueAssignmentPage;
import ru.protei.portal.ui.issueassignment.client.view.desk.DeskView;
import ru.protei.portal.ui.issueassignment.client.view.desk.rowadd.DeskRowAddView;
import ru.protei.portal.ui.issueassignment.client.view.desk.rowissue.DeskRowIssueView;
import ru.protei.portal.ui.issueassignment.client.view.desk.rowissue.issue.DeskIssueView;
import ru.protei.portal.ui.issueassignment.client.view.desk.rowperson.DeskRowPersonView;
import ru.protei.portal.ui.issueassignment.client.view.desk.rowstate.DeskRowStateView;
import ru.protei.portal.ui.issueassignment.client.view.issueassignment.IssueAssignmentView;

public class IssueAssignmentClientModule extends AbstractGinModule {
    @Override
    protected void configure() {

        bind(IssueAssignmentPage.class).asEagerSingleton();

        bind(IssueAssignmentActivity.class).asEagerSingleton();
        bind(AbstractIssueAssignmentView.class).to(IssueAssignmentView.class).in(Singleton.class);
        bind(DeskActivity.class).asEagerSingleton();
        bind(AbstractDeskView.class).to(DeskView.class).in(Singleton.class);
        bind(AbstractDeskRowStateView.class).to(DeskRowStateView.class);
        bind(AbstractDeskRowPersonView.class).to(DeskRowPersonView.class);
        bind(AbstractDeskRowIssueView.class).to(DeskRowIssueView.class);
        bind(AbstractDeskIssueView.class).to(DeskIssueView.class);
        bind(AbstractDeskRowAddView.class).to(DeskRowAddView.class);
    }
}
