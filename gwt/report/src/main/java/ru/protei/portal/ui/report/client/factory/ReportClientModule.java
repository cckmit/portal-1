package ru.protei.portal.ui.report.client.factory;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;
import ru.protei.portal.ui.report.client.activity.edit.AbstractReportEditView;
import ru.protei.portal.ui.report.client.activity.edit.ReportEditActivity;
import ru.protei.portal.ui.report.client.page.ReportPage;
import ru.protei.portal.ui.report.client.activity.table.AbstractReportTableView;
import ru.protei.portal.ui.report.client.activity.table.ReportTableActivity;
import ru.protei.portal.ui.report.client.view.edit.ReportEditView;
import ru.protei.portal.ui.report.client.view.table.ReportTableView;

public class ReportClientModule extends AbstractGinModule {

    @Override
    protected void configure() {
        bind(ReportPage.class).asEagerSingleton();

        bind(ReportTableActivity.class).asEagerSingleton();
        bind(AbstractReportTableView.class).to(ReportTableView.class);

        bind(ReportEditActivity.class).asEagerSingleton();
        bind(AbstractReportEditView.class).to(ReportEditView.class).in(Singleton.class);
    }
}
