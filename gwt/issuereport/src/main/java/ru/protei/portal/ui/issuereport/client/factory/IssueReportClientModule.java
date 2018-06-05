package ru.protei.portal.ui.issuereport.client.factory;

import com.google.gwt.inject.client.AbstractGinModule;
import ru.protei.portal.ui.issuereport.client.activity.page.IssueReportPage;
import ru.protei.portal.ui.issuereport.client.activity.table.AbstractIssueReportTableView;
import ru.protei.portal.ui.issuereport.client.activity.table.IssueReportTableActivity;
import ru.protei.portal.ui.issuereport.client.view.table.IssueReportTableView;

public class IssueReportClientModule extends AbstractGinModule {

    @Override
    protected void configure() {
        bind(IssueReportPage.class).asEagerSingleton();

        bind(IssueReportTableActivity.class).asEagerSingleton();
        bind(AbstractIssueReportTableView.class).to(IssueReportTableView.class);
    }
}
