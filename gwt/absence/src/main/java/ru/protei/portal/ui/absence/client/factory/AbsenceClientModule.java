package ru.protei.portal.ui.absence.client.factory;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;
import ru.protei.portal.ui.absence.client.activity.edit.AbsenceEditActivity;
import ru.protei.portal.ui.absence.client.activity.edit.AbstractAbsenceEditView;
import ru.protei.portal.ui.absence.client.activity.report.AbsenceReportCreateActivity;
import ru.protei.portal.ui.absence.client.activity.report.AbstractAbsenceReportCreateView;
import ru.protei.portal.ui.absence.client.activity.report.paramview.AbstractAbsenceFilterParamWidget;
import ru.protei.portal.ui.absence.client.activity.table.AbsenceTableActivity;
import ru.protei.portal.ui.absence.client.activity.table.AbstractAbsenceTableView;
import ru.protei.portal.ui.absence.client.view.edit.AbsenceEditView;
import ru.protei.portal.ui.absence.client.view.report.AbsenceReportCreateView;
import ru.protei.portal.ui.absence.client.widget.paramview.AbsenceFilterParamWidget;
import ru.protei.portal.ui.absence.client.view.table.AbsenceTableView;
import ru.protei.portal.ui.common.client.widget.selector.absencereason.AbsenceReasonModel;

/**
 * Описание классов фабрики
 */
public class AbsenceClientModule extends AbstractGinModule {

    @Override
    protected void configure() {

        bind(AbsenceReasonModel.class).asEagerSingleton();

        bind(AbsenceTableActivity.class).asEagerSingleton();
        bind(AbstractAbsenceTableView.class).to(AbsenceTableView.class).in(Singleton.class);

        bind(AbsenceEditActivity.class).asEagerSingleton();
        bind(AbstractAbsenceEditView.class).to(AbsenceEditView.class).in(Singleton.class);

        bind(AbsenceReportCreateActivity.class).asEagerSingleton();
        bind(AbstractAbsenceReportCreateView.class).to(AbsenceReportCreateView.class).in(Singleton.class);
        bind(AbstractAbsenceFilterParamWidget.class).to(AbsenceFilterParamWidget.class).in(Singleton.class);
    }
}
