package ru.protei.portal.ui.issuereport.client.factory;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;
import ru.protei.portal.ui.issuereport.client.activity.create.AbstractIssueReportCreateView;
import ru.protei.portal.ui.issuereport.client.activity.create.IssueReportCreateActivity;
import ru.protei.portal.ui.issuereport.client.activity.page.IssueReportPage;
import ru.protei.portal.ui.issuereport.client.activity.table.AbstractIssueReportTableView;
import ru.protei.portal.ui.issuereport.client.activity.table.IssueReportTableActivity;
import ru.protei.portal.ui.issuereport.client.view.create.IssueReportCreateView;
import ru.protei.portal.ui.issuereport.client.view.table.IssueReportTableView;
import ru.protei.portal.ui.issuereport.client.widget.issuefilter.IssueFilterModel;

public class IssueReportClientModule extends AbstractGinModule {

    @Override
    protected void configure() {
        bind(IssueReportPage.class).asEagerSingleton();

        bind(IssueReportTableActivity.class).asEagerSingleton();
        bind(AbstractIssueReportTableView.class).to(IssueReportTableView.class);

        bind(IssueReportCreateActivity.class).asEagerSingleton();
        bind(AbstractIssueReportCreateView.class).to(IssueReportCreateView.class).in(Singleton.class);

        bind(IssueFilterModel.class).asEagerSingleton();
    }
}
