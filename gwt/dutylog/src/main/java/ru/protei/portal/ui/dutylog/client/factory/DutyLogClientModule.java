package ru.protei.portal.ui.dutylog.client.factory;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;
import ru.protei.portal.ui.dutylog.client.activity.edit.AbstractDutyLogEditView;
import ru.protei.portal.ui.dutylog.client.activity.edit.DutyLogEditActivity;
import ru.protei.portal.ui.dutylog.client.activity.filter.AbstractDutyLogFilterParamView;
import ru.protei.portal.ui.dutylog.client.activity.page.DutyLogPage;
import ru.protei.portal.ui.dutylog.client.activity.report.AbstractDutyLogReportCreateView;
import ru.protei.portal.ui.dutylog.client.activity.report.DutyLogReportCreateActivity;
import ru.protei.portal.ui.dutylog.client.activity.table.AbstractDutyLogTableView;
import ru.protei.portal.ui.dutylog.client.activity.table.DutyLogTableActivity;
import ru.protei.portal.ui.dutylog.client.view.edit.DutyLogEditView;
import ru.protei.portal.ui.dutylog.client.view.report.DutyLogReportCreateView;
import ru.protei.portal.ui.dutylog.client.view.table.DutyLogTableView;
import ru.protei.portal.ui.dutylog.client.widget.filter.paramview.DutyLogFilterParamWidget;

/**
 * Описание классов фабрики
 */
public class DutyLogClientModule extends AbstractGinModule {

    @Override
    protected void configure() {
        bind(DutyLogPage.class).asEagerSingleton();

        bind(DutyLogTableActivity.class).asEagerSingleton();
        bind(AbstractDutyLogTableView.class).to(DutyLogTableView.class).in(Singleton.class);

        bind(DutyLogEditActivity.class).asEagerSingleton();
        bind(AbstractDutyLogEditView.class).to(DutyLogEditView.class).in(Singleton.class);

        bind(AbstractDutyLogFilterParamView.class).to(DutyLogFilterParamWidget.class).in(Singleton.class);

        bind(DutyLogReportCreateActivity.class).asEagerSingleton();
        bind(AbstractDutyLogReportCreateView.class).to(DutyLogReportCreateView.class).in(Singleton.class);
    }
}
